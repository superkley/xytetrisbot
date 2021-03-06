/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
package cn.keke.qqtetris;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public final class TransformationTask implements Runnable {
    private final boolean[] board;
    private final static ThreadLocal<boolean[]> boardCopyLocal = new ThreadLocal<boolean[]>() {
        @Override
        public boolean[] initialValue() {
            return new boolean[CurrentData.CALCULATED.board.length];
        }
    };
    private final int[] piecesHeight;
    private final BlockType[] blocks;
    private final List<TransformationResult[]> prevResults;
    private TransformationResult result;
    private final Semaphore executorLock;
    private final ThreadPoolExecutor executor;
    private final TransformationResult[] predictedResults;
    private final StrategyType strategy;
    private final double[] strategyAttrs;
    private final AtomicInteger taskCounter;
    private final QQCalculatorAsync calc;

    public TransformationTask(QQCalculatorAsync calc, TransformationResult[] predictedResults, boolean[] board,
            int[] highests, BlockType[] nextBlocks, List<TransformationResult[]> prList, Semaphore lock,
            ThreadPoolExecutor executor, StrategyType strategy, double[] strategyAttrs, AtomicInteger taskCounter) {
        this.taskCounter = taskCounter;
        this.calc = calc;
        this.predictedResults = predictedResults;
        this.board = board;
        this.piecesHeight = highests;
        this.blocks = nextBlocks;
        this.prevResults = prList;
        this.executorLock = lock;
        this.executor = executor;
        this.strategy = strategy;
        this.strategyAttrs = strategyAttrs;
    }

    public void setResult(TransformationResult result) {
        this.result = result;
    }

    public TransformationResult getResult() {
        return this.result;
    }

    public void run() {
        // merge tranforms (inserts and cleanups)
        try {
            if (!calc.isCancelled()) {
                final int l = MoveCalculator.findNextBlocksLimit(blocks);
                if (l > 0) {
                    // STOPPER.start();
                    final LinkedList<TransformationResult[]> prList = new LinkedList<TransformationResult[]>();
                    final BlockType[] nextBlocks = Arrays.copyOfRange(this.blocks, 1, l);
                    if (this.prevResults.isEmpty()) {
                        runCalculation(nextBlocks, QQCalculatorAsync.EMPTY_TRANSFORMATION_RESULTS, prList);
                    } else {
                        for (TransformationResult[] pr : this.prevResults) {
                            runCalculation(nextBlocks, pr, prList);
                        }
                    }
                    if (!calc.isCancelled() && !prList.isEmpty()) {
                        final TransformationTask task = new TransformationTask(calc, this.predictedResults, this.board,
                                this.piecesHeight, nextBlocks, prList, this.executorLock, this.executor,
                                this.strategy, this.strategyAttrs, this.taskCounter);
                        // send jobs
                        this.taskCounter.incrementAndGet();
                        this.executor.execute(task);
                    }
                    // STOPPER.printTime("spread");
                } else {
                    // STOPPER.start();
                    for (TransformationResult[] pr : this.prevResults) {
                        // calculate stats for each combination
                        final double score = BoardUtils.mergeAndCalcScore(this.board, pr, this.strategy,
                                this.strategyAttrs);

                        // get max score
                        final TransformationResult firstTransResult = pr[0];
                        final int x = firstTransResult.getX();
                        final TransformationResult tr = this.predictedResults[x + QQTetris.BlockDrawSize];
                        // System.out.println(firstTransResult +", score="+score);
                        if (score > tr.getScore()) {
                            tr.update(firstTransResult.getRotationIdx(), firstTransResult.getX(),
                                    firstTransResult.getY(), score, firstTransResult.getCleverPoints());
                        }
                        // System.out.println("score: r=" + tr.getRotationIdx() +
                        // ", x=" + tr.getX() + ", y=" + tr.getY() + " -> " +
                        // tr.getScore());
                    }
                    // STOPPER.printTime("merge");
                }
            }
        } finally {
            if (this.taskCounter.decrementAndGet() == 0) {
            	this.executorLock.release();	
            }
        }
    }

    private void runCalculation(final BlockType[] nextBlocks, final TransformationResult[] pr, LinkedList<TransformationResult[]> prList) {
        // create combinations with rotations * moves
        final BlockType type = this.blocks[0];
        int i, h, max;
        BlockRotation rt;
        // pieces on top
        final int[] testPiecesHeight = BoardUtils.calcPiecesHeight(this.piecesHeight, pr);
        // System.out.println("pieces on top: " +
        // Arrays.toString(testPiecesHeight));
        // calculate fit
        final int prevLength = pr.length;
        LinkedList<Point> cleverPoints = null;
        for (int idx = 0; idx < type.rotations.length; idx++) {
            rt = type.rotations[idx];
            cleverPoints = null;
            for (int x = -rt.freeLeft; x <= QQTetris.PiecesWidth - rt.freeLeft - rt.width; x++) {
                max = -1;
                for (i = 0; i < rt.width; i++) {
                    h = testPiecesHeight[x + i + rt.freeLeft] + rt.piecesBottoms[i] + 1;
                    if (h > max) {
                        max = h;
                    }
                }
                int y = QQTetris.PiecesHeight - max;
                // QQDebug.printBlock(rt.form);
                // System.out.println(rt + ", x: " + x + ", y: " + y);
                // System.out.println(Arrays.toString(testPiecesHeight));
                if (QQTetris.cleverMode) {
		                if (y >= QQTetris.BlockDrawSize) {
		                    final boolean[] boardCopy = TransformationTask.boardCopyLocal.get();
		                    System.arraycopy(this.board, 0, boardCopy, 0, this.board.length);
		                    BoardUtils.mergeResults(boardCopy, pr);
                        cleverPoints = MoveCalculator.findCleverMove(boardCopy, rt, x, y);
                        if (cleverPoints != null) {
                            y = cleverPoints.get(0).y;
                        }
                    }
                }
                final boolean correctPlacement = prevLength > 0 || cleverPoints != null
                        || rt.faultChecker.check(this.board, x, y);
                if (y >= 0 && correctPlacement) {
	                	if (!calc.isCancelled()) {
		                    // create jobs for each combination
		                    final TransformationResult tr = new TransformationResult(type, idx, x, y, -1, cleverPoints);
		                    final TransformationResult[] newResults = new TransformationResult[prevLength + 1];
		                    System.arraycopy(pr, 0, newResults, 0, prevLength);
		                    newResults[newResults.length - 1] = tr;
		                    prList.add(newResults);
		                    
		                    // System.out.println("pre result: " + tr);
		                    if (prList.size() >= QQCalculatorAsync.BATCH_SIZE) {
		                        final TransformationTask task = new TransformationTask(calc, this.predictedResults, this.board,
		                                this.piecesHeight, nextBlocks, new ArrayList<TransformationResult[]>(prList), this.executorLock, this.executor,
		                                this.strategy, this.strategyAttrs, this.taskCounter);
		                        // send jobs
		                        this.taskCounter.incrementAndGet();
		                        this.executor.execute(task);
		                        prList.clear();
		                    }
	                  } else {
                        return;
	                  }
                }
            }
        }
    }
}

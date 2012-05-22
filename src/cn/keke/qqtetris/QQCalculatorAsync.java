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

import static cn.keke.qqtetris.QQTetris.BlockDrawSize;
import static cn.keke.qqtetris.QQTetris.PiecesWidth;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QQCalculatorAsync extends MoveCalculator {
    private final Semaphore lock = new Semaphore(0);
    public final static int BATCH_SIZE = 10000;
    public static final TransformationResult[] EMPTY_TRANSFORMATION_RESULTS = {};
    private static final int AVAILABLE_PROCESSORS = Math.max(2, Runtime.getRuntime().availableProcessors() - 1);
    private static final ThreadPoolExecutor CALC_EXECUTOR = new ThreadPoolExecutor(AVAILABLE_PROCESSORS,
            AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS, new LiFoDeque<Runnable>(),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    private static final long SLEEP_MAX = 1000;

    final TransformationResult[] predictedResults = new TransformationResult[PiecesWidth + BlockDrawSize];
    private int timeouts;
    private int recovers;

    AtomicInteger taskCounter = new AtomicInteger(0);
    public QQCalculatorAsync() {
        for (int i = 0; i < predictedResults.length; i++) {
            predictedResults[i] = new TransformationResult();
        }
    }

    @Override
    public void findBestMove(final boolean[] boardData, final Tetromino t, final BlockType[] nextBlocks, QQStats stats,
            StrategyType strategy, double[] strategyAttrs) {
        initializeNextBlocks(nextBlocks, strategy, stats);
        for (int i = 0; i < predictedResults.length; i++) {
            predictedResults[i].set(t.block);
        }
        taskCounter.set(0);
        final int[] piecesHeight = BoardUtils.calcBoardHeight(boardData);
        // System.out.println("orgin heights: " + Arrays.toString(piecesHeight));
        final TransformationTask task = new TransformationTask(this, predictedResults, boardData, piecesHeight,
                nextBlocks, new LinkedList<TransformationResult[]>(), lock, CALC_EXECUTOR, strategy, strategyAttrs,
                taskCounter);
        if (!cancelled) {
            taskCounter.incrementAndGet();
            CALC_EXECUTOR.execute(task);
            boolean success = false;
            try {
                success = lock.tryAcquire(SLEEP_MAX, TimeUnit.MILLISECONDS);
                if (cancelled) {
                    return;
                } else if (!success) {
                    this.timeouts++;
                    System.err.println("电脑速度太慢！");
                } else {
                    if (this.timeouts > 0) {
                        this.recovers++;
                        if (this.recovers < 5) {
                            this.timeouts = 0;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!success) {
                // throw new
                // BestMoveCalculationException("Failed to calculate best cleverPoints with tetromino " +
                // t);
            } else {
                double bestScore = NO_RESULT_SCORE;
                final TransformationResult bestResult = new TransformationResult();
                for (TransformationResult r : predictedResults) {
                    // System.out.println("score: r=" + r.getRotationIdx() + ", x=" + r.getX() + ", y=" +
                    // r.getY() +
                    // " -> "
                    // + r.getScore());
                    if (r.getScore() > bestScore) {
                        bestResult.set(r);
                        bestScore = r.getScore();
                    }
                }
                // System.out.println("best score: " + bestResult.getX() + " -> " + bestResult.getScore());
                // calculate MoveResult
                if (bestResult != null) {
                    if (bestResult.getCleverPoints() == null) {
                        // System.out.println("normal: " + bestResult);
                        createMove(bestResult, t);
                    } else {
                        // System.out.println("clever: " + bestResult);
                        createCleverMove(t, bestResult.getRotationIdx(), bestResult.getCleverPoints(),
                                bestResult.getScore());
                    }
                }
            }
        }
    }

    @Override
    public void cancel() {
        this.cancelled = true;
        this.lock.release();
    }

    public final boolean isCancelled() {
        return cancelled;
    }

}

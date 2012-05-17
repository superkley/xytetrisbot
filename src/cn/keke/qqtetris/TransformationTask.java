package cn.keke.qqtetris;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformationTask implements Runnable {
    private final boolean[] board;
    private final static ThreadLocal<boolean[]> boardCopyLocal = new ThreadLocal<boolean[]>() {
        @Override
        public boolean[] initialValue() {
            return new boolean[CurrentData.CALCULATED.board.length];
        }
    };
    private final int[] piecesHeight;
    private final BlockType[] blocks;
    private final LinkedList<TransformationResult[]> prevResults;
    private TransformationResult result;
    private final Semaphore executorLock;
    private final ThreadPoolExecutor executor;
    private final TransformationResult[] predictedResults;
    private final StrategyType strategy;
    private final double[] strategyAttrs;
    private LinkedList<TransformationResult[]> prList;
    private final AtomicInteger taskCounter;
    private final QQCalculatorAsync calc;

    public TransformationTask(QQCalculatorAsync calc, TransformationResult[] predictedResults, boolean[] board,
            int[] highests, BlockType[] nextBlocks, LinkedList<TransformationResult[]> prList, Semaphore lock,
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
        if (this.prevResults == null) {
            System.err.println("prev results null!");
        }
    }

    public void setResult(TransformationResult result) {
        this.result = result;
    }

    public TransformationResult getResult() {
        return this.result;
    }

    @Override
    public void run() {
        // merge tranforms (inserts and cleanups)
        try {
            if (!calc.isCancelled()) {
                final int l = MoveCalculator.findNextBlocksLimit(blocks);
                if (l > 0) {
                    // STOPPER.start();
                    this.prList = new LinkedList<TransformationResult[]>();
                    final BlockType[] nextBlocks = Arrays.copyOfRange(this.blocks, 1, l);
                    if (this.prevResults.isEmpty()) {
                        runCalculation(nextBlocks, QQCalculatorAsync.EMPTY_TRANSFORMATION_RESULTS);
                    } else {
                        for (TransformationResult[] pr : this.prevResults) {
                            runCalculation(nextBlocks, pr);
                        }
                    }
                    if (!calc.isCancelled() && !this.prList.isEmpty()) {
                        final TransformationTask task = new TransformationTask(calc, this.predictedResults, this.board,
                                this.piecesHeight, nextBlocks, this.prList, this.executorLock, this.executor,
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
            this.taskCounter.decrementAndGet();
            checkFinish();
        }
    }

    private final void checkFinish() {
        // System.out.println("now: " + this.taskCounter.intValue());
        if (this.taskCounter.intValue() == 0 && this.executor.getQueue().isEmpty()
                && this.executor.getActiveCount() <= 1) {
            // System.out.println("release");
            this.executorLock.release();
        }
    }

    private void runCalculation(final BlockType[] nextBlocks, final TransformationResult[] pr) {
        // create combinations with rotations * moves
        final BlockType type = this.blocks[0];
        int i, h, max;
        BlockRotation rt;
        // pieces on top
        final int[] testPiecesHeight = BoardUtils.calcPiecesHeight(this.piecesHeight, pr);
        // System.out.println("pieces on top: " +
        // Arrays.toString(testPiecesHeight));
        // calculate fit
        TransformationResult tr;
        TransformationResult[] newResults;
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
                if (y >= QQTetris.BlockDrawSize) {
                    final boolean[] boardCopy = TransformationTask.boardCopyLocal.get();
                    System.arraycopy(this.board, 0, boardCopy, 0, this.board.length);
                    BoardUtils.mergeResults(boardCopy, pr);
                    cleverPoints = MoveCalculator.findCleverMove(boardCopy, rt, x, y);
                    if (cleverPoints != null) {
                        y = cleverPoints.get(0).y;
                    }
                }
                final boolean correctPlacement = prevLength > 0 || cleverPoints != null
                        || rt.faultChecker.check(this.board, x, y);
                if (y >= 0 && correctPlacement) {
                    // create jobs for each combination
                    tr = new TransformationResult(type, idx, x, y, -1, cleverPoints);
                    // System.out.println("pre result: " + tr);
                    newResults = new TransformationResult[prevLength + 1];
                    System.arraycopy(pr, 0, newResults, 0, prevLength);
                    newResults[newResults.length - 1] = tr;
                    if (this.prList.size() < QQCalculatorAsync.BATCH_SIZE) {
                        this.prList.add(newResults);
                    } else if (!calc.isCancelled()) {
                        final TransformationTask task = new TransformationTask(calc, this.predictedResults, this.board,
                                this.piecesHeight, nextBlocks, this.prList, this.executorLock, this.executor,
                                this.strategy, this.strategyAttrs, this.taskCounter);
                        // send jobs
                        this.taskCounter.incrementAndGet();
                        this.executor.execute(task);
                        this.prList = new LinkedList<TransformationResult[]>();
                    } else {
                        return;
                    }
                }
            }
        }
    }
}

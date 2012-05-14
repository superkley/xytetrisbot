package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.BlockDrawSize;
import static cn.keke.qqtetris.QQTetris.PiecesWidth;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QQCalculatorAsync extends MoveCalculator {
    private static final Semaphore             SEMAPHORE                    = new Semaphore(0);
    public final static int                    BATCH_SIZE                   = 10000;
    private final StopWatch             STOPPER                      = new StopWatch("calc");
    public static final TransformationResult[] EMPTY_TRANSFORMATION_RESULTS = {};
    private static final int                   AVAILABLE_PROCESSORS         = Runtime.getRuntime().availableProcessors();
    private static final ThreadPoolExecutor    CALC_EXECUTOR                = new ThreadPoolExecutor(AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS,
                                                                                     0L, TimeUnit.MILLISECONDS, new LiFoDeque<Runnable>());

    private int                                timeouts;
    private int                                recovers;

    public QQCalculatorAsync() {
    }

    public MoveResult findBestMove(QQStats stats, StrategyType strategy, double[] strategyAttrs) {
        Tetromino t = stats.tetromino;
        BlockType[] nextBlocks = initializeNextBlocks(stats.nextBlocks, strategy, stats);
        if (QQTetris.ANALYZE) {
            STOPPER.start();
        }
        TransformationResult[] predictedResults = new TransformationResult[PiecesWidth + BlockDrawSize];
        for (int i = 0; i < predictedResults.length; i++) {
            predictedResults[i] = new TransformationResult(t.block);
        }
        int[] piecesHeight = BoardUtils.calcBoardHeight(QQTetris.BOARD_DATA);
        // System.out.println("orgin heights: " + Arrays.toString(piecesHeight));
        AtomicInteger taskCounter = new AtomicInteger(0);
        TransformationTask task = new TransformationTask(predictedResults, QQTetris.BOARD_DATA, piecesHeight, nextBlocks, new ArrayList<TransformationResult[]>(),
                SEMAPHORE, CALC_EXECUTOR, strategy, strategyAttrs, taskCounter);
        CALC_EXECUTOR.execute(task);
        boolean success = false;
        try {
            success = SEMAPHORE.tryAcquire(QQScreenCaptureThread.SLEEP_MAX, TimeUnit.MILLISECONDS);
            if (!success) {
                this.timeouts++;
                System.err.println("Computer too slow for calculation!");
            } else if (this.timeouts > 0) {
                this.recovers++;
                if (this.recovers < 5) {
                    this.timeouts = 0;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!success) {
            if (QQTetris.ANALYZE) {
                STOPPER.printTime("not found");
            }
            // throw new BestMoveCalculationException("Failed to calculate best cleverPoints with tetromino " + t);
            return NO_MOVE;
        } else {
            double bestScore = NO_RESULT_SCORE;
            TransformationResult bestResult = null;
            for (TransformationResult r : predictedResults) {
                if (QQTetris.TEST) {
                    System.out.println("score: r=" + r.getRotationIdx() + ", x=" + r.getX() + ", y=" + r.getY() + " -> " + r.getScore());
                }
                // System.out.println("score: r=" + r.getRotationIdx() + ", x=" + r.getX() + ", y=" + r.getY() + " -> " + r.getScore());
                if (r.getScore() > bestScore) {
                    bestResult = r;
                    bestScore = r.getScore();
                }
            }
            // System.out.println("best score: " + bestResult.getX() + " -> " + bestResult.getScore());
            if (QQTetris.TEST) {
                System.out.println("best score: " + bestResult.getX() + " -> " + bestResult.getScore());
            }
            // calculate MoveResult
            MoveResult moveResult = NO_MOVE;
            if (bestResult != null) {
                if (bestResult.getCleverPoints() == null) {
                    moveResult = createMove(bestResult, t);
                } else {
                    moveResult = createCleverMove(t, bestResult.getRotationIdx(), bestResult.getCleverPoints(), bestResult.getScore());
                }
            }
            if (QQTetris.ANALYZE) {
                this.STOPPER.printTime("found");
            }
            return moveResult;
        }
    }
}

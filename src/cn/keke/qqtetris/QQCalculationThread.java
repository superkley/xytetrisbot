package cn.keke.qqtetris;

import java.util.concurrent.Semaphore;

public class QQCalculationThread extends Thread {
    private Semaphore lock = new Semaphore(3);
    private final MoveCalculator calculator;
    private static final int SLEEP_MIN = 0;
    private static final int SLEEP_MAX = 1000;
    private int sleep = 200;
    private int maxDuration;
    private int maxDurationFailed;

    public QQCalculationThread(final MoveCalculator calculator) {
        super("QQCalculationThread");
        this.calculator = calculator;
    }

     @Override
    public void run() {
        this.lock.acquireUninterruptibly(this.lock.availablePermits());
        while (true) {
            this.lock.acquireUninterruptibly();
            // System.out.println("计算（开始）：" + Arrays.toString(CurrentData.CALCULATED.nextBlocks));
            // QQDebug.printBoard(CurrentData.CALCULATED.board);
            final long start = System.currentTimeMillis();            
            try {
                if (this.calculator.start()) {
                    // System.out.println("计算（结束）：" + CurrentData.CALCULATED.tetromino.move);
                    final int duration = (int) (System.currentTimeMillis() - start);
                    checkDuration(duration);
                    // Thread.sleep(Math.max(0, sleep - duration));
                    QQTetris.captureScreenThread.followMove();                    
                    continue;
                }
            } catch (Throwable t) {
                // System.out.println("计算（错误）：" + CurrentData.CALCULATED.tetromino.move);
            }
            System.out.println("计算（错误）：" + CurrentData.CALCULATED.tetromino.move);
            checkDurationFailed((int) (System.currentTimeMillis() - start));
            QQTetris.captureScreenThread.onFailure();
        }
    }

    private final void checkDuration(final int duration) {
        if (duration > this.maxDuration) {
            System.out.println("计算（max）：" + duration + "，" + CurrentData.CALCULATED.tetromino.move);
            this.maxDuration = duration;
        }
    }

    private final void checkDurationFailed(final int duration) {
        if (duration > this.maxDurationFailed) {
            System.out.println("计算（max-failed）：" + duration);
            this.maxDurationFailed = duration;
        }
    }

    public void startCalculation() {
        this.lock.release();
    }

    public void cancel() {
        this.calculator.cancel();
    }

    public void mergeMove() {
        if (CurrentData.CALCULATED.tetromino.move.isValid()) {
            BoardUtils.mergeMoveResult(CurrentData.CALCULATED.board, CurrentData.CALCULATED.tetromino,
                    CurrentData.CALCULATED.tetromino.move);
            BoardUtils.clearFullLines(CurrentData.CALCULATED.board);
            CurrentData.CALCULATED.stats.setCalculate(false);
        }
    }

    public void increaseSpeed() {
        this.setSleep(this.sleep - 50);
    }

    private final void setSleep(final int sleep) {
        if (sleep >= SLEEP_MIN && sleep <= SLEEP_MAX) {
            this.sleep = sleep;
        } else if (sleep < SLEEP_MIN) {
            this.sleep = SLEEP_MIN;
        } else {
            this.sleep = SLEEP_MAX;
        }
    }

    public void decreaseSpeed() {
        this.setSleep(this.sleep + 50);
    }

    public String getSpeedPct() {
        int range = SLEEP_MAX - SLEEP_MIN;
        long pct = Math.round(100 - (this.sleep - SLEEP_MIN) * 100.0 / range);
        if (pct < 10) {
            return "  " + pct + "%";
        } else if (pct < 100) {
            return " " + pct + "%";
        } else {
            return pct + "%";
        }
    }

}

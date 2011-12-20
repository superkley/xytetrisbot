package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.ANALYZE;
import static cn.keke.qqtetris.QQTetris.DEBUG;
import static cn.keke.qqtetris.QQTetris.MyCoordX;
import static cn.keke.qqtetris.QQTetris.MyCoordY;
import static cn.keke.qqtetris.QQTetris.QQCoord;

import java.util.concurrent.locks.ReentrantLock;

import cn.keke.qqtetris.exceptions.BestMoveCalculationException;
import cn.keke.qqtetris.exceptions.BoardNotReadyException;
import cn.keke.qqtetris.exceptions.MissingTetrisWindowException;
import cn.keke.qqtetris.exceptions.UnknownBoardStateException;

/**
 * <pre>
 * 1. Make screen shot, interpret board, next blocks, calculate statistics e.g. heights
 * 2. Check and use items
 * 3. Calculate best move
 * 4. Do move
 * 5. Sleep
 * </pre>
 */
public class QQThread extends Thread {
    public final static int        SLEEP_MAX         = 2000;
    public final static int        SLEEP_MIN         = 25;
    private int                    sleep             = 100;
    private boolean                started           = false;
    private boolean                lastRunSuccessful = false;
    // no atomic for performance
    private boolean                running           = false;
    private final static StopWatch STOPPER           = new StopWatch("run");
    private int                    lastBoardHash     = Integer.MIN_VALUE;
    private final QQTetris         qqWindow;
    private MoveCalculator         calculator;
    ReentrantLock                  runningLock       = new ReentrantLock();
    private boolean                inDanger          = false;

    public QQThread(QQTetris qqWindow, MoveCalculator calculator) {
        super("QQThread");
        this.qqWindow = qqWindow;
        // this.calculator = QQCalculatorAsync.INSTANCE;
        // this.calculator = QQCalculatorSync.INSTANCE;
        this.calculator = calculator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (this.isLastRunSuccessful()) {
                    Thread.sleep(this.sleep);
                } else {
                    Thread.sleep(SLEEP_MIN);
                }
                // System.out.println("run: " + System.currentTimeMillis());
                if (this.running) {
                    if (this.isStarted()) {
                        if (QQTetris.ANALYZE) {
                            STOPPER.start();
                        }
                        // find board and next blocks and make stats
                        QQStats stats = QQRobot.makeStats(this.qqWindow.isAutoBlue());
                        if (QQTetris.ANALYZE) {
                            STOPPER.printTime("stats");
                        }
                        if (stats.hash != this.lastBoardHash && stats.isValid()) {
                            // STOPPER.start();
                            StrategyType strategy = this.qqWindow.getStrategy();
                            updateInDanger(stats);
                            MoveResult move = this.calculator.findBestMove(stats, strategy, strategy.getAttrs(this.inDanger));
                            // STOPPER.printTime("find");
                            if (move != MoveCalculator.NO_MOVE) {
                                move.doMove(stats);
                                this.lastBoardHash = stats.hash;
                                if (stats.isInDanger()) {
                                    this.setLastRunSuccessful(false);
                                    Thread.sleep(SLEEP_MIN);
                                } else {
                                    this.setLastRunSuccessful(true);
                                    Thread.sleep(this.sleep);
                                }
                            }
                        }
                    } else {
                        initRobot();
                    }
                } else {
                    Thread.sleep(SLEEP_MAX);
                }
            } catch (UnknownBoardStateException e) {
                this.setLastRunSuccessful(false);
                if (DEBUG) {
                    System.err.println(e.toString());
                } else if (ANALYZE) {
                    System.err.println(".");
                }
            } catch (BoardNotReadyException e) {
                this.setLastRunSuccessful(false);
                if (DEBUG) {
                    System.err.println(e.toString());
                } else if (ANALYZE) {
                    System.err.println(".");
                }
            } catch (MissingTetrisWindowException e) {
                this.setLastRunSuccessful(true);
                this.setStarted(false);
            } catch (BestMoveCalculationException e) {
                this.setLastRunSuccessful(false);
                if (DEBUG) {
                    System.err.println(e.toString());
                } else if (ANALYZE) {
                    System.err.println(".");
                }
            } catch (RuntimeException e) {
                this.setLastRunSuccessful(false);
                if (ANALYZE) {
                    System.err.println(".");
                } else {
                    System.err.println(e.toString());
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                if (ANALYZE) {
                    System.err.println(".");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateInDanger(QQStats stats) {
        if (inDanger) {
            if (stats.highest < 6) {
                inDanger = false;
            }
        } else {
            if (stats.highest > 12) {
                inDanger = true;
            }
        }
    }

    public void pause() {
        if (this.runningLock.tryLock()) {
            try {
                // System.out.println("pause: " + System.currentTimeMillis());
                this.running = false;
                this.setStarted(false);
                this.qqWindow.setState(QQState.STOPPED);
            } finally {
                this.runningLock.unlock();
            }
        }
    }

    public void go() {
        if (this.runningLock.tryLock()) {
            try {
                // System.out.println("go: " + System.currentTimeMillis());
                this.qqWindow.setState(QQState.WAITING);
                this.setStarted(false);
                initRobot();
                this.qqWindow.activate();
                this.running = true;
                if (ANALYZE) {
                    STOPPER.printTime("go");
                }
            } finally {
                this.runningLock.unlock();
            }
        }
    }

    /**
     * find tetris window and my space
     */
    private void initRobot() {
        try {
            QQRobot.init();
            this.setStarted(true);
        } catch (MissingTetrisWindowException e) {
            if (DEBUG) {
                System.err.println(e.getLocalizedMessage());
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public int getSleep() {
        return this.sleep;
    }

    public synchronized void increaseSpeed() {
        if (this.sleep - 100 > SLEEP_MIN) {
            this.setSleep(this.sleep - 100);
        } else {
            this.setSleep(SLEEP_MIN);
        }
    }

    public synchronized void decreaseSpeed() {
        if (this.sleep + 100 < SLEEP_MAX) {
            this.setSleep(this.sleep + 100);
        } else {
            this.setSleep(SLEEP_MAX);
        }
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

    public void setStarted(boolean started) {
        if (this.started != started) {
            this.qqWindow.setState(started ? QQState.PLAYING : QQState.WAITING);
            this.started = started;
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setLastRunSuccessful(boolean successful) {
        this.qqWindow.setState((!successful) ? QQState.WARNING : (this.started ? QQState.PLAYING : QQState.WAITING));
        this.lastRunSuccessful = successful;
    }

    public boolean isLastRunSuccessful() {
        return this.lastRunSuccessful;
    }

}

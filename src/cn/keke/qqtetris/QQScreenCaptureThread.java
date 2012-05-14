package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.ANALYZE;

import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 * 1. Make screen shot, interpret board, next blocks, calculate statistics e.g. heights
 * 2. Check and use items
 * 3. Calculate best move
 * 4. Do move
 * 5. Sleep
 * </pre>
 */
public class QQScreenCaptureThread extends Thread {
    private boolean started = false;
    // no atomic for performance
    private boolean running = false;
    private final static StopWatch STOPPER = new StopWatch("run");
    ReentrantLock runningLock = new ReentrantLock();
    private WorkflowStep step = WorkflowStep.DETECT_WINDOW;

    public QQScreenCaptureThread() {
        super("QQThread");
        setPriority(Thread.NORM_PRIORITY);
        // this.calculator = QQCalculatorAsync.INSTANCE;
        // this.calculator = QQCalculatorSync.INSTANCE;
    }

    @Override
    public void run() {
        if (this.running) {
            step = step.execute();
        }
        QQTetris.executor.execute(this);
    }

    public void pause() {
        if (this.runningLock.tryLock()) {
            try {
                // System.out.println("pause: " + System.currentTimeMillis());
                this.running = false;
                this.setStarted(false);
                QQTetris.setState(QQState.STOPPED);
            } finally {
                this.runningLock.unlock();
            }
        }
    }

    public void go() {
        if (this.runningLock.tryLock()) {
            try {
                // System.out.println("go: " + System.currentTimeMillis());
                QQTetris.setState(QQState.WAITING);
                this.step = WorkflowStep.DETECT_WINDOW;
                this.setStarted(false);
                this.running = true;
                if (ANALYZE) {
                    STOPPER.printTime("go");
                }
            } finally {
                this.runningLock.unlock();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setStarted(boolean started) {
        if (this.started != started) {
            QQTetris.setState(started ? QQState.PLAYING : QQState.WAITING);
            this.started = started;
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setLastRunSuccessful(boolean successful) {
        QQTetris.setState((!successful) ? QQState.WARNING : (this.started ? QQState.PLAYING : QQState.WAITING));
    }

}

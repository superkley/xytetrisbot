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
public final class QQScreenCaptureThread extends Thread {
    private boolean started = false;
    // no atomic for performance
    private boolean running = false;
    ReentrantLock runningLock = new ReentrantLock();
    WorkflowStep step = WorkflowStep.DETECT_WINDOW;
    private WorkflowStep nextStep;

    public QQScreenCaptureThread() {
        super("QQScreenCaptureThread");
        setPriority(Thread.NORM_PRIORITY);
        // this.calculator = QQCalculatorAsync.INSTANCE;
        // this.calculator = QQCalculatorSync.INSTANCE;
    }

    @Override
    public void run() {
        while (true) {
          	try {
		            if (this.running) {
		                checkNextStep();
		                // final WorkflowStep oldStep = this.step;
		                step = step.execute();
		                // if (step != oldStep) {
		                // System.out.println("步：" + this.step);
		                // }
		            } else {
                    Thread.sleep(2000);
		            }
	          } catch (InterruptedException e) {
	            // ignore
		        }
        }
    }

    private final void checkNextStep() {
        if (nextStep != null) {
            step = nextStep;
            nextStep = null;
        }
    }

    public void pause() {
        if (this.runningLock.tryLock()) {
            try {
                // System.out.println("pause: " + System.currentTimeMillis());
                this.running = false;
                this.setStarted(false);
                QQTetris.setState(QQState.STOPPED);
                interrupt();
            } finally {
                this.runningLock.unlock();
            }
        }
    }

    public void go() {
        if (!this.running) {
            if (this.runningLock.tryLock()) {
                try {
                    // System.out.println("go: " + System.currentTimeMillis());
                    QQTetris.setState(QQState.WAITING);
                    this.nextStep = WorkflowStep.DETECT_WINDOW;
                    // System.out.println("步：" + this.step);
                    this.setStarted(false);
                    this.running = true;
                    interrupt();
                } finally {
                    this.runningLock.unlock();
                }
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

    public void followMove() {
        if (CurrentData.CALCULATED.tetromino.move.isValid()) {
            // System.out.println("结果：" + CurrentData.CALCULATED.tetromino.move);
            this.nextStep = WorkflowStep.FOLLOW_MOVE;
            // System.out.println("步：" + this.nextStep);
        } else {
            // System.err.println("无结果：" + this.step + "，" + CurrentData.CALCULATED.tetromino.move);
            this.nextStep = WorkflowStep.INITIAL_BOARD;
            // System.out.println("步：" + this.nextStep);
        }
        interrupt();
    }

    public void onFailure() {
        synchronized (this) {
            this.nextStep = this.step.fail();
            // System.out.println("步：" + this.nextStep);
        }
    }
}

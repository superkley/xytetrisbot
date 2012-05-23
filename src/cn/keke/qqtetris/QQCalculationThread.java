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

import java.util.concurrent.Semaphore;

public class QQCalculationThread extends Thread {
    private Semaphore lock = new Semaphore(3);
    private final MoveCalculator calculator;
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
}

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

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class KeyboardThread extends Thread {
    private Semaphore lock = new Semaphore(20);
    private LinkedList<MoveType> moves = new LinkedList<MoveType>();

    public KeyboardThread() {
        super("Keyboard");
    }
    
    @Override
    public void run() {
        this.lock.acquireUninterruptibly(this.lock.availablePermits());
        while (true) {
            this.lock.acquireUninterruptibly();
            synchronized (this.moves) {
                if (!this.moves.isEmpty()) {
                    try {
                        QQRobot.press(this.moves.removeFirst());
                    } catch (InterruptedException e) {
                        // errr
                    }
                }
            }
        }
    }

    public final void putMoves(final MoveType[] moves) {
        synchronized (this.moves) {
            for (MoveType m : moves) {
                putMove(m);
            }
        }
    }

    private final void putMove(final MoveType move) {
        this.moves.addLast(move);
        this.lock.release();
    }
}

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

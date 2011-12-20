package cn.keke.qqtetris;

import java.util.concurrent.LinkedBlockingQueue;

public class KeyboardThread extends Thread {
    LinkedBlockingQueue<MoveType> moves = new LinkedBlockingQueue<MoveType>();

    public void run() {
        while (true) {
            try {
                if (this.moves.isEmpty()) {
                    Thread.sleep(50);
                } else {
                    QQRobot.press(this.moves.take());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void putMoves(MoveType[] move) throws InterruptedException {
        for (MoveType m : move) {
            this.moves.put(m);
        }
    }
}

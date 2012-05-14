package cn.keke.qqtetris;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class KeyboardThread extends Thread {
    private LinkedList<MoveType> moves = new LinkedList<MoveType>();

    public void run() {
        while (true) {
            try {
                if (this.moves.isEmpty()) {
                    Thread.sleep(50);
                } else {
                    QQRobot.press(this.moves.removeFirst());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void putMoves(MoveType[] moves) {
        for (MoveType m : moves) {
            this.moves.addLast(m);
        }

    }

    public void putMove(MoveType move) {
        this.moves.addLast(move);
    }
}

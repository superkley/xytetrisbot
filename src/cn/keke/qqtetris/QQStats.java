package cn.keke.qqtetris;

import java.util.Arrays;

public class QQStats {
    private final static StopWatch STOPPER = new StopWatch("stats");
    private CurrentData data;
    public int hash;
    public int occupied;
    public int highest;
    public int lowest;
    public int holes;
    public int[] heights;
    public boolean dead;
    private boolean calculated;

    private boolean calculate() {
        if (!calculated) {
            if (QQTetris.ANALYZE) {
                STOPPER.start();
            }
            if (data.tetromino.isValid()) {
                this.heights = new int[QQTetris.PiecesWidth];
                final int[] stats = BoardUtils.calcDetailedBoardStats(data.board, this.heights);
                this.hash = stats[0];
                this.occupied = stats[1];
                this.highest = stats[2];
                this.lowest = stats[3];
                this.holes = stats[4];
                if (this.highest >= QQTetris.PiecesHeight - 1) {
                    this.dead = true;
                } else {
                    this.dead = false;
                }
                calculated = true;
                return true;
            }
            if (QQTetris.ANALYZE) {
                STOPPER.printTime("construct");
            }
        }
        return false;
    }

    public QQStats(CurrentData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QQStats [hash=" + this.hash + ", occupied=" + this.occupied + ", highest=" + this.highest + ", lowest="
                + this.lowest + ", holes=" + this.holes + "]";
    }

    public boolean isInDanger() {
        if (calculate()) {
            return this.lowest > 3 || highest > QQTetris.PiecesHeight - 8;
        }
        return false;
    }

    public final boolean isValid() {
        return hash == Integer.MIN_VALUE;
    }

    public void reset() {
        hash = Integer.MIN_VALUE;
    }

}

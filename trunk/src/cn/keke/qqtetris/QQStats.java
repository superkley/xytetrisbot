package cn.keke.qqtetris;

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

    public boolean calculate() {
        if (!calculated) {
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
        calculate();
        // System.out.println("lowest: " + this.lowest + ", height: " + highest);
        return this.lowest > 3 || highest > 8;
    }

    public final boolean isValid() {
        return hash == Integer.MIN_VALUE;
    }

    public void reset() {
        hash = Integer.MIN_VALUE;
        this.calculated = false;
    }

    public void setCalculate(boolean b) {
        this.calculated = false;
    }

}

package cn.keke.qqtetris;

import java.util.Arrays;

public class QQStats {
    private final static StopWatch STOPPER  = new StopWatch("stats");
    public final boolean[]         boardData;
    public final Tetromino         tetromino;
    public final BlockType[]       nextBlocks;
    public final int               hash;
    public final int               occupied;
    public final int               highest;
    public final int               lowest;
    public final int               holes;
    public final int               clears;
    public final int[]             heights;
    private boolean                inDanger = false;
    public final boolean           dead;

    public QQStats(boolean[] boardData, Tetromino currentBlock, BlockType[] nextBlocks) {
        super();
        if (QQTetris.ANALYZE) {
            STOPPER.start();
        }
        this.boardData = boardData;
        this.tetromino = currentBlock;
        this.nextBlocks = nextBlocks;
        if (currentBlock != null) {
            this.heights = new int[QQTetris.PiecesWidth];
            int[] stats = BoardUtils.calcDetailedBoardStats(boardData, this.heights);
            this.hash = stats[0];
            this.occupied = stats[1];
            this.highest = stats[2];
            this.lowest = stats[3];
            this.holes = stats[4];
            this.clears = stats[5];
            if (this.highest >= QQTetris.PiecesHeight - 1) {
                this.dead = true;
            } else {
                this.dead = false;
            }
            cleanup();
        } else {
            this.hash = -1;
            this.occupied = -1;
            this.highest = -1;
            this.lowest = -1;
            this.holes = -1;
            this.clears = -1;
            this.heights = null;
            this.dead = false;
        }
        if (QQTetris.ANALYZE) {
            STOPPER.printTime("construct");
        }
    }

    private void cleanup() {
        if (this.clears > 0) {
            BoardUtils.clearFullLines(this.boardData);
        }
    }

    @Override
    public String toString() {
        String type = null;
        if (this.tetromino != null) {
            type = this.tetromino.block.name();
        }
        return "QQStats [tetromino=" + type + ", nextBlocks=" + Arrays.toString(this.nextBlocks) + ", hash=" + this.hash + ", occupied=" + this.occupied
               + ", highest=" + this.highest + ", lowest=" + this.lowest + ", holes=" + this.holes + ", clears=" + this.clears + "]";
    }

    public void setInDanger(boolean inDanger) {
        this.inDanger = inDanger;
    }

    public boolean isInDanger() {
        return inDanger;
    }

    public boolean isValid() {
        if (this.boardData != null && this.tetromino != null && this.nextBlocks != null) {
            return true;
        }
        return false;
    }

}

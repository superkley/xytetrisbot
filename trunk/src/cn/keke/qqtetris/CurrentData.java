package cn.keke.qqtetris;

public enum CurrentData {
    REAL,
    CALCULATED;
    public final boolean[] board = new boolean[QQTetris.PiecesHeight * QQTetris.PiecesWidth];
    public final Tetromino tetromino = new Tetromino();
    public final BlockType[] nextBlocks = new BlockType[3];
    public final QQStats stats = new QQStats(this);

    public final void reset() {
        tetromino.reset();
        nextBlocks[0] = null;
        nextBlocks[1] = null;
        nextBlocks[2] = null;
        stats.reset();
    }

    public final CurrentData set(final boolean[] b, final Tetromino t, final BlockType[] n) {
        reset();
        System.arraycopy(b, 0, board, 0, b.length);
        tetromino.from(t);
        for (int i = 0; i<n.length; i++) {
            nextBlocks[i] = n[i];
        }
        return this;
    }

}

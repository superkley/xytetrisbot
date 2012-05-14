package cn.keke.qqtetris;

public enum CurrentData {
    REAL,
    CALCULATED;
    public final boolean[] board = new boolean[QQTetris.PiecesHeight * QQTetris.PiecesWidth];
    public final Tetromino tetromino = new Tetromino();
    public final BlockType[] futures = new BlockType[2];
    public final QQStats stats = new QQStats(this);

    public final void reset() {
        tetromino.reset();
        futures[0] = null;
        futures[1] = null;
        stats.reset();
    }

}

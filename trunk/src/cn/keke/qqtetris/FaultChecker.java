package cn.keke.qqtetris;

public class FaultChecker {
    public FaultChecker(BlockRotation r, int faultMin, int[] coordinates) {
        super();
        this.rotation = r;
        this.faultMin = faultMin;
        this.coordinates = coordinates;
    }

    public final BlockRotation rotation;
    public final int   faultMin;
    public final int[] coordinates;

    public final boolean check(final boolean[] board, final int x, final int y) {
        if (this.faultMin == 0) {
            return true;
        } else {
            int counter = 0;
            int mx, my;

            for (int i = 0; i < this.coordinates.length;) {
                mx = x + this.coordinates[i++];
                my = y + this.coordinates[i++];
                if (mx >= 0 && mx < QQTetris.PiecesWidth &&
                        my >= 0 && my < QQTetris.PiecesHeight &&
                        !board[mx + my * QQTetris.PiecesWidth]) {
                    if (++counter >= this.faultMin) {
                        // System.out.println("x=" + x + ", y=" + y);
                        return false;
                    }
                }
            }
            return true;
        }
    }
}

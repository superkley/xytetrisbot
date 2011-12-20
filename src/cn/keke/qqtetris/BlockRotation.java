package cn.keke.qqtetris;

import java.util.Arrays;

public class BlockRotation {
    public final boolean[] form;
    public final int       hash;
    public final int       freeLeft;
    public final int       freeRight;
    public final int       freeTop;
    public final int       freeBottom;
    // y-index of most bottom piece
    public final int[]     piecesBottoms;
    public final int[]     piecesTops;
    public final int[]     piecesVerticalSum;
    public final int       width;
    public final int       height;
    public final FaultChecker faultChecker;

    public BlockRotation(boolean[] faults, int faultLimit, boolean... form) {
        assert (form.length == QQTetris.BlockDrawSize * QQTetris.BlockDrawSize);
        this.form = form;
        this.hash = BoardUtils.calcBlockStats(form)[0];
        int[] free = BoardUtils.calcBlockHorizontalFree(form);
        this.freeLeft = free[0];
        this.freeRight = free[1];
        this.width = QQTetris.BlockDrawSize - this.freeLeft - this.freeRight;
        this.piecesBottoms = BoardUtils.calcBlockBottoms(form, this.freeLeft, this.freeRight);
        this.piecesTops = BoardUtils.calcBlockTops(form, this.freeLeft, this.freeRight);
        this.piecesVerticalSum = BoardUtils.calcBlockVerticalsum(form, this.freeLeft, this.freeRight);
        free = BoardUtils.calcVerticalFree(this.piecesTops, this.piecesBottoms);
        this.freeTop = free[0];
        this.freeBottom = free[1];
        this.height = QQTetris.BlockDrawSize - this.freeTop - this.freeBottom;
        this.faultChecker = new FaultChecker(this, faultLimit, toCoordinates(faults));
    }

    private int[] toCoordinates(boolean[] faults) {
        int count = 0;
        for (int i = 0; i < faults.length; i++) {
            if (faults[i]) {
                count++;
            }
        }
        int[] result = new int[count * 2];
        count = 0;
        FINISHED:
        for (int x = 0; x < QQTetris.BlockDrawSize; x++) {
            for (int y = 0; y < QQTetris.BlockDrawSize; y++) {
                if (faults[x + y * QQTetris.BlockDrawSize]) {
                    result[count++] = x;
                    result[count++] = y;
                    if (count >= result.length) {
                        break FINISHED;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "BlockRotation [freeLeft=" + this.freeLeft + ", freeRight=" + this.freeRight + ", freeTop=" + this.freeTop + ", freeBottom=" + this.freeBottom
               + ", piecesBottoms="
               + Arrays.toString(this.piecesBottoms) + ", piecesTops=" + Arrays.toString(this.piecesTops) + ", piecesVerticalSum="
               + Arrays.toString(this.piecesVerticalSum)
               + ", width=" + this.width + ", height=" + this.height + "]";
    }

}

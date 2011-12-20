package cn.keke.qqtetris;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <pre>
 * 1. Optional: check cached moves and return
 * 2. calc list of possible positions of next blocks on current board, also the tricky (clever) ones
 * 3. calc score of each possibility
 * 4. choose position best score
 * 5. create and return move
 * </pre>
 */
public abstract class MoveCalculator {
    public static final MoveResult NO_MOVE         = new MoveResult(null, null, 0, 0);
    public static final double NO_RESULT_SCORE = Double.NEGATIVE_INFINITY;

    public abstract MoveResult findBestMove(QQStats stats, StrategyType strategy, double[] strategyAttrs);

    public static final long SLOW_MOVE_MILLIS = 1000;

    protected boolean slowMoveDetected = false;

    private QQLevel          level            = QQLevel.HARD;

    public final BlockType[] initializeNextBlocks(BlockType[] blocks, StrategyType strategy, QQStats stats) {
        int l = blocks.length;
        if (l <= 1) {
            return blocks;
        } else if (this.getLevel() == QQLevel.EASY) {
            return new BlockType[] { blocks[0] };
        } else if (strategy.fastInDanger && stats.highest > QQTetris.PiecesHeight - QQTetris.BlockDrawSize * 3) {
            stats.setInDanger(true);
            if (this.slowMoveDetected && l > 2) {
                return Arrays.copyOf(blocks, 2);
            } else {
                return blocks;
            }
        } else if (this.getLevel() == QQLevel.MEDIUM && l > 2) {
            return Arrays.copyOf(blocks, 2);
        } else {
            return blocks;
        }
    }

    public void setLevel(QQLevel level) {
        this.level = level;
    }

    public QQLevel getLevel() {
        return this.level;

    }

    public static final MoveResult createMove(TransformationResult bestResult, Tetromino t) {
        MoveResult moveResult;
        int dx = bestResult.getX() - t.x;
        int didx = 0;
        int rDiff = bestResult.getRotationIdx() - t.rotationIdx;
        if (rDiff > 0) {
            didx = rDiff;
        } else if (rDiff < 0) {
            didx = t.block.rotations.length + rDiff;
        }
        moveResult = new MoveResult(bestResult, t, didx, dx);
        return moveResult;
    }

    public static final ArrayList<Point> findCleverMove(boolean[] board, BlockRotation br, int x, int y) {
        int h = br.height;
        ArrayList<Point> list;
        for (int j = QQTetris.PiecesHeight - h - br.freeTop; j > y; j--) {
            if (BoardUtils.fitFormInner(board, br, x, j)) {
                list = new ArrayList<Point>(5);
                list.add(new Point(x, j));
                int q = y - h;
                // find left
                int i = BoardUtils.findLeftFree(board, br, x, j, q);
                if (i == BoardUtils.NOT_FOUND) {
                    // find right
                    i = BoardUtils.findRightFree(board, br, x, j, q);
                }
                if (i != BoardUtils.NOT_FOUND) {
                    list.add(new Point(i, j));
                    if (BoardUtils.isVerticalFree(board, br, i, q, 0)) {
                        return list;
                    } else {
                        int p = BoardUtils.findLeftFree(board, br, i, q, 0);
                        if (p == BoardUtils.NOT_FOUND) {
                            p = BoardUtils.findRightFree(board, br, i, q, 0);
                        }
                        if (p != BoardUtils.NOT_FOUND) {
                            list.add(new Point(p, q));
                        }
                        return list;
                    }
                }
            }
        }
        return null;
    }

    public static final MoveResult createCleverMove(Tetromino t, int rIdx, ArrayList<Point> list, double score) {
        int didx = 0;
        int rDiff = rIdx - t.rotationIdx;
        if (rDiff > 0) {
            didx = rDiff;
        } else if (rDiff < 0) {
            didx = t.block.rotations.length + rDiff;
        }

        return new CleverMoveResult(t, rIdx, didx, list, score);
    }

    public static final int getY(BlockRotation br, int x, int[] heights) {
        int y;
        int maxHeight = -1;
        for (int i = 0; i < br.width; i++) {
            int h = heights[x + i + br.freeLeft] + br.piecesBottoms[i] + 1;
            if (h > maxHeight) {
                maxHeight = h;
            }
        }
        y = QQTetris.PiecesHeight - maxHeight;
        return y;
    }

}

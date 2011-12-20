package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.BlockDrawSize;
import static cn.keke.qqtetris.QQTetris.DEBUG;
import static cn.keke.qqtetris.QQTetris.PiecesHeight;
import static cn.keke.qqtetris.QQTetris.PiecesWidth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BoardUtils {
    private static final int[]                  EMPTY_DATA_STATS          = new int[] { 0, 0 };
    private static final int[]                  EMPTY_DETAILED_DATA_STATS = new int[] { 0, 0, 0,
                                                                          0, 0, 0, 0, 0 };
    private static final int                    HASH_SEED                 = 173;
    private static final int                    HASH_PRIME                = 37;
    private static final ThreadLocal<int[]>     boardStats                = new ThreadLocal<int[]>() {
                                                                              @Override
                                                                              protected int[] initialValue() {
                                                                                  return new int[] { 0, 0 };
                                                                              }
                                                                          };
    private static final ThreadLocal<int[]>     boardDetailedStats        = new ThreadLocal<int[]>() {
                                                                              @Override
                                                                              protected int[] initialValue() {
                                                                                  return new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
                                                                              }
                                                                          };
    private static final ThreadLocal<boolean[]> hashTestBlock             = new ThreadLocal<boolean[]>() {
                                                                              @Override
                                                                              protected boolean[] initialValue() {
                                                                                  return new boolean[QQTetris.BlockDrawSize * QQTetris.BlockDrawSize];
                                                                              }
                                                                          };
    public static final int                     NOT_FOUND                 = Integer.MIN_VALUE;

    /**
     * 
     * @param boardData
     * @return [hash, occupied]
     */
    public static final int[] calcBoardStats(final boolean[] boardData) {
        if (boardData == null) {
            return EMPTY_DATA_STATS;
        }
        int[] stats = boardStats.get();
        int h = HASH_SEED;
        int sum = 0;
        for (boolean b : boardData) {
            h = (HASH_PRIME * h) + (b ? 1231 : 1237);
            if (b) {
                sum++;
            }
        }
        stats[0] = h;
        stats[1] = sum;
        return stats;
    }

    public static final int[] calcBlockStats(final boolean[] blockArray) {
        int hash = 0;
        int sum = 0;
        for (boolean bit : blockArray) {
            hash <<= 1;
            if (bit) {
                hash += 1;
                sum++;
            }
        }
        return new int[] { hash, sum };
    }

    /**
     * 
     * @param boardData
     * @param initial
     * @return [hash, -, highest, lowest, holes, -, -, heights]
     */
    public static final int[] calcDetailedBoardStats(final boolean[] boardData,
            int[] initial) {
        if (boardData == null) {
            return EMPTY_DETAILED_DATA_STATS;
        }
        int[] stats = boardDetailedStats.get();
        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;
        int occupied = 0;
        int holes = 0;
        int hash = HASH_SEED;
        int[] above;
        if (initial == null) {
            above = new int[PiecesWidth];
        } else {
            above = initial;
        }
        int heights = 0;
        int h;
        boolean found;
        boolean b;
        for (int x = 0; x < PiecesWidth; x++) {
            found = false;
            for (int y = 0; y < PiecesHeight; y++) {
                b = getBoardValue(boardData, x, y);
                hash = (HASH_PRIME * hash) + (b ? 1231 : 1237);
                if (b) {
                    occupied++;
                }
                if (b && !found) {
                    found = true;
                    h = PiecesHeight - y;
                    if (h > highest) {
                        highest = h;
                    } else if (h < lowest) {
                        lowest = h;
                    }
                    heights += h;
                    if (above[x] == 0) {
                        above[x] = h;

                    }
                } else if (found) {
                    holes++;
                }
            }
            if (!found) {
                if (highest < 0) {
                    highest = 0;
                }
                lowest = 0;
            }
        }
        stats[0] = hash;
        stats[1] = occupied;
        stats[2] = highest;
        stats[3] = lowest;
        stats[4] = holes;
        stats[5] = 0;
        stats[6] = 0;
        stats[7] = heights;
        return stats;
    }

    public static final int getBoardPos(final int x, final int y) {
        return y * PiecesWidth + x;
    }

    public static final boolean getBoardValue(final boolean[] data,
            final int x, final int y) {
        if (x < 0 || y < 0 || x >= PiecesWidth || y >= PiecesHeight) {
            return false;
        } else {
            return data[getBoardPos(x, y)];
        }
    }

    public static final boolean getBlockValue(final boolean[] blockArray,
            final int x, final int y) {
        if (x < 0 || y < 0 || x >= QQTetris.BlockDrawSize
                || y >= QQTetris.BlockDrawSize) {
            return false;
        } else {
            return blockArray[getBlockPos(x, y)];
        }
    }

    private static final int getBlockPos(final int x, final int y) {
        return y * BlockDrawSize + x;
    }

    public static final Tetromino getAndCleanNextType(final boolean[] data) {
        Tetromino result = null;
        int i, j, hash, occupied;
        boolean valid;
        boolean[] hashTest = hashTestBlock.get();
        FOUND_TETROMINO:
        for (int x = -2, y = -1; y < PiecesHeight
                                     - BlockDrawSize;) {
            valid = true;
            // last line of block is empty
            for (i = 0; i < BlockDrawSize; i++) {
                if (getBoardValue(data, i, y + BlockDrawSize)) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                // line under block is empty (I)
                valid = true;
                for (i = 0; i < BlockDrawSize; i++) {
                    if (getBoardValue(data, i, y + BlockDrawSize - 1)) {
                        valid = false;
                        break;
                    }
                }
            }
            if (valid) {
                i = 0;
                j = 0;
                for (int k = 0; k < hashTest.length; k++) {
                    hashTest[k] = getBoardValue(data, x + i, y + j);
                    if (++i >= BlockDrawSize) {
                        i = 0;
                        j++;
                    }
                }
                int[] stats = calcBlockStats(hashTest);
                hash = stats[0];
                occupied = stats[1];
                if (occupied == 4) {
                    int idx = Arrays.binarySearch(
                            BlockType.BLOCK_ROTATION_HASHCODES, hash);
                    // System.out.println("x: " + x + ", y: " + y + ", h: " +
                    // hash + ", idx: " + idx);
                    // printBoard(hashTest, BlockDrawSize, BlockDrawSize);
                    if (idx >= 0 && (x <= 0 || !getBoardValue(data, x - 1, y)) && (y <= 0 || !getBoardValue(data, x, y-1))) {
                        result = new Tetromino(
                                BlockType.BLOCK_HASHCODE_TYPE_REFS[idx],
                                BlockType.BLOCK_HASHCODE_ROTATION_REFS[idx], x,
                                y);
                        if (DEBUG) {
                            System.out.println("Found: " + result);
                            // printBlock(result.getRotation().form);
                        }
                        break FOUND_TETROMINO;
                    }
                }
            }
            if (x > PiecesWidth - BlockDrawSize + 2) {
                x = -2;
                y++;
            } else {
                x++;
            }
        }
        if (result != null) {
            // erase current block from board
            i = 0;
            j = 0;
            int x = result.x, y = result.y;
            for (int k = 0; k < hashTest.length; k++) {
                if (x + i >= 0 && y + j >= 0 && x + i < PiecesWidth
                        && y + j < PiecesHeight) {
                    data[getBoardPos(x + i, y + j)] = false;
                }
                if (++i >= BlockDrawSize) {
                    i = 0;
                    j++;
                }
            }
        }
        return result;
    }

    public static final int clearFullLines(final boolean[] boardData) {
        int clears = 0;
        int lineOccupied = 0;
        int x = PiecesWidth - 1, y = PiecesHeight - 1;
        while (y >= clears) {
            if (getBoardValue(boardData, x, y)) {
                lineOccupied++;
            }
            if (lineOccupied == PiecesWidth) {
                System.arraycopy(boardData, clears * PiecesWidth, boardData,
                        (clears + 1) * PiecesWidth, getBoardPos(x, y - clears));
                y++;
                clears++;
            }
            if (--x < 0) {
                lineOccupied = 0;
                x = PiecesWidth - 1;
                y--;
            }
        }
        if (clears > 0) {
            Arrays.fill(boardData, 0, clears * PiecesWidth, false);
        }
        return clears;
    }

    public static final int clearFullLines(final int[] boardData) {
        int clears = 0;
        int lineOccupied = 0;
        int x = PiecesWidth - 1, y = PiecesHeight - 1;
        while (y >= clears) {
            if (boardData[getBoardPos(x, y)] > 0) {
                lineOccupied++;
            }
            if (lineOccupied == PiecesWidth) {
                System.arraycopy(boardData, clears * PiecesWidth, boardData,
                        (clears + 1) * PiecesWidth, getBoardPos(x, y - clears));
                y++;
                clears++;
            }
            if (--x < 0) {
                lineOccupied = 0;
                x = PiecesWidth - 1;
                y--;
            }
        }
        if (clears > 0) {
            Arrays.fill(boardData, 0, clears * PiecesWidth, 0);
        }
        return clears;
    }

    public static final int[] calcBoardHeight(final boolean[] boardArray) {
        int[] result = new int[QQTetris.PiecesWidth];
        int y;
        for (int x = 0; x < QQTetris.PiecesWidth; x++) {
            for (y = 0; y < QQTetris.PiecesHeight; y++) {
                if (BoardUtils.getBoardValue(boardArray, x, y)) {
                    result[x] = QQTetris.PiecesHeight - y;
                    break;
                }
            }
        }
        return result;
    }

    public static final int[] calcBlockVerticalsum(final boolean[] blockArray,
            final int left, final int right) {
        int[] result = new int[QQTetris.BlockDrawSize - left - right];
        int y, h;
        for (int x = left; x < QQTetris.BlockDrawSize - right; x++) {
            h = 0;
            for (y = QQTetris.BlockDrawSize - 1; y >= 0; y--) {
                if (BoardUtils.getBlockValue(blockArray, x, y)) {
                    h++;
                }
            }
            result[x - left] = h;
        }
        return result;
    }

    public static final int[] calcBlockTops(final boolean[] blockArray,
            final int left, final int right) {
        int[] result = new int[QQTetris.BlockDrawSize - left - right];
        int y;
        for (int x = left; x < QQTetris.BlockDrawSize - right; x++) {
            for (y = 0; y < QQTetris.BlockDrawSize; y++) {
                if (BoardUtils.getBlockValue(blockArray, x, y)) {
                    result[x - left] = y;
                    break;
                }
            }
        }
        return result;
    }

    public static final int[] calcVerticalFree(final int[] tops,
            final int[] bottoms) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int i : bottoms) {
            if (i > max) {
                max = i;
            }
        }
        for (int i : tops) {
            if (i < min) {
                min = i;
            }
        }
        return new int[] { min, QQTetris.BlockDrawSize - max - 1 };
    }

    public static final int[] calcBlockBottoms(final boolean[] blockArray,
            final int left, final int right) {
        int[] result = new int[QQTetris.BlockDrawSize - left - right];
        int y;
        for (int x = left; x < QQTetris.BlockDrawSize - right; x++) {
            for (y = QQTetris.BlockDrawSize - 1; y >= 0; y--) {
                if (BoardUtils.getBlockValue(blockArray, x, y)) {
                    result[x - left] = y;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param blockArray
     *            blockArray
     * @return [freeLeft, freeRight]
     */
    public static final int[] calcBlockHorizontalFree(final boolean[] blockArray) {
        int y;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int x = 0; x < QQTetris.BlockDrawSize; x++) {
            for (y = 0; y < QQTetris.BlockDrawSize; y++) {
                if (BoardUtils.getBlockValue(blockArray, x, y)) {
                    if (x < min) {
                        min = x;
                    }
                    if (x > max) {
                        max = x;
                    }
                    break;
                }
            }
        }
        return new int[] { min, QQTetris.BlockDrawSize - max - 1 };
    }

    public static final double mergeAndCalcScore(final boolean[] board,
            final TransformationResult[] prevResults,
            final StrategyType strategy, double[] strategyAttrs) {
        // copy board
        int[] boardCopy = booleansToInts(board);

        // merge
        mergeResults(boardCopy, prevResults);

        return strategy.calculateScore(boardCopy, strategyAttrs);
    }

    public static final int[] booleansToInts(final boolean[] board) {
        int[] boardCopy = new int[board.length];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = board[i] ? 1 : 0;
        }
        return boardCopy;
    }

    public static final void mergeMoveResult(final boolean[] board,
            final Tetromino t, final MoveResult move) {
        BlockRotation br = t.block.rotations[t.rotationIdx + move.rotationDelta];
        int x;
        if (move.moveDelta == MoveResult.CLEVER_MOVE) {
            x = ((CleverMoveResult) move).getTargetX();
        } else {
            x = t.x + move.moveDelta;
        }
        int y = move.y;
        for (int j = y + br.freeTop; j < y + br.freeTop + br.height; j++) {
            for (int i = x + br.freeLeft; i < x + br.freeLeft + br.width; i++) {
                boolean blockPiece = getBlockValue(br.form, i - x, j - y);
                if (blockPiece && i >= 0 && j >= 0 && i < PiecesWidth
                        && j < PiecesHeight) {
                    board[getBoardPos(i, j)] = true;
                }
            }
        }
    }

    public final static void mergeResults(final boolean[] boardCopy,
            final TransformationResult[] prevResults) {
        BlockRotation br;
        for (TransformationResult tr : prevResults) {
            int rotationIdx = tr.getRotationIdx();
            if (rotationIdx == -1) {
                break;
            } else {
                br = tr.getBlock().rotations[rotationIdx];
                int x = tr.getX();
                int y = tr.getY();
                for (int j = y + br.freeTop; j < y + br.freeTop + br.height; j++) {
                    for (int i = x + br.freeLeft; i < x + br.freeLeft
                                                      + br.width; i++) {
                        boolean blockPiece = getBlockValue(br.form, i - x, j
                                                                           - y);
                        if (blockPiece && i >= 0 && j >= 0 && i < PiecesWidth
                                && j < PiecesHeight) {
                            boardCopy[getBoardPos(i, j)] = true;
                        }
                    }
                }
            }
        }
    }

    public final static void mergeResults(final int[] boardCopy,
            final TransformationResult[] prevResults) {
        BlockRotation br;
        for (TransformationResult tr : prevResults) {
            br = tr.getBlock().rotations[tr.getRotationIdx()];
            int x = tr.getX();
            int y = tr.getY();
            for (int j = y + br.freeTop; j < y + br.freeTop + br.height; j++) {
                for (int i = x + br.freeLeft; i < x + br.freeLeft + br.width; i++) {
                    boolean blockPiece = getBlockValue(br.form, i - x, j - y);
                    if (blockPiece && i >= 0 && j >= 0 && i < PiecesWidth
                            && j < PiecesHeight) {
                        boardCopy[getBoardPos(i, j)] = 2;
                    }
                }
            }
        }
    }

    public static final boolean[] intsToBooleans(final int... intArray) {
        boolean[] result = new boolean[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            result[i] = intArray[i] > 0;
        }
        return result;
    }

    public final static int[] calcPiecesHeight(final int[] piecesHeights,
            final TransformationResult[] prevResults) {
        int i, h;
        int x, y;
        int l;
        BlockRotation rt;
        int[] testPiecesHeight = Arrays.copyOf(piecesHeights,
                QQTetris.PiecesWidth);
        for (TransformationResult tr : prevResults) {
            if (tr != null) {
                rt = tr.block.rotations[tr.getRotationIdx()];
                x = tr.getX() + rt.freeLeft;
                y = QQTetris.PiecesHeight - tr.getY();
                l = x + rt.width;
                for (i = x; i < l; i++) {
                    h = rt.piecesTops[i - x];
                    testPiecesHeight[i] = y - h;
                }
            }
        }
        return testPiecesHeight;
    }

    public final static int[] calcPiecesHeight(final int[] piecesHeights,
            final BlockRotation rt, final int x, final int y) {
        int i, h;
        int l;
        int[] testPiecesHeight = Arrays.copyOf(piecesHeights,
                QQTetris.PiecesWidth);
        int realX = x + rt.freeLeft;
        int realY = QQTetris.PiecesHeight - y;
        l = realX + rt.width;
        for (i = realX; i < l; i++) {
            h = rt.piecesTops[i - realX];
            testPiecesHeight[i] = Math.max(realY - h, testPiecesHeight[i]);
        }
        return testPiecesHeight;
    }

    public static final int getBoardValue(final int[] data, final int x,
            final int y) {
        if (x < 0 || y < 0 || x >= PiecesWidth || y >= PiecesHeight) {
            return 0;
        } else {
            return data[getBoardPos(x, y)];
        }
    }

    public static boolean fitFormInner(boolean[] board, BlockRotation br,
            int xTouch, int yTouch) {
        // int wl = Math.min(QQTetris.PiecesWidth, x + br.freeLeft + br.width);
        // int hl = Math.min(QQTetris.PiecesHeight, y + br.freeTop + br.height);
        final int wl = xTouch + br.freeLeft + br.width;
        final int hl = yTouch + br.freeTop + br.height;
        for (int i = xTouch + br.freeLeft, x = br.freeLeft; i < wl; i++, x++) {
            for (int j = yTouch + br.freeTop, y = br.freeTop; j < hl; j++, y++) {
                if (BoardUtils.getBlockValue(br.form, x, y)
                        && BoardUtils.getBoardValue(board, i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int findLeftFree(boolean[] board, BlockRotation br, int xTry,
            int yTry, int targetY) {
        // try leftwards than upwards
        final int limit = -br.freeLeft;
        int x = xTry, y = yTry;

        FOUND:
        while (x-- > limit) {
            if (BoardUtils.fitFormInner(board, br, x, y)) {
                // try upwards
                for (y = yTry - 1; y >= targetY; y--) {
                    if (!BoardUtils.fitFormInner(board, br, x, y)) {
                        continue FOUND;
                    }
                }
            } else {
                return NOT_FOUND;
            }
            return x;
        }
        return NOT_FOUND;
    }

    public static int findRightFree(boolean[] board, BlockRotation br,
            int xTry, int yTry, int targetY) {
        // try rightwards than upwards
        final int limit = QQTetris.PiecesWidth - br.freeLeft - br.width;
        int x = xTry, y = yTry;

        FOUND:
        while (x++ < limit) {
            if (BoardUtils.fitFormInner(board, br, x, y)) {
                // try upwards
                for (y = yTry - 1; y >= targetY; y--) {
                    if (!BoardUtils.fitFormInner(board, br, x, y)) {
                        continue FOUND;
                    }
                }
            } else {
                return NOT_FOUND;
            }
            return x;
        }
        return NOT_FOUND;
    }

    public static boolean isVerticalFree(boolean[] board, BlockRotation br,
            int xTry, int yTry, int targetY) {
        final int wl = xTry + br.width + br.freeLeft;
        final int ws = xTry + br.freeLeft;
        for (int y = yTry - 1 + br.freeTop; y >= targetY; y--) {
            for (int x = ws; x < wl; x++) {
                if (BoardUtils.getBoardValue(board, x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void fillRamdomPieces(boolean[] board, int max) {
        Random random = new Random();
        int k = 0;
        for (int j = QQTetris.PiecesHeight - max; j < QQTetris.PiecesHeight; j++) {
            if (k++ < 3) {
                for (int i = 0; i < QQTetris.PiecesWidth; i++) {
                    board[getBoardPos(i, j)] = random.nextBoolean();
                }
            } else {
                for (int i = random.nextInt(2); i < QQTetris.PiecesWidth; i += 2) {
                    board[getBoardPos(i, j)] = true;
                }
            }
        }
    }

}

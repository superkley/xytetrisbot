/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
package cn.keke.qqtetris;

import java.awt.Point;
import java.util.LinkedList;

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
    public static final double NO_RESULT_SCORE = Double.NEGATIVE_INFINITY;

    public abstract void findBestMove(final boolean[] board, final Tetromino tetromino, final BlockType[] futures,
            final QQStats stats, final StrategyType strategy, final double[] strategyAttrs);

    public static final long SLOW_MOVE_MILLIS = 1000;

    protected boolean slowMoveDetected = false;

    private QQLevel level = QQLevel.HARD;

    public final int initializeNextBlocks(final BlockType[] blocks, final StrategyType strategy, final QQStats stats) {
        if (this.getLevel() == QQLevel.EASY) {
            blocks[1] = null;
        } else if (this.getLevel() == QQLevel.MEDIUM) {
            blocks[2] = null;
        } else if (this.slowMoveDetected && strategy.fastInDanger && stats.isInDanger()) {
            blocks[2] = null;
        }
        return findNextBlocksLimit(blocks);
    }

    public static final int findNextBlocksLimit(final BlockType[] blocks) {
    	for (int i = 0; i < blocks.length; i++) {
    		if (blocks[i] == null) {
    			return i;
    		}
    	}
      return blocks.length;
    }

    public void setLevel(QQLevel level) {
        this.level = level;
    }

    public QQLevel getLevel() {
        return this.level;

    }

    public static final MoveResult createMove(final TransformationResult bestResult, final Tetromino t) {
        final int dx = bestResult.getX() - t.x;
        int ridx = 0;
        final int rDiff = bestResult.getRotationIdx() - t.rotationIdx;
        if (rDiff > 0) {
            ridx = rDiff;
        } else if (rDiff < 0) {
            ridx = t.block.rotations.length + rDiff;
        }
        return t.move.set(bestResult, t, ridx, dx);
    }

    public static final LinkedList<Point> findCleverMove(boolean[] board, BlockRotation br, int x, int y) {
        int h = br.height;
        LinkedList<Point> list;
        for (int j = QQTetris.PiecesHeight - h - br.freeTop; j > y; j--) {
            if (BoardUtils.fitFormInner(board, br, x, j)) {
                list = new LinkedList<Point>();
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

    public static final MoveResult createCleverMove(Tetromino t, int rIdx, LinkedList<Point> list, double score) {
        int didx = 0;
        int rDiff = rIdx - t.rotationIdx;
        if (rDiff > 0) {
            didx = rDiff;
        } else if (rDiff < 0) {
            didx = t.block.rotations.length + rDiff;
        }
        return t.move.set(t, rIdx, didx, list, score);
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

    protected boolean cancelled;

    public abstract void cancel();

    public boolean start() {
        this.cancelled = false;
        CurrentData.CALCULATED.tetromino.move.reset();
        final StrategyType strategy = QQTetris.getStrategy();
        findBestMove(CurrentData.CALCULATED.board, CurrentData.CALCULATED.tetromino, CurrentData.CALCULATED.nextBlocks,
                CurrentData.CALCULATED.stats, strategy, strategy.getAttrs(CurrentData.CALCULATED.stats.isInDanger()));
        return CurrentData.CALCULATED.tetromino.move.isValid();
    }

    public final MoveResult findBestMove(final CurrentData data, final StrategyType strategy, final double[] attrs) {
        findBestMove(data.board, data.tetromino, data.nextBlocks, data.stats, strategy, attrs);
        return data.tetromino.move;
    }
}

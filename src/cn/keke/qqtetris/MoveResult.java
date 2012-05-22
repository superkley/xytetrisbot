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

import cn.keke.qqtetris.exceptions.MoveExpiredException;
import cn.keke.qqtetris.exceptions.MoveNotPossibleException;

public final class MoveResult {
    private final LinkedList<Point> cleverPoints;
    private boolean valid;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + moveDelta;
        result = prime * result + rotationDelta;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MoveResult other = (MoveResult) obj;
        if (moveDelta != other.moveDelta)
            return false;
        if (rotationDelta != other.rotationDelta)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    public static final int CLEVER_MOVE = Integer.MIN_VALUE;
    private long ts = -1;

    public MoveResult set(final Tetromino tetromino, final int rIdx, final int rDelta, final LinkedList<Point> points,
            final double score) {
        this.tetromino = tetromino;
        this.rotationDelta = rDelta;
        final Point point = points.getFirst();
        this.rIdx = rIdx;
        this.x = point.x;
        this.y = point.y;
        this.score = score;
        this.moveDelta = CLEVER_MOVE;
        this.cleverPoints.clear();
        this.cleverPoints.addAll(points);
        this.moveFinished = false;
        this.valid = true;
        this.clever = true;
        this.ts = System.currentTimeMillis();
        return this;
    }

    public MoveResult set(final TransformationResult result, final Tetromino tetromino, final int rotationDelta,
            final int moveDelta) {
        if (result == null) {
            this.rIdx = -1;
            this.x = -1;
            this.y = -1;
            this.score = Double.NEGATIVE_INFINITY;
            this.valid = false;
            this.moveFinished = true;
            this.clever = false;
        } else {
            this.rIdx = result.getRotationIdx();
            this.x = result.getX();
            this.y = result.getY();
            this.score = result.getScore();
            this.ts = System.currentTimeMillis();
            this.tetromino.from(tetromino);
            this.moveDelta = moveDelta;
            this.rotationDelta = rotationDelta;
            this.valid = true;
            this.moveFinished = false;
            this.clever = false;
        }
        return this;
    }

    public MoveResult(TransformationResult result, Tetromino tetromino, int rotationDelta, int moveDelta, boolean moved) {
        super();
        if (result == null) {
            this.rIdx = -1;
            this.x = -1;
            this.y = -1;
            this.score = Double.NEGATIVE_INFINITY;
        } else {
            this.rIdx = result.getRotationIdx();
            this.x = result.getX();
            this.y = result.getY();
            this.score = result.getScore();
        }
        if (tetromino != null) {
            this.tetromino.from(tetromino);
        }
        this.moveDelta = moveDelta;
        this.rotationDelta = rotationDelta;
        this.moveFinished = moved;
        this.cleverPoints = new LinkedList<Point>();

    }

    public int rIdx;
    public int x;
    public int y;
    public double score;
    public Tetromino tetromino = new Tetromino(this);
    public boolean moveFinished;
    public boolean clever;

    @Override
    public String toString() {
        return "MoveResult [valid=" + valid + ", b=" + tetromino.block + ", x=" + this.x + ", y=" + this.y + ", r="
                + rIdx + ", score=" + score + ", moveDelta=" + this.moveDelta + ", rotationDelta=" + this.rotationDelta
                + ", clever=" + clever + "]";
    }

    public int moveDelta;
    public int rotationDelta;

    public void doMove() {
        try {
            QQTetris.activate();
            if (System.currentTimeMillis() - ts > 2000) {
                throw new MoveExpiredException("Move expired!");
            }
            if (clever) {
                doCleverMove();
            } else {
                if (this.rotationDelta > 0) {
                    this.tetromino.rotationIdx += this.rotationDelta;
                    if (this.tetromino.rotationIdx >= this.tetromino.block.rotations.length) {
                        this.tetromino.rotationIdx -= this.tetromino.block.rotations.length;
                    }
                    this.tetromino.rotation = this.tetromino.block.rotations[this.tetromino.rotationIdx];

                    for (int i = 0; i < this.rotationDelta; i++) {
                        QQTetris.pressDirect(MoveType.CLOCKWISE);
                    }
                    this.rotationDelta = 0;
                    // return;
                }

                int moves = Math.abs(this.moveDelta);
                if (moves > 0) {
                    this.tetromino.x += moveDelta;
                    for (int i = 0; i < moves; i++) {
                        if (moveDelta > 0) {
                            QQTetris.pressDirect(MoveType.RIGHT);
                        } else {
                            QQTetris.pressDirect(MoveType.LEFT);
                        }
                    }
                    moveDelta = 0;
                    // return;
                }
                QQTetris.pressDirect(MoveType.FALL);
                moveFinished = true;
            }
        } catch (Exception e) {
            throw new MoveNotPossibleException("操作失败！");
        }
    }

    public final boolean hasMove() {
        return !moveFinished;
    }

    public final void doCleverMove() throws InterruptedException {
        if (this.rotationDelta > 0) {
            this.tetromino.rotationIdx += this.rotationDelta;
            if (this.tetromino.rotationIdx >= this.tetromino.block.rotations.length) {
                this.tetromino.rotationIdx -= this.tetromino.block.rotations.length;
            }
            this.tetromino.rotation = this.tetromino.block.rotations[this.tetromino.rotationIdx];

            for (int i = 0; i < this.rotationDelta; i++) {
                QQTetris.pressDirect(MoveType.CLOCKWISE);
            }
            this.rotationDelta = 0;
            // return;
        }

//        if (!this.cleverPoints.isEmpty()) {
//            final Point p = this.cleverPoints.removeLast();
				while (!this.cleverPoints.isEmpty()) {
						final Point p = this.cleverPoints.removeLast();
			
						final int tx = this.tetromino.x;
						final int tY = this.tetromino.y;
						final int dx = p.x - tx;
						final int moves = Math.abs(dx);
						for (int j = 0; j < moves; j++) {
								if (dx > 0) {
										QQTetris.pressDirect(MoveType.RIGHT);
								} else {
										QQTetris.pressDirect(MoveType.LEFT);
								}
						}
						final int dy = p.y - tY;
						for (int j = 0; j < dy; j++) {
						    QQTetris.pressDirect(MoveType.DOWN);
						}
						this.tetromino.x = p.x;
						this.tetromino.y = p.y;
				}    
            moveFinished = true;
//        } else {
//            moveFinished = true;
//        }
        // QQDebug.printBoard(stats.boardData);
        // System.out.println(out.toString());
    }

    public boolean isValid() {
        return valid;
    }

    public void reset() {
        valid = false;
    }
}

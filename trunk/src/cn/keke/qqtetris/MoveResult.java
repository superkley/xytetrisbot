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
        this.fallen = 0;
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
            this.fallen = 0;
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
            this.fallen = 0;
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
    public int fallen;
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
                    return;
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
                    return;
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
            return;
        }

        if (!this.cleverPoints.isEmpty()) {
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
                if (fallen == 0) {
                    QQTetris.pressDirect(MoveType.DOWN);
                } else {
                    fallen--;
                }
            }
            this.tetromino.x = p.x;
            this.tetromino.y = p.y;
        } else {
            moveFinished = true;
        }
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

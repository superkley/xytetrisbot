package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.ANALYZE;

import java.awt.Point;
import java.util.ArrayList;

public class MoveResult {
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

    public MoveResult(Tetromino tetromino, int rIdx, int rDelta, ArrayList<Point> points, double score) {
        this.tetromino = tetromino;
        this.rotationDelta = rDelta;
        Point point = points.get(0);
        this.rIdx = rIdx;
        this.x = point.x;
        this.y = point.y;
        this.score = score;
        this.moveDelta = CLEVER_MOVE;
    }

    public MoveResult(TransformationResult result, Tetromino tetromino, int rotationDelta, int moveDelta) {
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
        this.tetromino = tetromino;
        this.moveDelta = moveDelta;
        this.rotationDelta = rotationDelta;
    }

    public final int       rIdx;
    public final int       x;
    public final int       y;
    public final double    score;
    public final Tetromino tetromino;

    @Override
    public String toString() {
        return "MoveResult [x=" + this.x + ", y=" + this.y + ", moveDelta=" + this.moveDelta + ", rotationDelta=" + this.rotationDelta + "]";
    }

    public final int moveDelta;
    public final int rotationDelta;

    public void doMove(QQStats stats) throws InterruptedException {
        if (ANALYZE) {
            // QQRobot.saveScreen("move_" + cleverPoints.rotationDelta + "_" + cleverPoints.moveDelta + "_" + (System.currentTimeMillis() / 1000));
            System.out.println("MOVE: " + this);
        }
        for (int i = 0; i < this.rotationDelta; i++) {
            QQRobot.press(MoveType.CLOCKWISE);
        }
        int moves = Math.abs(this.moveDelta);
        for (int i = 0; i < moves; i++) {
            if (moveDelta > 0) {
                QQRobot.press(MoveType.RIGHT);
            } else {
                QQRobot.press(MoveType.LEFT);
            }
        }
        QQRobot.press(MoveType.FALL);
    }
}

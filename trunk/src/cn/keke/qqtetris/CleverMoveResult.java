package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.ANALYZE;

import java.awt.Point;
import java.util.ArrayList;

import cn.keke.qqtetris.exceptions.UnexpectedStateException;

public class CleverMoveResult extends MoveResult {
	private static final int CLEVER_MOVE_SLEEP = 10;
	@Override
	public String toString() {
		return "CleverMoveResult [rIdx=" + this.rIdx + ", x=" + this.x + ", y="
				+ this.y + ", drot=" + this.rotationDelta + ", clever="
				+ this.cleverPoints + "]";
	}

	private final ArrayList<Point> cleverPoints;

	public int getTargetX() {
		return this.cleverPoints.get(0).x;
	}

	public int getTargetY() {
		return this.cleverPoints.get(0).y;
	}

	public CleverMoveResult(Tetromino t, int rIdx, int dIdx,
			ArrayList<Point> list, double score) {
		super(t, rIdx, dIdx, list, score);
		this.cleverPoints = list;
	}

	@Override
	public void doMove(QQStats stats) throws InterruptedException {
		if (ANALYZE) {
			// QQRobot.saveScreen("move_" + cleverPoints.rotationDelta + "_" +
			// cleverPoints.moveDelta + "_" + (System.currentTimeMillis() /
			// 1000));
			System.out.println("MOVE: " + this);
		}
		StringBuffer out = new StringBuffer();
		out.append(stats.tetromino.block.name()).append(": ");
		for (int i = 0; i < this.rotationDelta; i++) {
			QQRobot.press(MoveType.CLOCKWISE);
			out.append('C');
		}
		int dx, dy, moves;
		int tx = this.tetromino.x;
		int oldY = this.tetromino.y;
		int ty;
		// TODO wait till one step later
		do {
			ty = QQRobot.findVertical(this.tetromino.block,
					this.tetromino.rotation, this.tetromino.x, oldY);
			if (ty == -1) {
				throw new UnexpectedStateException(
						"Clever move but no tetromino found!");
			}
		} while (ty > oldY);
		for (int i = this.cleverPoints.size() - 1; i >= 0; i--) {
			Point p = this.cleverPoints.get(i);
			dx = p.x - tx;
			moves = Math.abs(dx);
			for (int j = 0; j < moves; j++) {
				if (dx > 0) {
					QQRobot.press(MoveType.RIGHT, CLEVER_MOVE_SLEEP);
					out.append('R');
				} else {
					QQRobot.press(MoveType.LEFT, CLEVER_MOVE_SLEEP);
					out.append('L');
				}
			}
			dy = p.y - ty;
			for (int j = 0; j < dy; j++) {
				QQRobot.press(MoveType.DOWN, CLEVER_MOVE_SLEEP);
				out.append('D');
			}
			tx = p.x;
			ty = p.y;
		}
		// QQDebug.printBoard(stats.boardData);
		// System.out.println(out.toString());
	}
}

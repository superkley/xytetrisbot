package cn.keke.qqtetris;

import java.awt.Point;

public class MoveResultSlow extends MoveResult {

	public MoveResultSlow(TransformationResult result, Tetromino tetromino, int rotationDelta, int moveDelta, boolean moved) {
		super(result, tetromino, rotationDelta, moveDelta, moved);
	}

	@Override
	protected final void doNormalMove() {
			if (this.rotationDelta != 0) {
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
	
			if (this.moveDelta != 0) {
				final int moves = Math.abs(this.moveDelta);
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

	@Override
  protected final void doCleverMove() {
      if (this.rotationDelta != 0) {
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

      while (!this.cleverPoints.isEmpty()) {
          final Point p = this.cleverPoints.getLast();
					final int tx = this.tetromino.x;
					final int tY = this.tetromino.y;
					final int dx = p.x - tx;
					if (dx != 0) {
							final int moves = Math.abs(dx);
							for (int j = 0; j < moves; j++) {
									if (dx > 0) {
											QQTetris.pressDirect(MoveType.RIGHT);
									} else {
											QQTetris.pressDirect(MoveType.LEFT);
									}
							}
							this.tetromino.x = p.x;
							return;
					}
					final int dy = p.y - tY;
					for (int j = 0; j < dy; j++) {
					    QQTetris.pressDirect(MoveType.DOWN);
					}
					this.tetromino.y = p.y;
					this.cleverPoints.removeLast();
      }
      moveFinished = true;
  }

}

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

import cn.keke.qqtetris.exceptions.NoFuturesFoundException;
import cn.keke.qqtetris.exceptions.NoTetrominoFoundException;
import cn.keke.qqtetris.exceptions.UnexpectedBoardChangeException;

public enum WorkflowStep {
    DETECT_WINDOW(2000) {
        @Override
        public boolean detect() {
            // clear window coordinate, detect and check
            // true if window detected
            // false if cannot find window
            QQRobot.findWindowLocation(RGB_SCREEN);
            if (QQTetris.QQCoord.x != -1) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public WorkflowStep fail() {
            // detect again
            return this;
        }

        @Override
        public WorkflowStep next() {
            // starts with board detection
            QQTetris.captureScreenThread.setStarted(true);
            QQTetris.activate();
            return INITIAL_BOARD.execute();
        }

        @Override
        public void capture() {
            // capture full screen
            // update RGB_SCREEN
            captureScreen();
        }
    },
    INITIAL_BOARD(100) {
        @Override
        public boolean detect() {
            // detect board, tetromino and futures, check real tetromino
            // true if board and tetromino is detected (other blocks optional)
            // false if cannot find tetromino
            // error if cannot find board
            QQRobot.checkBoardExists(RGB_MY_SPACE);
            CurrentData.REAL.reset();
            QQRobot.findBoard(RGB_MY_SPACE, CurrentData.REAL.board);
            QQRobot.findAndCleanBoard(CurrentData.REAL.board, CurrentData.REAL.tetromino, CurrentData.REAL.nextBlocks);
            QQRobot.findFutures(RGB_MY_SPACE, CurrentData.REAL.nextBlocks);

            if (CurrentData.REAL.tetromino.isValid()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public WorkflowStep fail() {
            // e.g. board is not there, window moved? hidden?
            return DETECT_WINDOW;
        }

        @Override
        public WorkflowStep next() {
            // send data to calculator and go on
            startCalculator(true);
            return DETECT_ANOMALIES;
        }

        @Override
        public void capture() {
            // capture my place
            // update RGB_MY_PLACE
            captureMySpace();
        }
    },
    DETECT_ANOMALIES(200) {
        private int boardChangeDetected;

        @Override
        public boolean detect() {
            // check anomalies, e.g. board, compare real and calculated
            // true if calculation finished (should not happen, since signal comes from calculator)
            // false if calculator is calculating -> detect while calculating
            // error if cannot find board
            // error if 3 x board hash is not calculated board hash
            QQRobot.checkBoardExists(RGB_MY_SPACE);
            QQRobot.findBoard(RGB_MY_SPACE, CurrentData.REAL.board);
            if (!BoardUtils.isSameBoard(CurrentData.REAL.board, CurrentData.CALCULATED.board)) {
                this.boardChangeDetected++;
                if (this.boardChangeDetected == 3) {
                    throw new UnexpectedBoardChangeException("找到变动！");
                }
            }
            return false;
        }

        @Override
        public WorkflowStep fail() {
            // abort calculator
            // no extra screen capture needed
            this.boardChangeDetected = 0;
            QQTetris.calculationThread.cancel();
            return INITIAL_BOARD.execute(false);
        }

        @Override
        public WorkflowStep next() {
            this.boardChangeDetected = 0;
            return FOLLOW_MOVE.execute(false);
        }

        @Override
        public void capture() {
            // capture my place
            // update RGB_MY_PLACE
            captureMySpace();
        }
    },
    DETECT_BLOCKS(50) {
        private int boardChangeDetected;
        private int missingTetromino;
        private int missingFutures;

        @Override
        public boolean detect() {
            // detect tetromino and futures and check board and anomalies
            // true if tetromino and blocks are detected
            // true if tetromino detected and 3 x future blocks not found
            // false if tetromino is not there
            // error if cannot find board
            // error if 3 x board has been changed
            QQRobot.checkBoardExists(RGB_MY_SPACE);
            if (!BoardUtils.isSameBoard(CurrentData.REAL.board, CurrentData.CALCULATED.board)) {
                this.boardChangeDetected++;
                if (this.boardChangeDetected == 3) {
                    throw new UnexpectedBoardChangeException("找到变动！");
                }
            }
            CurrentData.REAL.reset();
            QQRobot.findTetromino(RGB_MY_SPACE, CurrentData.REAL.tetromino, CurrentData.REAL.nextBlocks, 5);
            QQRobot.findFutures(RGB_MY_SPACE, CurrentData.REAL.nextBlocks);
            boolean valid = false;
            if (CurrentData.REAL.tetromino.isValid()) {
                if (CurrentData.REAL.nextBlocks[1] == null) {
                    this.missingFutures++;
                    if (this.missingFutures == 3) {
                        throw new NoFuturesFoundException("没找到预知块！");
                    }
                } else {
                    valid = true;
                }
            } else {
                this.missingTetromino++;
                if (this.missingTetromino == 2000 / this.delayMillis) {
                    throw new NoTetrominoFoundException("没找到游戏块！");
                }
            }
            if (valid) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public WorkflowStep fail() {
            // no extra screen capture needed
            this.boardChangeDetected = 0;
            this.missingFutures = 0;
            this.missingTetromino = 0;
            return INITIAL_BOARD.execute(false);
        }

        @Override
        public WorkflowStep next() {
            // send data (only blocks data) to calculator
            this.boardChangeDetected = 0;
            this.missingFutures = 0;
            this.missingTetromino = 0;
            startCalculator(false);
            return DETECT_ANOMALIES;
        }

        @Override
        public void capture() {
            // capture my place
            // update RGB_MY_PLACE
            captureMySpace();
        }
    },
    FOLLOW_MOVE(30) {
        private int missingTetromino;
        private boolean firstScan = true;

        @Override
        public boolean detect() {
            // check possible piece positions (current and 2 x below)
            // skip down moves if piece has been continued
            // update real position
            // true if no more moves
            // false if there are still moves to do
            // error if cannot find piece or piece sticked
        	  slowDown();
            final MoveResult move = CurrentData.CALCULATED.tetromino.move;
						if (move.hasMove()) {
                final Tetromino moveTetromino = move.tetromino;
                final int y = QQRobot.findTetromino(moveTetromino, 3 + missingTetromino * 2);
                if (y == -1) {
                    missingTetromino++;
                    if (missingTetromino == 3) {
                        // System.out.println("没找到块" + nr + "！" + CurrentData.CALCULATED.tetromino.move + ", "
                        // + CurrentData.CALCULATED.tetromino + ", "
                        // + CurrentData.CALCULATED.tetromino.move.tetromino);
                        // QQDebug.save(QQRobot.getScreen(), "qqtetris_" + nr);
                        // nr++;
                        throw new NoTetrominoFoundException("没找到块！");
                        // CurrentData.CALCULATED.tetromino.move.doMove();
                    }
                } else {
                	  final int fallen = y - moveTetromino.y;
                    if (fallen > 0) {
                        // System.out.println("掉落：" + fallen);
                        moveTetromino.y = y;
                    }                	  
                	  if (move.clever) {
                	  	  if (firstScan) {
                	  	      firstScan = false;
                	  	  } else if (fallen > 0) {
			                      move.doMove();                	  	  	  
                	  	  }
                	  } else {
		                    move.doMove();
                	  }
                }
            }
            if (move.hasMove()) {
                return false;
            } else {
                // QQDebug.printBoard(CurrentData.CALCULATED.board);
                return true;
            }
        }

				private final void slowDown() {
						if (timeCalculationStarted > 0) {
								long sleepTime = (timeCalculationStarted + QQTetris.getSleep()) - System.currentTimeMillis();
								CurrentData.CALCULATED.stats.calculate();
								final int h = CurrentData.CALCULATED.stats.highest;
								if (h > QQTetris.PiecesHeight - 6) {
									sleepTime *= 0.5;
								} else if (h > QQTetris.PiecesHeight - 10) {
									sleepTime *= 0.6; 
								} else if (h > QQTetris.PiecesHeight - 13) {
									sleepTime *= 0.7; 
								} else if (h > QQTetris.PiecesHeight - 15) {
									sleepTime *= 0.8; 
								} else if (h > QQTetris.PiecesHeight - 17) {
									sleepTime *= 0.9; 
								}
								if (sleepTime > 0) {
									  try {
											Thread.sleep(sleepTime);
											timeCalculationStarted = 0;
										} catch (InterruptedException e) {
											// ignore
										}
								}
						}
				}

        @Override
        public WorkflowStep fail() {
            // err? game finished?
            this.missingTetromino = 0;
            timeCalculationStarted = 0;
            firstScan = true;
            return INITIAL_BOARD.execute(false);
        }

        @Override
        public WorkflowStep next() {
            // send move finished to calculator
            // merge calculated board with finished move
            // calculator.mergeBoard(move);
            this.missingTetromino = 0;
            timeCalculationStarted = 0;
            firstScan = true;
            if (checkAutoBlue()) {
                nosync = 0;
                try {
                    Thread.sleep(MIN_BLUE_TIME);
                } catch (InterruptedException e) {
                    // wait
                }
                return INITIAL_BOARD;
            } else {
	            	if (QQTetris.getSleep() < 300) {
		                if (++nosync >= MAX_NOSYNC) {
		                    nosync = 0;
		                    return INITIAL_BOARD;
		                } else {
		                    QQTetris.calculationThread.mergeMove();
		                    return DETECT_BLOCKS;
		                }
	            	} else {
	            			return INITIAL_BOARD;
	            	}
            }
        }

        @Override
        public void capture() {
            // no capture needed, uses getRGBPixel(x, y)
        }

    };
    private static final int[] RGB_SCREEN = new int[QQRobot.RECT_SCREEN.width * QQRobot.RECT_SCREEN.height];
    private static final int[] RGB_MY_SPACE = new int[QQRobot.RECT_MY.width * QQRobot.RECT_MY.height];
    private static final int MAX_NOSYNC = 5;
    private static final int MIN_BLUE_TIME = 500;
    protected final int delayMillis;
    private int maxDuration = 150;
    private int maxDurationNext = 150;
    private int maxDurationError = 200;
    private static int nosync;
    private static long timeCalculationStarted;

    WorkflowStep(final int delay) {
        this.delayMillis = delay;
    }

    static void captureScreen() {
        // RGB_SCREEN
        QQRobot.captureScreen(QQRobot.RECT_SCREEN, RGB_SCREEN);
    }

    static void captureMySpace() {
        // RGB_MY_PLACE
        QQRobot.captureScreen(QQRobot.RECT_MY, RGB_MY_SPACE);
    }

    private static long lastAutoBlue;
    private static final long MIN_AUTO_BLUE_PAUSE = 3000;

    static boolean checkAutoBlue() {
        if (QQTetris.isAutoBlue()) {
            final long now = System.currentTimeMillis();
            if (now > lastAutoBlue + MIN_AUTO_BLUE_PAUSE && CurrentData.CALCULATED.stats.isInDanger()) {
                // System.out.println("calc autoblue");
                lastAutoBlue = now;
                return QQRobot.doAutoBlue(RGB_MY_SPACE);
            }
        }
        return false;
    }

    private final void checkDuration(final int duration) {
        if (duration > this.maxDuration) {
            System.out.println(name() + " (max): " + duration);
            this.maxDuration = duration;
        }
    }

    private final void checkDurationNext(final int duration) {
        if (duration > this.maxDurationNext) {
            System.out.println(name() + " (max-next): " + duration);
            this.maxDurationNext = duration;
        }
    }

    private final void checkDurationError(final int duration) {
        if (duration > this.maxDurationError) {
            System.out.println(name() + " (max-error): " + duration);
            this.maxDurationError = duration;
        }
    }

    private static final void waitMillis(final WorkflowStep step, final int duration) {
        try {
            Thread.sleep(Math.max(1, step.delayMillis - duration));
        } catch (InterruptedException e) {
            // silent
        }
    }

    public WorkflowStep execute() {
        return execute(true);
    }

    WorkflowStep execute(final boolean captureScreen) {
        final long start = System.currentTimeMillis();
        try {
            if (captureScreen) {
                capture();
            }
            if (detect()) {
                final int duration = (int) (System.currentTimeMillis() - start);
                checkDurationNext(duration);
                return doNext();
            } else {
                // System.out.print(".");
                return doAgain(this, start);
            }
        } catch (Throwable t) {
            final int duration = (int) (System.currentTimeMillis() - start);
            checkDurationError(duration);
            return doError(t);
        }
    }

    private final WorkflowStep doError(final Throwable t) {
        // System.err.println("骤：" + t.toString());
        final WorkflowStep nextStep = fail();
        waitMillis(nextStep, 0);
        return nextStep;
    }

    private final WorkflowStep doAgain(final WorkflowStep nextStep, final long start) {
        final int duration = (int) (System.currentTimeMillis() - start);
        checkDuration(duration);
        waitMillis(nextStep, duration);
        return nextStep;
    }

    private final WorkflowStep doNext() {
        final WorkflowStep nextStep = next();
        // System.out.print("\n" + nextStep.name() + "：");
        waitMillis(nextStep, 0);
        return nextStep;
    }

    private static void startCalculator(final boolean copyBoard) {
    	  timeCalculationStarted = System.currentTimeMillis();
        if (copyBoard) {
            System.arraycopy(CurrentData.REAL.board, 0, CurrentData.CALCULATED.board, 0,
                    CurrentData.CALCULATED.board.length);
        }
        CurrentData.CALCULATED.reset();
        CurrentData.CALCULATED.nextBlocks[0] = CurrentData.REAL.nextBlocks[0];
        CurrentData.CALCULATED.nextBlocks[1] = CurrentData.REAL.nextBlocks[1];
        CurrentData.CALCULATED.nextBlocks[2] = CurrentData.REAL.nextBlocks[2];
        CurrentData.CALCULATED.tetromino.from(CurrentData.REAL.tetromino);
        // send wakeup to calculator
        // calculator will stopped after calculating and change step to follow move
        QQTetris.calculationThread.startCalculation();
    }

    public abstract void capture();

    public abstract boolean detect();

    public abstract WorkflowStep fail();

    public abstract WorkflowStep next();
}

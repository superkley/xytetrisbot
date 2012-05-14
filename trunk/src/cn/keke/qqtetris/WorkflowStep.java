package cn.keke.qqtetris;

import cn.keke.qqtetris.exceptions.NoTetrominoFoundException;
import cn.keke.qqtetris.exceptions.UnexpectedBoardChangeException;

public enum WorkflowStep {
    DETECT_WINDOW(1000) {
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
        public WorkflowStep onError() {
            // detect again
            return this;
        }

        @Override
        public WorkflowStep doNext() {
            // starts with board detection
            QQTetris.captureScreenThread.setStarted(true);
            QQTetris.activate();
            return DETECT_MY_SPACE.execute();
        }

        @Override
        public void capture() {
            // capture full screen
            // update RGB_SCREEN
            captureScreen();
        }
    },
    DETECT_MY_SPACE(100) {
        @Override
        public boolean detect() {
            // detect board, tetromino and futures, check real tetromino
            // true if board and tetromino is detected (other blocks optional)
            // false if cannot find tetromino
            // error if cannot find board
            QQRobot.checkBoardExists(RGB_MY_SPACE);
            CurrentData.REAL.reset();
            QQRobot.findBoard(RGB_MY_SPACE, CurrentData.REAL.board);
            QQRobot.findAndCleanBoard(CurrentData.REAL.board, CurrentData.REAL.tetromino);
            QQRobot.findFutures(RGB_MY_SPACE, CurrentData.REAL.futures);

            if (QQTetris.DEBUG) {
                QQDebug.debugScreen(CurrentData.REAL);
            }

            if (CurrentData.REAL.tetromino.isValid()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public WorkflowStep onError() {
            // e.g. board is not there, window moved? hidden?
            return DETECT_WINDOW;
        }

        @Override
        public WorkflowStep doNext() {
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
    DETECT_ANOMALIES(100) {
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
        public WorkflowStep onError() {
            // abort calculator
            // no extra screen capture needed
            this.boardChangeDetected = 0;
            abortCalculation();
            return DETECT_MY_SPACE.execute(false);
        }

        @Override
        public WorkflowStep doNext() {
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
    DETECT_BLOCKS(100) {
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
            QQRobot.findTetromino(RGB_MY_SPACE, CurrentData.REAL.tetromino);
            QQRobot.findFutures(RGB_MY_SPACE, CurrentData.REAL.futures);
            boolean valid = false;
            if (CurrentData.REAL.tetromino.isValid()) {
                if (CurrentData.REAL.futures[0] == null) {
                    this.missingFutures++;
                    if (this.missingFutures == 3) {
                        valid = true;
                    }
                } else {
                    valid = true;
                }
            } else {
                this.missingTetromino++;
                if (this.missingTetromino == 2000 / this.delayMillis) {
                    throw new NoTetrominoFoundException("没找到块！");
                }
            }
            if (valid) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public WorkflowStep onError() {
            // no extra screen capture needed
            this.boardChangeDetected = 0;
            this.missingFutures = 0;
            this.missingTetromino = 0;
            return DETECT_MY_SPACE.execute(false);
        }

        @Override
        public WorkflowStep doNext() {
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

        @Override
        public boolean detect() {
            // check possible piece positions (current and 2 x below)
            // skip down moves if piece has been continued
            // update real position
            // true if no more moves
            // false if there are still moves to do
            // error if cannot find piece or piece stucked
            if (CurrentData.CALCULATED.tetromino.move.hasMove()) {
                final int ty = QQRobot.findTetromino(CurrentData.CALCULATED.tetromino);
                final int fallen = ty - CurrentData.CALCULATED.tetromino.y;
                if (fallen > 0) {
                    CurrentData.CALCULATED.tetromino.move.fallen = fallen;
                    CurrentData.CALCULATED.tetromino.y = ty;
                } else if (fallen == -1) {
                    missingTetromino++;
                    if (missingTetromino == 5) {
                        throw new NoTetrominoFoundException("没找到块！");
                    }
                }
                if (CurrentData.CALCULATED.tetromino.move.hasMove()) {
                    CurrentData.CALCULATED.tetromino.move.doMove();
                }
            }
            if (CurrentData.CALCULATED.tetromino.move.hasMove()) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public WorkflowStep onError() {
            // err? game finished?
            this.missingTetromino = 0;
            return DETECT_MY_SPACE.execute(false);
        }

        @Override
        public WorkflowStep doNext() {
            // send move finished to calculator
            afterMoveFinished();
            checkAutoBlue();
            this.missingTetromino = 0;
            return DETECT_BLOCKS;
        }

        @Override
        public void capture() {
            // no capture needed, uses getRGBPixel(x, y)
        }

    };
    private static final int[] RGB_SCREEN = new int[QQRobot.RECT_SCREEN.width * QQRobot.RECT_SCREEN.height];
    private static final int[] RGB_MY_SPACE = new int[QQRobot.RECT_MY.width * QQRobot.RECT_MY.height];

    protected final int delayMillis;
    private int maxDuration;

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
    private static final long MIN_AUTO_BLUE_PAUSE = 5000;

    static void checkAutoBlue() {
        if (QQTetris.isAutoBlue()) {
            final long now = System.currentTimeMillis();
            if (now > lastAutoBlue + MIN_AUTO_BLUE_PAUSE && CurrentData.CALCULATED.stats.isInDanger()) {
                QQRobot.doAutoBlue();
                lastAutoBlue = now;
            }
        }
    }

    private final void checkDuration(final int duration) {
        if (duration > this.maxDuration) {
            System.out.println(name() + " (max): " + duration);
            this.maxDuration = duration;
        }
    }

    private static void waitMillis(final WorkflowStep step, final int duration) {
        try {
            Thread.currentThread().wait(Math.max(1, step.delayMillis - duration));
        } catch (InterruptedException e) {
            // silent
        }
    }

    public WorkflowStep execute() {
        return execute(true);
    }

    WorkflowStep execute(final boolean captureScreen) {
        WorkflowStep nextStep = this;
        final long start = System.currentTimeMillis();
        try {
            if (captureScreen) {
                capture();
            }
            if (detect()) {
                nextStep = doNext();
                waitMillis(nextStep, 0);
            } else {
                final int duration = (int) (System.currentTimeMillis() - start);
                checkDuration(duration);
                waitMillis(nextStep, duration);
            }
        } catch (Throwable t) {
            nextStep = onError();
            waitMillis(nextStep, 0);
        }
        return nextStep;
    }

    private static void startCalculator(final boolean copyBoard) {
        if (copyBoard) {
            System.arraycopy(CurrentData.REAL.board, 0, CurrentData.CALCULATED.board, 0,
                    CurrentData.CALCULATED.board.length);
        }
        CurrentData.CALCULATED.reset();
        CurrentData.CALCULATED.futures[0] = CurrentData.REAL.futures[0];
        CurrentData.CALCULATED.futures[1] = CurrentData.REAL.futures[1];
        CurrentData.CALCULATED.tetromino.from(CurrentData.REAL.tetromino);
        // send wakeup to calculator
        // calculator will stopped after calculating and change step to follow move
        QQTetris.calculationThread.startCalculation();
    }

    private static void abortCalculation() {
        // send abort to calculator
        QQTetris.calculationThread.cancel();
    }

    private static void afterMoveFinished() {
        // merge calculated board with finished move
        // calculator.mergeBoard(move);
        QQTetris.calculationThread.mergeMove();
    }

    public abstract void capture();

    public abstract boolean detect();

    public abstract WorkflowStep onError();

    public abstract WorkflowStep doNext();
}

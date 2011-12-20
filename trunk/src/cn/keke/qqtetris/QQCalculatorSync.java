package cn.keke.qqtetris;

import java.awt.Point;
import java.util.ArrayList;

public class QQCalculatorSync extends MoveCalculator {
    private final StopWatch       STOPPER       = new StopWatch("calcSync");
    private TransformationResult[]       cachedResults = new TransformationResult[QQTetris.MAX_BLOCKS - 1];
    private boolean                      cached;

    public QQCalculatorSync() {
        // nothing to do
    }

    public static class State {
        public int              r;
        public int              x;
        public ArrayList<Point> cleverPoints;

        public State(int r, int x) {
            super();
            this.r = r;
            this.x = x;
        }
    }

    private static final boolean incrementState(final State[] states, final int stage, final int width, final BlockType b) {
        State s = states[stage];
        if (s == null) {
            s = new State(0, 0);
            states[stage] = s;
        } else if (++s.x > QQTetris.PiecesWidth - b.rotations[s.r].width) {
            if (++s.r == b.rotations.length) {
                s.x = -1;
                s.r = 0;
                return false;
            } else {
                s.x = 0;
            }
        }
        return true;
    }

    @Override
    public MoveResult findBestMove(QQStats stats, StrategyType strategy, double[] strategyAttrs) {
        TransformationResult bestResult = null;

        Tetromino t = stats.tetromino;
        BlockType[] nextBlocks = initializeNextBlocks(stats.nextBlocks, strategy, stats);

        if (strategy.fastInDanger && stats.isInDanger() && this.cached && this.cachedResults[0].block == t.block) {
            bestResult = this.cachedResults[0];

            if (this.cachedResults[1] != null) {
                this.cachedResults[0] = this.cachedResults[1];
                this.cachedResults[1] = null;
            } else {
                this.cached = false;
                this.cachedResults[0] = null;
            }
        } else {
            boolean[] board = stats.boardData;
            boolean[] boardCopy = new boolean[board.length];
            System.arraycopy(board, 0, boardCopy, 0, board.length);
            STOPPER.start();
            int width = QQTetris.PiecesWidth;
            int l = nextBlocks.length;
            State[] states = new State[l];

            int stage = 0;
            State s;
            BlockType bt;
            BlockRotation br;
            ArrayList<Point> cleverPoints;
            int x, y;

            final int[][] piecesHeights = new int[l][];
            piecesHeights[0] = stats.heights;
            final TransformationResult[] results = new TransformationResult[l];
            for (int i = 0; i < l; i++) {
                results[i] = new TransformationResult(nextBlocks[i]);
            }
            int[] heights;

            double bestScore = NO_RESULT_SCORE;

            // STOPPER.start();
            while (true) {
                if (stage == -1) {
                    break;
                } else {
                    bt = nextBlocks[stage];
                    if (incrementState(states, stage, width, bt)) {
                        s = states[stage];
                        s.cleverPoints = null;
                        br = bt.rotations[s.r];
                        heights = piecesHeights[stage];
                        x = s.x - br.freeLeft;
                        y = getY(br, x, heights);
                        // deeper search for useful holes, find clever move if y is greater than 4
                        // !stats.isInDanger() &&
                        if (y >= QQTetris.BlockDrawSize) {
                            if (stage > 0) {
                                BoardUtils.mergeResults(boardCopy, results);
                            }
                            cleverPoints = findCleverMove(boardCopy, br, x, y);
                            if (cleverPoints != null) {
                                s.cleverPoints = cleverPoints;
                                y = cleverPoints.get(0).y;
                            }
                            if (stage > 0) {
                                System.arraycopy(board, 0, boardCopy, 0, board.length);
                            }
                        }
                        boolean correctPlacement = stage > 0 || s.cleverPoints != null || br.faultChecker.check(board, x, y);
                        if (y >= 0 && correctPlacement) {
                            results[stage].update(s.r, x, y, -1, s.cleverPoints);
                            if (stage == l - 1) {
                                // calculate stats for each combination
                                double score = BoardUtils.mergeAndCalcScore(board, results, strategy, strategyAttrs);
                                if (QQTetris.DEBUG) {
                                    System.out.println("score: " + (int) score + ", stage: " + stage + ", r: " + s.r + ", x: " + s.x + ", y: " + y
                                                       + ", clever: "
                                                       + s.cleverPoints);
                                }
                                if (score > bestScore) {
                                    bestScore = score;
                                    bestResult = new TransformationResult(results[0], score);
                                    if (l > 2) {
                                        this.cachedResults[0] = new TransformationResult(results[1]);
                                        this.cachedResults[1] = new TransformationResult(results[2]);
                                        this.cached = true;
                                    } else if (l == 2) {
                                        this.cachedResults[0] = new TransformationResult(results[1]);
                                        this.cachedResults[1] = null;
                                        this.cached = true;
                                    } else {
                                        this.cached = false;
                                    }
                                }
                            } else {
                                piecesHeights[stage + 1] = BoardUtils.calcPiecesHeight(heights, br, x, y);
                                stage++;
                            }
                        } else {
                            continue;
                            // if (stage != l - 1) {
                            // stage--;
                            // }
                        }
                    } else {
                        stage--;
                    }
                }
            }
            // STOPPER.printTime("while");
        }

        // calculate MoveResult
        MoveResult moveResult = NO_MOVE;
        if (bestResult != null) {
            if (bestResult.getCleverPoints() == null) {
                moveResult = createMove(bestResult, t);
            } else {
                moveResult = createCleverMove(t, bestResult.getRotationIdx(), bestResult.getCleverPoints(), bestResult.getScore());
            }
        }
        if (QQTetris.ANALYZE) {
            STOPPER.printTime("calcSync");
        }
        if (STOPPER.measure() > SLOW_MOVE_MILLIS) {
            this.slowMoveDetected = true;
        }
        return moveResult;
    }
}

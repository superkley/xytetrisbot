package cn.keke.qqtetris;

import java.awt.Point;
import java.util.LinkedList;

public class QQCalculatorSync extends MoveCalculator {
    private final StopWatch STOPPER = new StopWatch("calcSync");
    private final TransformationResult[] cachedResults = new TransformationResult[QQTetris.MAX_BLOCKS - 1];

    public QQCalculatorSync() {
        // nothing to do
        for (int i = 0; i < cachedResults.length; i++) {
            cachedResults[i] = new TransformationResult();
        }
    }

    public static class State {
        public int r;
        public int x;
        public LinkedList<Point> cleverPoints;

        public State(int r, int x) {
            super();
            this.r = r;
            this.x = x;
        }
    }

    private static final boolean incrementState(final State[] states, final int stage, final int width,
            final BlockType b) {
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

    private final boolean[] BUFFER_BOARD = new boolean[QQTetris.PiecesHeight * QQTetris.PiecesWidth];

    @Override
    public final void findBestMove(final boolean[] board, final Tetromino t, final BlockType[] nextBlocks,
            final QQStats stats, final StrategyType strategy, final double[] strategyAttrs) {
        final TransformationResult bestResult = new TransformationResult();

        final int l = initializeNextBlocks(nextBlocks, strategy, stats);

        final TransformationResult cached = this.cachedResults[0];
        if (strategy.fastInDanger && this.cachedResults[0].isValid() && stats.isInDanger() && cached.block == t.block) {
            bestResult.set(cached);
            if (this.cachedResults[1].isValid()) {
                this.cachedResults[0].set(this.cachedResults[1]);
                this.cachedResults[1].invalidate();
            } else {
                this.cachedResults[0].invalidate();
            }
        } else {
            stats.calculate();
            System.arraycopy(board, 0, BUFFER_BOARD, 0, board.length);
            STOPPER.start();
            final int width = QQTetris.PiecesWidth;
            final State[] states = new State[l];

            int stage = 0;
            State s;
            BlockType bt;
            BlockRotation br;
            LinkedList<Point> cleverPoints;
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
            while (!cancelled) {
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
                                BoardUtils.mergeResults(BUFFER_BOARD, results);
                            }
                            cleverPoints = findCleverMove(BUFFER_BOARD, br, x, y);
                            if (cleverPoints != null) {
                                s.cleverPoints = cleverPoints;
                                y = cleverPoints.get(0).y;
                            }
                            if (stage > 0) {
                                System.arraycopy(board, 0, BUFFER_BOARD, 0, board.length);
                            }
                        }
                        boolean correctPlacement = stage > 0 || s.cleverPoints != null
                                || br.faultChecker.check(board, x, y);
                        if (y >= 0 && correctPlacement) {
                            results[stage].update(s.r, x, y, -1, s.cleverPoints);
                            if (stage == l - 1) {
                                // calculate stats for each combination
                                double score = BoardUtils.mergeAndCalcScore(board, results, strategy, strategyAttrs);
                                if (QQTetris.DEBUG) {
                                    System.out.println("score: " + (int) score + ", stage: " + stage + ", r: " + s.r
                                            + ", x: " + s.x + ", y: " + y + ", clever: " + s.cleverPoints);
                                }
                                if (score > bestScore) {
                                    bestScore = score;
                                    bestResult.set(results[0], score);
                                    if (l > 2) {
                                        this.cachedResults[0].set(results[1]);
                                        this.cachedResults[1].set(results[2]);
                                    } else if (l == 2) {
                                        this.cachedResults[0].set(results[1]);
                                        this.cachedResults[1].invalidate();
                                    } else {
                                        this.cachedResults[0].invalidate();
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
        if (cancelled) {
            this.cachedResults[0].invalidate();
            this.slowMoveDetected = false;
        } else {
            // calculate MoveResult
            if (bestResult != null) {
                if (bestResult.getCleverPoints() == null) {
                    createMove(bestResult, t);
                } else {
                    createCleverMove(t, bestResult.getRotationIdx(), bestResult.getCleverPoints(),
                            bestResult.getScore());
                }
            }
            if (STOPPER.measure() > SLOW_MOVE_MILLIS) {
                this.slowMoveDetected = true;
            }
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}

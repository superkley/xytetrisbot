package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.PiecesHeight;

import cn.keke.qqtetris.simulator.StrategyOptimizer;

public enum StrategyType {
	// Generation 8; Candidate 2: [1.08, 1.93, -1.33, -2.1, -4.97, -1.78, 1.1]
	// Score = 2320388 Lines = 50001
	// Generation 2; Candidate 13: [3.62, 1.0, 0.12, -4.96, -4.9, -3.92, -4.85]
	// Score = 2363946 Lines = 50001
	// Generation 16; Candidate 10: [4.6, 1.35, 1.04, -2.36, -4.97, -1.78, 1.1]
	// Score = 2365230 Lines = 50001
	// Generation 6; Candidate 20: [1.26, 0.69, 1.14, -2.32, -2.41, -3.12, 1.63]
	// Score = 2373327 Lines = 50001
	NORMAL(true, 4.426457995608443, 2.0614439190354164, 4.0965606199526,
			-0.44177209724864763, -3.6510151099057597, -4.201733292845843,
			-5.953559518514682, 3.8559029143902563, 1.746174039115659,
			2.872297231994768),
	// NORMAL(4.5952, 1.3462, 1.0432, -2.3555, -4.9735, -1.7756, 1.0966, false,
	// false),
	// MEDIUM(3.615, 1.001, 0.118, -4.957, -4.904, -3.918, -4.847, false,
	// false),
	// Generation 4; Candidate 15: [2.04, 1.86, -2.47, -4.27, -4.99, -4.7, 3.09]
	// Score = 429785
	// Generation 9; Candidate 11: [3.62, -0.13, -0.76, -3.3, -1.87, -2.68, 0.4]
	// Score = 433811
	// [2.0022764313144323, -0.6587265075373603, 4.599933469285261,
	// -0.12318631416001047, -2.768561835290717, -3.3973296774759545,
	// -0.5103118204848123,
	// 3.793387275240578, 0.13494895496925174, 1.0663625386513873]
	// KILL_ALL(false, 3.7822177942489414, 0.6947087007358634,
	// -1.9973036795285335, -0.3686910843090576, -3.2571969792628686,
	// -1.406052298647432,
	// -1.835230832597877, 1.951300981939566, -4.090004742527626,
	// -3.819524779835036) {
	KILL_ALL(false, 3.9877449546903403, 2.5349554867882524, 0.6072347995260703,
			-0.14162290332100014, -2.2520407801057463, -4.940246338705455,
			-4.5128500926146895, 1.600659418070152, 2.276333723973657,
			-4.740684933170122) {
		@Override
		public int calculateAttributesScore(int[] clearedLines) {
			return clearedLines[3] * 30 + clearedLines[4] * 120
					- clearedLines[5] / 10000;
		}
	},
	SAVE_KILL(false, 2.0022764313144323, -0.6587265075373603,
			4.599933469285261, -0.12318631416001047, -2.768561835290717,
			-3.3973296774759545, -0.5103118204848123, 3.793387275240578,
			0.13494895496925174, 1.0663625386513873) {
		@Override
		public int calculateAttributesScore(int[] clearedLines) {
			return clearedLines[3] * 30 + clearedLines[4] * 120
					- clearedLines[5] / 10000;
		}

		@Override
		public double[] getAttrs(boolean inDanger) {
			if (inDanger) {
				return LONG_LIFE.attrs;
			} else {
				return KILL_ALL.attrs;
			}
		}
	},
	// Generation 5; Candidate 14: [2.72, 2.32, -3.23, -0.31, -0.11, -1.76,
	// -0.58] Score = 12752293
	// Generation 14; Candidate 10: [2.72, 3.32, -6.45, -0.31, -0.11, 0.46,
	// -4.11] Score = 15499054
	// Generation 4; Candidate 14: [0.3, 4.35, -2.12, -2.99, -4.84, -3.27, 3.39]
	// Score = 17541872
	// LONG_LIFE(2.715, 2.322, -3.226, -0.310, -0.111, -1.764, -0.579, false,
	// true),
	// LONG_LIFE(2.715, 3.3206, -6.4545, -0.3103, -0.1115, 0.4551, -4.113,
	// false, false),
	LONG_LIFE(false, 4.8599662225890015, 3.798593925040892, 4.947656047509955,
			-1.6023623526660935, -3.0801282344881873, -0.359263214453895,
			-0.9978199937705634, 0.9383247620120475, -1.475462825797776,
			-2.7685717203177465) {
		@Override
		public int calculateAttributesScore(int[] clearedLines) {
			return StrategyOptimizer.MAX_STEPS_LONG_LIFE - clearedLines[6]
					- clearedLines[5] / 100000;
		}
	},
	// Generation 1; Candidate 11: [3.58, 4.93, 4.65, -1.12, -1.01, -4.94, 2.57]
	// Score = 18078157
	// Generation 11; Candidate 12: [6.32, 4.71, 0.32, -2.79, -0.22, 1.45,
	// -0.05] Score = 18498610
	MORE_TREASURE(false, 3.4262581205910045, 4.38313985562888,
			2.62196365982369, -0.9365658598261071, -1.8839890939655337,
			-0.024439890183996837, 0.6495452697028705, -0.31954345347017377,
			0.41110926540379644, 4.047144981768074) {
		@Override
		public int calculateAttributesScore(int[] clearedLines) {
			return clearedLines[1] + clearedLines[2] + clearedLines[3]
					+ clearedLines[4] - clearedLines[5] / 100000;
		}
	};
	public static final int ATTRS = 10;
	private final double[] attrs;
	public final static int SCORE_EDGE = 0;
	public final static int SCORE_WALL = 1;
	public final static int SCORE_FLOOR = 2;
	public final static int SCORE_HEIGHT = 3;
	public final static int SCORE_HOLE = 4;
	public final static int SCORE_BLOCKAGE = 5;
	public final static int SCORE_CLEAR = 6;
	public final static int SCORE_TETRIS = 7;
	public final static int SCORE_SMOOTHNESS = 8;
	public final static int SCORE_AVERSION = 9;
	public final boolean fastInDanger;

	StrategyType(boolean fastInDanger, double... attributes) {
		if (attributes.length != ATTRS) {
			throw new IllegalArgumentException(ATTRS
					+ " attributes expected but got " + attributes.length);
		}
		this.fastInDanger = fastInDanger;
		this.attrs = attributes;
	}

	public double calculateScore(final int[] boardCopy) {
		return calculateScore(boardCopy, this.attrs);
	}

	public double calculateScore(final int[] boardCopy, double[] attributes) {
		// clears lines
		int clears = BoardUtils.clearFullLines(boardCopy);

		// calculate stats
		int holes = 0;
		int blockages = 0;
		int heights = 0;
		int hMax = Integer.MIN_VALUE;
		int hMinX = 0;
		int hMin = Integer.MAX_VALUE;

		int walls = 0;
		int edges = 0;
		int floors = 0;

		int h;
		boolean empty;
		int v, y;
		int occupied = 0;
		int[] surface = new int[QQTetris.PiecesWidth];
		for (int x = 0; x < QQTetris.PiecesWidth; x++) {
			// top-down
			empty = true;

			for (y = 0; y < QQTetris.PiecesHeight; y++) {
				v = boardCopy[BoardUtils.getBoardPos(x, y)];
				if (v > 0) {
					occupied++;
					h = PiecesHeight - y;
					heights += h;
					if (v == 2) {
						if (x == 0 || x == QQTetris.PiecesWidth - 1) {
							walls++;
						}
						if (y == QQTetris.PiecesHeight - 1) {
							floors++;
						}
					}
					if (x < QQTetris.PiecesWidth - 1
							&& v + boardCopy[BoardUtils.getBoardPos(x + 1, y)] > 2) {
						edges++;
					}
					if (y < QQTetris.PiecesHeight - 1
							&& v + boardCopy[BoardUtils.getBoardPos(x, y + 1)] > 2) {
						edges++;
					}
					if (empty) {
						surface[x] = h;
						if (h > hMax) {
							hMax = h;
						} else if (h < hMin
								&& (x == 0 || x == 4
										|| x == QQTetris.PiecesWidth - 1 || x == QQTetris.PiecesWidth - 5)) {
							hMin = h;
							hMinX = x;
						}
					}
					empty = false;
				} else if (!empty) {
					holes++;
				}
			}

			if (empty) {
				if (x == 0 || x == 4 || x == QQTetris.PiecesWidth - 1
						|| x == QQTetris.PiecesWidth - 5) {
					hMin = 0;
					hMinX = x;
				}
				if (hMax == Integer.MIN_VALUE) {
					hMax = 0;
				}
			}
			// bottom-up
			empty = false;
			// h = 0;
			for (y = QQTetris.PiecesHeight - 1; y >= 0; y--) {
				v = boardCopy[BoardUtils.getBoardPos(x, y)];
				if (v == 0) {
					empty = true;
					// h++;
				} else if (empty) {
					// blockages += h;
					blockages++;
				}
			}
		}

		int aversionCount = 0;
		for (y = QQTetris.PiecesHeight - 1; y >= 0; y--) {
			v = boardCopy[BoardUtils.getBoardPos(hMinX, y)];
			if (v > 0) {
				empty = true;
				aversionCount++;
			}
		}

		double tetrisBonus = 0;
		if (clears > 3) {
			tetrisBonus = clears * attributes[SCORE_TETRIS];
		}

		int surfacePoints = calculateSurfacePoints(surface);

		double score = clears * attributes[SCORE_CLEAR] + edges
				* attributes[SCORE_EDGE] + walls * attributes[SCORE_WALL]
				+ floors * attributes[SCORE_FLOOR] + heights
				* attributes[SCORE_HEIGHT] + holes * attributes[SCORE_HOLE]
				+ blockages * attributes[SCORE_BLOCKAGE] + tetrisBonus
				+ surfacePoints * attributes[SCORE_SMOOTHNESS] + aversionCount
				* attributes[SCORE_AVERSION];

		// if (QQTetris.ANALYZE && DEBUG) {
		// System.out.println("score = clears " + clears + " * " +
		// this.scoreClear + " + edges " + edges + " * " +
		// this.scoreEdge + " + walls "
		// + walls + " * " + this.scoreWall + " + floors " + floors + " * " +
		// this.scoreFloor + " + heights " +
		// heights + " * " + this.scoreHeight + " + holes " +
		// holes + " * " + this.scoreHole + " + blockages " +
		// blockages + " * " + this.scoreBlockage + " = " + score);
		// QQDebug.printBoard(intsToBooleans(boardCopy));
		// }
		return score;
	}

	private int calculateSurfacePoints(int[] surface) {
		boolean[] surfaceShapes = new boolean[16];
		for (int i = 1; i < surface.length; i++) {
			if (surface[i] == surface[i - 1]) {
				surfaceShapes[0] = true;
				if (i > 1) {
					if (surface[i - 2] == surface[i]) {
						surfaceShapes[5] = true;
						if (i > 2 && surface[i - 3] == surface[i]) {
							surfaceShapes[11] = true;
						}
					} else if (surface[i - 2] == surface[i] - 1) {
						surfaceShapes[1] = true;
					} else if (surface[i - 2] == surface[i] + 1) {
						surfaceShapes[7] = true;
					}
				}
			} else if (i > 1) {
				if (surface[i - 2] == surface[i - 1]) {
					surfaceShapes[6] = true;
				} else if (surface[i - 2] == surface[i] + 1) {
					surfaceShapes[2] = true;
				} else if (surface[i - 2] == surface[i]
						&& surface[i - 1] == surface[i] - 1) {
					surfaceShapes[10] = true;
				}
			}
			int diff = surface[i] - surface[i - 1];
			switch (diff) {
			case 1:
				surfaceShapes[8] = true;
				break;
			case -1:
				surfaceShapes[9] = true;
				break;
			case 2:
				surfaceShapes[4] = true;
				break;
			case -2:
				surfaceShapes[3] = true;
				break;
			case 3:
				surfaceShapes[14] = true;
				break;
			case -3:
				surfaceShapes[12] = true;
				break;
			default:
				if (diff > 3) {
					surfaceShapes[15] = true;
				} else if (diff < -3) {
					surfaceShapes[13] = true;
				}
			}
		}

		int surfacePoints = 0;
		for (int i = 0; i < surfaceShapes.length; i++) {
			if (surfaceShapes[i]) {
				switch (i) {
				case 0:
					surfacePoints += 2;
					break;
				case 1:
					surfacePoints++;
					break;
				case 2:
					surfacePoints++;
					break;
				case 3:
					surfacePoints++;
					break;
				case 4:
					surfacePoints++;
					break;
				case 5:
					surfacePoints++;
					break;
				case 6:
					surfacePoints++;
					break;
				case 7:
					surfacePoints++;
					break;
				case 8:
					surfacePoints++;
					break;
				case 9:
					surfacePoints++;
					break;
				case 10:
					surfacePoints++;
					break;
				case 11:
					surfacePoints++;
					break;
				case 12:
					surfacePoints++;
					break;
				case 13:
					// skip
					break;
				case 14:
					if (!surfaceShapes[12]) {
						surfacePoints++;
					}
					break;
				case 15:
					// skip
					break;
				}
			}
		}
		return surfacePoints;
	}

	public int calculateAttributesScore(int[] clearedLines) {
		return clearedLines[1] * 4 + clearedLines[2] * 10 + clearedLines[3]
				* 30 + clearedLines[4] * 120 - clearedLines[5] / 10000;
	}

	public double[] getAttrs(boolean inDanger) {
		return this.attrs;
	}

}

package cn.keke.qqtetris.test;

import java.util.Arrays;

import cn.keke.qqtetris.BlockType;
import cn.keke.qqtetris.BoardUtils;
import cn.keke.qqtetris.MoveResult;
import cn.keke.qqtetris.QQCalculatorAsync;
import cn.keke.qqtetris.QQDebug;
import cn.keke.qqtetris.StrategyType;
import cn.keke.qqtetris.Tetromino;
import cn.keke.qqtetris.TransformationResult;

import junit.framework.TestCase;

public class TransformationTest extends TestCase {
    private static boolean[] TEST_BOARD_1 = BoardUtils.intsToBooleans(
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0,
                                           0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1
                                           );

    private static final TransformationResult   r1           = new TransformationResult(BlockType.S, 0, 0, 19, -1, null);
    private static final TransformationResult   r2           = new TransformationResult(BlockType.I, 1, 9, 16, -1, null);

    private static final TransformationResult[] TEST_RESULTS_1 = { r1, r2 };

    public void testPiecesHeight() {
        int[] heights = BoardUtils.calcBoardHeight(TEST_BOARD_1);
        assertEquals(5, heights[5]);
        assertEquals(0, heights[0]);
        assertEquals(1, heights[11]);
    }

    public void testPiecesHeightMerge() {
        int[] heights = BoardUtils.calcPiecesHeight(BoardUtils.calcBoardHeight(TEST_BOARD_1), TEST_RESULTS_1);
        System.out.println(Arrays.toString(heights));
        assertEquals(1, heights[0]);
        assertEquals(2, heights[1]);
        assertEquals(2, heights[2]);
        assertEquals(0, heights[3]);
        assertEquals(5, heights[5]);
        assertEquals(5, heights[11]);
    }
    
    public void testBlocksMerge() {
        int[] boardCopy = BoardUtils.booleansToInts(TEST_BOARD_1);
        BoardUtils.mergeResults(boardCopy, TEST_RESULTS_1);
        double score = StrategyType.NORMAL.calculateScore(boardCopy);

        System.out.println(TEST_RESULTS_1[0].getBlock().rotations[TEST_RESULTS_1[0].getRotationIdx()]);
        System.out.println(TEST_RESULTS_1[1].getBlock().rotations[TEST_RESULTS_1[1].getRotationIdx()]);
        QQDebug.printBoard(BoardUtils.intsToBooleans(boardCopy));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 11, 16));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 11, 17));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 11, 18));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 11, 19));
        assertTrue(1 == BoardUtils.getBoardValue(boardCopy, 11, 20));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 0, 20));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 1, 19));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 1, 20));
        assertTrue(2 == BoardUtils.getBoardValue(boardCopy, 2, 19));
        System.out.println(score);
    }

    public void testBoardStats() {
        int[] stats = BoardUtils.calcDetailedBoardStats(TEST_BOARD_1, null);
        assertEquals(9, stats[1]);
        assertEquals(5, stats[2]);
        assertEquals(0, stats[3]);
        assertEquals(2, stats[4]);
        assertEquals(0, stats[5]);
        assertEquals(1, stats[6]);
        assertEquals(11, stats[7]);

    }
}

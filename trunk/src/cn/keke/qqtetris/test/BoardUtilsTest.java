package cn.keke.qqtetris.test;

import junit.framework.TestCase;
import cn.keke.qqtetris.BlockType;
import cn.keke.qqtetris.BoardUtils;
import cn.keke.qqtetris.QQTetris;
import cn.keke.qqtetris.StopWatch;

public class BoardUtilsTest extends TestCase {
    private final static StopWatch STOPPER = new StopWatch("test");

    public void testFitFormInner() {
        boolean[] testBoard1 = BoardUtils.intsToBooleans(
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
                0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1
                );
        assertTrue(BoardUtils.fitFormInner(testBoard1, BlockType.J.rotations[2], 2, 15));
        assertFalse(BoardUtils.fitFormInner(testBoard1, BlockType.J.rotations[2], 2, 14));
        assertFalse(BoardUtils.fitFormInner(testBoard1, BlockType.J.rotations[2], 2, 16));
        assertTrue(BoardUtils.fitFormInner(testBoard1, BlockType.J.rotations[2], 2, 0));
        assertFalse(BoardUtils.fitFormInner(testBoard1, BlockType.J.rotations[2], 2, QQTetris.PiecesHeight - QQTetris.BlockDrawSize));
    }

    public void testFindLeftFree() {
        boolean[] testBoard1 = BoardUtils.intsToBooleans(
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
                0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1,
                0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1,
                1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1
                );
        assertEquals(-1, BoardUtils.findLeftFree(testBoard1, BlockType.L.rotations[2], 0, 15, 0));
        assertEquals(2, BoardUtils.findLeftFree(testBoard1, BlockType.L.rotations[2], 4, 16, 0));
    }

    public void testFindRightFree() {
        boolean[] testBoard1 = BoardUtils.intsToBooleans(
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
                0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0,
                0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0,
                1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0,
                1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1
                );
        assertEquals(9, BoardUtils.findRightFree(testBoard1, BlockType.L.rotations[2], 8, 17, 0));
        assertEquals(BoardUtils.NOT_FOUND, BoardUtils.findRightFree(testBoard1, BlockType.L.rotations[2], 2, 17, 0));
    }

}

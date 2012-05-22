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
package cn.keke.qqtetris.test;

import java.util.Arrays;

import junit.framework.TestCase;
import cn.keke.qqtetris.BlockType;
import cn.keke.qqtetris.BoardUtils;
import cn.keke.qqtetris.QQDebug;
import cn.keke.qqtetris.StrategyType;
import cn.keke.qqtetris.TransformationResult;

public class TransformationTest extends TestCase {
    // @formatter:off
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
    // @formatter:on
    private static final TransformationResult r1 = new TransformationResult(BlockType.S, 0, 0, 19, -1, null);
    private static final TransformationResult r2 = new TransformationResult(BlockType.I, 1, 9, 16, -1, null);

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

    }
}

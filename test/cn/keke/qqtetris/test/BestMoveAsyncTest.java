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

import junit.framework.TestCase;
import cn.keke.qqtetris.BlockType;
import cn.keke.qqtetris.BoardUtils;
import cn.keke.qqtetris.CurrentData;
import cn.keke.qqtetris.MoveCalculator;
import cn.keke.qqtetris.MoveResult;
import cn.keke.qqtetris.QQCalculatorAsync;
import cn.keke.qqtetris.QQDebug;
import cn.keke.qqtetris.StopWatch;
import cn.keke.qqtetris.StrategyType;
import cn.keke.qqtetris.Tetromino;

public class BestMoveAsyncTest extends TestCase {
    private MoveCalculator calculator = new QQCalculatorAsync();
    private final static StopWatch STOPPER = new StopWatch("test");

    // private MoveCalculator calculator = new QQCalculatorAsync();

    public void testFreeSlots() {
        // @formatter:off
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
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1
                );
        // @formatter:on
        CurrentData.CALCULATED.reset();
        System.arraycopy(testBoard1, 0, CurrentData.CALCULATED.board, 0, testBoard1.length);
        CurrentData.CALCULATED.tetromino.set(BlockType.I, 0, 0, 0);
        CurrentData.CALCULATED.nextBlocks[0] = CurrentData.CALCULATED.tetromino.block;
        CurrentData.CALCULATED.nextBlocks[1] = BlockType.O;

        QQDebug.printBoard(CurrentData.CALCULATED.board);
        this.calculator.findBestMove(CurrentData.CALCULATED.board, CurrentData.CALCULATED.tetromino,
                CurrentData.CALCULATED.nextBlocks, CurrentData.CALCULATED.stats, StrategyType.NORMAL,
                StrategyType.NORMAL.getAttrs(false));
        System.out.println(CurrentData.CALCULATED.tetromino.move);
        assertEquals(2, CurrentData.CALCULATED.tetromino.move.moveDelta);
        assertEquals(1, CurrentData.CALCULATED.tetromino.move.rotationDelta);
    }

    public void testTwoBlocks() {
        // @formatter:off
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
                1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1,
                0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0,
                1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1
                );
        // @formatter:on
        CurrentData.CALCULATED.reset();
        System.arraycopy(testBoard1, 0, CurrentData.CALCULATED.board, 0, testBoard1.length);
        CurrentData.CALCULATED.tetromino.set(BlockType.S, 0, 0, 0);
        CurrentData.CALCULATED.nextBlocks[0] = CurrentData.CALCULATED.tetromino.block;
        CurrentData.CALCULATED.nextBlocks[1] = BlockType.S;
        CurrentData.CALCULATED.nextBlocks[2] = BlockType.Z;

        QQDebug.printBlock(CurrentData.CALCULATED.tetromino.rotation.form);
        QQDebug.printBoard(CurrentData.CALCULATED.board);
        this.calculator.findBestMove(CurrentData.CALCULATED.board, CurrentData.CALCULATED.tetromino,
                CurrentData.CALCULATED.nextBlocks, CurrentData.CALCULATED.stats, StrategyType.NORMAL,
                StrategyType.NORMAL.getAttrs(false));
        BoardUtils.mergeMoveResult(CurrentData.CALCULATED.board, CurrentData.CALCULATED.tetromino,
                CurrentData.CALCULATED.tetromino.move);
        QQDebug.printBoard(CurrentData.CALCULATED.board);

        CurrentData.CALCULATED.reset();
        CurrentData.CALCULATED.tetromino.set(BlockType.S, 0, 0, 0);
        CurrentData.CALCULATED.nextBlocks[0] = CurrentData.CALCULATED.tetromino.block;
        CurrentData.CALCULATED.nextBlocks[1] = BlockType.Z;
        CurrentData.CALCULATED.nextBlocks[2] = null;
        QQDebug.printBlock(CurrentData.CALCULATED.tetromino.rotation.form);
        this.calculator.findBestMove(CurrentData.CALCULATED.board, CurrentData.CALCULATED.tetromino,
                CurrentData.CALCULATED.nextBlocks, CurrentData.CALCULATED.stats, StrategyType.NORMAL,
                StrategyType.NORMAL.getAttrs(false));
        BoardUtils.mergeMoveResult(CurrentData.CALCULATED.board, CurrentData.CALCULATED.tetromino,
                CurrentData.CALCULATED.tetromino.move);
        QQDebug.printBoard(CurrentData.CALCULATED.board);
        assertEquals(3, CurrentData.CALCULATED.tetromino.move.moveDelta);
        assertEquals(0, CurrentData.CALCULATED.tetromino.move.rotationDelta);
    }

    public void testThreeBlocks() {
        // @formatter:off        
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
                0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1,
                0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0,
                1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1
                );
        // @formatter:on
        MoveResult move = null;
        STOPPER.start();
        for (int i = 0; i < 10; i++) {
            move = this.calculator.findBestMove(
                    CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), new BlockType[] {
                            BlockType.L, BlockType.J, BlockType.T }), StrategyType.NORMAL,
                    StrategyType.NORMAL.getAttrs(false));
        }
        STOPPER.printTime("threeblocks");
        // QQDebug.printBoard(testBoard1);
        // BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), cleverPoints);
        // QQDebug.printBoard(testBoard1);
        assertEquals(3, move.moveDelta);
        assertEquals(3, move.rotationDelta);
    }

    public void testThreeTetrises() {
        // @formatter:off
        boolean[] testBoard1 = BoardUtils.intsToBooleans(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.I, 0, 0, 0), new BlockType[] { BlockType.I }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        assertTrue(move.isValid());
        assertEquals(9, move.moveDelta);
        assertEquals(1, move.rotationDelta);
    }

    public void testO1() {
        // @formatter:off
        boolean[] testBoard1 = BoardUtils.intsToBooleans(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1,
                0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.O, 0, 0, 0), new BlockType[] { BlockType.O }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        assertTrue(move.isValid());
        assertEquals(0, move.moveDelta);
        assertEquals(0, move.rotationDelta);
    }

    public void testT() {
        // @formatter:off
        boolean[] testBoard1 = BoardUtils.intsToBooleans(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1,
                0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.T, 0, 0, 0), new BlockType[] { BlockType.T }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        assertTrue(move.isValid());
        assertEquals(6, move.moveDelta);
        assertEquals(0, move.rotationDelta);
    }

    public void testClever() {
        // @formatter:off
        boolean[] testBoard1 = BoardUtils.intsToBooleans(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1,
                0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0,
                1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0,
                1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0,
                1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.O, 0, 0, 0), new BlockType[] { BlockType.O }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        System.out.println(move);
        assertTrue(move.isValid());
        assertEquals(MoveResult.CLEVER_MOVE, move.moveDelta);
        assertEquals(0, move.rotationDelta);
    }

    public void testCleverI() {
        // @formatter:off
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
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.I, 0, 0, 0), new BlockType[] { BlockType.I }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        System.out.println(move);
        assertTrue(move.isValid());
        assertEquals(MoveResult.CLEVER_MOVE, move.moveDelta);
        assertEquals(0, move.rotationDelta);
        assertEquals(3, move.x);
    }

    public void testCleverJ1() {
        // @formatter:off
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
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1,
                0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0,
                0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.J, 0, 0, 0), new BlockType[] { BlockType.J }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        System.out.println(move);
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.J, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        assertTrue(move.isValid());
        assertEquals(-1, move.moveDelta);
        assertEquals(0, move.rotationDelta);
    }

    public void testCleverJ2() {
        // @formatter:off
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
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1,
                0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0,
                0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.J, 0, 0, 0), new BlockType[] { BlockType.J }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        System.out.println(move);
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.J, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        assertTrue(move.isValid());
        assertEquals(MoveResult.CLEVER_MOVE, move.moveDelta);
        assertEquals(2, move.rotationDelta);
        assertEquals(8, move.x);
    }

    public void testCleverL1() {
        // @formatter:off
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
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
                0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0,
                0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.L, 0, 0, 0), new BlockType[] { BlockType.L }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        System.out.println(move);
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        assertTrue(move.isValid());
        assertEquals(MoveResult.CLEVER_MOVE, move.moveDelta);
        assertEquals(7, move.x);
    }

    public void testCleverSZL() {
        // @formatter:off
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
                0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(
                CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.Z, 0, 0, 0), new BlockType[] {
                        BlockType.Z, BlockType.L, BlockType.Z }), StrategyType.NORMAL,
                StrategyType.NORMAL.getAttrs(false));
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.Z, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        move = this.calculator.findBestMove(
                CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), new BlockType[] {
                        BlockType.L, BlockType.Z }), StrategyType.NORMAL, StrategyType.NORMAL.getAttrs(false));
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        System.out.println(move);
        move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.Z, 0, 0, 0),
                new BlockType[] { BlockType.Z }), StrategyType.NORMAL, StrategyType.NORMAL.getAttrs(false));
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.Z, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        System.out.println(move);
        assertEquals(3, move.moveDelta);
        assertEquals(0, move.rotationDelta);
    }

    public void testCleverZ() {
        // @formatter:off
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
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0,
                1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.Z, 0, 0, 0), new BlockType[] { BlockType.Z }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        System.out.println(move);
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.Z, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        assertTrue(move.isValid());
        assertEquals(0, move.moveDelta);
        assertEquals(0, move.x);
    }

    public void testCleverL() {
        // @formatter:off
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
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.L, 0, 0, 0), new BlockType[] { BlockType.L }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        System.out.println(move);
        BoardUtils.mergeMoveResult(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), move);
        QQDebug.printBoard(testBoard1);
        assertTrue(move.isValid());
        assertEquals(MoveResult.CLEVER_MOVE, move.moveDelta);
        assertEquals(8, move.x);
    }

    public void testCleverT() {
        // @formatter:off
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
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0,
                1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1
                );
        // @formatter:on
        MoveResult move = this.calculator.findBestMove(CurrentData.CALCULATED.set(testBoard1, new Tetromino(
                BlockType.T, 0, 0, 0), new BlockType[] { BlockType.T }), StrategyType.NORMAL, StrategyType.NORMAL
                .getAttrs(false));
        QQDebug.printBlock(CurrentData.CALCULATED.tetromino.rotation.form);
        QQDebug.printBoard(CurrentData.CALCULATED.board);
        BoardUtils.mergeMoveResult(CurrentData.CALCULATED.board, new Tetromino(BlockType.T, 0, 0, 0), move);
        QQDebug.printBoard(CurrentData.CALCULATED.board);
        assertTrue(move.isValid());
        assertEquals(MoveResult.CLEVER_MOVE, move.moveDelta);
        assertEquals(9, move.x);
    }
}

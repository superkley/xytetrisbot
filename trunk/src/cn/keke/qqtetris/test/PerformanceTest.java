package cn.keke.qqtetris.test;

import junit.framework.TestCase;
import cn.keke.qqtetris.BlockType;
import cn.keke.qqtetris.BoardUtils;
import cn.keke.qqtetris.CurrentData;
import cn.keke.qqtetris.MoveCalculator;
import cn.keke.qqtetris.MoveResult;
import cn.keke.qqtetris.QQCalculatorAsync;
import cn.keke.qqtetris.QQCalculatorSync;
import cn.keke.qqtetris.StopWatch;
import cn.keke.qqtetris.StrategyType;
import cn.keke.qqtetris.Tetromino;

public class PerformanceTest extends TestCase {
    private MoveCalculator calculatorSync = new QQCalculatorSync();
    private final static StopWatch STOPPER = new StopWatch("test");
    private MoveCalculator calculatorAsync = new QQCalculatorAsync();

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
        STOPPER.start();
        final int rounds = 10;
        for (int i = 0; i < rounds; i++) {
            this.calculatorSync.findBestMove(
                    CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), new BlockType[] {
                            BlockType.L, BlockType.J, BlockType.T }), StrategyType.NORMAL,
                    StrategyType.NORMAL.getAttrs(false));
        }
        final long durationSync = STOPPER.printTime("threeblocks sync");
        assertTrue(durationSync / rounds < 600);
        STOPPER.start();
        for (int i = 0; i < rounds; i++) {
            this.calculatorAsync.findBestMove(
                    CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.L, 0, 0, 0), new BlockType[] {
                            BlockType.L, BlockType.J, BlockType.T }), StrategyType.NORMAL,
                    StrategyType.NORMAL.getAttrs(false));
        }
        final long durationAsync = STOPPER.printTime("threeblocks async");
        assertTrue(durationAsync / rounds < 200);
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
        STOPPER.start();
        final int rounds = 10;
        for (int i = 0; i < rounds; i++) {
            this.calculatorSync.findBestMove(
                    CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.T, 0, 0, 0), new BlockType[] {
                            BlockType.T, BlockType.T, BlockType.T }), StrategyType.NORMAL,
                    StrategyType.NORMAL.getAttrs(false));
        }
        final long durationSync = STOPPER.printTime("t sync");
        assertTrue(durationSync / rounds < 200);
        STOPPER.start();
        for (int i = 0; i < rounds; i++) {
            this.calculatorAsync.findBestMove(
                    CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.T, 0, 0, 0), new BlockType[] {
                            BlockType.T, BlockType.T, BlockType.T }), StrategyType.NORMAL,
                    StrategyType.NORMAL.getAttrs(false));
        }
        final long durationAsync = STOPPER.printTime("t async");
        assertTrue(durationAsync / rounds < 200);
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
                1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0,
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
        STOPPER.start();
        final int rounds = 10;
        for (int i = 0; i < rounds; i++) {
            calculatorSync.findBestMove(
                    CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.O, 0, 0, 0), new BlockType[] {
                            BlockType.O, BlockType.O, BlockType.O }), StrategyType.NORMAL,
                    StrategyType.NORMAL.getAttrs(false));
        }
        final long durationSync = STOPPER.printTime("clever sync");
        assertTrue(durationSync / rounds < 100);
        STOPPER.start();
        for (int i = 0; i < rounds; i++) {
            calculatorAsync.findBestMove(
                    CurrentData.CALCULATED.set(testBoard1, new Tetromino(BlockType.O, 0, 0, 0), new BlockType[] {
                            BlockType.O, BlockType.O, BlockType.O }), StrategyType.NORMAL,
                    StrategyType.NORMAL.getAttrs(false));
        }
        final long durationAsync = STOPPER.printTime("clever async");
        assertTrue(durationAsync / rounds < 100);
    }

}

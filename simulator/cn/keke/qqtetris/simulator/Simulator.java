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
package cn.keke.qqtetris.simulator;

import java.util.Arrays;
import java.util.Random;

import cn.keke.qqtetris.BlockType;
import cn.keke.qqtetris.BoardUtils;
import cn.keke.qqtetris.CurrentData;
import cn.keke.qqtetris.MoveCalculator;
import cn.keke.qqtetris.MoveResult;
import cn.keke.qqtetris.QQCalculatorAsync;
import cn.keke.qqtetris.QQCalculatorSync;
import cn.keke.qqtetris.QQDebug;
import cn.keke.qqtetris.QQStats;
import cn.keke.qqtetris.QQTetris;
import cn.keke.qqtetris.StopWatch;
import cn.keke.qqtetris.StrategyType;
import cn.keke.qqtetris.Tetromino;

public class Simulator {
    private static int SIM_COUNTER = 1;
    private StopWatch stopper = new StopWatch("sim-" + SIM_COUNTER++);
    public static final Random RANDOM = new Random();
    private StrategyType strategy;
    private MoveCalculator calculator;
    private final int[] initialHeights = new int[QQTetris.PiecesWidth];

    public Simulator(StrategyType strategy) {
        //this.calculator = new QQCalculatorSync();
        this.calculator = new QQCalculatorAsync();
        this.strategy = strategy;
    }

    public int[] play() {
        return play(Integer.MAX_VALUE, this.strategy.getAttrs(false));
    }

    public int[] play(int maxSteps, double[] strategyAttrs) {
        boolean[] board = new boolean[QQTetris.PiecesWidth * QQTetris.PiecesHeight];
        if (this.strategy == StrategyType.LONG_LIFE) {
            BoardUtils.fillRamdomPieces(board, 14);
            // QQDebug.printBoard(board);
        }
        BlockType[] types = { BlockType.values()[RANDOM.nextInt(BlockType.values().length)],
                BlockType.values()[RANDOM.nextInt(BlockType.values().length)],
                BlockType.values()[RANDOM.nextInt(BlockType.values().length)] };
        Tetromino t;
        QQStats stats = CurrentData.CALCULATED.stats;
        MoveResult move;
        int[] clearedLines = new int[7];
        clearedLines[6] = maxSteps;
        int clears;
        int steps = 0;
        this.stopper.start();
        double averageHeight = 0;
        while (steps < maxSteps) {
            t = new Tetromino(types[0], 0, QQTetris.PiecesWidth / 2, 0);
            move = this.calculator.findBestMove(CurrentData.CALCULATED.set(board, t, types), this.strategy,
                    strategyAttrs);
            QQDebug.printBoard(CurrentData.CALCULATED.board);
            System.out.println(Arrays.toString(CurrentData.CALCULATED.nextBlocks));
            stats.calculate();
            if (!move.isValid()) {
                clearedLines[5] = (int) Math.round(averageHeight * 1000000);
                this.stopper.printTime("> ENDED at step " + steps + ": " + Arrays.toString(clearedLines));
                break;
            }
            if (this.strategy == StrategyType.MORE_TREASURE && stats.highest > 10) {
                this.stopper.printTime("> ENDED at step " + steps + ": " + Arrays.toString(clearedLines));
                break;
            }
            if (this.strategy == StrategyType.NORMAL && stats.highest > 15) {
                this.stopper.printTime("> ENDED at step " + steps + ": " + Arrays.toString(clearedLines));
                break;
            }
            if (this.strategy == StrategyType.LONG_LIFE && stats.holes == 0) {
                clearedLines[6] = steps;
                this.stopper.printTime("> CLEARED at step " + steps + ": " + Arrays.toString(clearedLines));
                break;
            }

            // System.out.println(move);
            BoardUtils.mergeMoveResult(board, t, move);
            clears = BoardUtils.clearFullLines(board);
            int height = BoardUtils.calcDetailedBoardStats(board, this.initialHeights)[2];
            averageHeight = (height + averageHeight * steps) / (steps + 1);
            // QQDebug.printBoard(this.board);
            // if (clears > 0) {
            // System.out.println("avg: "+averageHeight + ", h: " +
            // height+", clear: "+clears+", sum: "+Arrays.toString(clearedLines));
            // }
            clearedLines[clears] = clearedLines[clears] + 1;
            types[0] = types[1];
            types[1] = types[2];
            types[2] = BlockType.values()[RANDOM.nextInt(BlockType.values().length)];
            steps++;
            if (steps % 100 == 0) {
                clearedLines[5] = (int) Math.round(averageHeight * 1000000);
                // QQDebug.printBoard(board);
                // this.stopper.printTime("steps " + steps + ": " + Arrays.toString(clearedLines) + ", holes=" +
                // stats.holes);
                if (steps % 1000 == 0) {
                    // QQDebug.printBoard(board);
                }
            }
        }
        clearedLines[5] = (int) Math.round(averageHeight * 1000000);
        // QQDebug.printBoard(board);
        return clearedLines;
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator(StrategyType.LONG_LIFE);
        int[] lines = simulator.play();
        System.out.println(Arrays.toString(lines));
    }

    public static void resetSimCounter() {
        SIM_COUNTER = 1;
    }
}

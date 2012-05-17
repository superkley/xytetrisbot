package cn.keke.qqtetris.test;

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;
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
import cn.keke.qqtetris.StrategyType;
import cn.keke.qqtetris.Tetromino;

public class SyncAsyncTest extends TestCase {
    private MoveCalculator sync = new QQCalculatorSync();
    private MoveCalculator async = new QQCalculatorAsync();
    public static final Random RANDOM = new Random();

    public void testParallel() {
        long syncTotal = 0;
        long asyncTotal = 0;
        boolean[] board = CurrentData.CALCULATED.board;
        StrategyType strategy = StrategyType.KILL_ALL;
        BoardUtils.fillRamdomPieces(board, 5);
        BlockType[] types = { BlockType.values()[RANDOM.nextInt(BlockType.values().length)],
                BlockType.values()[RANDOM.nextInt(BlockType.values().length)],
                BlockType.values()[RANDOM.nextInt(BlockType.values().length)] };
        Tetromino t;
        QQStats stats;
        MoveResult moveSync;
        MoveResult moveAsync;
        int maxSteps = 100;
        int steps = 0;
        while (steps < maxSteps) {
            t = new Tetromino(types[0], 0, QQTetris.PiecesWidth / 2, 0);
            long start = System.currentTimeMillis();
            moveSync = this.sync.findBestMove(CurrentData.CALCULATED.set(board, t, types), strategy,
                    strategy.getAttrs(false));
            syncTotal += System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            moveAsync = this.async.findBestMove(CurrentData.CALCULATED.set(board, t, types), strategy,
                    strategy.getAttrs(false));
            asyncTotal += System.currentTimeMillis() - start;
            if (!moveSync.equals(moveAsync) && (int) moveSync.score > (int) moveAsync.score) {
                System.out.println("t: " + t);
                QQDebug.printBoard(board);
                System.out.println("sync: " + moveSync + ", s=" + moveSync.score);
                System.out.println("async: " + moveAsync + ", s=" + moveAsync.score);
                moveSync = sync.findBestMove(CurrentData.CALCULATED.set(board, t, types), strategy,
                        strategy.getAttrs(false));
                moveAsync = async.findBestMove(CurrentData.CALCULATED.set(board, t, types), strategy,
                        strategy.getAttrs(false));
                System.out.println("sync 2. check: " + moveSync);
                System.out.println("async 2. check: " + moveAsync);

                boolean[] boardCopySync = Arrays.copyOf(board, board.length);
                BoardUtils.mergeMoveResult(boardCopySync, t, moveSync);
                QQDebug.printBoard(boardCopySync);

                boolean[] boardCopyAsync = Arrays.copyOf(board, board.length);
                BoardUtils.mergeMoveResult(boardCopyAsync, t, moveAsync);
                QQDebug.printBoard(boardCopyAsync);
                fail();
            }
            if (moveSync.isValid() && moveSync.rIdx != -1) {
                BoardUtils.mergeMoveResult(board, t, moveSync);
                BoardUtils.clearFullLines(board);

                types[0] = types[1];
                types[1] = types[2];
                types[2] = BlockType.values()[RANDOM.nextInt(BlockType.values().length)];

                steps++;
            } else {
                QQDebug.printBlock(CurrentData.CALCULATED.tetromino.rotation.form);
                QQDebug.printBoard(CurrentData.CALCULATED.board);
            }
        }
        System.out.println("sync total: " + syncTotal + ", " + (syncTotal / maxSteps));
        System.out.println("async total: " + asyncTotal + ", " + (asyncTotal / maxSteps));
    }
}

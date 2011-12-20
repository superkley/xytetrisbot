package cn.keke.qqtetris.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.keke.qqtetris.BlockType;
import cn.keke.qqtetris.BoardUtils;
import cn.keke.qqtetris.QQDebug;
import cn.keke.qqtetris.QQStats;
import cn.keke.qqtetris.Tetromino;
import cn.keke.qqtetris.exceptions.UnknownBoardStateException;


import static cn.keke.qqtetris.QQRobot.*;

public class FileSourceTest {
    // 20, 55, 65, 68, 109, 117, 176
    public static void main(String[] args) {
        // QQDebug.debugColor(0xff215621);
        // QQDebug.debugColor(BlockType.S.bottomColor);
        // QQDebug.debugColor(BlockType.S.bottomColor ^ 0xff215621);
        // int i = 11;
        boolean[] testBoard = { true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false,
                false, false, true, true, true, false, false, false, false, false, false, false, false, false, true, true, true, false, false, false, false,
                false, false, false, false, true, true, true, true, true, true, false, true, true, false, false, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true, true, true, false, true, false, true, false, true, false, true, false, true, true, true,
                true, true, true, true, true, true, true, true, true };
        // QQDebug.printBoard(testBoard);
        // BoardUtils.clearFullLines(testBoard);
        // QQDebug.printBoard(testBoard);
        for (int i = 0; i < 300; i++) {
            try {
                // System.out.print(i + "> ");
                testReadBoard("board_" + i, true);
            } catch (UnknownBoardStateException e) {
                System.err.println(i + ": " + e.toString());
            }
            testReadFuture1("f1_" + i);
            testReadFuture2("f2_" + i);
        }
        // testReadBoard("board_" + 16, true);
    }

    public static void testReadBoard(String imgFile, boolean debug) {
        try {
            BufferedImage img = ImageIO.read(new File("D:\\tetris\\" + imgFile + ".png"));
            boolean[] data = readBoardData(img);
            Tetromino t = BoardUtils.getAndCleanNextType(data);
            if (debug) {
                System.out.println(imgFile + ": " + new QQStats(data, t, null));
                QQDebug.printBoard(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testReadFuture1(String imgFile) {
        try {
            BufferedImage img = ImageIO.read(new File("D:\\tetris\\" + imgFile + ".png"));
            BlockType t = readFutureBlock(img);
            System.out.print(imgFile + ": ");
            System.out.println(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testReadFuture2(String imgFile) {
        try {
            BufferedImage img = ImageIO.read(new File("D:\\tetris\\" + imgFile + ".png"));
            BlockType t = readFuture2Block(img);
            System.out.print(imgFile + ": ");
            System.out.println(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

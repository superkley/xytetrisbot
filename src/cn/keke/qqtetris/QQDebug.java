package cn.keke.qqtetris;

import static cn.keke.qqtetris.QQTetris.BlockDrawSize;
import static cn.keke.qqtetris.QQTetris.PiecesHeight;
import static cn.keke.qqtetris.QQTetris.PiecesWidth;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QQDebug {
    private static int counter       = 0;
    private static int lastBoardHash = -1;

    public static void debugColor(int c) {
        int r = (c >> 16) & 0x000000FF;
        int g = (c >> 8) & 0x000000FF;
        int b = (c) & 0x000000FF;
        System.out.println("(r,g,b) = (" + r + "," + g + "," + b + "), hex: " + Integer.toHexString(c));
    }

    public static void save(BufferedImage image, String fileName) {
        try {
            File outputfile = new File(System.getenv("tmp") + "\\" + fileName + ".png");
            ImageIO.write(image, "png", outputfile);
            System.out.println("Saved screen under '" + outputfile.getAbsolutePath() + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void debugScreen(BufferedImage f1Img, BufferedImage f2Img, BufferedImage boardImg, boolean[] data, Tetromino t, BlockType ft1, BlockType ft2) {
        int hash = BoardUtils.calcBoardStats(data)[0];
        if (hash != lastBoardHash && ft1 != null) {
            save(f1Img, "f1_" + counter);
            save(f2Img, "f2_" + counter);
            save(boardImg, "board_" + counter);
            counter++;
            // BoardUtils.printBoard(data);
            System.out.println("t: " + t + ", f1: " + ft1 + ", f2: " + ft2 + ", h: " + hash);
            lastBoardHash = hash;
        }
    }

    public static void debugScreen(boolean[] data, Tetromino t, BlockType ft1, BlockType ft2) {
        int hash = BoardUtils.calcBoardStats(data)[0];
        if (hash != lastBoardHash && ft1 != null) {
            // BoardUtils.printBoard(data);
            System.out.println("t: " + t + ", f1: " + ft1 + ", f2: " + ft2 + ", h: " + Integer.toHexString(hash));
            lastBoardHash = hash;
        }
    }

    public static void printBlock(boolean[] data) {
        printBoard(data, BlockDrawSize, BlockDrawSize);
    }

    public static void printBoard(boolean[] data) {
        printBoard(data, PiecesWidth, PiecesHeight);
    }

    public static void printBoard(boolean[] data, int width, int height) {
        for (int i = 0; i < width * height; i++) {
            if (i % width == 0) {
                System.out.println();
            }
            System.out.print(data[i] ? 'X' : '-');
        }
        System.out.println();
    }
}

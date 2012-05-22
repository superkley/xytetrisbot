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
            File outputfile = new File("c:\\temp\\" + fileName + ".png");
            ImageIO.write(image, "png", outputfile);
            System.out.println("Saved screen under '" + outputfile.getAbsolutePath() + "'");
        } catch (IOException e) {
            // ignore
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

    public static void debugScreen(CurrentData data) {
        if (data.nextBlocks[1] != null) {
            // BoardUtils.printBoard(data);
            System.out.println("t: " + data.tetromino + ", f1: " + data.nextBlocks[1] + ", f2: " + data.nextBlocks[2]);
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

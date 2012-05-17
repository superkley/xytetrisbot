package cn.keke.qqtetris.test;

import static cn.keke.qqtetris.QQRobot.readFuture2Block;
import static cn.keke.qqtetris.QQRobot.readFutureBlock;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.keke.qqtetris.BlockType;

public class FileSourceTest {

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

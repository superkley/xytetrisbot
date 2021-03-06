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

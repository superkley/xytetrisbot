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

public class FaultChecker {
    public FaultChecker(BlockRotation r, int faultMin, int[] coordinates) {
        super();
        this.rotation = r;
        this.faultMin = faultMin;
        this.coordinates = coordinates;
    }

    public final BlockRotation rotation;
    public final int   faultMin;
    public final int[] coordinates;

    public final boolean check(final boolean[] board, final int x, final int y) {
        if (this.faultMin == 0) {
            return true;
        } else {
            int counter = 0;
            int mx, my;

            for (int i = 0; i < this.coordinates.length;) {
                mx = x + this.coordinates[i++];
                my = y + this.coordinates[i++];
                if (mx >= 0 && mx < QQTetris.PiecesWidth &&
                        my >= 0 && my < QQTetris.PiecesHeight &&
                        !board[mx + my * QQTetris.PiecesWidth]) {
                    if (++counter >= this.faultMin) {
                        // System.out.println("x=" + x + ", y=" + y);
                        return false;
                    }
                }
            }
            return true;
        }
    }
}

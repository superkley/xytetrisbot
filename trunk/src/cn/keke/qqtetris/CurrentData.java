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

public enum CurrentData {
    REAL,
    CALCULATED;
    public final boolean[] board = new boolean[QQTetris.PiecesHeight * QQTetris.PiecesWidth];
    public final Tetromino tetromino = new Tetromino();
    public final BlockType[] nextBlocks = new BlockType[3];
    public final QQStats stats = new QQStats(this);

    public final void reset() {
        tetromino.reset();
        nextBlocks[0] = null;
        nextBlocks[1] = null;
        nextBlocks[2] = null;
        stats.reset();
    }

    public final CurrentData set(final boolean[] b, final Tetromino t, final BlockType[] n) {
        reset();
        System.arraycopy(b, 0, board, 0, b.length);
        tetromino.from(t);
        for (int i = 0; i<n.length; i++) {
            nextBlocks[i] = n[i];
        }
        return this;
    }

}

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

public class Tetromino {
    public BlockType block;
    public BlockRotation rotation;
    public int rotationIdx;
    public int x;
    public int y;
    public MoveResult move;

    public Tetromino(BlockType block, int rotationIdx, int x, int y) {
        super();
        this.block = block;
        this.rotation = block.rotations[rotationIdx];
        this.rotationIdx = rotationIdx;
        this.x = x;
        this.y = y;
    }

    public void set(final BlockType block, final int rotationIdx, final int x, final int y) {
        this.block = block;
        this.rotation = block.rotations[rotationIdx];
        this.rotationIdx = rotationIdx;
        this.x = x;
        this.y = y;
    }

    public Tetromino() {
        this.move = new MoveResult(null, null, 0, 0, false);
    }
    
    public Tetromino(MoveResult move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "Tetromino [block=" + this.block + ", rotation=" + this.rotation + ", x=" + this.x + ", y=" + this.y
                + "]";
    }

    public void reset() {
        this.block = null;
        this.move.reset();
    }

    public boolean isValid() {
        return this.block != null;
    }

    public void setMove(MoveResult result) {
        this.move = result;
    }

    public void from(Tetromino o) {
        this.block = o.block;
        this.rotation = o.rotation;
        this.rotationIdx = o.rotationIdx;
        this.x = o.x;
        this.y = o.y;
    }
    
    public static final Tetromino from(final Tetromino o, final MoveResult move) {
        final Tetromino t = new Tetromino(move);
        t.set(o.block, o.rotationIdx, o.x, o.y);
        return t;
    }

}

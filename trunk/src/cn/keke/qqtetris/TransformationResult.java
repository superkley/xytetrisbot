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

import java.awt.Point;
import java.util.LinkedList;

public final class TransformationResult {
    public TransformationResult(BlockType block, int rotationIdx, int targetX, int targetY, int score,
            LinkedList<Point> cleverPoints) {
        this.score = score;
        this.x = targetX;
        this.y = targetY;
        this.rotationIdx = rotationIdx;
        this.block = block;
        this.cleverPoints = cleverPoints;
    }

    public TransformationResult(BlockType block) {
        this.block = block;
        this.x = -1;
        this.y = -1;
        this.rotationIdx = -1;
        this.score = Integer.MIN_VALUE;
    }

    public TransformationResult(TransformationResult r) {
        this.block = r.block;
        this.x = r.x;
        this.y = r.y;
        this.rotationIdx = r.rotationIdx;
        this.score = r.score;
        this.cleverPoints = r.getCleverPoints();
    }

    public TransformationResult(TransformationResult r, double score) {
        this(r);
        this.score = score;
    }

    public TransformationResult() {
    }

    public void update(int rIdx, int x, int y, double s, LinkedList<Point> cleverPoints) {
        this.score = s;
        this.rotationIdx = rIdx;
        this.x = x;
        this.y = y;
        this.cleverPoints = cleverPoints;
    }

    public BlockType block;
    private int rotationIdx;
    private int x;
    private int y;
    private double score;
    LinkedList<Point> cleverPoints;

    /**
     * @return the block
     */
    public BlockType getBlock() {
        return this.block;
    }

    /**
     * @return the rotationIdx
     */
    public int getRotationIdx() {
        return this.rotationIdx;
    }

    /**
     * @return the x
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return this.y;
    }

    /**
     * @return the score
     */
    public double getScore() {
        return this.score;
    }

    @Override
    public String toString() {
        return "TR [t=" + this.block + ", rIdx=" + this.rotationIdx + ", x=" + this.x + ", y=" + this.y
                + ", score=" + this.score + ", clever=" + this.cleverPoints + "]";
    }

    public LinkedList<Point> getCleverPoints() {
        return this.cleverPoints;
    }

    public void set(final BlockType block, final int rotationIdx, final int targetX, final int targetY,
            final int score, final LinkedList<Point> cleverPoints) {
        this.score = score;
        this.x = targetX;
        this.y = targetY;
        this.rotationIdx = rotationIdx;
        this.block = block;
        this.cleverPoints = cleverPoints;
    }

    public void set(final BlockType block) {
        this.block = block;
        this.x = -1;
        this.y = -1;
        this.rotationIdx = -1;
        this.score = Integer.MIN_VALUE;
        this.cleverPoints = null;
    }

    public void set(final TransformationResult r) {
        this.block = r.block;
        this.x = r.x;
        this.y = r.y;
        this.rotationIdx = r.rotationIdx;
        this.score = r.score;
        this.cleverPoints = r.getCleverPoints();
    }

    public void set(final TransformationResult r, final double score) {
        set(r);
        this.score = score;
    }

    public final void invalidate() {
        this.block = null;
    }

    public final boolean isValid() {
        return this.block != null;
    }
}

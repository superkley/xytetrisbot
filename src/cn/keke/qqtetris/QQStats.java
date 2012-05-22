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

public class QQStats {
    private final static StopWatch STOPPER = new StopWatch("stats");
    private CurrentData data;
    public int hash;
    public int occupied;
    public int highest;
    public int lowest;
    public int holes;
    public int[] heights;
    public boolean dead;
    private boolean calculated;

    public boolean calculate() {
        if (!calculated) {
            if (data.tetromino.isValid()) {
                this.heights = new int[QQTetris.PiecesWidth];
                final int[] stats = BoardUtils.calcDetailedBoardStats(data.board, this.heights);
                this.hash = stats[0];
                this.occupied = stats[1];
                this.highest = stats[2];
                this.lowest = stats[3];
                this.holes = stats[4];
                if (this.highest >= QQTetris.PiecesHeight - 1) {
                    this.dead = true;
                } else {
                    this.dead = false;
                }
                calculated = true;
                return true;
            }
        }
        return false;
    }

    public QQStats(CurrentData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QQStats [hash=" + this.hash + ", occupied=" + this.occupied + ", highest=" + this.highest + ", lowest="
                + this.lowest + ", holes=" + this.holes + "]";
    }

    public boolean isInDanger() {
        calculate();
        // System.out.println("lowest: " + this.lowest + ", height: " + highest);
        return this.lowest > 3 || highest > 8;
    }

    public final boolean isValid() {
        return hash == Integer.MIN_VALUE;
    }

    public void reset() {
        hash = Integer.MIN_VALUE;
        this.calculated = false;
    }

    public void setCalculate(boolean b) {
        this.calculated = false;
    }

}

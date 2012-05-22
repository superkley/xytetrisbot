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

public class StopWatch {
    private long start;
    private String name;

    public StopWatch(String name) {
        this.name = name;
    }

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public int time() {
        return (int) (System.currentTimeMillis() - this.start);
    }

    public long printTime(String taskName) {
        final long duration = System.currentTimeMillis() - this.start;
        System.out.println(this.name + " (" + formatMillis(duration) + "): " + taskName);
        return duration;
    }

    private String formatMillis(long l) {
        if (l < 1000) {
            return String.valueOf(l);
        } else if (l < 1000 * 60) {
            return (l / 1000) + "." + (l % 1000);
        } else if (l < 1000 * 60 * 60) {
            return (l / 1000 / 60) + ":" + (l / 1000 % 60) + "." + (l % 1000);
        } else if (l < 1000 * 60 * 60 * 24) {
            return (l / 1000 / 60 / 60) + ":" + (l / 1000 / 60 % 60) + ":" + (l / 1000 % 60) + "." + (l % 1000);
        } else {
            return (l / 1000 / 60 / 60 / 24) + ":" + (l / 1000 / 60 / 60 % 24) + ":" + (l / 1000 / 60 % 60) + ":" + (l / 1000 % 60) + "." + (l % 1000);
        }
    }

    public long measure() {
        return System.currentTimeMillis() - this.start;
    }
}

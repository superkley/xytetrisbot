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
package cn.keke.qqtetris.simulator;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

import cn.keke.qqtetris.StrategyType;

/**
 * 1. optimizer.jar NORMAL (optional edit csv as input) > output.txt (WAIT 3 days)
 * 2. grabber.jar output.txt > best.txt
 * 3. finetune.jar NORMAL best.txt 0 > tune.txt (WAIT 3 days)
 * 4. grabber.jar output.txt > best.txt
 * 5. edit attributes of StrategyType.Normal
 * 
 */
public class OptimizerTask implements Runnable {
    private final double[]     chromosomes;
    private final Simulator    sim;
    private final int          taskNr;
    private final int[]        scores;
    private final StrategyType strategy;
    private final Semaphore    semaphore;
    private final int          min;

    public OptimizerTask(Semaphore semaphore, StrategyType strategy, int i,
            double[] chromosomes, int[] scores, int min)
            throws InterruptedException {
        System.out
                .println("Started " + i + ": " + Arrays.toString(chromosomes));
        this.sim = new Simulator(strategy);
        this.strategy = strategy;
        this.semaphore = semaphore;
        this.semaphore.acquire();
        this.chromosomes = chromosomes;
        this.scores = scores;
        this.taskNr = i;
        this.min = min;
    }

    public void run() {
        try {
            if (scores[taskNr] == Integer.MIN_VALUE) {
                System.out.println("Failed earlier " + (taskNr + 1) + ": "
                                   + Arrays.toString(chromosomes));
            } else {
                int[] clearedLines;
                if (this.strategy == StrategyType.LONG_LIFE) {
                    int rounds = 5;
                    int[][] cs = new int[rounds][];
                    for (int i = 0; i < rounds; i++) {
                        cs[i] = this.sim.play(
                                StrategyOptimizer.MAX_STEPS_LONG_LIFE,
                                this.chromosomes);
                    }
                    int l = cs[0].length;
                    clearedLines = new int[l];
                    for (int i = 0; i < l; i++) {
                        double sum = 0;
                        for (int j = 0; j < rounds; j++) {
                            sum += cs[j][i];
                        }
                        clearedLines[i] = (int) (sum / rounds);
                    }
                } else {
                    clearedLines = this.sim.play(StrategyOptimizer.MAX_STEPS,
                            this.chromosomes);
                }
                int s = this.strategy.calculateAttributesScore(clearedLines);

                if (s < min) {
                    markFailed(taskNr, scores);
                    System.out.println("Failed " + (taskNr + 1) + ": "
                                       + Arrays.toString(chromosomes) + " -> "
                                       + this.scores[this.taskNr]);
                } else {
                    this.scores[this.taskNr] = s;
                    System.out.println("Ended " + (taskNr + 1) + ": "
                                       + Arrays.toString(chromosomes) + " -> "
                                       + this.scores[this.taskNr]);
                }
            }
        } finally {
            this.semaphore.release();
        }
    }

    private void markFailed(int taskNr, int[] scores) {
        int i = taskNr - taskNr % FineTuner.NUM_TESTS;
        int l = i + FineTuner.NUM_TESTS;

        for (; i < l; i++) {
            scores[i] = Integer.MIN_VALUE;
        }
    }

}

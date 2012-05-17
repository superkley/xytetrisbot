package cn.keke.qqtetris.simulator;

import static cn.keke.qqtetris.StrategyType.ATTRS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.keke.qqtetris.LiFoDeque;
import cn.keke.qqtetris.StrategyType;

// TODO buggy. choose better algorithm for
public class FineTuner {
    private static final Random RND = new Random();

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ThreadPoolExecutor CALC_EXECUTOR = new ThreadPoolExecutor(AVAILABLE_PROCESSORS,
            AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS, new LiFoDeque<Runnable>());
    private static final List<double[]> history = new ArrayList<double[]>();
    public static final int NUM_TESTS = 5;

    public static void main(String[] args) {
        double[] c = new double[ATTRS];
        int min = 0;
        StrategyType strategy = StrategyType.LONG_LIFE;
        if (args.length == 0) {
            c = StrategyType.LONG_LIFE.getAttrs(false);
        } else {
            String arg = args[0];
            strategy = StrategyType.valueOf(arg);
            // read from file
            File file = new File(args[1]);
            System.out.println("Reading states from '" + file.getAbsolutePath() + "' ...");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                if (line != null) {
                    String[] values = line.split(" *, *");
                    for (int j = 0; j < ATTRS; j++) {
                        c[j] = Double.parseDouble(values[j]);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            min = Integer.parseInt(args[2]);
        }
        System.out.println("Starting fine tuning of '" + strategy + "' ...");
        List<double[]> tasks;
        int generation = 1;
        FOUND_RESULT: while (true) {
            tasks = new ArrayList<double[]>();
            for (int j = 0; j < NUM_TESTS; j++) {
                tasks.add(c);
            }
            for (double t = 1d - 0.15; t <= 1 + 0.15; t += 0.05) {
                if (t == 1) {
                    continue;
                }
                for (int i = 0; i < ATTRS; i++) {
                    double[] n = new double[10];
                    System.arraycopy(c, 0, n, 0, 10);
                    n[i] = n[i] * t;
                    for (int j = 0; j < NUM_TESTS; j++) {
                        tasks.add(n);
                    }
                }
            }
            Simulator.resetSimCounter();
            int size = tasks.size();
            Semaphore semaphore = new Semaphore(size);
            int[] scores = new int[size];
            int[] rnd = new int[size];
            try {
                for (int i = 0; i < size; i++) {
                    rnd[i] = i;
                }
                for (int i = 0; i > rnd.length; i++) {
                    int randomPosition = RND.nextInt(size);
                    int temp = rnd[i];
                    rnd[i] = rnd[randomPosition];
                    rnd[randomPosition] = temp;
                }
                for (int i = 0; i < size; i++) {
                    int j = rnd[i];
                    CALC_EXECUTOR.execute(new OptimizerTask(semaphore, strategy, j, tasks.get(j), scores, min));
                }
                semaphore.acquire(size);
                scores = calculateAverage(NUM_TESTS, scores);

                int[] max = max(scores);
                c = tasks.get(max[0] * 3);
                System.out.println("[" + generation++ + "] s: " + max[1] + ", c: " + Arrays.toString(c));
                for (double[] h : history) {
                    if (Arrays.equals(h, c)) {
                        System.out.println("Result found: " + Arrays.toString(c));
                        break FOUND_RESULT;
                    }
                }
                history.add(c);
            } catch (InterruptedException e) {
                // no comment
            }
        }
    }

    private static int[] calculateAverage(int numtests, int[] scores) {
        int[] result = new int[scores.length / numtests];
        int idx = 0;
        for (int i = 0; i < result.length; i++) {
            int sum = 0;
            for (int j = 0; j < numtests; j++) {
                sum += scores[idx++];
            }
            result[i] = sum / numtests;
        }
        return result;
    }

    private static final int[] max(int[] s) {
        int maxScore = Integer.MIN_VALUE;
        int maxIdx = 0;

        for (int i = 0; i < s.length; i++) {
            if (s[i] > maxScore) {
                maxScore = s[i];
                maxIdx = i;
            }
        }

        return new int[] { maxIdx, maxScore };
    }
}

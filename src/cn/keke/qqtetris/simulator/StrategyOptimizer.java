package cn.keke.qqtetris.simulator;

import static cn.keke.qqtetris.StrategyType.ATTRS;
import static cn.keke.qqtetris.simulator.Simulator.RANDOM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.keke.qqtetris.LiFoDeque;
import cn.keke.qqtetris.StrategyType;

public class StrategyOptimizer {
    public static final String              DIR_USER             = System.getProperty("user.dir");
    public static final String              NAME_APP;
    public static final String              FILE_CSV;
    static {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement main = stack[stack.length - 1];
        String mainClassName = main.getClass().getSimpleName().toLowerCase();

        String[] appPath = StrategyOptimizer.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath().split("[/\\\\]");
        String appName = appPath[appPath.length - 1];
        if (appName.endsWith(".jar")) {
            NAME_APP = appName.substring(0, appName.lastIndexOf(".jar")) + "-"
                       + mainClassName;
        } else {
            NAME_APP = mainClassName;
        }
        FILE_CSV = DIR_USER + "/" + NAME_APP + ".csv";
    }

    private int                             generation           = 1;
    private final static int                CONTEST              = 8;
    private final static int                POPULATION           = ATTRS * CONTEST * 10;
    private final static int                WINNERS              = POPULATION / CONTEST;
    private double                          mutationRate         = 0.1;
    final double[][]                        chromosomes          = new double[POPULATION][ATTRS];
    private int[]                           scores               = new int[POPULATION];
    private StrategyType                    strategy;
    public final static int                 MAX_STEPS            = 10000;
    public final static int                 MAX_STEPS_LONG_LIFE  = 2000;
    final TreeMap<Integer, double[]>        history              = new TreeMap<Integer, double[]>();

    private static final int                AVAILABLE_PROCESSORS = Runtime.getRuntime()
                                                                         .availableProcessors();
    private static final ThreadPoolExecutor CALC_EXECUTOR        = new ThreadPoolExecutor(
                                                                         AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS, 0L,
                                                                         TimeUnit.MILLISECONDS, new LiFoDeque<Runnable>());
    private static int min = 0;
    
    public StrategyOptimizer(StrategyType strategy) {
        this.strategy = strategy;
        System.out.println("Optimizing " + strategy + "...");
    }

    private void init() {
        int i = 0;
        // read from file
        File file = new File(FILE_CSV);
        if (file.isFile() && file.canRead()) {
            System.out.println("Reading states from '" + FILE_CSV + "' ...");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = reader.readLine()) != null && i < POPULATION) {
                    String[] values = line.split(" *, *");
                    double[] d = new double[ATTRS];
                    for (int j = 0; j < ATTRS; j++) {
                        d[j] = Double.parseDouble(values[j]);
                    }
                    this.chromosomes[i++] = d;
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // generate chromosomes
        for (; i < POPULATION; i++) {
            for (int j = 0; j < ATTRS; j++) {
                this.chromosomes[i][j] = RANDOM.nextDouble() * 10 - 5;
            }
        }
        // take original chromosome from strategy;
        this.chromosomes[POPULATION - 1] = this.strategy.getAttrs(false);
    }

    private static final void shuffle(double[][] c) {
        int l = c.length;

        for (int i = l; i > 1; i--) {
            swap(c, i - 1, RANDOM.nextInt(i));
        }
    }

    private static final void swap(double[][] p, int i, int j) {
        double[] tmp = p[i];
        p[i] = p[j];
        p[j] = tmp;
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

    private void createNewGeneration() {
        int[] maxScore = max(this.scores);
        int mScore = maxScore[1];
        System.out.println("Generation " + this.generation
                           + " ended: max score=" + mScore + ", winner="
                           + Arrays.toString(this.chromosomes[maxScore[0]]));

        for (int i = 0; i < POPULATION; i++) {
            System.out.println(this.generation + "-" + (i + 1) + ": score="
                               + this.scores[i] + ", attrs="
                               + Arrays.toString(this.chromosomes[i]));
        }
        // add winner of current generation to history
        if (this.history.isEmpty()
                || mScore > this.history.firstKey().intValue()) {
            if (this.history.size() > WINNERS / 4) {
                this.history.pollFirstEntry();
            }
            this.history.put(Integer.valueOf(mScore),
                    this.chromosomes[maxScore[0]]);
        }

        // choose winners
        double[][] winners = new double[WINNERS][ATTRS];
        int i = 0, max, score, win = 0, idx = 0;

        // first add winners from highscore history
        Collection<double[]> historyAttrs = this.history.tailMap(
                Integer.valueOf(mScore)).values();
        for (double[] hAttrs : historyAttrs) {
            winners[i++] = hAttrs;
        }

        // second add some randoms
        int limit = Math.min(WINNERS, i + WINNERS / 16);
        double[] attrs;
        for (; i < limit; i++) {
            attrs = winners[i];
            for (int j = 0; j < ATTRS; j++) {
                attrs[j] = RANDOM.nextDouble() * 10 - 5;
            }
        }

        // and finally make competition
        for (; i < WINNERS; i++) {
            max = Integer.MIN_VALUE;
            for (int j = 0; j < CONTEST; j++) {
                score = this.scores[idx++];
                if (score > max) {
                    max = score;
                    win = idx - 1;
                }
            }
            winners[i] = this.chromosomes[win];
        }

        // pairup winners
        idx = 0;
        i = 0;
        shuffle(winners);
        while (i < WINNERS) {
            double[] winner1 = winners[i++];
            double[] winner2 = winners[i++];

            // generate offsprings
            for (int off = 0; off < CONTEST; off++) {
                double[] child = new double[ATTRS];

                for (int j = 0; j < ATTRS; j++) {
                    child[j] = RANDOM.nextInt(2) > 0 ? winner1[j] : winner2[j];

                    // mutation
                    boolean mutate = RANDOM.nextDouble() < this.mutationRate;
                    if (mutate) {
                        double change = RANDOM.nextDouble() * 10 - 5;
                        child[j] += change;
                    }
                }
                this.chromosomes[idx++] = child;
            }
        }
        shuffle(this.chromosomes);
        this.generation++;
    }

    public void start() {
        while (true) {
            Simulator.resetSimCounter();
            Semaphore semaphore = new Semaphore(POPULATION);
            try {
                for (int i = 0; i < POPULATION; i++) {
                    CALC_EXECUTOR
                            .execute(new OptimizerTask(semaphore,
                                    this.strategy, i, this.chromosomes[i],
                                    this.scores, min));
                }
                semaphore.acquire(POPULATION);
                createNewGeneration();
            } catch (InterruptedException e) {
                // no comment
            }
        }
    }

    public static void main(String[] args) {
        final StrategyOptimizer optimizer;
        if (args.length > 0) {
            String arg = args[0];
            StrategyType strategy = StrategyType.valueOf(arg);
            if (strategy != null) {
                optimizer = new StrategyOptimizer(strategy);
            } else {
                optimizer = new StrategyOptimizer(StrategyType.LONG_LIFE);
            }
            min = Integer.parseInt(args[1]);
        } else {
            optimizer = new StrategyOptimizer(StrategyType.LONG_LIFE);
        }
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(optimizer));
        optimizer.init();
        optimizer.start();

    }
}

package cn.keke.qqtetris;

public class QQCalculateMoveThread extends Thread {
    private final MoveCalculator calculator;
    private boolean started;
    private boolean cancelled;
    private boolean mergeMove;

    public QQCalculateMoveThread(final MoveCalculator calculator) {
        super("Calculator");
        setPriority(Thread.MAX_PRIORITY);
        this.calculator = calculator;
    }

    @Override
    public void run() {
        // TODO
        if (cancelled) {
            this.calculator.cancel();
            cancelled = false;
        }
        if (mergeMove) {
            if (CurrentData.CALCULATED.tetromino.move.isValid()) {
                asdf
            }
            mergeMove = false;
        }
        if (started) {
            final StrategyType strategy = QQTetris.getStrategy();
            this.calculator.findBestMove(CurrentData.CALCULATED.stats, strategy, strategy.getAttrs(CurrentData.CALCULATED.stats.isInDanger()));
            started = false;
        }
    }

    public void startCalculation() {
        started = true;
    }

    public void cancel() {
        cancelled = true;
    }

    public void mergeMove() {
        mergeMove = true;
    }

}

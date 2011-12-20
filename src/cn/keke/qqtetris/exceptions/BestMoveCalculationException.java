package cn.keke.qqtetris.exceptions;

public class BestMoveCalculationException extends RuntimeException {
    private static final long serialVersionUID = 3397180756851566467L;

    public BestMoveCalculationException(String msg) {
        super(msg);
    }
}

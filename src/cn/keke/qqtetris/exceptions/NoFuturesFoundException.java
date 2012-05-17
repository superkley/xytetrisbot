package cn.keke.qqtetris.exceptions;

public class NoFuturesFoundException extends RuntimeException {
    private static final long serialVersionUID = 1397180756851566467L;

    public NoFuturesFoundException(String msg) {
        super(msg);
    }
}

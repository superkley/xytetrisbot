package cn.keke.qqtetris.exceptions;

public class MoveExpiredException extends RuntimeException {
    private static final long serialVersionUID = 3397180756851566467L;

    public MoveExpiredException(String msg) {
        super(msg);
    }
}

package cn.keke.qqtetris.exceptions;

public class MoveNotPossibleException extends RuntimeException {
    private static final long serialVersionUID = 3397180756851566467L;

    public MoveNotPossibleException(String msg) {
        super(msg);
    }
}

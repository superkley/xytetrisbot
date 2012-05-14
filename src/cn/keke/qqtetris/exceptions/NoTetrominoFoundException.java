package cn.keke.qqtetris.exceptions;

public class NoTetrominoFoundException extends RuntimeException {
    private static final long serialVersionUID = 3397180756851566467L;

    public NoTetrominoFoundException(String msg) {
        super(msg);
    }
}

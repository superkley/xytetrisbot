package cn.keke.qqtetris.exceptions;

public class UnexpectedStateException extends RuntimeException {
    private static final long serialVersionUID = 3317110756851566467L;

    public UnexpectedStateException(String msg) {
        super(msg);
    }

}

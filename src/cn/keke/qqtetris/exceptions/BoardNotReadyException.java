package cn.keke.qqtetris.exceptions;

public class BoardNotReadyException extends RuntimeException {
    private static final long serialVersionUID = 3317180756851566467L;

    public BoardNotReadyException(String msg) {
        super(msg);
    }

}

package cn.keke.qqtetris.exceptions;

public class UnexpectedBoardChangeException extends RuntimeException {
    private static final long serialVersionUID = 3317180756851566467L;

    public UnexpectedBoardChangeException(String msg) {
        super(msg);
    }

}

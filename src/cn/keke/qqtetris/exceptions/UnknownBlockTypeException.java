package cn.keke.qqtetris.exceptions;

public class UnknownBlockTypeException extends RuntimeException {
    private static final long serialVersionUID = -6542646536657461809L;

    public UnknownBlockTypeException(String msg) {
        super(msg);
    }
}

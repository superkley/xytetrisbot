package cn.keke.qqtetris.exceptions;

public class UnknownBoardStateException extends RuntimeException {

    private static final long serialVersionUID = -7229175231988491004L;

    public UnknownBoardStateException(String msg) {
        super(msg);
    }
}

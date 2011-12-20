package cn.keke.qqtetris.exceptions;

public class MissingTetrisWindowException extends RuntimeException {
    private static final long serialVersionUID = 219662921125492824L;

    public MissingTetrisWindowException(String msg) {
        super(msg);
    }
}

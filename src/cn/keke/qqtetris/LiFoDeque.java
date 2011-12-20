package cn.keke.qqtetris;

import java.util.concurrent.LinkedBlockingDeque;

public class LiFoDeque<E> extends LinkedBlockingDeque<E> {
    private static final long serialVersionUID = 3656401110581784290L;

    @Override
    public boolean offer(E e) {
        return offerFirst(e);
    }
}

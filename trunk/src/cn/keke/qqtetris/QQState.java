package cn.keke.qqtetris;

import java.awt.Color;

public enum QQState {
    WAITING(Color.red),
    WARNING(Color.orange),
    PLAYING(Color.green),
    STOPPED(Color.lightGray);
    public final Color color;

    QQState(Color c) {
        this.color = c;
    }
}

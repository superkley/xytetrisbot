package cn.keke.qqtetris;

import java.awt.Color;

public enum QQLevel {
    HARD(Color.red),
    MEDIUM(Color.orange),
    EASY(Color.green);
    public final Color color;

    QQLevel(Color c) {
        this.color = c;
    }
}

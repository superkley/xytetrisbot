package cn.keke.qqtetris;
import java.awt.event.KeyEvent;

public enum MoveType {
    LEFT(KeyEvent.VK_LEFT),
    RIGHT(KeyEvent.VK_RIGHT),
    CLOCKWISE(KeyEvent.VK_UP),
    DOWN(KeyEvent.VK_DOWN),
    FALL(KeyEvent.VK_SPACE),
    SKIP_ITEM(KeyEvent.VK_S),
    PERSON_ME(KeyEvent.VK_1);
    
    public final int KEY;

    MoveType(int evt) {
        this.KEY = evt;
    }
}

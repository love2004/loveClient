package me.love2004.porn.event.events;

import me.love2004.porn.event.EventStage;

public class KeyEvent
        extends EventStage {
    public boolean info;
    public boolean pressed;

    public KeyEvent(int stage, boolean info, boolean pressed) {
        super(stage);
        this.info = info;
        this.pressed = pressed;
    }
}


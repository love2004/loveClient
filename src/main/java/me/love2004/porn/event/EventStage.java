package me.love2004.porn.event;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.lang.reflect.InvocationTargetException;

public class EventStage
        extends Event {
    private int stage;
    private boolean cancelled;

    public EventStage() {
    }

    public EventStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return this.stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}


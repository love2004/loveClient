package me.love2004.porn.event.events;

import me.love2004.porn.event.EventStage;
import me.love2004.porn.setting.Setting;

public class ValueChangeEvent
        extends EventStage {
    public Setting setting;
    public Object value;

    public ValueChangeEvent(Setting setting, Object value) {
        this.setting = setting;
        this.value = value;
    }
}


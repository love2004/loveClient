package me.love2004.porn.modules.player;

import me.love2004.porn.modules.Module;

public class TrueDurability
        extends Module {
    private static TrueDurability instance;

    public TrueDurability() {
        super("TrueDurability", "Shows True Durability of items", Module.Category.PLAYER, false, false, false);
        instance = this;
    }

    public static TrueDurability getInstance() {
        if (instance == null) {
            instance = new TrueDurability();
        }
        return instance;
    }
}


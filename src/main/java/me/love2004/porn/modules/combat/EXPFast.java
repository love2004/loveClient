package me.love2004.porn.modules.combat;

import me.love2004.porn.modules.Module;
import net.minecraft.item.ItemExpBottle;

import static net.minecraft.client.Minecraft.getMinecraft;

public class EXPFast
extends Module {
    public EXPFast() {
        super("EXPFast", "XP fast zoom",  Module.Category.COMBAT, true,false,false);
    }

    @Override
    public void onUpdate() {
        if (getMinecraft().player.inventory.getCurrentItem().getItem() instanceof ItemExpBottle) {
            getMinecraft().rightClickDelayTimer = 0;
        }
    }
}


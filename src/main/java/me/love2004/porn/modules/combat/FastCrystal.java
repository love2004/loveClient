package me.love2004.porn.modules.combat;

import me.love2004.porn.modules.Module;
import net.minecraft.init.Items;

public class FastCrystal extends Module {
    public FastCrystal(){
        super("FastCrystal","FastCrystal",Category.COMBAT,true,false,false);
    }

    @Override
    public void onUpdate() {
        if (FastCrystal.mc.player != null && (FastCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || FastCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)) {
            FastCrystal.mc.rightClickDelayTimer = 0;
        }
    }

}

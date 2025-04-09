package me.love2004.porn.modules.combat;

import me.love2004.porn.event.events.PacketEvent;
import me.love2004.porn.modules.Module;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class noDesync extends Module
{
    public noDesync() {
        super("noDesync","", Category.COMBAT,true,false,false);
    }
    
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                try {
                    for (final Entity e : mc.world.loadedEntityList) {
                        if (e instanceof EntityEnderCrystal && e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0) {
                            e.setDead();
                        }
                    }
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}

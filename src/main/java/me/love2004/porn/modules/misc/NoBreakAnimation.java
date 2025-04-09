
package me.love2004.porn.modules.misc;

import me.love2004.porn.event.events.EventSendPacket;
import me.love2004.porn.event.events.EventTarget;
import me.love2004.porn.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class NoBreakAnimation
extends Module {
    private boolean isMining = false;
    private BlockPos lastPos = null;
    private EnumFacing lastFacing = null;

    public NoBreakAnimation() {
        super("NoBreakAnimation", "Prevents block break animation server side", Module.Category.PLAYER, true,false,false);
    }

    @EventTarget
    public void onSend(EventSendPacket event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging cPacketPlayerDigging = (CPacketPlayerDigging)event.getPacket();
            for (Entity entity : NoBreakAnimation.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(cPacketPlayerDigging.getPosition()))) {
                if (entity instanceof EntityEnderCrystal) {
                    this.resetMining();
                    return;
                }
                if (!(entity instanceof EntityLivingBase)) continue;
                this.resetMining();
                return;
            }
            if (cPacketPlayerDigging.getAction().equals((Object)CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                this.isMining = true;
                this.setMiningInfo(cPacketPlayerDigging.getPosition(), cPacketPlayerDigging.getFacing());
            }
            if (cPacketPlayerDigging.getAction().equals((Object)CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                this.resetMining();
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!NoBreakAnimation.mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.resetMining();
            return;
        }
        if (this.isMining && this.lastPos != null && this.lastFacing != null) {
            NoBreakAnimation.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
        }
    }

    private void setMiningInfo(BlockPos lastPos, EnumFacing lastFacing) {
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public void resetMining() {
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
    }
}


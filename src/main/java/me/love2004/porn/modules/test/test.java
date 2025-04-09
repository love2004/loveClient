package me.love2004.porn.modules.test;

import me.love2004.porn.event.events.MoveEvent;
import me.love2004.porn.event.events.PushEvent;
import me.love2004.porn.modules.Module;
import me.love2004.porn.modules.movement.Phase;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.util.BlockUtil;
import me.love2004.porn.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class test extends Module {
    public test(){
        super("test","",Category.TEST,true,false,false);
    }


    static Phase phase = new Phase();
    Timer.Timeridk timer = new Timer.Timeridk();
    public final Setting<Boolean> packet = this.register(new Setting<>("Packet", Boolean.FALSE));
    public final Setting<Float> delay = this.register(new Setting<>("Delay", 1.0f));

    @Override
    public void onUpdate() {
        BlockPos pos = mc.player.getPosition().add(0.5f,-1,0.5f);

        if (mc.world.getBlockState(pos.down()).getBlock() == Blocks.AIR && BlockUtil.isPositionPlaceable(pos.down(), false) == 3) {
            BlockUtil.placeBlock(pos.down(), EnumHand.MAIN_HAND, false, this.packet.getValue(), false);
        }

        if (timer.hasReached(longValue(delay.getValue()))) {

            mc.player.jump();

            if (mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) {

                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1, mc.player.posZ, false));
                mc.player.setPosition(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
            }

        }
    }

    public long longValue(float value) {
        return (long)value;
    }
}

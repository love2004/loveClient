package me.love2004.porn.modules.test;

import me.love2004.porn.modules.Module;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.util.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BurrowTest extends Module {

    public BurrowTest(){
        super("Burrow","",Category.TEST,true,false,false);
    }

    public Setting<blocks> block = register(new Setting<>("Block",blocks.Obby));
    public Setting<Boolean> rotate = register(new Setting<>("Rorate",false));
    public Setting<Integer> offset = register(new Setting<>("Offset",7,0,20));

    public enum blocks{
        Obby,EChest
    }

    private BlockPos originalPos;
    private Vec3d centeredBlock = Vec3d.ZERO;

    @Override
    public void onEnable() {
        originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (intersects(this.originalPos)) {
            toggle();
            return;
        }
        if (mc.player.onGround) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        centeredBlock = getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    @Override
    public void onUpdate() {
        if (centeredBlock != Vec3d.ZERO && mc.player.onGround) {
            double xDeviation = Math.abs(centeredBlock.x - mc.player.posX);
            double zDeviation = Math.abs(centeredBlock.z - mc.player.posZ);

            if (xDeviation <= 0.1 && zDeviation <= 0.1){
                centeredBlock = Vec3d.ZERO;
            } else {
                double newX;
                double newZ;

                if (mc.player.posX > Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) + 0.5;
                } else if (mc.player.posX < Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) - 0.5;
                } else {
                    newX = mc.player.posX;
                }

                if (mc.player.posZ > Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) + 0.5;
                } else if (mc.player.posZ < Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) - 0.5;
                } else {
                    newZ = mc.player.posZ;
                }

                mc.player.connection.sendPacket(new CPacketPlayer.Position(newX, mc.player.posY, newZ, true));
                mc.player.setPosition(newX, mc.player.posY, newZ);
            }
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
        BlockUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));
        toggle();
    }


    private boolean intersects(final BlockPos pos) {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity.equals(mc.player)) continue;
            if (entity instanceof EntityItem) continue;
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) return true;
        }
        return false;
    }

    private Vec3d getCenter(double playerX, double playerY, double playerZ) {

        double newX = Math.floor(playerX) + 0.5;
        double newY = Math.floor(playerY);
        double newZ = Math.floor(playerZ) + 0.5;

        return new Vec3d(newX, newY, newZ);
    }
}
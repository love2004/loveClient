//package me.love2004.porn.modules.combat;
//
//import me.love2004.porn.modules.Module;
//import me.love2004.porn.setting.Setting;
//import me.love2004.porn.util.BlockUtil;
//import me.love2004.porn.util.Timer;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockAir;
//import net.minecraft.block.BlockLiquid;
//import net.minecraft.block.BlockObsidian;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.item.EntityItem;
//import net.minecraft.entity.item.EntityXPOrb;
//import net.minecraft.item.ItemBlock;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.play.client.CPacketEntityAction;
//import net.minecraft.network.play.client.CPacketPlayer;
//import net.minecraft.network.play.client.CPacketPlayerDigging;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.GameType;
//
//import static net.minecraft.client.Minecraft.getMinecraft;
//
//public class Burrow extends Module
//{
//
//    public Burrow(){
//        super("Burrow","",Category.COMBAT,true,false,false);
//
//        delay = this.register(new Setting<>("Delay", 1.0f));
//        noGlitchBlocks = this.register(new Setting<>("NoGlitchBlocks", Boolean.FALSE));
//        rotate = this.register(new Setting<>("Rotate", Boolean.FALSE));
//        timer = new Timer.Timeridk();
//    }
//
//    private final Setting<Float> delay;
//    private final Setting<Boolean> noGlitchBlocks;
//    private final Setting<Boolean> rotate;
//    Timer.Timeridk timer;
//    int lastHotbarSlot;
//    int playerHotbarSlot;
//    boolean isSneaking;
//
//
//
//    public void onEnable() {
//        if (mc.player == null) {
//            disable();
//            return;
//        }
//        playerHotbarSlot = mc.player.inventory.currentItem;
//        lastHotbarSlot = -1;
//        mc.player.jump();
//        timer.reset();
//    }
//
//    public void onDisable() {
//        if (mc.player == null) {
//            return;
//        }
//        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
//            mc.player.inventory.currentItem = playerHotbarSlot;
//        }
//        if (isSneaking) {
//            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
//            isSneaking = false;
//        }
//        playerHotbarSlot = -1;
//        lastHotbarSlot = -1;
//    }
//
//    @Override
//    public void onUpdate() {
//        if (timer.hasReached(longValue(delay.getValue()))) {
//            final BlockPos offsetPos = new BlockPos(0, -1, 0);
//            final BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
//            if (placeBlock(targetPos)) {
//                if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
//                    mc.player.inventory.currentItem = playerHotbarSlot;
//                    lastHotbarSlot = playerHotbarSlot;
//                }
//                if (isSneaking) {
//                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
//                    isSneaking = false;
//                }
//                mc.player.onGround = false;
//                mc.player.motionY = 20.0;
//            }
//            disable();
//        }
//    }
//
//    public static EnumFacing getPlaceableSide(final BlockPos pos) {
//        for (final EnumFacing side : EnumFacing.values()) {
//            final BlockPos neighbour = pos.offset(side);
//            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
//                final IBlockState blockState = mc.world.getBlockState(neighbour);
//                if (!blockState.getMaterial().isReplaceable()) {
//                    return side;
//                }
//            }
//        }
//        return null;
//    }
//
//    public long longValue(final float value) {
//        return (long)value;
//    }
//
//    private boolean placeBlock(final BlockPos pos) {
//        final Block block = mc.world.getBlockState(pos).getBlock();
//        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
//            return false;
//        }
//        for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
//            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
//                return false;
//            }
//        }
//        final EnumFacing side = getPlaceableSide(pos);
//        if (side == null) {
//            return false;
//        }
//        final BlockPos neighbour = pos.offset(side);
//        final EnumFacing opposite = side.getOpposite();
//        if (!canBeClicked(neighbour)) {
//            return false;
//        }
//        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
//        final Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
//        final int obiSlot = findObiInHotbar();
//        if (obiSlot == -1) {
//            disable();
//        }
//        if (lastHotbarSlot != obiSlot) {
//            mc.player.inventory.currentItem = obiSlot;
//            lastHotbarSlot = obiSlot;
//        }
//        if ((!isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
//            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
//            isSneaking = true;
//        }
//        if (rotate.getValue()) {
//            faceVectorPacketInstant(hitVec);
//        }
//        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
//        mc.player.swingArm(EnumHand.MAIN_HAND);
//        mc.rightClickDelayTimer = 4;
//        if (noGlitchBlocks.getValue() && !mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)) {
//            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite));
//        }
//        return true;
//    }
//
//    private static float[] getLegitRotations(final Vec3d vec) {
//        final Vec3d eyesPos = getEyesPos();
//        final double diffX = vec.x - eyesPos.x;
//        final double diffY = vec.y - eyesPos.y;
//        final double diffZ = vec.z - eyesPos.z;
//        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
//        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
//        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
//        return new float[] { getMinecraft().player.rotationYaw + MathHelper.wrapDegrees(yaw - getMinecraft().player.rotationYaw), getMinecraft().player.rotationPitch + MathHelper.wrapDegrees(pitch - getMinecraft().player.rotationPitch) };
//    }
//
//    private static Vec3d getEyesPos() {
//        return new Vec3d(getMinecraft().player.posX, getMinecraft().player.posY + getMinecraft().player.getEyeHeight(), getMinecraft().player.posZ);
//    }
//
//    public static void faceVectorPacketInstant(final Vec3d vec) {
//        final float[] rotations = getLegitRotations(vec);
//        getMinecraft().player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], getMinecraft().player.onGround));
//    }
//
//    public static boolean canBeClicked(final BlockPos pos) {
//        return getBlock(pos).canCollideCheck(getState(pos), false);
//    }
//
//    private static Block getBlock(final BlockPos pos) {
//        return getState(pos).getBlock();
//    }
//
//    private static IBlockState getState(final BlockPos pos) {
//        return getMinecraft().world.getBlockState(pos);
//    }
//
//    public int findObiInHotbar() {
//        int slot = -1;
//        for (int i = 0; i < 9; ++i) {
//            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
//            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
//                final Block block = ((ItemBlock)stack.getItem()).getBlock();
//                if (block instanceof BlockObsidian) {
//                    slot = i;
//                    break;
//                }
//            }
//        }
//        return slot;
//    }
//}

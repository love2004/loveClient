package me.love2004.porn.modules.test;

import me.love2004.porn.event.events.PacketEvent;
import me.love2004.porn.manager.FriendManager;
import me.love2004.porn.modules.Module;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.util.RenderUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AutoCrystalTestTwo extends Module
{
    private final Setting<Integer> attackSpeed;
    private final Setting<Integer> placeSpeed;
    private final Setting<Integer> placeRange;
    private final Setting<Integer> attackRange;
    private final Setting<Integer> minDamage;
    private final Setting<Integer> enemyRange;
    private final Setting<Boolean> multiPlace;
    private final Setting<Boolean> onlyOwn;
    private final Setting<Integer> facePlaceHealth;
    private final Setting<Boolean> itemSwitch;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> rainbowSpeed;
    private final ArrayList<BlockPos> ownCrystals;
    private BlockPos render;
    private long placeSystemTime;
    private long breakSystemTime;
    private long multiPlaceSystemTime;
    private long antiStuckSystemTime;
    private boolean togglePitch;
    private boolean switchCooldown;
    private boolean isSpoofingAngles;
    private double yaw;
    private double pitch;
    private float hue;
    
    public AutoCrystalTestTwo() {
        super("AutoCrystalTestTwo", "category", Category.TEST,true,false,false);
        attackSpeed = register(new Setting<>("AttackSpeed", 17, 0, 20));
        placeSpeed = register(new Setting<>("PlaceSpeed", 18, 0, 20));
        placeRange = register(new Setting<>("PlaceRange", 5, 1, 10));
        attackRange = register(new Setting<>("AttackRange", 4, 1, 10));
        minDamage = register(new Setting<>("MinDamage", 4, 0, 16));
        enemyRange = register(new Setting<>("EnemyRange", 9, 1, 20));
        multiPlace = register(new Setting<>("MultiPlace", false));
        onlyOwn = register(new Setting<>("OnlyOwn", true));
        facePlaceHealth = register(new Setting<>("FacePlaceHealth", 7, 0, 16));
        itemSwitch = register(new Setting<>("ItemSwitch", true));
        red = register(new Setting<>("Red", 255, 0, 255));
        green = register(new Setting<>("Green", 20, 0, 255));
        blue = register(new Setting<>("Blue", 20, 0, 255));
        alpha = register(new Setting<>("Alpha", 100, 0, 255));
        rainbowSpeed = register(new Setting<>("RainbowSpeed", 5, 0, 10));
        ownCrystals = new ArrayList<>();
        placeSystemTime = -1L;
        breakSystemTime = -1L;
        multiPlaceSystemTime = -1L;
        antiStuckSystemTime = -1L;
        togglePitch = false;
        switchCooldown = false;
        hue = 0.0f;
    }
    
    public float calculateDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final double size = entity.getDistance(posX, posY, posZ) / 12.0;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double v = (1.0 - size) * blockDensity;
        final float damage = (float)(int)((v * v + v) / 2.0 * 7.0 * 12.0 + 1.0);
        double finals = 1.0;
        if (entity instanceof EntityLivingBase) {
            finals = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion(mc.world, mc.player, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finals;
    }
    
    public float getBlastReduction(final EntityLivingBase entity, final float damage, final Explosion explosion) {
        float d = damage;
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer) entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            d = CombatRules.getDamageAfterAbsorb(d, (float)ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            final int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            final float f = MathHelper.clamp((float)k, 0.0f, 20.0f);
            d *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(Objects.requireNonNull(Potion.getPotionById(11)))) {
                d -= d / 4.0f;
            }
        }
        else {
            d = CombatRules.getDamageAfterAbsorb(d, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        }
        return d;
    }
    
    private float getDamageMultiplied(final float damage) {
        final int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }
    
    public boolean canBlockBeSeen(final BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) == null;
    }
    
    @SubscribeEvent
    public void onPacket(final PacketEvent.Send event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer && this.isSpoofingAngles) {
            ((CPacketPlayer)event.getPacket()).yaw = (float) yaw;
            ((CPacketPlayer)event.getPacket()).pitch = (float) pitch;
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        final EntityEnderCrystal crystal = (EntityEnderCrystal)mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);
        if (crystal != null && mc.player.getDistance(crystal) <= attackRange.getValue()) {
            if (System.nanoTime() / 1000000L - breakSystemTime >= 420 - attackSpeed.getValue() * 20 && (!onlyOwn.getValue() || ownCrystals.contains(render))) {
                lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
                mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                ownCrystals.remove(render);
                breakSystemTime = System.nanoTime() / 1000000L;
            }
            if (multiPlace.getValue()) {
                if (System.nanoTime() / 1000000L - multiPlaceSystemTime >= 20L * placeSpeed.getValue() && System.nanoTime() / 1000000L - antiStuckSystemTime <= 400 + (400 - attackSpeed.getValue() * 20)) {
                    multiPlaceSystemTime = System.nanoTime() / 1000000L;
                    return;
                }
            }
            else if (System.nanoTime() / 1000000L - antiStuckSystemTime <= 400 + (400 - attackSpeed.getValue() * 20)) {
                return;
            }
        }
        else {
            resetRotation();
        }
        int crystalSlot = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        }
        else if (crystalSlot == -1) {
            return;
        }
        BlockPos finalPos = null;
        final List<BlockPos> blocks = findCrystalBlocks();
        final ArrayList<Entity> entities = mc.world.playerEntities.stream().filter(entityPlayer -> !FriendManager.isFriend(entityPlayer.getName())).collect(Collectors.toCollection(ArrayList::new));
        double damage = 0.5;
        for (final Entity entity2 : entities) {
            if (entity2 != mc.player && ((EntityLivingBase)entity2).getHealth() > 0.0f && mc.player.getDistanceSq(entity2) <= enemyRange.getValue() * enemyRange.getValue()) {
                for (final BlockPos blockPos : blocks) {
                    if (canBlockBeSeen(blockPos) || mc.player.getDistanceSq(blockPos) <= 25.0) {
                        final double b = entity2.getDistanceSq(blockPos);
                        if (b > 56.2) {
                            continue;
                        }
                        final double d = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, entity2);
                        if ((d < minDamage.getValue() && ((EntityLivingBase)entity2).getHealth() + ((EntityLivingBase)entity2).getAbsorptionAmount() > facePlaceHealth.getValue()) || d <= damage) {
                            continue;
                        }
                        final double self = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, mc.player);
                        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() - self <= 7.0 || self > d) {
                            continue;
                        }
                        damage = d;
                        finalPos = blockPos;
                    }
                }
            }
        }
        if (damage == 0.5) {
            render = null;
            resetRotation();
            return;
        }
        render = finalPos;
        if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
            if (itemSwitch.getValue()) {
                mc.player.inventory.currentItem = crystalSlot;
                resetRotation();
                switchCooldown = true;
            }
            return;
        }
        lookAtPacket(finalPos.getX() + 0.5, finalPos.getY() - 0.5, finalPos.getZ() + 0.5, mc.player);
        final RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(finalPos.getX() + 0.5, finalPos.getY() - 0.5, finalPos.getZ() + 0.5));
        EnumFacing f;
        if (result != null && result.sideHit != null) {
            f = result.sideHit;
        }
        else {
            f = EnumFacing.UP;
        }
        if (switchCooldown) {
            switchCooldown = false;
            return;
        }
        if (System.nanoTime() / 1000000L - placeSystemTime >= placeSpeed.getValue() * 5L) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(finalPos, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            ownCrystals.add(finalPos);
            antiStuckSystemTime = System.nanoTime() / 1000000L;
            placeSystemTime = System.nanoTime() / 1000000L;
        }
        if (isSpoofingAngles) {
            if (togglePitch) {
                final EntityPlayerSP player2;
                final EntityPlayerSP player = player2 = mc.player;
                player2.rotationPitch += 4.0E-4f;
                togglePitch = false;
            }
            else {
                final EntityPlayerSP player3;
                final EntityPlayerSP player = player3 = mc.player;
                player3.rotationPitch -= 4.0E-4f;
                togglePitch = true;
            }
        }
    }
    
    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (render != null) {
            hue += rainbowSpeed.getValue() / 1000.0f;
            final int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            final int r = rgb >> 16 & 0xFF;
            final int g = rgb >> 8 & 0xFF;
            final int b = rgb & 0xFF;
            RenderUtil.drawBoxFromBlockpos(render, red.getValue() / 255.0f, green.getValue() / 255.0f, blue.getValue() / 255.0f, alpha.getValue() / 255.0f);
        }
    }
    
    private void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float)v[0], (float)v[1]);
    }
    
    private double[] calculateLookAt(final double px, final double py, final double pz, final EntityPlayer me) {
        double dirX = me.posX - px;
        double dirY = me.posY - py;
        double dirZ = me.posZ - pz;
        final double len = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX /= len;
        dirY /= len;
        dirZ /= len;
        double pitch = Math.asin(dirY);
        double yaw = Math.atan2(dirZ, dirX);
        pitch = pitch * 180.0 / 3.141592653589793;
        yaw = yaw * 180.0 / 3.141592653589793;
        yaw += 90.0;
        return new double[] { yaw, pitch };
    }
    
    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
    
    private List<BlockPos> findCrystalBlocks() {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), (float)placeRange.getValue()).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }
    
    private boolean canPlaceCrystal(final Object o) {
        final BlockPos blockPos = (BlockPos)o;
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }
    
    private List<BlockPos> getSphere(final BlockPos loc, final float r) {
        final List<BlockPos> blocks = new ArrayList<>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                for (int y = cy - (int)r; y < cy + r; ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (cy - y) * (cy - y);
                    if (dist < r * r) {
                        final BlockPos l = new BlockPos(x, y, z);
                        blocks.add(l);
                    }
                }
            }
        }
        return blocks;
    }
    
    private void setYawAndPitch(final float yaw1, final float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }
    
    private void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }
    
    @Override
    public void onDisable() {
        render = null;
        resetRotation();
    }
}

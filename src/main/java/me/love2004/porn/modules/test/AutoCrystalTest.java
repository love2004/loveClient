package me.love2004.porn.modules.test;

import me.love2004.porn.event.events.ClientEvent;
import me.love2004.porn.event.events.PacketEvent;
import me.love2004.porn.event.events.Render3DEvent;
import me.love2004.porn.event.events.UpdateWalkingPlayerEvent;
import me.love2004.porn.loveClient;
import me.love2004.porn.modules.Module;
import me.love2004.porn.modules.client.ClickGui;
import me.love2004.porn.modules.combat.AutoTrap;
import me.love2004.porn.modules.combat.Surround;
import me.love2004.porn.setting.Bind;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.util.Timer;
import me.love2004.porn.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class AutoCrystalTest extends Module
{
    private final Setting<Settings> setting;
    public Setting<Raytrace> raytrace;
    public Setting<Boolean> place;
    public Setting<Integer> placeDelay;
    public Setting<Float> placeRange;
    public Setting<Float> minDamage;
    public Setting<Integer> wasteAmount;
    public Setting<Boolean> wasteMinDmgCount;
    public Setting<Float> facePlace;
    public Setting<Float> placetrace;
    public Setting<Boolean> antiSurround;
    public Setting<Boolean> limitFacePlace;
    public Setting<Boolean> oneDot15;
    public Setting<Boolean> doublePop;
    public Setting<Float> popDamage;
    public Setting<Integer> popTime;
    public Setting<Boolean> explode;
    public Setting<Switch> switchMode;
    public Setting<Integer> breakDelay;
    public Setting<Float> breakRange;
    public Setting<Integer> packets;
    public Setting<Float> breaktrace;
    public Setting<Boolean> manual;
    public Setting<Boolean> manualMinDmg;
    public Setting<Integer> manualBreak;
    public Setting<Boolean> sync;
    public Setting<Boolean> instant;
    public Setting<Boolean> render;
    public Setting<Boolean> colorSync;
    public Setting<Boolean> box;
    public Setting<Boolean> outline;
    public Setting<Boolean> text;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> boxAlpha;
    private final Setting<Float> lineWidth;
    public Setting<Boolean> customOutline;
    private final Setting<Integer> cRed;
    private final Setting<Integer> cGreen;
    private final Setting<Integer> cBlue;
    private final Setting<Integer> cAlpha;
    public Setting<Float> range;
    public Setting<Target> targetMode;
    public Setting<Integer> minArmor;
    private final Setting<Integer> switchCooldown;
    public Setting<AutoSwitch> autoSwitch;
    public Setting<Bind> switchBind;
    public Setting<Boolean> offhandSwitch;
    public Setting<Boolean> switchBack;
    public Setting<Boolean> lethalSwitch;
    public Setting<Boolean> mineSwitch;
    public Setting<Rotate> rotate;
    public Setting<Boolean> suicide;
    public Setting<Boolean> webAttack;
    public Setting<Boolean> fullCalc;
    public Setting<Boolean> extraSelfCalc;
    public Setting<Logic> logic;
    public Setting<Boolean> doubleMap;
    public Setting<DamageSync> damageSync;
    public Setting<Integer> damageSyncTime;
    public Setting<Float> dropOff;
    public Setting<Integer> confirm;
    public Setting<Boolean> syncedFeetPlace;
    public Setting<Boolean> fullSync;
    public Setting<Boolean> syncCount;
    public Setting<Boolean> hyperSync;
    public Setting<Boolean> gigaSync;
    public Setting<Boolean> syncySync;
    public Setting<Boolean> enormousSync;
    public Setting<Boolean> placeSwing;
    public Setting<Boolean> exactHand;
    private final Setting<Integer> eventMode;
    private final Setting<ThreadMode> threadMode;
    public Setting<Integer> threadDelay;
    public Setting<Integer> syncThreads;
    private Queue<Entity> attackList;
    private Map<Entity, Float> crystalMap;
    private final Timer switchTimer;
    private final Timer manualTimer;
    private final Timer breakTimer;
    private final Timer placeTimer;
    private final Timer syncTimer;
    public static EntityPlayer target;
    private Entity efficientTarget;
    private double currentDamage;
    private double lastDamage;
    private boolean didRotation;
    private boolean switching;
    private BlockPos placePos;
    private BlockPos renderPos;
    private boolean mainHand;
    private boolean rotating;
    private boolean offHand;
    private int crystalCount;
    private int minDmgCount;
    private int lastSlot;
    private float yaw;
    private float pitch;
    private BlockPos webPos;
    private final Timer renderTimer;
    private BlockPos lastPos;
    public static Set<BlockPos> placedPos;
    public static Set<BlockPos> brokenPos;
    private boolean posConfirmed;
    private boolean foundDoublePop;
    private final AtomicBoolean shouldInterrupt;
    private ScheduledExecutorService executor;
    private final Timer syncroTimer;
    private Thread thread;
    private EntityPlayer currentSyncTarget;
    private BlockPos syncedPlayerPos;
    private BlockPos syncedCrystalPos;
    private static AutoCrystalTest instance;
    private final Map<EntityPlayer, Timer> totemPops;
    
    public AutoCrystalTest() {
        super("AutoCrystal", "Best CA on the market", Category.TEST, true, false, false);
        this.setting = (Setting<Settings>)this.register(new Setting("Settings", Settings.PLACE));
        this.raytrace = (Setting<Raytrace>)this.register(new Setting("Raytrace", Raytrace.NONE, v -> this.setting.getValue() == Settings.MISC));
        this.place = (Setting<Boolean>)this.register(new Setting("Place", true, v -> this.setting.getValue() == Settings.PLACE));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("PlaceDelay", 0, 0, 1000, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 6.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", 4.0f, 0.1f, 20.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteAmount = (Setting<Integer>)this.register(new Setting("WasteAmount", 1, 1, 5, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteMinDmgCount = (Setting<Boolean>)this.register(new Setting("CountMinDmg", true, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.facePlace = (Setting<Float>)this.register(new Setting("FacePlace", 8.0f, 0.1f, 20.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.placetrace = (Setting<Float>)this.register(new Setting("Placetrace", 6.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK));
        this.antiSurround = (Setting<Boolean>)this.register(new Setting("AntiSurround", false, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.limitFacePlace = (Setting<Boolean>)this.register(new Setting("LimitFacePlace", true, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.oneDot15 = (Setting<Boolean>)this.register(new Setting("1.15", false, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.doublePop = (Setting<Boolean>)this.register(new Setting("AntiTotem", false, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.popDamage = (Setting<Float>)this.register(new Setting("PopDamage", 4.0f, 0.0f, 6.0f, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.doublePop.getValue()));
        this.popTime = (Setting<Integer>)this.register(new Setting("PopTime", 500, 0, 1000, v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.doublePop.getValue()));
        this.explode = (Setting<Boolean>)this.register(new Setting("Break", true, v -> this.setting.getValue() == Settings.BREAK));
        this.switchMode = (Setting<Switch>)this.register(new Setting("Attack", Switch.BREAKSLOT, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("BreakDelay", 0, 0, 1000, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", 6.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.packets = (Setting<Integer>)this.register(new Setting("Packets", 1, 1, 6, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breaktrace = (Setting<Float>)this.register(new Setting("Breaktrace", 6.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE));
        this.manual = (Setting<Boolean>)this.register(new Setting("Manual", false, v -> this.setting.getValue() == Settings.BREAK));
        this.manualMinDmg = (Setting<Boolean>)this.register(new Setting("ManMinDmg", false, v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.manualBreak = (Setting<Integer>)this.register(new Setting("ManualDelay", 500, 0, 1000, v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.sync = (Setting<Boolean>)this.register(new Setting("Sync", true, v -> this.setting.getValue() == Settings.BREAK && (this.explode.getValue() || this.manual.getValue())));
        this.instant = (Setting<Boolean>)this.register(new Setting("Predict", false, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue()));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", true, v -> this.setting.getValue() == Settings.RENDER));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false, v -> this.setting.getValue() == Settings.RENDER));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", true, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.text = (Setting<Boolean>)this.register(new Setting("Text", false, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 125, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.5f, 0.1f, 5.0f, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", false, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.cRed = (Setting<Integer>)this.register(new Setting("OL-Red", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cGreen = (Setting<Integer>)this.register(new Setting("OL-Green", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cBlue = (Setting<Integer>)this.register(new Setting("OL-Blue", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", 255, 0, 255, v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
        this.range = (Setting<Float>)this.register(new Setting("Range", 12.0f, 0.1f, 20.0f, v -> this.setting.getValue() == Settings.MISC));
        this.targetMode = (Setting<Target>)this.register(new Setting("Target", Target.CLOSEST, v -> this.setting.getValue() == Settings.MISC));
        this.minArmor = (Setting<Integer>)this.register(new Setting("MinArmor", 0, 0, 125, v -> this.setting.getValue() == Settings.MISC));
        this.switchCooldown = (Setting<Integer>)this.register(new Setting("Cooldown", 500, 0, 1000, v -> this.setting.getValue() == Settings.MISC));
        this.autoSwitch = (Setting<AutoSwitch>)this.register(new Setting("Switch", AutoSwitch.TOGGLE, v -> this.setting.getValue() == Settings.MISC));
        this.switchBind = (Setting<Bind>)this.register(new Setting("SwitchBind", new Bind(-1), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE));
        this.offhandSwitch = (Setting<Boolean>)this.register(new Setting("Offhand", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.switchBack = (Setting<Boolean>)this.register(new Setting("Switchback", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.offhandSwitch.getValue()));
        this.lethalSwitch = (Setting<Boolean>)this.register(new Setting("LethalSwitch", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.mineSwitch = (Setting<Boolean>)this.register(new Setting("MineSwitch", false, v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.rotate = (Setting<Rotate>)this.register(new Setting("Rotate", Rotate.OFF, v -> this.setting.getValue() == Settings.MISC));
        this.suicide = (Setting<Boolean>)this.register(new Setting("Suicide", false, v -> this.setting.getValue() == Settings.MISC));
        this.webAttack = (Setting<Boolean>)this.register(new Setting("WebAttack", false, v -> this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE));
        this.fullCalc = (Setting<Boolean>)this.register(new Setting("ExtraCalc", false, v -> this.setting.getValue() == Settings.MISC));
        this.extraSelfCalc = (Setting<Boolean>)this.register(new Setting("MinSelfDmg", true, v -> this.setting.getValue() == Settings.MISC));
        this.logic = (Setting<Logic>)this.register(new Setting("Logic", Logic.BREAKPLACE, v -> this.setting.getValue() == Settings.DEV));
        this.doubleMap = (Setting<Boolean>)this.register(new Setting("DoubleMap", false, v -> this.setting.getValue() == Settings.DEV && this.logic.getValue() == Logic.PLACEBREAK));
        this.damageSync = (Setting<DamageSync>)this.register(new Setting("DamageSync", DamageSync.NONE, v -> this.setting.getValue() == Settings.DEV));
        this.damageSyncTime = (Setting<Integer>)this.register(new Setting("SyncDelay", 500, 0, 1000, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.dropOff = (Setting<Float>)this.register(new Setting("DropOff", 5.0f, 0.0f, 10.0f, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() == DamageSync.BREAK));
        this.confirm = (Setting<Integer>)this.register(new Setting("Confirm", 250, 0, 1000, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.syncedFeetPlace = (Setting<Boolean>)this.register(new Setting("FeetSync", false, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.fullSync = (Setting<Boolean>)this.register(new Setting("FullSync", false, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.syncCount = (Setting<Boolean>)this.register(new Setting("SyncCount", true, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.hyperSync = (Setting<Boolean>)this.register(new Setting("HyperSync", false, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.gigaSync = (Setting<Boolean>)this.register(new Setting("GigaSync", false, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.syncySync = (Setting<Boolean>)this.register(new Setting("SyncySync", false, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.enormousSync = (Setting<Boolean>)this.register(new Setting("EnormousSync", false, v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", 3, 1, 3, v -> this.setting.getValue() == Settings.DEV));
        this.threadMode = (Setting<ThreadMode>)this.register(new Setting("Thread", ThreadMode.NONE, v -> this.setting.getValue() == Settings.DEV));
        this.threadDelay = (Setting<Integer>)this.register(new Setting("ThreadDelay", 25, 1, 1000, v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
        this.syncThreads = (Setting<Integer>)this.register(new Setting("SyncThreads", 1000, 1, 10000, v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
        this.placeSwing = this.register(new Setting<Object>("PlaceSwing", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
        this.exactHand = this.register(new Setting<Object>("ExactHand", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.placeSwing.getValue() != false));
        this.attackList = new ConcurrentLinkedQueue<Entity>();
        this.crystalMap = new HashMap<Entity, Float>();
        this.switchTimer = new Timer();
        this.manualTimer = new Timer();
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
        this.syncTimer = new Timer();
        this.efficientTarget = null;
        this.currentDamage = 0.0;
        this.lastDamage = 0.0;
        this.didRotation = false;
        this.switching = false;
        this.placePos = null;
        this.renderPos = null;
        this.mainHand = false;
        this.rotating = false;
        this.offHand = false;
        this.crystalCount = 0;
        this.minDmgCount = 0;
        this.lastSlot = -1;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        this.webPos = null;
        this.renderTimer = new Timer();
        this.lastPos = null;
        this.posConfirmed = false;
        this.foundDoublePop = false;
        this.shouldInterrupt = new AtomicBoolean(false);
        this.syncroTimer = new Timer();
        this.totemPops = new ConcurrentHashMap<>();
        AutoCrystalTest.instance = this;
    }
    
    public static AutoCrystalTest getInstance() {
        if (AutoCrystalTest.instance == null) {
            AutoCrystalTest.instance = new AutoCrystalTest();
        }
        return AutoCrystalTest.instance;
    }
    
    @Override
    public void onTick() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 3) {
            this.doAutoCrystal();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
        else if (this.eventMode.getValue() == 2) {
            this.doAutoCrystal();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 1) {
            this.doAutoCrystal();
        }
    }
    
    @Override
    public void onToggle() {
        AutoCrystalTest.brokenPos.clear();
        AutoCrystalTest.placedPos.clear();
        this.totemPops.clear();
        this.rotating = false;
    }
    
    @Override
    public void onDisable() {
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }
    
    @Override
    public void onEnable() {
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "§aSwitch";
        }
        if (AutoCrystalTest.target != null) {
            return AutoCrystalTest.target.getName() + "| FastMode";
        }
        return null;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && this.eventMode.getValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet = event.getPacket();
            packet.yaw = this.yaw;
            packet.pitch = this.pitch;
            this.rotating = false;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (this.explode.getValue() && this.instant.getValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !this.syncedFeetPlace.getValue() || this.damageSync.getValue() == DamageSync.NONE)) {
            final SPacketSpawnObject packet2 = event.getPacket();
            if (packet2.getType() == 51 && AutoCrystalTest.placedPos.contains(new BlockPos(packet2.getX(), packet2.getY(), packet2.getZ()).down())) {
                final CPacketUseEntity attackPacket = new CPacketUseEntity();
                attackPacket.entityId = packet2.getEntityID();
                attackPacket.action = CPacketUseEntity.Action.ATTACK;
                mc.player.connection.sendPacket(attackPacket);
            }
        }
        else if (event.getPacket() instanceof SPacketExplosion) {
            final SPacketExplosion packet3 = event.getPacket();
            final BlockPos pos = new BlockPos(packet3.getX(), packet3.getY(), packet3.getZ()).down();
            if (this.damageSync.getValue() == DamageSync.PLACE) {
                if (AutoCrystalTest.placedPos.contains(pos)) {
                    AutoCrystalTest.placedPos.remove(pos);
                    this.posConfirmed = true;
                }
            }
            else if (this.damageSync.getValue() == DamageSync.BREAK && AutoCrystalTest.brokenPos.contains(pos)) {
                AutoCrystalTest.brokenPos.remove(pos);
                this.posConfirmed = true;
            }
        }
        else if (event.getPacket() instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packet4 = event.getPacket();
            for (final int id : packet4.getEntityIDs()) {
                final Entity entity = mc.world.getEntityByID(id);
                if (entity instanceof EntityEnderCrystal) {
                    AutoCrystalTest.brokenPos.remove(new BlockPos(entity.getPositionVector()).down());
                    AutoCrystalTest.placedPos.remove(new BlockPos(entity.getPositionVector()).down());
                }
            }
        }
        else {
            final SPacketEntityStatus packet5;
            if (event.getPacket() instanceof SPacketEntityStatus && (packet5 = event.getPacket()).getOpCode() == 35 && packet5.getEntity(mc.world) instanceof EntityPlayer) {
                this.totemPops.put((EntityPlayer)packet5.getEntity(mc.world), new Timer().reset());
            }
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && this.render.getValue() && (this.box.getValue() || this.text.getValue() || this.outline.getValue())) {
            RenderUtil.drawBoxESP(this.renderPos, ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(255) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), ClickGui.getInstance().rainbow.getValue() ? ColorUtil.rainbow(255) : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true);
        }
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            if (this.thread != null) {
                this.shouldInterrupt.set(true);
            }
        }
    }
    
    private void processMultiThreading() {
        if (this.isOff()) {
            return;
        }
        if (this.threadMode.getValue() == ThreadMode.POOL) {
            this.handlePool();
        }
        else if (this.threadMode.getValue() == ThreadMode.WHILE) {
            this.handleWhile();
        }
    }
    
    private void handlePool() {
        if (this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || this.syncroTimer.passedMs(this.syncThreads.getValue())) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            this.executor = this.getExecutor();
            this.syncroTimer.reset();
        }
    }
    
    private void handleWhile() {
        if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncroTimer.passedMs(this.syncThreads.getValue())) {
            if (this.thread == null) {
                this.thread = new Thread(RAutoCrystal.getInstance(this));
            }
            else if (this.syncroTimer.passedMs(this.syncThreads.getValue()) && !this.shouldInterrupt.get()) {
                this.shouldInterrupt.set(true);
                this.syncroTimer.reset();
                return;
            }
            if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                this.thread = new Thread(RAutoCrystal.getInstance(this));
            }
            if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
                try {
                    this.thread.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.syncroTimer.reset();
            }
        }
    }
    
    private ScheduledExecutorService getExecutor() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoCrystal.getInstance(this), 0L, this.threadDelay.getValue(), TimeUnit.MILLISECONDS);
        return service;
    }
    
    public void doAutoCrystal() {
        if (this.check()) {
            switch (this.logic.getValue()) {
                case PLACEBREAK: {
                    this.placeCrystal();
                    if (this.doubleMap.getValue()) {
                        this.mapCrystals();
                    }
                    this.breakCrystal();
                    break;
                }
                case BREAKPLACE: {
                    this.breakCrystal();
                    this.placeCrystal();
                    break;
                }
            }
            this.manualBreaker();
        }
    }
    
    private boolean check() {
        if (fullNullCheck()) {
            return false;
        }
        if (this.syncTimer.passedMs(this.damageSyncTime.getValue())) {
            this.currentSyncTarget = null;
            this.syncedCrystalPos = null;
            this.syncedPlayerPos = null;
        }
        else if (this.syncySync.getValue() && this.syncedCrystalPos != null) {
            this.posConfirmed = true;
        }
        this.foundDoublePop = false;
        if (this.renderTimer.passedMs(500L)) {
            this.renderPos = null;
            this.renderTimer.reset();
        }
        this.mainHand = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        this.offHand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.currentDamage = 0.0;
        this.placePos = null;
        if (this.lastSlot != mc.player.inventory.currentItem || AutoTrap.isPlacing || Surround.isPlacing) {
            this.lastSlot = mc.player.inventory.currentItem;
            this.switchTimer.reset();
        }
        if (this.offHand || this.mainHand) {
            this.switching = false;
        }
        if ((!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching) || !DamageUtil.canBreakWeakness(mc.player) || !this.switchTimer.passedMs(this.switchCooldown.getValue())) {
            this.renderPos = null;
            AutoCrystalTest.target = null;
            return this.rotating = false;
        }
        if (this.mineSwitch.getValue() && mc.gameSettings.keyBindAttack.isKeyDown() && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
            this.switchItem();
        }
        this.mapCrystals();
        if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(this.confirm.getValue())) {
            this.syncTimer.setMs(this.damageSyncTime.getValue() + 1);
        }
        return true;
    }
    
    private void mapCrystals() {
        this.efficientTarget = null;
        if (this.packets.getValue() != 1) {
            this.attackList = new ConcurrentLinkedQueue<>();
            this.crystalMap = new HashMap<>();
        }
        this.crystalCount = 0;
        this.minDmgCount = 0;
        Entity maxCrystal = null;
        float maxDamage = 0.5f;
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal) {
                if (!this.isValid(entity)) {
                    continue;
                }
                if (this.syncedFeetPlace.getValue() && entity.getPosition().down().equals(this.syncedCrystalPos) && this.damageSync.getValue() != DamageSync.NONE) {
                    ++this.minDmgCount;
                    ++this.crystalCount;
                    if (this.syncCount.getValue()) {
                        this.minDmgCount = this.wasteAmount.getValue() + 1;
                        this.crystalCount = this.wasteAmount.getValue() + 1;
                    }
                    if (!this.hyperSync.getValue()) {
                        continue;
                    }
                    maxCrystal = null;
                    break;
                }
                else {
                    boolean count = false;
                    boolean countMin = false;
                    final float selfDamage = DamageUtil.calculateDamage(entity, mc.player);
                    if (selfDamage + 0.5 < EntityUtil.getHealth(mc.player) || !DamageUtil.canTakeDamage(this.suicide.getValue())) {
                        for (final EntityPlayer player : mc.world.playerEntities) {
                            if (player.getDistanceSq(entity) < MathUtil.square(this.range.getValue()) && EntityUtil.isValid(player, this.range.getValue() + this.breakRange.getValue())) {
                                final float damage;
                                if ((damage = DamageUtil.calculateDamage(entity, player)) <= selfDamage && (damage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth(player)) {
                                    continue;
                                }
                                if (damage > maxDamage) {
                                    maxDamage = damage;
                                    maxCrystal = entity;
                                }
                                if (this.packets.getValue() == 1) {
                                    if (damage >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                                        count = true;
                                    }
                                    countMin = true;
                                }
                                else {
                                    if (this.crystalMap.get(entity) != null && this.crystalMap.get(entity) >= damage) {
                                        continue;
                                    }
                                    this.crystalMap.put(entity, damage);
                                }
                            }
                        }
                    }
                    if (!countMin) {
                        continue;
                    }
                    ++this.minDmgCount;
                    if (!count) {
                        continue;
                    }
                    ++this.crystalCount;
                }
            }
        }
        if (this.damageSync.getValue() == DamageSync.BREAK && (maxDamage > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
            this.lastDamage = maxDamage;
        }
        if (this.enormousSync.getValue() && this.syncedFeetPlace.getValue() && this.damageSync.getValue() != DamageSync.NONE && this.syncedCrystalPos != null) {
            if (this.syncCount.getValue()) {
                this.minDmgCount = this.wasteAmount.getValue() + 1;
                this.crystalCount = this.wasteAmount.getValue() + 1;
            }
            return;
        }
        if (this.webAttack.getValue() && this.webPos != null) {
            if (mc.player.getDistanceSq(this.webPos.up()) > MathUtil.square(this.breakRange.getValue())) {
                this.webPos = null;
            }
            else {
                for (final Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.webPos.up()))) {
                    if (!(entity instanceof EntityEnderCrystal)) {
                        continue;
                    }
                    this.attackList.add(entity);
                    this.efficientTarget = entity;
                    this.webPos = null;
                    this.lastDamage = 0.5;
                    return;
                }
            }
        }
        if (this.manual.getValue() && this.manualMinDmg.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && ((this.offHand && mc.player.getActiveHand() == EnumHand.OFF_HAND) || (this.mainHand && mc.player.getActiveHand() == EnumHand.MAIN_HAND)) && maxDamage < this.minDamage.getValue()) {
            this.efficientTarget = null;
            return;
        }
        if (this.packets.getValue() == 1) {
            this.efficientTarget = maxCrystal;
        }
        else {
            this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
            for (final Map.Entry entry : this.crystalMap.entrySet()) {
                final Object crystal = entry.getKey();
                final float damage2 = (float) entry.getValue();
                if (damage2 >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                    ++this.crystalCount;
                }
                this.attackList.add((Entity) crystal);
                ++this.minDmgCount;
            }
        }
    }
    
    private void placeCrystal() {
        int crystalLimit = this.wasteAmount.getValue();
        if (this.placeTimer.passedMs(this.placeDelay.getValue()) && this.place.getValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || (this.switchMode.getValue() == Switch.BREAKSLOT && this.switching))) {
            if ((this.offHand || this.mainHand || (this.switchMode.getValue() != Switch.ALWAYS && !this.switching)) && this.crystalCount >= crystalLimit && (!this.antiSurround.getValue() || this.lastPos == null || !this.lastPos.equals(this.placePos))) {
                return;
            }
            this.calculateDamage(this.getTarget(this.targetMode.getValue() == Target.UNSAFE));
            if (AutoCrystalTest.target != null && this.placePos != null) {
                if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > this.minDamage.getValue() || (this.lethalSwitch.getValue() && EntityUtil.getHealth(AutoCrystalTest.target) < this.facePlace.getValue())) && !this.switchItem()) {
                    return;
                }
                if (this.currentDamage < this.minDamage.getValue() && this.limitFacePlace.getValue()) {
                    crystalLimit = 1;
                }
                if ((this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (this.antiSurround.getValue() && this.lastPos != null && this.lastPos.equals(this.placePos))) && (this.currentDamage > this.minDamage.getValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0 && (DamageUtil.isArmorLow(AutoCrystalTest.target, this.minArmor.getValue()) || EntityUtil.getHealth(AutoCrystalTest.target) < this.facePlace.getValue() || this.currentDamage > this.minDamage.getValue())) {
                    final float damageOffset = (this.damageSync.getValue() == DamageSync.BREAK) ? (this.dropOff.getValue() - 5.0f) : 0.0f;
                    boolean syncflag = false;
                    if (this.syncedFeetPlace.getValue() && this.placePos.equals(this.lastPos) && !this.syncTimer.passedMs(this.damageSyncTime.getValue()) && AutoCrystalTest.target.equals(this.currentSyncTarget) && AutoCrystalTest.target.getPosition().equals(this.syncedPlayerPos) && this.damageSync.getValue() != DamageSync.NONE) {
                        this.syncedCrystalPos = this.placePos;
                        this.lastDamage = this.currentDamage;
                        if (this.fullSync.getValue()) {
                            this.lastDamage = 100.0;
                        }
                        syncflag = true;
                    }
                    if (syncflag || this.currentDamage - damageOffset > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE) {
                        if (!syncflag && this.damageSync.getValue() != DamageSync.BREAK) {
                            this.lastDamage = this.currentDamage;
                        }
                        this.renderPos = this.placePos;
                        if (this.switchItem()) {
                            this.currentSyncTarget = AutoCrystalTest.target;
                            this.syncedPlayerPos = AutoCrystalTest.target.getPosition();
                            if (this.foundDoublePop) {
                                this.totemPops.put(AutoCrystalTest.target, new Timer().reset());
                            }
                            this.rotateToPos(this.placePos);
                            AutoCrystalTest.placedPos.add(this.placePos);
                            BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing.getValue(), this.exactHand.getValue());
                            this.lastPos = this.placePos;
                            this.placeTimer.reset();
                            this.posConfirmed = false;
                            if (this.syncTimer.passedMs(this.damageSyncTime.getValue())) {
                                this.syncedCrystalPos = null;
                                this.syncTimer.reset();
                            }
                        }
                    }
                }
            }
            else {
                this.renderPos = null;
            }
        }
    }
    
    private boolean switchItem() {
        if (this.offHand || this.mainHand) {
            return true;
        }
        switch (this.autoSwitch.getValue()) {
            case NONE: {
                return false;
            }
            case TOGGLE: {
                if (!this.switching) {
                    return false;
                }
                break;
            }
        }
        return false;
    }
    
    private void calculateDamage(final EntityPlayer targettedPlayer) {
        if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !this.fullCalc.getValue()) {
            return;
        }
        float maxDamage = 0.5f;
        EntityPlayer currentTarget = null;
        BlockPos currentPos = null;
        float maxSelfDamage = 0.0f;
        this.foundDoublePop = false;
        BlockPos setToAir = null;
        IBlockState state = null;
        final BlockPos playerPos;
        if (this.webAttack.getValue() && targettedPlayer != null && mc.world.getBlockState(playerPos = new BlockPos(targettedPlayer.getPositionVector())).getBlock() == Blocks.WEB) {
            setToAir = playerPos;
            state = mc.world.getBlockState(playerPos);
            mc.world.setBlockToAir(playerPos);
        }
        for (final BlockPos pos : BlockUtil.possiblePlacePositions(this.placeRange.getValue(), this.antiSurround.getValue(), this.oneDot15.getValue())) {
            if (!BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && mc.player.getDistanceSq(pos) > MathUtil.square(this.placetrace.getValue()), 1.0f)) {
                continue;
            }
            float selfDamage = -1.0f;
            if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                selfDamage = DamageUtil.calculateDamage(pos, mc.player);
            }
            if (selfDamage + 0.5 >= EntityUtil.getHealth(mc.player)) {
                continue;
            }
            if (targettedPlayer != null) {
                final float playerDamage = DamageUtil.calculateDamage(pos, targettedPlayer);
                if (this.isDoublePoppable(targettedPlayer, playerDamage) && (currentPos == null || targettedPlayer.getDistanceSq(pos) < targettedPlayer.getDistanceSq(currentPos))) {
                    currentTarget = targettedPlayer;
                    maxDamage = playerDamage;
                    currentPos = pos;
                    this.foundDoublePop = true;
                }
                else {
                    if (this.foundDoublePop || (playerDamage <= maxDamage && (!this.extraSelfCalc.getValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage))) {
                        continue;
                    }
                    if (playerDamage <= selfDamage && (playerDamage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage <= EntityUtil.getHealth(targettedPlayer)) {
                        continue;
                    }
                    maxDamage = playerDamage;
                    currentTarget = targettedPlayer;
                    currentPos = pos;
                    maxSelfDamage = selfDamage;
                }
            }
            else {
                for (final EntityPlayer player : mc.world.playerEntities) {
                    final float playerDamage2;
                    if (EntityUtil.isValid(player, this.placeRange.getValue() + this.range.getValue()) && ((playerDamage2 = DamageUtil.calculateDamage(pos, player)) > maxDamage || (this.extraSelfCalc.getValue() && playerDamage2 >= maxDamage && selfDamage < maxSelfDamage))) {
                        if (playerDamage2 <= selfDamage && (playerDamage2 <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage2 <= EntityUtil.getHealth(player)) {
                            continue;
                        }
                        maxDamage = playerDamage2;
                        currentTarget = player;
                        currentPos = pos;
                        maxSelfDamage = selfDamage;
                    }
                }
            }
        }
        if (setToAir != null) {
            mc.world.setBlockState(setToAir, state);
            this.webPos = currentPos;
        }
        AutoCrystalTest.target = currentTarget;
        this.currentDamage = maxDamage;
        this.placePos = currentPos;
    }
    
    private EntityPlayer getTarget(final boolean unsafe) {
        if (this.targetMode.getValue() == Target.DAMAGE) {
            return null;
        }
        EntityPlayer currentTarget = null;
        for (final EntityPlayer player : mc.world.playerEntities) {
            if (!EntityUtil.isntValid(player, this.placeRange.getValue() + this.range.getValue())) {
                if (unsafe && EntityUtil.isSafe(player)) {
                    continue;
                }
                if (this.minArmor.getValue() > 0 && DamageUtil.isArmorLow(player, this.minArmor.getValue())) {
                    currentTarget = player;
                    break;
                }
                if (currentTarget == null) {
                    currentTarget = player;
                }
                else {
                    if (mc.player.getDistanceSq(player) >= mc.player.getDistanceSq(currentTarget)) {
                        continue;
                    }
                    currentTarget = player;
                }
            }
        }
        if (unsafe && currentTarget == null) {
            return this.getTarget(false);
        }
        return currentTarget;
    }
    
    private void breakCrystal() {
        if (this.explode.getValue() && this.breakTimer.passedMs(this.breakDelay.getValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
            if (this.packets.getValue() == 1 && this.efficientTarget != null) {
                if (this.syncedFeetPlace.getValue() && this.gigaSync.getValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
                this.rotateTo(this.efficientTarget);
                EntityUtil.attackEntity(this.efficientTarget, this.sync.getValue(), true);
                AutoCrystalTest.brokenPos.add(new BlockPos(this.efficientTarget.getPositionVector()).down());
            }
            else if (!this.attackList.isEmpty()) {
                if (this.syncedFeetPlace.getValue() && this.gigaSync.getValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
                for (int i = 0; i < this.packets.getValue(); ++i) {
                    final Entity entity = this.attackList.poll();
                    if (entity != null) {
                        this.rotateTo(entity);
                        EntityUtil.attackEntity(entity, this.sync.getValue(), true);
                        AutoCrystalTest.brokenPos.add(new BlockPos(entity.getPositionVector()).down());
                    }
                }
            }
            this.breakTimer.reset();
        }
    }
    
    private void manualBreaker() {
        if (this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() != 2 && this.rotating) {
            if (this.didRotation) {
                mc.player.rotationPitch += (float)4.0E-4;
                this.didRotation = false;
            }
            else {
                mc.player.rotationPitch -= (float)4.0E-4;
                this.didRotation = true;
            }
        }
        final RayTraceResult result;
        if ((this.offHand || this.mainHand) && this.manual.getValue() && this.manualTimer.passedMs(this.manualBreak.getValue()) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.BOW && mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && (result = mc.objectMouseOver) != null) {
            switch (result.typeOfHit) {
                case ENTITY: {
                    final Entity entity = result.entityHit;
                    if (!(entity instanceof EntityEnderCrystal)) {
                        break;
                    }
                    EntityUtil.attackEntity(entity, this.sync.getValue(), true);
                    this.manualTimer.reset();
                    break;
                }
                case BLOCK: {
                    final BlockPos mousePos = mc.objectMouseOver.getBlockPos().up();
                    for (final Entity target : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(mousePos))) {
                        if (!(target instanceof EntityEnderCrystal)) {
                            continue;
                        }
                        EntityUtil.attackEntity(target, this.sync.getValue(), true);
                        this.manualTimer.reset();
                    }
                    break;
                }
            }
        }
    }
    
    private void rotateTo(final Entity entity) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case BREAK:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
                if (this.eventMode.getValue() == 2) {
                    loveClient.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }
    
    private void rotateToPos(final BlockPos pos) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case PLACE:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() - 0.5f, pos.getZ() + 0.5f));
                if (this.eventMode.getValue() == 2) {
                    loveClient.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }
    
    private boolean isDoublePoppable(final EntityPlayer player, final float damage) {
        final float health;
        if (this.doublePop.getValue() && (health = EntityUtil.getHealth(player)) <= 1.0 && damage > health + 0.5 && damage <= this.popDamage.getValue()) {
            final Timer timer = this.totemPops.get(player);
            return timer == null || timer.passedMs(this.popTime.getValue());
        }
        return false;
    }
    
    private boolean isValid(final Entity entity) {
        return entity != null && mc.player.getDistanceSq(entity) <= MathUtil.square(this.breakRange.getValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || mc.player.canEntityBeSeen(entity) || (!mc.player.canEntityBeSeen(entity) && mc.player.getDistanceSq(entity) <= MathUtil.square(this.breaktrace.getValue())));
    }
    
    static {
        AutoCrystalTest.target = null;
        AutoCrystalTest.placedPos = new HashSet<>();
        AutoCrystalTest.brokenPos = new HashSet<>();
    }
    
    public enum Settings
    {
        PLACE, 
        BREAK, 
        RENDER, 
        MISC, 
        DEV
    }
    
    public enum Raytrace
    {
        NONE, 
        PLACE, 
        BREAK, 
        FULL
    }
    
    public enum Switch
    {
        ALWAYS, 
        BREAKSLOT, 
        CALC
    }
    
    public enum Target
    {
        CLOSEST, 
        UNSAFE, 
        DAMAGE
    }
    
    public enum AutoSwitch
    {
        NONE, 
        TOGGLE, 
        ALWAYS
    }
    
    public enum Rotate
    {
        OFF, 
        PLACE, 
        BREAK, 
        ALL
    }
    
    public enum Logic
    {
        BREAKPLACE, 
        PLACEBREAK
    }
    
    public enum DamageSync
    {
        NONE, 
        PLACE, 
        BREAK
    }
    
    public enum ThreadMode
    {
        NONE, 
        WHILE, 
        POOL
    }
    
    private static class RAutoCrystal implements Runnable
    {
        private static RAutoCrystal instance;
        private AutoCrystalTest autoCrystalTest;
        
        public static RAutoCrystal getInstance(final AutoCrystalTest autoCrystalTest) {
            if (RAutoCrystal.instance == null) {
                RAutoCrystal.instance = new RAutoCrystal();
            }
            RAutoCrystal.instance.autoCrystalTest = autoCrystalTest;
            return RAutoCrystal.instance;
        }
        
        @Override
        public void run() {
            if (this.autoCrystalTest.threadMode.getValue() == ThreadMode.POOL) {
                if (this.autoCrystalTest.isEnable()) {
                    this.autoCrystalTest.doAutoCrystal();
                }
            }
            else if (this.autoCrystalTest.threadMode.getValue() == ThreadMode.WHILE) {
                while (this.autoCrystalTest.isEnable() && this.autoCrystalTest.threadMode.getValue() == ThreadMode.WHILE) {
                    if (this.autoCrystalTest.shouldInterrupt.get()) {
                        this.autoCrystalTest.shouldInterrupt.set(false);
                        this.autoCrystalTest.syncroTimer.reset();
                        this.autoCrystalTest.thread.interrupt();
                        break;
                    }
                    this.autoCrystalTest.doAutoCrystal();
                    try {
                        sleep(this.autoCrystalTest.threadDelay.getValue());
                    }
                    catch (InterruptedException e) {
                        this.autoCrystalTest.thread.interrupt();
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

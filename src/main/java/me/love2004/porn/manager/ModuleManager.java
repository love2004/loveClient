package me.love2004.porn.manager;

import me.love2004.porn.event.events.Render2DEvent;
import me.love2004.porn.event.events.Render3DEvent;
import me.love2004.porn.Feature;
import me.love2004.porn.gui.PhobosGui;
import me.love2004.porn.modules.Module;
import me.love2004.porn.modules.client.Cosmetics;
import me.love2004.porn.modules.client.*;
import me.love2004.porn.modules.client.TrayIcon;
import me.love2004.porn.modules.combat.*;
import me.love2004.porn.modules.misc.*;
import me.love2004.porn.modules.movement.*;
import me.love2004.porn.modules.player.*;
import me.love2004.porn.modules.render.*;
import me.love2004.porn.modules.test.AutoCrystalTest;
import me.love2004.porn.modules.test.AutoCrystalTestTwo;
import me.love2004.porn.modules.test.BurrowTest;
import me.love2004.porn.modules.test.test;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager
        extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<Module> alphabeticallySortedModules = new ArrayList<Module>();
    public Map<Module, Color> moduleColorMap = new HashMap<Module, Color>();

    public void init() {

        modules.add(new test());
        modules.add(new noDesync());
        modules.add(new AutoCrystalTest());
        modules.add(new AutoCrystalTestTwo());
        modules.add(new Trapper());

        modules.add(new Offhand());
        modules.add(new Surround());
        modules.add(new AutoTrap());
        modules.add(new AutoCrystal());
        modules.add(new Criticals());
        modules.add(new BowSpam());
        modules.add(new Killaura());
        modules.add(new HoleFiller());
        modules.add(new Selftrap());
        modules.add(new Webaura());
        modules.add(new BurrowTest());
        modules.add(new AutoArmor());
        modules.add(new Nametags());
        modules.add(new AntiTrap());
        modules.add(new BedBomb());
        modules.add(new ArmorMessage());
        modules.add(new Crasher());
        modules.add(new Tags());
        modules.add(new Auto32k());
        modules.add(new AntiCrystal());
        modules.add(new AnvilAura());
        modules.add(new GodModule());
        modules.add(new NoteBot());
        modules.add(new ChatModifier());
        modules.add(new BetterPortals());
        modules.add(new BuildHeight());

        modules.add(new NoHandShake());
        modules.add(new AutoRespawn());
        modules.add(new NoRotate());
        modules.add(new MCF());
        modules.add(new PingSpoof());
        modules.add(new NoSoundLag());
        modules.add(new AutoLog());
//        modules.add(new KitDelete());
        modules.add(new Exploits());
        modules.add(new Spammer());
        modules.add(new AntiVanish());
        modules.add(new ExtraTab());
        modules.add(new MobOwner());
        modules.add(new Nuker());
        modules.add(new AutoReconnect());
        modules.add(new NoAFK());
        modules.add(new Tracker());
        modules.add(new AntiPackets());
        modules.add(new Logger());
        modules.add(new RPC());
        modules.add(new AutoGG());
        modules.add(new Godmode());
        modules.add(new Companion());
        modules.add(new EntityControl());
        modules.add(new GhastNotifier());
        modules.add(new ReverseStep());
        modules.add(new Bypass());
        modules.add(new Strafe());
        modules.add(new Velocity());
        modules.add(new Speed());
        modules.add(new Step());
        modules.add(new StepOld());
        modules.add(new Sprint());
        modules.add(new AntiLevitate());
        modules.add(new Phase());
        modules.add(new Static());
        modules.add(new TPSpeed());
        modules.add(new Flight());
        modules.add(new ElytraFlight());
        modules.add(new NoSlowDown());
        modules.add(new HoleTP());
        modules.add(new NoFall());
        modules.add(new IceSpeed());
        modules.add(new AutoWalk());
        modules.add(new TestPhase());
        modules.add(new LongJump());
        modules.add(new LagBlock());
        modules.add(new FastSwim());
        modules.add(new StairSpeed());
        modules.add(new BoatFly());
        modules.add(new VanillaSpeed());
        modules.add(new Reach());
        modules.add(new LiquidInteract());
        modules.add(new FakePlayer());
        modules.add(new TimerSpeed());
        modules.add(new FastPlace());
        modules.add(new Freecam());
        modules.add(new Speedmine());
        modules.add(new SafeWalk());
        modules.add(new Blink());
        modules.add(new MultiTask());
        modules.add(new BlockTweaks());
        modules.add(new XCarry());
        modules.add(new Replenish());
        modules.add(new NoHunger());
        modules.add(new Jesus());
        modules.add(new Scaffold());
        modules.add(new EchestBP());
        modules.add(new TpsSync());
        modules.add(new MCP());
        modules.add(new TrueDurability());
        modules.add(new Yaw());
        modules.add(new NoDDoS());
        modules.add(new StorageESP());
        modules.add(new NoRender());
        modules.add(new SmallShield());
        modules.add(new Fullbright());
        modules.add(new CameraClip());
        modules.add(new Chams());
        modules.add(new Skeleton());
        modules.add(new ESP());
        modules.add(new HoleESP());
        modules.add(new BlockHighlight());
        modules.add(new Trajectories());
        modules.add(new Tracer());
        modules.add(new LogoutSpots());
        modules.add(new XRay());
        modules.add(new PortalESP());
        modules.add(new Ranges());
        modules.add(new OffscreenESP());
        modules.add(new HandColor());
        modules.add(new VoidESP());
        modules.add(new Cosmetics());
//        modules.add(new TestNametags());
        modules.add(new FastCrystal());
        modules.add(new CrystalScale());
        modules.add(new EXPFast());
        modules.add(new Notifications());
        modules.add(new HUD());
        modules.add(new ToolTips());
        modules.add(new FontMod());
        modules.add(new ClickGui());
        modules.add(new Managers());
        modules.add(new Components());
        modules.add(new StreamerMode());
//        modules.add(new Capes());
        modules.add(new Colors());
        modules.add(new TrayIcon());
        modules.add(new ServerModule());
        modules.add(new Screens());
        modules.add(new Media());
        //modules.add(new IRC());
        moduleColorMap.put(getModuleByClass(AntiTrap.class), new Color(128, 53, 69));
        moduleColorMap.put(getModuleByClass(AnvilAura.class), new Color(90, 227, 96));
        moduleColorMap.put(getModuleByClass(ArmorMessage.class), new Color(255, 51, 51));
        moduleColorMap.put(getModuleByClass(Auto32k.class), new Color(185, 212, 144));
        moduleColorMap.put(getModuleByClass(AutoArmor.class), new Color(74, 227, 206));
        moduleColorMap.put(getModuleByClass(AutoCrystal.class), new Color(255, 15, 43));
        moduleColorMap.put(getModuleByClass(AutoTrap.class), new Color(193, 49, 244));
        moduleColorMap.put(getModuleByClass(BedBomb.class), new Color(185, 80, 195));
        moduleColorMap.put(getModuleByClass(BowSpam.class), new Color(204, 191, 153));
        moduleColorMap.put(getModuleByClass(Crasher.class), new Color(208, 66, 9));
        moduleColorMap.put(getModuleByClass(Criticals.class), new Color(204, 151, 184));
        moduleColorMap.put(getModuleByClass(HoleFiller.class), new Color(166, 55, 110));
        moduleColorMap.put(getModuleByClass(Killaura.class), new Color(255, 37, 0));
        moduleColorMap.put(getModuleByClass(Offhand.class), new Color(185, 212, 144));
        moduleColorMap.put(getModuleByClass(Selftrap.class), new Color(22, 127, 145));
        moduleColorMap.put(getModuleByClass(Surround.class), new Color(100, 0, 150));
        moduleColorMap.put(getModuleByClass(Webaura.class), new Color(11, 161, 121));
        moduleColorMap.put(getModuleByClass(AntiCrystal.class), new Color(255, 161, 121));
        moduleColorMap.put(getModuleByClass(AntiPackets.class), new Color(155, 186, 115));
        moduleColorMap.put(getModuleByClass(AntiVanish.class), new Color(25, 209, 135));
        moduleColorMap.put(getModuleByClass(AutoGG.class), new Color(240, 49, 110));
        moduleColorMap.put(getModuleByClass(AutoLog.class), new Color(176, 176, 176));
        moduleColorMap.put(getModuleByClass(AutoReconnect.class), new Color(17, 85, 153));
        moduleColorMap.put(getModuleByClass(BetterPortals.class), new Color(71, 214, 187));
        moduleColorMap.put(getModuleByClass(BuildHeight.class), new Color(64, 136, 199));
        moduleColorMap.put(getModuleByClass(Bypass.class), new Color(194, 214, 81));
        moduleColorMap.put(getModuleByClass(Companion.class), new Color(140, 252, 146));
        moduleColorMap.put(getModuleByClass(ChatModifier.class), new Color(255, 59, 216));
        moduleColorMap.put(getModuleByClass(Exploits.class), new Color(255, 0, 0));
        moduleColorMap.put(getModuleByClass(ExtraTab.class), new Color(161, 113, 173));
        moduleColorMap.put(getModuleByClass(Godmode.class), new Color(1, 35, 95));
        moduleColorMap.put(getModuleByClass(KitDelete.class), new Color(229, 194, 255));
        moduleColorMap.put(getModuleByClass(Logger.class), new Color(186, 0, 109));
        moduleColorMap.put(getModuleByClass(MCF.class), new Color(17, 85, 255));
        moduleColorMap.put(getModuleByClass(MobOwner.class), new Color(255, 254, 204));
        moduleColorMap.put(getModuleByClass(NoAFK.class), new Color(80, 5, 98));
        moduleColorMap.put(getModuleByClass(NoHandShake.class), new Color(173, 232, 139));
        moduleColorMap.put(getModuleByClass(NoRotate.class), new Color(69, 81, 223));
        moduleColorMap.put(getModuleByClass(NoSoundLag.class), new Color(255, 56, 0));
        moduleColorMap.put(getModuleByClass(Nuker.class), new Color(152, 169, 17));
        moduleColorMap.put(getModuleByClass(PingSpoof.class), new Color(23, 214, 187));
        moduleColorMap.put(getModuleByClass(RPC.class), new Color(0, 64, 255));
        moduleColorMap.put(getModuleByClass(Spammer.class), new Color(140, 87, 166));
        moduleColorMap.put(getModuleByClass(ToolTips.class), new Color(209, 125, 156));
        moduleColorMap.put(getModuleByClass(Translator.class), new Color(74, 82, 15));
        moduleColorMap.put(getModuleByClass(Tracker.class), new Color(0, 255, 225));
        moduleColorMap.put(getModuleByClass(GhastNotifier.class), new Color(200, 200, 220));
        moduleColorMap.put(getModuleByClass(OffscreenESP.class), new Color(193, 219, 20));
        moduleColorMap.put(getModuleByClass(BlockHighlight.class), new Color(103, 182, 224));
        moduleColorMap.put(getModuleByClass(CameraClip.class), new Color(247, 169, 107));
        moduleColorMap.put(getModuleByClass(Chams.class), new Color(34, 152, 34));
        moduleColorMap.put(getModuleByClass(ESP.class), new Color(255, 27, 155));
        moduleColorMap.put(getModuleByClass(Fullbright.class), new Color(255, 164, 107));
        moduleColorMap.put(getModuleByClass(HandColor.class), new Color(96, 138, 92));
        moduleColorMap.put(getModuleByClass(HoleESP.class), new Color(95, 83, 130));
        moduleColorMap.put(getModuleByClass(LogoutSpots.class), new Color(2, 135, 134));
        moduleColorMap.put(getModuleByClass(Nametags.class), new Color(98, 82, 223));
        moduleColorMap.put(getModuleByClass(NoRender.class), new Color(255, 164, 107));
        moduleColorMap.put(getModuleByClass(PortalESP.class), new Color(26, 242, 62));
        moduleColorMap.put(getModuleByClass(Ranges.class), new Color(144, 212, 196));
        moduleColorMap.put(getModuleByClass(Skeleton.class), new Color(219, 219, 219));
        moduleColorMap.put(getModuleByClass(SmallShield.class), new Color(145, 223, 187));
        moduleColorMap.put(getModuleByClass(StorageESP.class), new Color(97, 81, 223));
        moduleColorMap.put(getModuleByClass(Tracer.class), new Color(255, 107, 107));
        moduleColorMap.put(getModuleByClass(Trajectories.class), new Color(98, 18, 223));
        moduleColorMap.put(getModuleByClass(VoidESP.class), new Color(68, 178, 142));
        moduleColorMap.put(getModuleByClass(XRay.class), new Color(217, 118, 37));
        moduleColorMap.put(getModuleByClass(AntiLevitate.class), new Color(206, 255, 255));
        moduleColorMap.put(getModuleByClass(AutoWalk.class), new Color(153, 153, 170));
        moduleColorMap.put(getModuleByClass(ElytraFlight.class), new Color(55, 161, 201));
        moduleColorMap.put(getModuleByClass(Flight.class), new Color(186, 164, 178));
        moduleColorMap.put(getModuleByClass(HoleTP.class), new Color(68, 178, 142));
        moduleColorMap.put(getModuleByClass(IceSpeed.class), new Color(33, 193, 247));
        moduleColorMap.put(getModuleByClass(LongJump.class), new Color(228, 27, 213));
        moduleColorMap.put(getModuleByClass(NoFall.class), new Color(61, 204, 78));
        moduleColorMap.put(getModuleByClass(NoSlowDown.class), new Color(61, 204, 78));
        moduleColorMap.put(getModuleByClass(TestPhase.class), new Color(238, 59, 27));
        moduleColorMap.put(getModuleByClass(Phase.class), new Color(186, 144, 212));
        moduleColorMap.put(getModuleByClass(SafeWalk.class), new Color(182, 186, 164));
        moduleColorMap.put(getModuleByClass(Speed.class), new Color(55, 161, 196));
        moduleColorMap.put(getModuleByClass(Sprint.class), new Color(148, 184, 142));
        moduleColorMap.put(getModuleByClass(Static.class), new Color(86, 53, 98));
        moduleColorMap.put(getModuleByClass(Step.class), new Color(144, 212, 203));
        moduleColorMap.put(getModuleByClass(StepOld.class), new Color(144, 212, 203));
        moduleColorMap.put(getModuleByClass(Strafe.class), new Color(0, 204, 255));
        moduleColorMap.put(getModuleByClass(TPSpeed.class), new Color(20, 177, 142));
        moduleColorMap.put(getModuleByClass(Velocity.class), new Color(115, 134, 140));
        moduleColorMap.put(getModuleByClass(ReverseStep.class), new Color(1, 134, 140));
        moduleColorMap.put(getModuleByClass(NoDDoS.class), new Color(67, 191, 181));
        moduleColorMap.put(getModuleByClass(Blink.class), new Color(144, 184, 141));
        moduleColorMap.put(getModuleByClass(BlockTweaks.class), new Color(89, 223, 235));
        moduleColorMap.put(getModuleByClass(EchestBP.class), new Color(255, 243, 30));
        moduleColorMap.put(getModuleByClass(FakePlayer.class), new Color(37, 192, 170));
        moduleColorMap.put(getModuleByClass(FastPlace.class), new Color(217, 118, 37));
        moduleColorMap.put(getModuleByClass(Freecam.class), new Color(206, 232, 128));
        moduleColorMap.put(getModuleByClass(Jesus.class), new Color(136, 221, 235));
        moduleColorMap.put(getModuleByClass(LiquidInteract.class), new Color(85, 223, 235));
        moduleColorMap.put(getModuleByClass(MCP.class), new Color(153, 68, 170));
        moduleColorMap.put(getModuleByClass(MultiTask.class), new Color(17, 223, 235));
        moduleColorMap.put(getModuleByClass(NoHunger.class), new Color(86, 53, 98));
        moduleColorMap.put(getModuleByClass(Reach.class), new Color(9, 223, 187));
        moduleColorMap.put(getModuleByClass(Replenish.class), new Color(153, 223, 235));
        moduleColorMap.put(getModuleByClass(Scaffold.class), new Color(152, 166, 113));
        moduleColorMap.put(getModuleByClass(Speedmine.class), new Color(152, 166, 113));
        moduleColorMap.put(getModuleByClass(TimerSpeed.class), new Color(255, 133, 18));
        moduleColorMap.put(getModuleByClass(TpsSync.class), new Color(93, 144, 153));
        moduleColorMap.put(getModuleByClass(TrueDurability.class), new Color(254, 161, 51));
        moduleColorMap.put(getModuleByClass(XCarry.class), new Color(254, 161, 51));
        moduleColorMap.put(getModuleByClass(Yaw.class), new Color(115, 39, 141));
//        moduleColorMap.put(getModuleByClass(Capes.class), new Color(26, 135, 104));
        moduleColorMap.put(getModuleByClass(ClickGui.class), new Color(26, 81, 135));
        moduleColorMap.put(getModuleByClass(Colors.class), new Color(135, 133, 26));
        moduleColorMap.put(getModuleByClass(Components.class), new Color(135, 26, 26));
        moduleColorMap.put(getModuleByClass(FontMod.class), new Color(135, 26, 88));
        moduleColorMap.put(getModuleByClass(HUD.class), new Color(110, 26, 135));
        moduleColorMap.put(getModuleByClass(Managers.class), new Color(26, 90, 135));
        moduleColorMap.put(getModuleByClass(Notifications.class), new Color(170, 153, 255));
        moduleColorMap.put(getModuleByClass(ServerModule.class), new Color(60, 110, 175));
        moduleColorMap.put(getModuleByClass(Media.class), new Color(138, 45, 13));
        moduleColorMap.put(getModuleByClass(Screens.class), new Color(165, 89, 101));
        moduleColorMap.put(getModuleByClass(StreamerMode.class), new Color(0, 0, 0));
        for (Module module : modules) {
            module.animation.start();
        }
    }

    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }

    public void enableModule(Class clazz) {
        Object module = getModuleByClass(clazz);
        if (module != null) {
            ((Module) module).enable();
        }
    }

    public void disableModule(Class clazz) {
        Object module = getModuleByClass(clazz);
        if (module != null) {
            ((Module) module).disable();
        }
    }

    public void enableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = getModuleByName(name);
        return module != null && module.isEnable();
    }

    public boolean isModuleEnabled(Class clazz) {
        Object module = getModuleByClass(clazz);
        return module != null && ((Module) module).isEnable();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : modules) {
            if (!module.isEnabled() && !module.isSliding()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        modules.stream().filter(Module::listening).forEach(((EventBus) MinecraftForge.EVENT_BUS)::register);
        modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void FastonUpdate(){
        for (int x = 0; x < 2 ; x++) {
            modules.stream().filter(Feature::isEnabled).forEach(Module::FastonUpdate);
        }
    }

    public void onTick() {
        modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        sortedModules = getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void alphabeticallySortModules() {
        alphabeticallySortedModules = getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(Module::getDisplayName)).collect(Collectors.toList());
    }

    public void onLogout() {
        modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof PhobosGui) {
            return;
        }
        modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    public List<Module> getAnimationModules(Module.Category category) {
        ArrayList<Module> animationModules = new ArrayList<Module>();
        for (Module module : getEnabledModules()) {
            if (module.getCategory() != category || module.isDisabled() || !module.isSliding() || !module.isDrawn())
                continue;
            animationModules.add(module);
        }
        return animationModules;
    }
}


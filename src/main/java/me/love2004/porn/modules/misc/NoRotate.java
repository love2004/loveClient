package me.love2004.porn.modules.misc;

import me.love2004.porn.event.events.PacketEvent;
import me.love2004.porn.command.Command;
import me.love2004.porn.modules.Module;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.util.Timer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate
        extends Module {
    private final Setting<Integer> waitDelay = this.register(new Setting<Integer>("Delay", 2500, 0, 10000));
    private final Timer timer = new Timer();
    private boolean cancelPackets = true;
    private boolean timerReset = false;

    public NoRotate() {
        super("NoRotate", "Dangerous to use might desync you.", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onLogout() {
        this.cancelPackets = false;
    }

    @Override
    public void onLogin() {
        this.timer.reset();
        this.timerReset = true;
    }

    @Override
    public void onUpdate() {
        if (this.timerReset && !this.cancelPackets && this.timer.passedMs(this.waitDelay.getValue().intValue())) {
            Command.sendMessage("<NoRotate> \u00a7cThis module might desync you!");
            this.cancelPackets = true;
            this.timerReset = false;
        }
    }

    @Override
    public void onEnable() {
        Command.sendMessage("<NoRotate> \u00a7cThis module might desync you!");
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && this.cancelPackets && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = event.getPacket();
            packet.yaw = NoRotate.mc.player.rotationYaw;
            packet.pitch = NoRotate.mc.player.rotationPitch;
        }
    }
}


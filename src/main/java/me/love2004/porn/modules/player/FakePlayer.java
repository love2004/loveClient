package me.love2004.porn.modules.player;

import com.mojang.authlib.GameProfile;
import me.love2004.porn.loveClient;
import me.love2004.porn.modules.Module;
import me.love2004.porn.modules.client.ClickGui;
import me.love2004.porn.modules.client.ServerModule;
import me.love2004.porn.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakePlayer
        extends Module {
    public static final String[][] phobosInfo = new String[][]{{"8af022c8-b926-41a0-8b79-2b544f300fcf", "love", "3", "0"}, {"0aa3b02f-786a-49c8-bea9-026ee0dd1e85", "love", "-3", "0"}, {"19b13f1f-fe06-4c86-bea5-3dad5df89414", "love", "0", "-3"}, {"e47d6671-99c2-415b-955e-c4bc6b55941b", "love", "0", "3"}, {"b01f9bc1-c17c-429a-2178-93d771f00926", "love", "6", "0"}, {"b232130c-c28a-4e10-8c90-f152235465c5", "love", "-6", "0"}, {"ace08661-3db3-4579-98d3-390a67d5945b", "love", "0", "-6"}, {"5bead570-3bab-460d-af1d-4929950f40c2", "love", "0", "6"}, {"78ee4bd6-64c4-45f0-96e5-0b6747b17382", "love", "0", "9"}, {"28ee2b46-64c4-45f0-96e5-0b6747ba7382", "love", "0", "-9"}};
    private static final String[] fitInfo = new String[]{"fdee323e-7f0c-4c15-7d1c-0f277442342a", "loveClient"};
    private static FakePlayer INSTANCE = new FakePlayer();
    private final List<EntityOtherPlayerMP> fakeEntities = new ArrayList<>();
    public Setting<Boolean> multi = this.register(new Setting<>("Multi", false));
    public List<Integer> fakePlayerIdList = new ArrayList<>();
    private final Setting<Boolean> copyInv = this.register(new Setting<>("CopyInv", true));
    private final Setting<Integer> players = this.register(new Setting<Object>("Players", 1, 1, 9, v -> this.multi.getValue(), "Amount of other players."));

    public FakePlayer() {
        super("FakePlayer", "Spawns in a fake player", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    public static FakePlayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakePlayer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        this.disable();
    }

    @Override
    public void onEnable() {
        if (FakePlayer.fullNullCheck()) {
            this.disable();
            return;
        }
        if (ServerModule.getInstance().isConnected()) {
            FakePlayer.mc.player.connection.sendPacket(new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
            FakePlayer.mc.player.connection.sendPacket(new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "module FakePlayer set Enabled true"));
        }
        this.fakePlayerIdList = new ArrayList<>();
        if (this.multi.getValue()) {
            int amount = 0;
            int entityId = -101;
            for (String[] data : phobosInfo) {
                this.addFakePlayer(data[0], data[1], entityId, Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                if (++amount >= this.players.getValue()) {
                    return;
                }
                entityId -= amount;
            }
        } else {
            this.addFakePlayer(fitInfo[0], fitInfo[1], -100, 0, 0);
        }
    }

    @Override
    public void onDisable() {
        if (FakePlayer.fullNullCheck()) {
            return;
        }
        if (ServerModule.getInstance().isConnected()) {
            FakePlayer.mc.player.connection.sendPacket(new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
            FakePlayer.mc.player.connection.sendPacket(new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "module FakePlayer set Enabled false"));
        }
        for (int id : this.fakePlayerIdList) {
            FakePlayer.mc.world.removeEntityFromWorld(id);
        }
    }

    @Override
    public void onLogout() {
        if (this.isEnable()) {
            this.disable();
        }
    }

    private void addFakePlayer(String uuid, String name, int entityId, int offsetX, int offsetZ) {
        GameProfile profile = new GameProfile(UUID.fromString(uuid), name);
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(FakePlayer.mc.world, profile);
        fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.player);
        fakePlayer.posX += offsetX;
        fakePlayer.posZ += offsetZ;
        if (this.copyInv.getValue()) {
            for (PotionEffect potionEffect : loveClient.potionManager.getOwnPotions()) {
                fakePlayer.addPotionEffect(potionEffect);
            }
            fakePlayer.inventory.copyInventory(FakePlayer.mc.player.inventory);
        }
        fakePlayer.setHealth(FakePlayer.mc.player.getHealth() + FakePlayer.mc.player.getAbsorptionAmount());
        this.fakeEntities.add(fakePlayer);
        FakePlayer.mc.world.addEntityToWorld(entityId, fakePlayer);
        this.fakePlayerIdList.add(entityId);
    }
}


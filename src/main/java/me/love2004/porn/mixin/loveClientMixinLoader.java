package me.love2004.porn.mixin;

import java.util.Map;
import me.love2004.porn.loveClient;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class loveClientMixinLoader
implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public loveClientMixinLoader() {
        loveClient.LOGGER.info("Phobos mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.loveClient.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        loveClient.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}


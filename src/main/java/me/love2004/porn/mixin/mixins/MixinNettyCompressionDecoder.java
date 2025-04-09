package me.love2004.porn.mixin.mixins;

import me.love2004.porn.modules.misc.Bypass;
import net.minecraft.network.NettyCompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value={NettyCompressionDecoder.class})
public abstract class MixinNettyCompressionDecoder {
    @ModifyConstant(method={"decode"}, constant={@Constant(intValue=0x200000)})
    private int decodeHook(int n) {
        if (Bypass.getInstance().isEnable() && Bypass.getInstance().packets.getValue().booleanValue() && Bypass.getInstance().noLimit.getValue().booleanValue()) {
            return Integer.MAX_VALUE;
        }
        return n;
    }
}


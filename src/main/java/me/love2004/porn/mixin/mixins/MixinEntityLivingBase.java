package me.love2004.porn.mixin.mixins;

import me.love2004.porn.modules.movement.ElytraFlight;
import me.love2004.porn.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityLivingBase.class})
public abstract class MixinEntityLivingBase
extends Entity {
    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method={"isElytraFlying"}, at={@At(value="HEAD")}, cancellable=true)
    private void isElytraFlyingHook(CallbackInfoReturnable<Boolean> info) {
        if (Util.mc.player != null && Util.mc.player.equals((Object)this) && ElytraFlight.getInstance().isEnable() && ElytraFlight.getInstance().mode.getValue() == ElytraFlight.Mode.BETTER) {
            info.setReturnValue(false);
        }
    }
}


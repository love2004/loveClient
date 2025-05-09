package me.love2004.porn.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.love2004.porn.loveClient;
import me.love2004.porn.modules.misc.BetterPortals;
import me.love2004.porn.modules.movement.Phase;
import me.love2004.porn.modules.movement.TestPhase;
import me.love2004.porn.modules.player.TpsSync;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPlayer.class})
public abstract class MixinEntityPlayer
extends EntityLivingBase {
    public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn);
    }

    @Inject(method={"getCooldownPeriod"}, at={@At(value="HEAD")}, cancellable=true)
    private void getCooldownPeriodHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        if (TpsSync.getInstance().isEnable() && TpsSync.getInstance().attack.getValue().booleanValue()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf((float)(1.0 / ((EntityPlayer)EntityPlayer.class.cast((Object)this)).getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0 * (double) loveClient.serverManager.getTpsFactor())));
        }
    }

    @ModifyConstant(method={"getPortalCooldown"}, constant={@Constant(intValue=10)})
    private int getPortalCooldownHook(int cooldown) {
        int time = cooldown;
        if (BetterPortals.getInstance().isEnable() && BetterPortals.getInstance().fastPortal.getValue().booleanValue()) {
            time = BetterPortals.getInstance().cooldown.getValue();
        }
        return time;
    }

    @Inject(method={"isEntityInsideOpaqueBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void isEntityInsideOpaqueBlockHook(CallbackInfoReturnable<Boolean> info) {
        if (Phase.getInstance().isEnable() && Phase.getInstance().type.getValue() != Phase.PacketFlyMode.NONE) {
            info.setReturnValue(false);
        } else if (TestPhase.getInstance().isEnable()) {
            info.setReturnValue(false);
        }
    }
}


package me.love2004.porn.mixin.mixins;

import me.love2004.porn.modules.render.NoRender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelBiped.class})
public class MixinModelBiped {
    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (entityIn instanceof EntityPigZombie && NoRender.getInstance().pigmen.getValue().booleanValue()) {
            ci.cancel();
        }
    }
}


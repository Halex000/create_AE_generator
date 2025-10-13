package com.halex.create_ae_generator.mixin;

import com.halex.create_ae_generator.block.AENetworkHelper;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmartBlockEntity.class)
public abstract class MixinSmartBlockEntity {
    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void ae2SaveInject(CompoundTag tag, CallbackInfo ci) {
        Object self = this;
        if (self instanceof AENetworkHelper helper) {
            helper.saveAENetworkData(tag);
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void ae2RemoveInject(CallbackInfo ci) {
        Object self = this;
        if (self instanceof AENetworkHelper helper) {
            helper.removeAENetworkNode();
        }
    }
}

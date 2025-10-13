package com.halex.create_ae_generator.block;

import net.minecraft.nbt.CompoundTag;

public interface AENetworkHelper {
    void saveAENetworkData(CompoundTag tag);
    void removeAENetworkNode();
}

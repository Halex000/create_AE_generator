package com.halex.create_ae_generator.block;

import appeng.api.networking.*;
import appeng.api.orientation.BlockOrientation;
import appeng.api.util.AECableType;
import appeng.me.InWorldGridNode;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.*;

public abstract class NetworkedKineticBlockEntity extends KineticBlockEntity
        implements IGridConnectedBlockEntity, AENetworkHelper, IInWorldGridNodeHost
{
    private static final Map<BlockEntityType<?>, Item> REPRESENTATIVE_ITEMS = new HashMap<>();
    private final IManagedGridNode mainNode;

    protected NetworkedKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.mainNode = GridHelper.createManagedNode(this, BlockEntityNodeListener.INSTANCE)
                .setVisualRepresentation(getItemFromBlockEntity())
                .setInWorldNode(true)
                .setTagName("proxy");
    }

    @Override
    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    public static void registerBlockEntityItem(BlockEntityType<?> type, Item item) {
        REPRESENTATIVE_ITEMS.put(type, item);
    }

    protected Item getItemFromBlockEntity() {
        return REPRESENTATIVE_ITEMS.getOrDefault(getType(), Items.AIR);
    }

    public void saveChanges() {
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        exposeSides();
        GridHelper.onFirstTick(this, be -> onReady());
    }

    @MustBeInvokedByOverriders
    public void onReady() {
        if (!Objects.requireNonNull(this.getLevel()).isClientSide) {
            if (!mainNode.isActive()) {
                mainNode.create(getLevel(), getBlockPos());
            }
        }
        exposeSides();

    }

    @Override
    public void writeSafe(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.writeSafe(tag, provider);
        mainNode.saveToNBT(tag);
    }

    @Override
    public void read(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider, boolean clientPacket) {
        super.read(tag, provider, clientPacket);
        mainNode.loadFromNBT(tag);
        exposeSides();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        mainNode.destroy();
    }

    // Mixin for saveAdditional since create declares it as final
    public void saveAENetworkData(CompoundTag tag) {
        this.getMainNode().saveToNBT(tag);
    }

    // Mixin for setRemoved since create declares it as final
    public void removeAENetworkNode() {
        mainNode.destroy();
    }

    private void exposeSides() {
        mainNode.setExposedOnSides(getGridConnectableSides(BlockOrientation.get(getBlockState())));
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public AECableType getCableConnectionType(Direction direction) {
        return AECableType.COVERED;
    }

    @Override
    public IGridNode getGridNode(Direction dir) {
        IGridNode node = this.getMainNode().getNode();
        if (node instanceof InWorldGridNode inWorld && inWorld.isExposedOnSide(dir))
            return node;
        return null;
    }
}
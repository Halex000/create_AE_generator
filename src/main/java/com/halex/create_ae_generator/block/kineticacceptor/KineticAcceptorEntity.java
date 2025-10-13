package com.halex.create_ae_generator.block.kineticacceptor;

import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IPassiveEnergyGenerator;
import appeng.api.orientation.BlockOrientation;
import com.halex.create_ae_generator.CommonConfig;
import com.halex.create_ae_generator.CreateAEGenerator;
import com.halex.create_ae_generator.block.NetworkedKineticBlockEntity;
import com.halex.create_ae_generator.index.BlockRegistry;
import com.halex.create_ae_generator.index.LangRegistry;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class KineticAcceptorEntity extends NetworkedKineticBlockEntity {
    private double AEvalue;
    private final EnumSet<Direction> invalidSides = EnumSet.allOf(Direction.class);
    private final EnumMap<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> cache = new EnumMap<>(Direction.class);
    private boolean firstTick = true;

    public KineticAcceptorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.getMainNode().setIdlePowerUsage((double)0.0F);

        IPassiveEnergyGenerator passiveGenerator = new IPassiveEnergyGenerator() {
            @Override
            public double getRate() {
                return AEvalue;
            }

            @Override
            public void setSuppressed(boolean b) { }

            @Override
            public boolean isSuppressed() {
                return KineticAcceptorEntity.this.level == null
                        || KineticAcceptorEntity.this.level.isClientSide
                        || Math.abs(getSpeed()) == 0;
            }
        };

        this.getMainNode().addService(IPassiveEnergyGenerator.class, passiveGenerator);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangRegistry.builder()
                .add(Component.translatable(CreateAEGenerator.MOD_ID + ".tooltip.kinetic_acceptor.generator_stats").withStyle(ChatFormatting.WHITE))
                .forGoggles(tooltip);
        LangRegistry.builder()
                .add(Component.translatable(CreateAEGenerator.MOD_ID + ".tooltip.kinetic_acceptor.energy_production").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        LangRegistry.builder()
                .add(Component.literal(" " + (int) Math.round(this.AEvalue) + " AE/t ").withStyle(ChatFormatting.GOLD))
                .add(CreateLang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        LangRegistry.builder()
                .add(Component.translatable(CreateAEGenerator.MOD_ID + ".tooltip.kinetic_acceptor.kinetic_stress_impact").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
        LangRegistry.builder()
                .add(Component.literal(" " + (int) (this.lastStressApplied * Math.abs(getSpeed())) + "su ").withStyle(ChatFormatting.AQUA))
                .add(CreateLang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip);
        return true;
    }

    @Override
    public float calculateStressApplied() {
        float impact = CommonConfig.MAX_STRESS.get() / 256f;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        Direction shaft = getBlockState().getValue(KineticAcceptor.FACING);
        Set<Direction> sides = EnumSet.complementOf(EnumSet.of(shaft, shaft.getOpposite()));
        getMainNode().setExposedOnSides(sides);
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        Direction shaft = getBlockState().getValue(KineticAcceptor.FACING);
        return EnumSet.complementOf(EnumSet.of(shaft, shaft.getOpposite()));
    }

    protected Item getItemFromBlockEntity() {
        return BlockRegistry.KINETIC_ACCEPTOR.asItem();
    }

    @Override
    public void onReady() {
        super.onReady();

        IGrid grid = getMainNode().getGrid();
        if (grid == null) return;

        this.getMainNode().ifPresent((g, node) -> g.getTickManager().wakeDevice(node));
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider prov) {
        super.writeSafe(tag, prov);
        getMainNode().saveToNBT(tag);
    }

    @Override
    public void tick() {
        super.tick();
        double newAE = CommonConfig.AE_RPM.get() * (Math.abs((int) getSpeed()) / 256d);
        if (Double.compare(newAE, this.AEvalue) != 0) {
            this.AEvalue = newAE;
            this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        assert level != null;
        if (level.isClientSide) return;

        if (this.firstTick) {
            this.firstTick = false;
            this.notifyUpdate();
        }
    }

    @Override
    protected Block getStressConfigKey() {
        return BlockRegistry.KINETIC_ACCEPTOR.get();
    }

    public void updateCache() {
        if (level == null || level.isClientSide) return;
        for (Direction side : Direction.values()) {
            cache.put(side, BlockCapabilityCache.create(
                    Capabilities.EnergyStorage.BLOCK,
                    (ServerLevel) level,
                    getBlockPos().relative(side),
                    side.getOpposite(),
                    () -> !this.isRemoved(),
                    () -> invalidSides.add(side)
            ));
        }
    }
}



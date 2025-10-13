package com.halex.create_ae_generator.event;

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import com.halex.create_ae_generator.CreateAEGenerator;

import com.halex.create_ae_generator.block.NetworkedKineticBlockEntity;
import com.halex.create_ae_generator.index.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = CreateAEGenerator.MOD_ID)
public class EventHandler extends Event {

    @SubscribeEvent
    public static void onBlockBroken(BreakEvent ev) {
        Level world = (Level) ev.getLevel();
        if (world.isClientSide()) return;

        BlockPos pos = ev.getPos();
        if (!(world.getBlockEntity(pos) instanceof NetworkedKineticBlockEntity gridBE)) return;

        IManagedGridNode node = gridBE.getMainNode();
        node.destroy();
        gridBE.saveChanges();

        for (Direction d : Direction.values()) {
            BlockPos nPos = pos.relative(d);
            BlockState nState = world.getBlockState(nPos);
            world.neighborChanged(nState, nPos, Blocks.AIR, pos, false);
            world.sendBlockUpdated(nPos, nState, nState, Block.UPDATE_ALL);
        }
    }

    @SubscribeEvent
    public static void onRegisterCaps(RegisterCapabilitiesEvent evt) {
        evt.registerBlock(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                (Level level,
                 BlockPos pos,
                 BlockState state,
                 @Nullable BlockEntity be,
                 Void context) -> {
                    if (be instanceof IInWorldGridNodeHost host) {
                        return host;
                    }
                    return null;
                },
                BlockRegistry.KINETIC_ACCEPTOR.get()
        );
    }
}

package com.halex.create_ae_generator.block.kineticacceptor;

import appeng.api.orientation.BlockOrientation;
import com.halex.create_ae_generator.index.BlockEntityRegistry;
import com.halex.create_ae_generator.shapes.BlockShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KineticAcceptor extends DirectionalKineticBlock implements IBE<KineticAcceptorEntity>, IRotate  {

    public static final VoxelShaper HORIZ_SHAPE = BlockShapes
            .shape(0,0,4, 16,16,12)
            .add(1,1,2,15,15,14)
            .add(4,4,1, 12,12,15)
            .add(5,5,0, 11,11,16)
            .forDirectional(Direction.NORTH);
    public static final VoxelShaper VERT_SHAPE = BlockShapes
            .shape(0,4,0, 16,12,16)
            .add(1,2,1,15,14,15)
            .add(4,1,4, 12,15,12)
            .add(5,0,5, 11,16,11)
            .forDirectional(Direction.DOWN);

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        Direction f = state.getValue(FACING);
        return (f.getAxis() == Axis.Y ? VERT_SHAPE.get(f) : HORIZ_SHAPE.get(f));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) || preferred == null)
            return super.getStateForPlacement(context);
        return defaultBlockState().setValue(FACING, preferred);
    }

    public KineticAcceptor(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING) || face == state.getValue(FACING).getOpposite();
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public BlockEntityType<? extends KineticAcceptorEntity> getBlockEntityType() {
        return BlockEntityRegistry.KINETIC_ACCEPTOR.get();
    }

    @Override
    public Class<KineticAcceptorEntity> getBlockEntityClass() {
        return KineticAcceptorEntity.class;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.KINETIC_ACCEPTOR.create(pos, state);
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.SLOW;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult result = super.onWrenched(state, context);
        if (!result.consumesAction() || context.getLevel().isClientSide) {
            return result;
        }
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof KineticAcceptorEntity ke) {
            BlockState newState = world.getBlockState(pos);
            BlockOrientation ori = BlockOrientation.get(newState);
            ke.getMainNode().setExposedOnSides(ke.getGridConnectableSides(ori));
            ke.getMainNode().ifPresent((grid, node) ->
                    grid.getTickManager().wakeDevice(node)
            );
            ke.saveChanges();
        }
        return result;
    }
}
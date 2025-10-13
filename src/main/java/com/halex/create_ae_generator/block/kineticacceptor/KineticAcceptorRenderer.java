package com.halex.create_ae_generator.block.kineticacceptor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class KineticAcceptorRenderer extends KineticBlockEntityRenderer<KineticAcceptorEntity> {

    public KineticAcceptorRenderer(Context dispatcher) {
        super(dispatcher);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticAcceptorEntity be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT, state);
    }

    @Override
    protected void renderSafe(KineticAcceptorEntity be, float pt, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
        super.renderSafe(be, pt, ms, buf, light, overlay);
        BlockState shaftState = KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
        KineticBlockEntityRenderer.renderRotatingKineticBlock(be, shaftState, ms, buf.getBuffer(RenderType.cutoutMipped()), light);
    }
}
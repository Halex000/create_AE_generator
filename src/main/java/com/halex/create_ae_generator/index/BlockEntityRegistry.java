package com.halex.create_ae_generator.index;

import static com.halex.create_ae_generator.CreateAEGenerator.REGISTRATE;

import com.halex.create_ae_generator.block.kineticacceptor.KineticAcceptorEntity;
import com.halex.create_ae_generator.block.kineticacceptor.KineticAcceptorRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class BlockEntityRegistry {
    public static final BlockEntityEntry<KineticAcceptorEntity> KINETIC_ACCEPTOR = REGISTRATE
        .blockEntity("kinetic_acceptor", KineticAcceptorEntity::new)
        .validBlocks(BlockRegistry.KINETIC_ACCEPTOR)
        .renderer(() -> KineticAcceptorRenderer::new)
        .register();

    public static void register() {}
}
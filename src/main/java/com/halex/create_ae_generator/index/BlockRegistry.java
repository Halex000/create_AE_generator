package com.halex.create_ae_generator.index;

import com.halex.create_ae_generator.block.kineticacceptor.KineticAcceptor;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import static com.halex.create_ae_generator.CreateAEGenerator.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class BlockRegistry {
    public static final BlockEntry<KineticAcceptor> KINETIC_ACCEPTOR = REGISTRATE
            .block("kinetic_acceptor", KineticAcceptor::new)
            .initialProperties(SharedProperties::softMetal)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .blockstate((c, p) -> p.simpleBlock(c.get()))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
    }
}
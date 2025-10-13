package com.halex.create_ae_generator.index;

import com.halex.create_ae_generator.CreateAEGenerator;
import net.createmod.catnip.lang.LangBuilder;

public class LangRegistry {
    public static LangBuilder builder() {
        return new LangBuilder(CreateAEGenerator.MOD_ID);
    }
}
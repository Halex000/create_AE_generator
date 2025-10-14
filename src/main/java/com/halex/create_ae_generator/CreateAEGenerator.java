package com.halex.create_ae_generator;

import com.halex.create_ae_generator.index.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;

@Mod(CreateAEGenerator.MOD_ID)
public class CreateAEGenerator {

    public static final String MOD_ID = "create_ae_generator";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateAEGenerator.MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public CreateAEGenerator(IEventBus eventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        REGISTRATE.registerEventListeners(eventBus);

        BlockRegistry.register();
        BlockEntityRegistry.register();
        ItemRegistry.register();
        CreateAEGeneratorTabs.register(eventBus);
        eventBus.addListener(this::setup);
        eventBus.addListener(this::setupClient);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void setupClient(final FMLClientSetupEvent event) {
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }


}
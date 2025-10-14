package com.halex.create_ae_generator;


import com.halex.create_ae_generator.index.BlockRegistry;
import com.halex.create_ae_generator.index.ItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreateAEGeneratorTabs {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, "create_ae_generator");
    public static final Holder<CreativeModeTab> BASE = TAB_REGISTER.register("base", CreateAEGeneratorTabs::base);

    public static void register(IEventBus modBus) {
        TAB_REGISTER.register(modBus);
    }
    private static CreativeModeTab base(ResourceLocation id) {
        return CreativeModeTab.builder()
                .title(Component.translatable("item_group.create_ae_generator.tabs"))
                .icon(BlockRegistry.KINETIC_ACCEPTOR::asStack)
                .displayItems(CreateAEGeneratorTabs::buildBaseContents)
                .build();
    }

    private static void buildBaseContents(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(BlockRegistry.KINETIC_ACCEPTOR.get());
        output.accept(ItemRegistry.COPPER_COIL.get());
        output.accept(ItemRegistry.STATOR.get());
    }
}
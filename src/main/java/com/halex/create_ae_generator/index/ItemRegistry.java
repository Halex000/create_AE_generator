package com.halex.create_ae_generator.index;

import com.halex.create_ae_generator.CreateAEGenerator;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

public class ItemRegistry {

    public static final ItemEntry<Item> COPPER_COIL =
            CreateAEGenerator.REGISTRATE.item("copper_coil", Item::new)
                    .register();

    public static final ItemEntry<Item> STATOR =
            CreateAEGenerator.REGISTRATE.item("stator", Item::new)
                    .register();

    public static void register() {
    }
}
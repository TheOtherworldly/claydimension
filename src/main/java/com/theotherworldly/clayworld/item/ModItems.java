package com.theotherworldly.clayworld.item;

import com.theotherworldly.clayworld.ClayWorld;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ClayWorld.MOD_ID);
    public static final RegistryObject<Item> PAINT_WAND = ITEMS.register("paint_wand", PaintWand::new);

    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }
}

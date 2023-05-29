package com.theotherworldly.clayworld;

import com.theotherworldly.clayworld.ClayWorld;
import com.theotherworldly.clayworld.world.gen.ClayChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModDimensions {

    public static final DeferredRegister<PointOfInterestType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, ClayWorld.MOD_ID);
    public static final RegistryObject<PointOfInterestType> CLAY_PORTAL = POI_TYPES.register("clay_portal", () -> new PointOfInterestType("clay_portal", PointOfInterestType.getBlockStates(Blocks.TERRACOTTA.getBlock()), 0, 1));
    public static RegistryKey<World> clayworld = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(ClayWorld.MOD_ID, "clayworld"));
    public static final RegistryKey<DimensionType> clay_dimension = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(ClayWorld.MOD_ID, "clayworld"));
    public static void initDimension() {
        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(ClayWorld.MOD_ID, "clayworld"), ClayBiomeProvider.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(ClayWorld.MOD_ID, "clayworld"), ClayChunkGenerator.CODEC);
    }
}

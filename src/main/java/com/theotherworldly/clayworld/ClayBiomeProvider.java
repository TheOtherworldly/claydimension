package com.theotherworldly.clayworld;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.theotherworldly.clayworld.world.gen.ClayChunkGenerator;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.layer.Layer;

import java.util.List;

import static com.theotherworldly.clayworld.world.biome.ModBiomes.*;

public class ClayBiomeProvider extends BiomeProvider {

    public static final Codec<ClayBiomeProvider> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.LONG.fieldOf("seed")
                    .orElse(ClayChunkGenerator.hackSeed)
                    .forGetter((obj) -> obj.seed),
            RegistryLookupCodec.create(Registry.BIOME_REGISTRY)
                    .forGetter((obj) -> obj.registry)
    ).apply(instance, instance.stable(ClayBiomeProvider::new)));

    private final long seed;
    private final Registry<Biome> registry;
    private final Layer genBiomes;
    private static final List<RegistryKey<Biome>> biomes = ImmutableList.of(
           CLAY_FOREST_BIOME
    );

    public ClayBiomeProvider(long seed, Registry<Biome> registry) {
        super(biomes.stream().map(define -> () -> registry.getOrThrow(define)));
        this.seed = seed;
        this.registry = registry;
        this.genBiomes = ClayLayerUtil.makeLayers(seed, registry);
    }

    @Override
    public BiomeProvider withSeed(long s) {
        return new ClayBiomeProvider(s, registry);
    }

    @Override
    protected Codec<? extends BiomeProvider> codec() {
        return CODEC;
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return this.getBiomeFromPos(registry, x, z);
    }

    public Biome getBiomeFromPos(Registry<Biome> registry, int x, int z) {
        int i = genBiomes.area.get(x, z);
        Biome biome = registry.byId(i);
        if (biome == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw Util.pauseInIde(new IllegalStateException("Unknown biome id: " + i));
            } else {
                return registry.get(BiomeRegistry.byId(0));
            }
        } else {
            return biome;
        }
    }
}
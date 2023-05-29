package com.theotherworldly.clayworld.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.*;

import java.util.function.Supplier;

public class ClayChunkGenerator extends NoiseChunkGenerator {

    public static final Codec<ClayChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeProvider.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    Codec.LONG.fieldOf("seed")
                            .orElseGet(() -> ClayChunkGenerator.hackSeed)
                            .forGetter((obj) -> obj.seed),
                    DimensionSettings.CODEC.fieldOf("settings")
                            .forGetter(ClayChunkGenerator::getDimensionSettings)
            ).apply(instance, instance.stable(ClayChunkGenerator::new)));

    private long seed;
    public static long hackSeed; //DON'T TOUCH

    public ClayChunkGenerator(BiomeProvider provider, long seed, Supplier<DimensionSettings> settingsIn) {
//        super(worldIn, provider, 4, 8, 256, settingsIn, true);
        super(provider, seed, settingsIn);
        this.seed = seed;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new ClayChunkGenerator(biomeSource.withSeed(seed), seed, getDimensionSettings());
    }

    private Supplier<DimensionSettings> getDimensionSettings() {
        return settings;
    }
}

package com.theotherworldly.clayworld;

import com.theotherworldly.clayworld.world.biome.ModBiomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public class ClayBiomesLayer implements IAreaTransformer0 {

    private static final int UNCOMMON_BIOME_CHANCE = 8;
    private static final int RARE_BIOME_CHANCE = 16;
    protected int[] commonBiomes = new int[]{
            ClayLayerUtil.getBiomeId(ModBiomes.CLAY_FOREST_BIOME),
    };
    protected int[] uncommonBiomes = (new int[]{
    });
    protected int[] rareBiomes = (new int[]{
    });

    public ClayBiomesLayer() { }

    @Override
    public int applyPixel(INoiseRandom iNoiseRandom, int rand1, int rand2) {
        if (iNoiseRandom.nextRandom(RARE_BIOME_CHANCE) == 0) {
            return rareBiomes[iNoiseRandom.nextRandom(rareBiomes.length)];
        } else if (iNoiseRandom.nextRandom(UNCOMMON_BIOME_CHANCE) == 0) {
            return uncommonBiomes[iNoiseRandom.nextRandom(uncommonBiomes.length)];
        } else {
            return commonBiomes[iNoiseRandom.nextRandom(commonBiomes.length)];
        }
    }
}

package com.theotherworldly.clayworld.item;

import com.theotherworldly.clayworld.ModDimensions;
import com.theotherworldly.clayworld.block.ClayPortalBlock;
import com.theotherworldly.clayworld.block.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PaintWand extends Item {

    public PaintWand() {
        super(new Properties()
                .tab(ItemGroup.TAB_TOOLS)
                .stacksTo(1)
                .rarity(Rarity.RARE)
        );
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getPlayer() != null) {
            if(context.getPlayer().level.dimension() == ModDimensions.clayworld || context.getPlayer().level.dimension() == World.OVERWORLD) {
                for(Direction direction : Direction.Plane.VERTICAL) {
                    BlockPos framePos = context.getClickedPos().relative(direction);
                    if(((ClayPortalBlock) ModBlocks.CLAY_PORTAL.get()).trySpawnPortal(context.getLevel(), framePos)) {
                        context.getLevel().playSound(context.getPlayer(), framePos, SoundEvents.PORTAL_TRIGGER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return ActionResultType.CONSUME;
                    }
                    else return ActionResultType.FAIL;
                }
            }
        }
        return ActionResultType.FAIL;
    }
}

package com.theotherworldly.clayworld.block;

import com.theotherworldly.clayworld.ModDimensions;
import com.theotherworldly.clayworld.world.dimension.ClayTeleporter;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;
import java.util.Random;


public class ClayPortalBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public ClayPortalBlock() {
        super(Properties.of(Material.PORTAL)
                .strength(-1F)
                .noCollission()
                .lightLevel((state) -> 10)
                .noDrops()
        );
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }
    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(AXIS)) {
            case Z:
                return Z_AABB;
            case X:
            default:
                return X_AABB;
        }
    }

    public boolean trySpawnPortal(IWorld worldIn, BlockPos pos) {
       ClayPortalBlock.Size clayPortalBlockSize = this.isPortal(worldIn, pos);
        if (clayPortalBlockSize != null && !onTrySpawnPortal(worldIn, pos, clayPortalBlockSize)) {
            clayPortalBlockSize.createPortalBlocks();
            return true;
        } else {
            return false;
        }
    }
    public static boolean onTrySpawnPortal(IWorld world, BlockPos pos, Size size) {
        return MinecraftForge.EVENT_BUS.post(new PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
    }
    @Cancelable
    public static class PortalSpawnEvent extends BlockEvent {
        private final ClayPortalBlock.Size size;

        public PortalSpawnEvent(IWorld world, BlockPos pos, BlockState state, ClayPortalBlock.Size size) {
            super(world, pos, state);
            this.size = size;
        }

        public ClayPortalBlock.Size getPortalSize()
        {
            return size;
        }
    }
    @Nullable
    public ClayPortalBlock.Size isPortal(IWorld worldIn, BlockPos pos) {
        ClayPortalBlock.Size clayPortalBlockSize = new Size(worldIn, pos, Direction.Axis.X);
        if (clayPortalBlockSize.isValid() && clayPortalBlockSize.numPortalBlocks == 0) {
            return clayPortalBlockSize;
        } else {
            ClayPortalBlock.Size clayPortalBlockSize1 = new Size(worldIn, pos, Direction.Axis.Z);
            return clayPortalBlockSize1.isValid() && clayPortalBlockSize1.numPortalBlocks ==0 ? clayPortalBlockSize1 : null;
        }
    }

        @Deprecated
        @Override
        public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
            Direction.Axis direction$axis = facing.getAxis();
            Direction.Axis direction$axis1 = stateIn.getValue(AXIS);
            boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
            return !flag && facingState.getBlock() != this && !(new Size(worldIn, currentPos, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }

        @Override
        public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entity) {
            if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
                if(entity.isOnPortalCooldown()) {
                    entity.setPortalCooldown();
                     }
             World entityWorld = entity.level;
                if(entityWorld != null) {
                     MinecraftServer minecraftserver = entityWorld.getServer();
                        RegistryKey<World> destination = entity.level.dimension() == ModDimensions.clayworld ? World.OVERWORLD : ModDimensions.clayworld;
            if(minecraftserver != null) {
                ServerWorld destinationWorld = minecraftserver.getLevel(destination);
                if(destinationWorld != null && minecraftserver.isNetherEnabled() && !entity.isPassenger()) {
                    entity.level.getProfiler().push("clay_portal");
                    entity.setPortalCooldown();
                    entity.changeDimension(destinationWorld, new ClayTeleporter(destinationWorld));
                    entity.level.getProfiler().pop();
                }
            }
        }
    }
}


        @OnlyIn(Dist.CLIENT)
        @Override
        public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
            if (rand.nextInt(100) == 0) {
                worldIn.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
            }

            for (int i = 0; i < 4; ++i) {
                double d0 = (double) pos.getX() + rand.nextDouble();
                double d1 = (double) pos.getY() + rand.nextDouble();
                double d2 = (double) pos.getZ() + rand.nextDouble();
                double d3 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
                double d4 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
                double d5 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
                int j = rand.nextInt(2) * 2 - 1;
                if (!worldIn.getBlockState(pos.west()).is(this) && !worldIn.getBlockState(pos.east()).is(this)) {
                    d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
                    d3 = (double) (rand.nextFloat() * 2.0F * (float) j);
                } else {
                    d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) j;
                    d5 = (double) (rand.nextFloat() * 2.0F * (float) j);
                }

                worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
            }

        }


        public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
            return ItemStack.EMPTY;
        }

        @Override
        public BlockState rotate(BlockState state, Rotation rot) {
            switch (rot) {
                case COUNTERCLOCKWISE_90:
                case CLOCKWISE_90:
                    switch (state.getValue(AXIS)) {
                        case Z:
                            return state.setValue(AXIS, Direction.Axis.X);
                        case X:
                            return state.setValue(AXIS, Direction.Axis.Z);
                        default:
                            return state;
                    }
                default:
                    return state;
            }
        }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
    public static class Size {
        private static final IPositionPredicate FRAME_TEST = (state, reader, pos) -> state.getBlock() == Blocks.TERRACOTTA.getBlock();
        private final IWorld level;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private int numPortalBlocks;
        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;

        public Size(IWorld world, BlockPos block, Direction.Axis axis) {

            this.level = world;
            this.axis = axis;
            this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
            this.bottomLeft = this.calculateBottomLeft(block);
            if (this.bottomLeft == null) {
                this.bottomLeft = block;
                this.width = 1;
                this.height = 1;
            } else {
                this.width = this.calculateWidth();
                if (this.width > 0) {
                    this.height = this.calculateHeight();
                }
            }

        }

        @Nullable
        private BlockPos calculateBottomLeft(BlockPos p_242971_1_) {
            for (int i = Math.max(0, p_242971_1_.getY() - 21); p_242971_1_.getY() > i && isEmpty(this.level.getBlockState(p_242971_1_.below())); p_242971_1_ = p_242971_1_.below()) {
            }

            Direction direction = this.rightDir.getOpposite();
            int j = this.getDistanceUntilEdgeAboveFrame(p_242971_1_, direction) - 1;
            return j < 0 ? null : p_242971_1_.relative(direction, j);
        }

        private int calculateWidth() {
            int i = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
            return i >= 2 && i <= 21 ? i : 0;
        }

        private int getDistanceUntilEdgeAboveFrame(BlockPos p_242972_1_, Direction p_242972_2_) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (int i = 0; i < 22; ++i) {
                mutable.set(p_242972_1_).move(p_242972_2_, i);
                BlockState state = this.level.getBlockState(mutable);

                if (!isEmpty(state)) {
                    if (FRAME_TEST.test(state, level, mutable)) {
                        return i;
                    }
                    break;
                }

                BlockState state1 = this.level.getBlockState(mutable.move(Direction.DOWN));
                if (!FRAME_TEST.test(state1, level, mutable)) {
                    break;
                }
            }

            return 0;
        }

        private int calculateHeight() {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            int i = this.getDistanceUntilTop(blockpos$mutable);
            return i >= 3 && i <= 21 && this.hasTopFrame(blockpos$mutable, i) ? i : 0;
        }

        private boolean hasTopFrame(BlockPos.Mutable p_242970_1_, int p_242970_2_) {
            for (int i = 0; i < this.width; i++) {
                assert this.bottomLeft != null;
                BlockPos.Mutable blockpos$mutable = p_242970_1_.set(this.bottomLeft).move(Direction.UP, p_242970_2_).move(this.rightDir, i);
                if (!FRAME_TEST.test(this.level.getBlockState(p_242970_1_), level, blockpos$mutable)) {
                    return false;
                }
            }

            return true;
        }


        private int getDistanceUntilTop(BlockPos.Mutable p_242969_1_) {
            for (int i = 0; i < 21; ++i) {
                assert this.bottomLeft != null;
                p_242969_1_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
                if (!FRAME_TEST.test(this.level.getBlockState(p_242969_1_), this.level, p_242969_1_)) {
                    return i;
                }

                p_242969_1_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
                if (!FRAME_TEST.test(this.level.getBlockState(p_242969_1_), this.level, p_242969_1_)) {
                    return i;
                }

                for (int j = 0; j < this.width; ++j) {
                    p_242969_1_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                    BlockState blockstate = this.level.getBlockState(p_242969_1_);
                    if (!isEmpty(blockstate)) {
                        return i;
                    }

                    if (blockstate.getBlock() == (ModBlocks.CLAY_PORTAL.get())) {
                        ++this.numPortalBlocks;
                    }
                }
            }

            return 21;
        }


        private boolean isEmpty(BlockState p_196900_0_) {
            return p_196900_0_.isAir() || p_196900_0_.getBlock() == ModBlocks.CLAY_PORTAL.get();
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void createPortalBlocks() {
            BlockState blockstate = ModBlocks.CLAY_PORTAL.get().defaultBlockState().setValue(ClayPortalBlock.AXIS, this.axis);
            assert this.bottomLeft != null;
            BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((p_242967_2_) -> {
                this.level.setBlock(p_242967_2_, blockstate, 18);
            });
        }

        public boolean isComplete() {
            return this.isValid() && this.numPortalBlocks == this.width * this.height;
        }

        public Vector3d getRelativePosition(TeleportationRepositioner.Result p_242973_0_, Direction.Axis p_242973_1_, Vector3d p_242973_2_, EntitySize p_242973_3_) {
            double d0 = (double) p_242973_0_.axis1Size - (double) p_242973_3_.width;
            double d1 = (double) p_242973_0_.axis2Size - (double) p_242973_3_.height;
            BlockPos blockpos = p_242973_0_.minCorner;
            double d2;
            if (d0 > 0.0D) {
                float f = (float) blockpos.get(p_242973_1_) + p_242973_3_.width / 2.0F;
                d2 = MathHelper.clamp(MathHelper.inverseLerp(p_242973_2_.get(p_242973_1_) - (double) f, 0.0D, d0), 0.0D, 1.0D);
            } else {
                d2 = 0.5D;
            }

            double d4;
            if (d1 > 0.0D) {
                Direction.Axis direction$axis = Direction.Axis.Y;
                d4 = MathHelper.clamp(MathHelper.inverseLerp(p_242973_2_.get(direction$axis) - (double) blockpos.get(direction$axis), 0.0D, d1), 0.0D, 1.0D);
            } else {
                d4 = 0.0D;
            }

            Direction.Axis direction$axis1 = p_242973_1_ == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            double d3 = p_242973_2_.get(direction$axis1) - ((double) blockpos.get(direction$axis1) + 0.5D);
            return new Vector3d(d2, d4, d3);
        }

        public PortalInfo createPortalInfo(ServerWorld p_242963_0_, TeleportationRepositioner.Result p_242963_1_, Direction.Axis p_242963_2_, Vector3d p_242963_3_, EntitySize p_242963_4_, Vector3d p_242963_5_, float p_242963_6_, float p_242963_7_) {
            BlockPos blockpos = p_242963_1_.minCorner;
            BlockState blockstate = p_242963_0_.getBlockState(blockpos);
            Direction.Axis direction$axis = blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            double d0 = (double) p_242963_1_.axis1Size;
            double d1 = (double) p_242963_1_.axis2Size;
            int i = p_242963_2_ == direction$axis ? 0 : 90;
            Vector3d vector3d = p_242963_2_ == direction$axis ? p_242963_5_ : new Vector3d(p_242963_5_.z, p_242963_5_.y, -p_242963_5_.x);
            double d2 = (double) p_242963_4_.width / 2.0D + (d0 - (double) p_242963_4_.width) * p_242963_3_.x();
            double d3 = (d1 - (double) p_242963_4_.height) * p_242963_3_.y();
            double d4 = 0.5D + p_242963_3_.z();
            boolean flag = direction$axis == Direction.Axis.X;
            Vector3d vector3d1 = new Vector3d((double) blockpos.getX() + (flag ? d2 : d4), (double) blockpos.getY() + d3, (double) blockpos.getZ() + (flag ? d4 : d2));
            return new PortalInfo(vector3d1, vector3d, p_242963_6_ + (float) i, p_242963_7_);
        }
    }

}

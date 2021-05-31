package net.minecraft.world.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.InteractionResult;

public class BoneMealItem extends Item {
    public BoneMealItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockPos ew5 = ew4.relative(bdu.getClickedFace());
        if (growCrop(bdu.getItemInHand(), bhr3, ew4)) {
            if (!bhr3.isClientSide) {
                bhr3.levelEvent(2005, ew4, 0);
            }
            return InteractionResult.SUCCESS;
        }
        final BlockState bvt6 = bhr3.getBlockState(ew4);
        final boolean boolean7 = bvt6.isFaceSturdy(bhr3, ew4, bdu.getClickedFace());
        if (boolean7 && growWaterPlant(bdu.getItemInHand(), bhr3, ew5, bdu.getClickedFace())) {
            if (!bhr3.isClientSide) {
                bhr3.levelEvent(2005, ew5, 0);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    public static boolean growCrop(final ItemStack bcj, final Level bhr, final BlockPos ew) {
        final BlockState bvt4 = bhr.getBlockState(ew);
        if (bvt4.getBlock() instanceof BonemealableBlock) {
            final BonemealableBlock bmx5 = (BonemealableBlock)bvt4.getBlock();
            if (bmx5.isValidBonemealTarget(bhr, ew, bvt4, bhr.isClientSide)) {
                if (!bhr.isClientSide) {
                    if (bmx5.isBonemealSuccess(bhr, bhr.random, ew, bvt4)) {
                        bmx5.performBonemeal(bhr, bhr.random, ew, bvt4);
                    }
                    bcj.shrink(1);
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean growWaterPlant(final ItemStack bcj, final Level bhr, final BlockPos ew, @Nullable final Direction fb) {
        if (bhr.getBlockState(ew).getBlock() == Blocks.WATER && bhr.getFluidState(ew).getAmount() == 8) {
            if (!bhr.isClientSide) {
                int integer5 = 0;
            Label_0039:
                while (integer5 < 128) {
                    BlockPos ew2 = ew;
                    Biome bio7 = bhr.getBiome(ew2);
                    BlockState bvt8 = Blocks.SEAGRASS.defaultBlockState();
                    while (true) {
                        for (int integer6 = 0; integer6 < integer5 / 16; ++integer6) {
                            ew2 = ew2.offset(BoneMealItem.random.nextInt(3) - 1, (BoneMealItem.random.nextInt(3) - 1) * BoneMealItem.random.nextInt(3) / 2, BoneMealItem.random.nextInt(3) - 1);
                            bio7 = bhr.getBiome(ew2);
                            if (bhr.getBlockState(ew2).isCollisionShapeFullBlock(bhr, ew2)) {
                                ++integer5;
                                continue Label_0039;
                            }
                        }
                        if (bio7 == Biomes.WARM_OCEAN || bio7 == Biomes.DEEP_WARM_OCEAN) {
                            if (integer5 == 0 && fb != null && fb.getAxis().isHorizontal()) {
                                bvt8 = ((AbstractStateHolder<O, BlockState>)BlockTags.WALL_CORALS.getRandomElement(bhr.random).defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)BaseCoralWallFanBlock.FACING, fb);
                            }
                            else if (BoneMealItem.random.nextInt(4) == 0) {
                                bvt8 = BlockTags.UNDERWATER_BONEMEALS.getRandomElement(BoneMealItem.random).defaultBlockState();
                            }
                        }
                        if (bvt8.getBlock().is(BlockTags.WALL_CORALS)) {
                            for (int integer6 = 0; !bvt8.canSurvive(bhr, ew2) && integer6 < 4; bvt8 = ((AbstractStateHolder<O, BlockState>)bvt8).<Comparable, Direction>setValue((Property<Comparable>)BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(BoneMealItem.random)), ++integer6) {}
                        }
                        if (!bvt8.canSurvive(bhr, ew2)) {
                            continue;
                        }
                        final BlockState bvt9 = bhr.getBlockState(ew2);
                        if (bvt9.getBlock() == Blocks.WATER && bhr.getFluidState(ew2).getAmount() == 8) {
                            bhr.setBlock(ew2, bvt8, 3);
                            continue;
                        }
                        if (bvt9.getBlock() == Blocks.SEAGRASS && BoneMealItem.random.nextInt(10) == 0) {
                            ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal(bhr, BoneMealItem.random, ew2, bvt9);
                        }
                        continue;
                    }
                }
                bcj.shrink(1);
            }
            return true;
        }
        return false;
    }
    
    public static void addGrowthParticles(final LevelAccessor bhs, final BlockPos ew, int integer) {
        if (integer == 0) {
            integer = 15;
        }
        final BlockState bvt4 = bhs.getBlockState(ew);
        if (bvt4.isAir()) {
            return;
        }
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            final double double6 = BoneMealItem.random.nextGaussian() * 0.02;
            final double double7 = BoneMealItem.random.nextGaussian() * 0.02;
            final double double8 = BoneMealItem.random.nextGaussian() * 0.02;
            bhs.addParticle(ParticleTypes.HAPPY_VILLAGER, ew.getX() + BoneMealItem.random.nextFloat(), ew.getY() + BoneMealItem.random.nextFloat() * bvt4.getShape(bhs, ew).max(Direction.Axis.Y), ew.getZ() + BoneMealItem.random.nextFloat(), double6, double7, double8);
        }
    }
}

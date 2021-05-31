package net.minecraft.world.level.pathfinder;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseRailBlock;
import java.util.EnumSet;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.level.block.state.BlockState;
import com.google.common.collect.Sets;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;

public class WalkNodeEvaluator extends NodeEvaluator {
    protected float oldWaterCost;
    
    @Override
    public void prepare(final LevelReader bhu, final Mob aiy) {
        super.prepare(bhu, aiy);
        this.oldWaterCost = aiy.getPathfindingMalus(BlockPathTypes.WATER);
    }
    
    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        super.done();
    }
    
    @Override
    public Node getStart() {
        int integer2;
        if (this.canFloat() && this.mob.isInWater()) {
            integer2 = Mth.floor(this.mob.getBoundingBox().minY);
            final BlockPos.MutableBlockPos a3 = new BlockPos.MutableBlockPos(this.mob.x, integer2, this.mob.z);
            for (BlockState bvt4 = this.level.getBlockState(a3); bvt4.getBlock() == Blocks.WATER || bvt4.getFluidState() == Fluids.WATER.getSource(false); bvt4 = this.level.getBlockState(a3)) {
                ++integer2;
                a3.set(this.mob.x, integer2, this.mob.z);
            }
            --integer2;
        }
        else if (this.mob.onGround) {
            integer2 = Mth.floor(this.mob.getBoundingBox().minY + 0.5);
        }
        else {
            BlockPos ew3;
            for (ew3 = new BlockPos(this.mob); (this.level.getBlockState(ew3).isAir() || this.level.getBlockState(ew3).isPathfindable(this.level, ew3, PathComputationType.LAND)) && ew3.getY() > 0; ew3 = ew3.below()) {}
            integer2 = ew3.above().getY();
        }
        BlockPos ew3 = new BlockPos(this.mob);
        final BlockPathTypes cnn4 = this.getBlockPathType(this.mob, ew3.getX(), integer2, ew3.getZ());
        if (this.mob.getPathfindingMalus(cnn4) < 0.0f) {
            final Set<BlockPos> set5 = (Set<BlockPos>)Sets.newHashSet();
            set5.add(new BlockPos(this.mob.getBoundingBox().minX, integer2, this.mob.getBoundingBox().minZ));
            set5.add(new BlockPos(this.mob.getBoundingBox().minX, integer2, this.mob.getBoundingBox().maxZ));
            set5.add(new BlockPos(this.mob.getBoundingBox().maxX, integer2, this.mob.getBoundingBox().minZ));
            set5.add(new BlockPos(this.mob.getBoundingBox().maxX, integer2, this.mob.getBoundingBox().maxZ));
            for (final BlockPos ew4 : set5) {
                final BlockPathTypes cnn5 = this.getBlockPathType(this.mob, ew4);
                if (this.mob.getPathfindingMalus(cnn5) >= 0.0f) {
                    return this.getNode(ew4.getX(), ew4.getY(), ew4.getZ());
                }
            }
        }
        return this.getNode(ew3.getX(), integer2, ew3.getZ());
    }
    
    @Override
    public Target getGoal(final double double1, final double double2, final double double3) {
        return new Target(this.getNode(Mth.floor(double1), Mth.floor(double2), Mth.floor(double3)));
    }
    
    @Override
    public int getNeighbors(final Node[] arr, final Node cnp) {
        int integer4 = 0;
        int integer5 = 0;
        final BlockPathTypes cnn6 = this.getBlockPathType(this.mob, cnp.x, cnp.y + 1, cnp.z);
        if (this.mob.getPathfindingMalus(cnn6) >= 0.0f) {
            integer5 = Mth.floor(Math.max(1.0f, this.mob.maxUpStep));
        }
        final double double7 = getFloorLevel(this.level, new BlockPos(cnp.x, cnp.y, cnp.z));
        final Node cnp2 = this.getLandNode(cnp.x, cnp.y, cnp.z + 1, integer5, double7, Direction.SOUTH);
        if (cnp2 != null && !cnp2.closed && cnp2.costMalus >= 0.0f) {
            arr[integer4++] = cnp2;
        }
        final Node cnp3 = this.getLandNode(cnp.x - 1, cnp.y, cnp.z, integer5, double7, Direction.WEST);
        if (cnp3 != null && !cnp3.closed && cnp3.costMalus >= 0.0f) {
            arr[integer4++] = cnp3;
        }
        final Node cnp4 = this.getLandNode(cnp.x + 1, cnp.y, cnp.z, integer5, double7, Direction.EAST);
        if (cnp4 != null && !cnp4.closed && cnp4.costMalus >= 0.0f) {
            arr[integer4++] = cnp4;
        }
        final Node cnp5 = this.getLandNode(cnp.x, cnp.y, cnp.z - 1, integer5, double7, Direction.NORTH);
        if (cnp5 != null && !cnp5.closed && cnp5.costMalus >= 0.0f) {
            arr[integer4++] = cnp5;
        }
        final Node cnp6 = this.getLandNode(cnp.x - 1, cnp.y, cnp.z - 1, integer5, double7, Direction.NORTH);
        if (this.isDiagonalValid(cnp, cnp3, cnp5, cnp6)) {
            arr[integer4++] = cnp6;
        }
        final Node cnp7 = this.getLandNode(cnp.x + 1, cnp.y, cnp.z - 1, integer5, double7, Direction.NORTH);
        if (this.isDiagonalValid(cnp, cnp4, cnp5, cnp7)) {
            arr[integer4++] = cnp7;
        }
        final Node cnp8 = this.getLandNode(cnp.x - 1, cnp.y, cnp.z + 1, integer5, double7, Direction.SOUTH);
        if (this.isDiagonalValid(cnp, cnp3, cnp2, cnp8)) {
            arr[integer4++] = cnp8;
        }
        final Node cnp9 = this.getLandNode(cnp.x + 1, cnp.y, cnp.z + 1, integer5, double7, Direction.SOUTH);
        if (this.isDiagonalValid(cnp, cnp4, cnp2, cnp9)) {
            arr[integer4++] = cnp9;
        }
        return integer4;
    }
    
    private boolean isDiagonalValid(final Node cnp1, @Nullable final Node cnp2, @Nullable final Node cnp3, @Nullable final Node cnp4) {
        return cnp4 != null && cnp3 != null && cnp2 != null && !cnp4.closed && cnp3.y <= cnp1.y && cnp2.y <= cnp1.y && cnp4.costMalus >= 0.0f && (cnp3.y < cnp1.y || cnp3.costMalus >= 0.0f) && (cnp2.y < cnp1.y || cnp2.costMalus >= 0.0f);
    }
    
    public static double getFloorLevel(final BlockGetter bhb, final BlockPos ew) {
        final BlockPos ew2 = ew.below();
        final VoxelShape ctc4 = bhb.getBlockState(ew2).getCollisionShape(bhb, ew2);
        return ew2.getY() + (ctc4.isEmpty() ? 0.0 : ctc4.max(Direction.Axis.Y));
    }
    
    @Nullable
    private Node getLandNode(final int integer1, int integer2, final int integer3, final int integer4, final double double5, final Direction fb) {
        Node cnp9 = null;
        final BlockPos ew10 = new BlockPos(integer1, integer2, integer3);
        final double double6 = getFloorLevel(this.level, ew10);
        if (double6 - double5 > 1.125) {
            return null;
        }
        BlockPathTypes cnn13 = this.getBlockPathType(this.mob, integer1, integer2, integer3);
        float float14 = this.mob.getPathfindingMalus(cnn13);
        final double double7 = this.mob.getBbWidth() / 2.0;
        if (float14 >= 0.0f) {
            cnp9 = this.getNode(integer1, integer2, integer3);
            cnp9.type = cnn13;
            cnp9.costMalus = Math.max(cnp9.costMalus, float14);
        }
        if (cnn13 == BlockPathTypes.WALKABLE) {
            return cnp9;
        }
        if ((cnp9 == null || cnp9.costMalus < 0.0f) && integer4 > 0 && cnn13 != BlockPathTypes.FENCE && cnn13 != BlockPathTypes.TRAPDOOR) {
            cnp9 = this.getLandNode(integer1, integer2 + 1, integer3, integer4 - 1, double5, fb);
            if (cnp9 != null && (cnp9.type == BlockPathTypes.OPEN || cnp9.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0f) {
                final double double8 = integer1 - fb.getStepX() + 0.5;
                final double double9 = integer3 - fb.getStepZ() + 0.5;
                final AABB csc21 = new AABB(double8 - double7, getFloorLevel(this.level, new BlockPos(double8, integer2 + 1, double9)) + 0.001, double9 - double7, double8 + double7, this.mob.getBbHeight() + getFloorLevel(this.level, new BlockPos(cnp9.x, cnp9.y, cnp9.z)) - 0.002, double9 + double7);
                if (!this.level.noCollision(this.mob, csc21)) {
                    cnp9 = null;
                }
            }
        }
        if (cnn13 == BlockPathTypes.WATER && !this.canFloat()) {
            if (this.getBlockPathType(this.mob, integer1, integer2 - 1, integer3) != BlockPathTypes.WATER) {
                return cnp9;
            }
            while (integer2 > 0) {
                --integer2;
                cnn13 = this.getBlockPathType(this.mob, integer1, integer2, integer3);
                if (cnn13 != BlockPathTypes.WATER) {
                    return cnp9;
                }
                cnp9 = this.getNode(integer1, integer2, integer3);
                cnp9.type = cnn13;
                cnp9.costMalus = Math.max(cnp9.costMalus, this.mob.getPathfindingMalus(cnn13));
            }
        }
        if (cnn13 == BlockPathTypes.OPEN) {
            final AABB csc22 = new AABB(integer1 - double7 + 0.5, integer2 + 0.001, integer3 - double7 + 0.5, integer1 + double7 + 0.5, integer2 + this.mob.getBbHeight(), integer3 + double7 + 0.5);
            if (!this.level.noCollision(this.mob, csc22)) {
                return null;
            }
            if (this.mob.getBbWidth() >= 1.0f) {
                final BlockPathTypes cnn14 = this.getBlockPathType(this.mob, integer1, integer2 - 1, integer3);
                if (cnn14 == BlockPathTypes.BLOCKED) {
                    cnp9 = this.getNode(integer1, integer2, integer3);
                    cnp9.type = BlockPathTypes.WALKABLE;
                    cnp9.costMalus = Math.max(cnp9.costMalus, float14);
                    return cnp9;
                }
            }
            int integer5 = 0;
            final int integer6 = integer2;
            while (cnn13 == BlockPathTypes.OPEN) {
                if (--integer2 < 0) {
                    final Node cnp10 = this.getNode(integer1, integer6, integer3);
                    cnp10.type = BlockPathTypes.BLOCKED;
                    cnp10.costMalus = -1.0f;
                    return cnp10;
                }
                final Node cnp10 = this.getNode(integer1, integer2, integer3);
                if (integer5++ >= this.mob.getMaxFallDistance()) {
                    cnp10.type = BlockPathTypes.BLOCKED;
                    cnp10.costMalus = -1.0f;
                    return cnp10;
                }
                cnn13 = this.getBlockPathType(this.mob, integer1, integer2, integer3);
                float14 = this.mob.getPathfindingMalus(cnn13);
                if (cnn13 != BlockPathTypes.OPEN && float14 >= 0.0f) {
                    cnp9 = cnp10;
                    cnp9.type = cnn13;
                    cnp9.costMalus = Math.max(cnp9.costMalus, float14);
                    break;
                }
                if (float14 < 0.0f) {
                    cnp10.type = BlockPathTypes.BLOCKED;
                    cnp10.costMalus = -1.0f;
                    return cnp10;
                }
            }
        }
        return cnp9;
    }
    
    @Override
    public BlockPathTypes getBlockPathType(final BlockGetter bhb, final int integer2, final int integer3, final int integer4, final Mob aiy, final int integer6, final int integer7, final int integer8, final boolean boolean9, final boolean boolean10) {
        final EnumSet<BlockPathTypes> enumSet12 = (EnumSet<BlockPathTypes>)EnumSet.noneOf((Class)BlockPathTypes.class);
        BlockPathTypes cnn13 = BlockPathTypes.BLOCKED;
        final double double14 = aiy.getBbWidth() / 2.0;
        final BlockPos ew16 = new BlockPos(aiy);
        cnn13 = this.getBlockPathTypes(bhb, integer2, integer3, integer4, integer6, integer7, integer8, boolean9, boolean10, enumSet12, cnn13, ew16);
        if (enumSet12.contains(BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        }
        BlockPathTypes cnn14 = BlockPathTypes.BLOCKED;
        for (final BlockPathTypes cnn15 : enumSet12) {
            if (aiy.getPathfindingMalus(cnn15) < 0.0f) {
                return cnn15;
            }
            if (aiy.getPathfindingMalus(cnn15) < aiy.getPathfindingMalus(cnn14)) {
                continue;
            }
            cnn14 = cnn15;
        }
        if (cnn13 == BlockPathTypes.OPEN && aiy.getPathfindingMalus(cnn14) == 0.0f) {
            return BlockPathTypes.OPEN;
        }
        return cnn14;
    }
    
    public BlockPathTypes getBlockPathTypes(final BlockGetter bhb, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final boolean boolean9, final EnumSet<BlockPathTypes> enumSet, BlockPathTypes cnn, final BlockPos ew) {
        for (int integer8 = 0; integer8 < integer5; ++integer8) {
            for (int integer9 = 0; integer9 < integer6; ++integer9) {
                for (int integer10 = 0; integer10 < integer7; ++integer10) {
                    final int integer11 = integer8 + integer2;
                    final int integer12 = integer9 + integer3;
                    final int integer13 = integer10 + integer4;
                    BlockPathTypes cnn2 = this.getBlockPathType(bhb, integer11, integer12, integer13);
                    cnn2 = this.evaluateBlockPathType(bhb, boolean8, boolean9, ew, cnn2);
                    if (integer8 == 0 && integer9 == 0 && integer10 == 0) {
                        cnn = cnn2;
                    }
                    enumSet.add(cnn2);
                }
            }
        }
        return cnn;
    }
    
    protected BlockPathTypes evaluateBlockPathType(final BlockGetter bhb, final boolean boolean2, final boolean boolean3, final BlockPos ew, BlockPathTypes cnn) {
        if (cnn == BlockPathTypes.DOOR_WOOD_CLOSED && boolean2 && boolean3) {
            cnn = BlockPathTypes.WALKABLE;
        }
        if (cnn == BlockPathTypes.DOOR_OPEN && !boolean3) {
            cnn = BlockPathTypes.BLOCKED;
        }
        if (cnn == BlockPathTypes.RAIL && !(bhb.getBlockState(ew).getBlock() instanceof BaseRailBlock) && !(bhb.getBlockState(ew.below()).getBlock() instanceof BaseRailBlock)) {
            cnn = BlockPathTypes.FENCE;
        }
        if (cnn == BlockPathTypes.LEAVES) {
            cnn = BlockPathTypes.BLOCKED;
        }
        return cnn;
    }
    
    private BlockPathTypes getBlockPathType(final Mob aiy, final BlockPos ew) {
        return this.getBlockPathType(aiy, ew.getX(), ew.getY(), ew.getZ());
    }
    
    private BlockPathTypes getBlockPathType(final Mob aiy, final int integer2, final int integer3, final int integer4) {
        return this.getBlockPathType(this.level, integer2, integer3, integer4, aiy, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
    }
    
    @Override
    public BlockPathTypes getBlockPathType(final BlockGetter bhb, final int integer2, final int integer3, final int integer4) {
        BlockPathTypes cnn6 = this.getBlockPathTypeRaw(bhb, integer2, integer3, integer4);
        if (cnn6 == BlockPathTypes.OPEN && integer3 >= 1) {
            final Block bmv7 = bhb.getBlockState(new BlockPos(integer2, integer3 - 1, integer4)).getBlock();
            final BlockPathTypes cnn7 = this.getBlockPathTypeRaw(bhb, integer2, integer3 - 1, integer4);
            cnn6 = ((cnn7 == BlockPathTypes.WALKABLE || cnn7 == BlockPathTypes.OPEN || cnn7 == BlockPathTypes.WATER || cnn7 == BlockPathTypes.LAVA) ? BlockPathTypes.OPEN : BlockPathTypes.WALKABLE);
            if (cnn7 == BlockPathTypes.DAMAGE_FIRE || bmv7 == Blocks.MAGMA_BLOCK || bmv7 == Blocks.CAMPFIRE) {
                cnn6 = BlockPathTypes.DAMAGE_FIRE;
            }
            if (cnn7 == BlockPathTypes.DAMAGE_CACTUS) {
                cnn6 = BlockPathTypes.DAMAGE_CACTUS;
            }
            if (cnn7 == BlockPathTypes.DAMAGE_OTHER) {
                cnn6 = BlockPathTypes.DAMAGE_OTHER;
            }
        }
        cnn6 = this.checkNeighbourBlocks(bhb, integer2, integer3, integer4, cnn6);
        return cnn6;
    }
    
    public BlockPathTypes checkNeighbourBlocks(final BlockGetter bhb, final int integer2, final int integer3, final int integer4, BlockPathTypes cnn) {
        if (cnn == BlockPathTypes.WALKABLE) {
            try (final BlockPos.PooledMutableBlockPos b7 = BlockPos.PooledMutableBlockPos.acquire()) {
                for (int integer5 = -1; integer5 <= 1; ++integer5) {
                    for (int integer6 = -1; integer6 <= 1; ++integer6) {
                        if (integer5 != 0 || integer6 != 0) {
                            final Block bmv11 = bhb.getBlockState(b7.set(integer5 + integer2, integer3, integer6 + integer4)).getBlock();
                            if (bmv11 == Blocks.CACTUS) {
                                cnn = BlockPathTypes.DANGER_CACTUS;
                            }
                            else if (bmv11 == Blocks.FIRE) {
                                cnn = BlockPathTypes.DANGER_FIRE;
                            }
                            else if (bmv11 == Blocks.SWEET_BERRY_BUSH) {
                                cnn = BlockPathTypes.DANGER_OTHER;
                            }
                        }
                    }
                }
            }
        }
        return cnn;
    }
    
    protected BlockPathTypes getBlockPathTypeRaw(final BlockGetter bhb, final int integer2, final int integer3, final int integer4) {
        final BlockPos ew6 = new BlockPos(integer2, integer3, integer4);
        final BlockState bvt7 = bhb.getBlockState(ew6);
        final Block bmv8 = bvt7.getBlock();
        final Material clo9 = bvt7.getMaterial();
        if (bvt7.isAir()) {
            return BlockPathTypes.OPEN;
        }
        if (bmv8.is(BlockTags.TRAPDOORS) || bmv8 == Blocks.LILY_PAD) {
            return BlockPathTypes.TRAPDOOR;
        }
        if (bmv8 == Blocks.FIRE) {
            return BlockPathTypes.DAMAGE_FIRE;
        }
        if (bmv8 == Blocks.CACTUS) {
            return BlockPathTypes.DAMAGE_CACTUS;
        }
        if (bmv8 == Blocks.SWEET_BERRY_BUSH) {
            return BlockPathTypes.DAMAGE_OTHER;
        }
        if (bmv8 instanceof DoorBlock && clo9 == Material.WOOD && !bvt7.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_WOOD_CLOSED;
        }
        if (bmv8 instanceof DoorBlock && clo9 == Material.METAL && !bvt7.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_IRON_CLOSED;
        }
        if (bmv8 instanceof DoorBlock && bvt7.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN)) {
            return BlockPathTypes.DOOR_OPEN;
        }
        if (bmv8 instanceof BaseRailBlock) {
            return BlockPathTypes.RAIL;
        }
        if (bmv8 instanceof LeavesBlock) {
            return BlockPathTypes.LEAVES;
        }
        if (bmv8.is(BlockTags.FENCES) || bmv8.is(BlockTags.WALLS) || (bmv8 instanceof FenceGateBlock && !bvt7.<Boolean>getValue((Property<Boolean>)FenceGateBlock.OPEN))) {
            return BlockPathTypes.FENCE;
        }
        final FluidState clk10 = bhb.getFluidState(ew6);
        if (clk10.is(FluidTags.WATER)) {
            return BlockPathTypes.WATER;
        }
        if (clk10.is(FluidTags.LAVA)) {
            return BlockPathTypes.LAVA;
        }
        if (bvt7.isPathfindable(bhb, ew6, PathComputationType.LAND)) {
            return BlockPathTypes.OPEN;
        }
        return BlockPathTypes.BLOCKED;
    }
}

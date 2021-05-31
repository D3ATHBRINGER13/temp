package net.minecraft.world.level.pathfinder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BaseRailBlock;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;

public class TurtleNodeEvaluator extends WalkNodeEvaluator {
    private float oldWalkableCost;
    private float oldWaterBorderCost;
    
    @Override
    public void prepare(final LevelReader bhu, final Mob aiy) {
        super.prepare(bhu, aiy);
        aiy.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.oldWalkableCost = aiy.getPathfindingMalus(BlockPathTypes.WALKABLE);
        aiy.setPathfindingMalus(BlockPathTypes.WALKABLE, 6.0f);
        this.oldWaterBorderCost = aiy.getPathfindingMalus(BlockPathTypes.WATER_BORDER);
        aiy.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 4.0f);
    }
    
    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WALKABLE, this.oldWalkableCost);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, this.oldWaterBorderCost);
        super.done();
    }
    
    @Override
    public Node getStart() {
        return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ));
    }
    
    @Override
    public Target getGoal(final double double1, final double double2, final double double3) {
        return new Target(this.getNode(Mth.floor(double1), Mth.floor(double2 + 0.5), Mth.floor(double3)));
    }
    
    @Override
    public int getNeighbors(final Node[] arr, final Node cnp) {
        int integer4 = 0;
        final int integer5 = 1;
        final BlockPos ew6 = new BlockPos(cnp.x, cnp.y, cnp.z);
        final double double7 = this.inWaterDependentPosHeight(ew6);
        final Node cnp2 = this.getAcceptedNode(cnp.x, cnp.y, cnp.z + 1, 1, double7);
        final Node cnp3 = this.getAcceptedNode(cnp.x - 1, cnp.y, cnp.z, 1, double7);
        final Node cnp4 = this.getAcceptedNode(cnp.x + 1, cnp.y, cnp.z, 1, double7);
        final Node cnp5 = this.getAcceptedNode(cnp.x, cnp.y, cnp.z - 1, 1, double7);
        final Node cnp6 = this.getAcceptedNode(cnp.x, cnp.y + 1, cnp.z, 0, double7);
        final Node cnp7 = this.getAcceptedNode(cnp.x, cnp.y - 1, cnp.z, 1, double7);
        if (cnp2 != null && !cnp2.closed) {
            arr[integer4++] = cnp2;
        }
        if (cnp3 != null && !cnp3.closed) {
            arr[integer4++] = cnp3;
        }
        if (cnp4 != null && !cnp4.closed) {
            arr[integer4++] = cnp4;
        }
        if (cnp5 != null && !cnp5.closed) {
            arr[integer4++] = cnp5;
        }
        if (cnp6 != null && !cnp6.closed) {
            arr[integer4++] = cnp6;
        }
        if (cnp7 != null && !cnp7.closed) {
            arr[integer4++] = cnp7;
        }
        final boolean boolean15 = cnp5 == null || cnp5.type == BlockPathTypes.OPEN || cnp5.costMalus != 0.0f;
        final boolean boolean16 = cnp2 == null || cnp2.type == BlockPathTypes.OPEN || cnp2.costMalus != 0.0f;
        final boolean boolean17 = cnp4 == null || cnp4.type == BlockPathTypes.OPEN || cnp4.costMalus != 0.0f;
        final boolean boolean18 = cnp3 == null || cnp3.type == BlockPathTypes.OPEN || cnp3.costMalus != 0.0f;
        if (boolean15 && boolean18) {
            final Node cnp8 = this.getAcceptedNode(cnp.x - 1, cnp.y, cnp.z - 1, 1, double7);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean15 && boolean17) {
            final Node cnp8 = this.getAcceptedNode(cnp.x + 1, cnp.y, cnp.z - 1, 1, double7);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean16 && boolean18) {
            final Node cnp8 = this.getAcceptedNode(cnp.x - 1, cnp.y, cnp.z + 1, 1, double7);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean16 && boolean17) {
            final Node cnp8 = this.getAcceptedNode(cnp.x + 1, cnp.y, cnp.z + 1, 1, double7);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        return integer4;
    }
    
    private double inWaterDependentPosHeight(final BlockPos ew) {
        if (!this.mob.isInWater()) {
            final BlockPos ew2 = ew.below();
            final VoxelShape ctc4 = this.level.getBlockState(ew2).getCollisionShape(this.level, ew2);
            return ew2.getY() + (ctc4.isEmpty() ? 0.0 : ctc4.max(Direction.Axis.Y));
        }
        return ew.getY() + 0.5;
    }
    
    @Nullable
    private Node getAcceptedNode(final int integer1, int integer2, final int integer3, final int integer4, final double double5) {
        Node cnp8 = null;
        final BlockPos ew9 = new BlockPos(integer1, integer2, integer3);
        final double double6 = this.inWaterDependentPosHeight(ew9);
        if (double6 - double5 > 1.125) {
            return null;
        }
        BlockPathTypes cnn12 = this.getBlockPathType(this.level, integer1, integer2, integer3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
        float float13 = this.mob.getPathfindingMalus(cnn12);
        final double double7 = this.mob.getBbWidth() / 2.0;
        if (float13 >= 0.0f) {
            cnp8 = this.getNode(integer1, integer2, integer3);
            cnp8.type = cnn12;
            cnp8.costMalus = Math.max(cnp8.costMalus, float13);
        }
        if (cnn12 == BlockPathTypes.WATER || cnn12 == BlockPathTypes.WALKABLE) {
            if (integer2 < this.mob.level.getSeaLevel() - 10 && cnp8 != null) {
                final Node node = cnp8;
                ++node.costMalus;
            }
            return cnp8;
        }
        if (cnp8 == null && integer4 > 0 && cnn12 != BlockPathTypes.FENCE && cnn12 != BlockPathTypes.TRAPDOOR) {
            cnp8 = this.getAcceptedNode(integer1, integer2 + 1, integer3, integer4 - 1, double5);
        }
        if (cnn12 == BlockPathTypes.OPEN) {
            final AABB csc16 = new AABB(integer1 - double7 + 0.5, integer2 + 0.001, integer3 - double7 + 0.5, integer1 + double7 + 0.5, integer2 + this.mob.getBbHeight(), integer3 + double7 + 0.5);
            if (!this.mob.level.noCollision(this.mob, csc16)) {
                return null;
            }
            final BlockPathTypes cnn13 = this.getBlockPathType(this.level, integer1, integer2 - 1, integer3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
            if (cnn13 == BlockPathTypes.BLOCKED) {
                cnp8 = this.getNode(integer1, integer2, integer3);
                cnp8.type = BlockPathTypes.WALKABLE;
                cnp8.costMalus = Math.max(cnp8.costMalus, float13);
                return cnp8;
            }
            if (cnn13 == BlockPathTypes.WATER) {
                cnp8 = this.getNode(integer1, integer2, integer3);
                cnp8.type = BlockPathTypes.WATER;
                cnp8.costMalus = Math.max(cnp8.costMalus, float13);
                return cnp8;
            }
            int integer5 = 0;
            while (integer2 > 0 && cnn12 == BlockPathTypes.OPEN) {
                --integer2;
                if (integer5++ >= this.mob.getMaxFallDistance()) {
                    return null;
                }
                cnn12 = this.getBlockPathType(this.level, integer1, integer2, integer3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
                float13 = this.mob.getPathfindingMalus(cnn12);
                if (cnn12 != BlockPathTypes.OPEN && float13 >= 0.0f) {
                    cnp8 = this.getNode(integer1, integer2, integer3);
                    cnp8.type = cnn12;
                    cnp8.costMalus = Math.max(cnp8.costMalus, float13);
                    break;
                }
                if (float13 < 0.0f) {
                    return null;
                }
            }
        }
        return cnp8;
    }
    
    @Override
    protected BlockPathTypes evaluateBlockPathType(final BlockGetter bhb, final boolean boolean2, final boolean boolean3, final BlockPos ew, BlockPathTypes cnn) {
        if (cnn == BlockPathTypes.RAIL && !(bhb.getBlockState(ew).getBlock() instanceof BaseRailBlock) && !(bhb.getBlockState(ew.below()).getBlock() instanceof BaseRailBlock)) {
            cnn = BlockPathTypes.FENCE;
        }
        if (cnn == BlockPathTypes.DOOR_OPEN || cnn == BlockPathTypes.DOOR_WOOD_CLOSED || cnn == BlockPathTypes.DOOR_IRON_CLOSED) {
            cnn = BlockPathTypes.BLOCKED;
        }
        if (cnn == BlockPathTypes.LEAVES) {
            cnn = BlockPathTypes.BLOCKED;
        }
        return cnn;
    }
    
    @Override
    public BlockPathTypes getBlockPathType(final BlockGetter bhb, final int integer2, final int integer3, final int integer4) {
        BlockPathTypes cnn6 = this.getBlockPathTypeRaw(bhb, integer2, integer3, integer4);
        if (cnn6 == BlockPathTypes.WATER) {
            for (final Direction fb10 : Direction.values()) {
                final BlockPathTypes cnn7 = this.getBlockPathTypeRaw(bhb, integer2 + fb10.getStepX(), integer3 + fb10.getStepY(), integer4 + fb10.getStepZ());
                if (cnn7 == BlockPathTypes.BLOCKED) {
                    return BlockPathTypes.WATER_BORDER;
                }
            }
            return BlockPathTypes.WATER;
        }
        if (cnn6 == BlockPathTypes.OPEN && integer3 >= 1) {
            final Block bmv7 = bhb.getBlockState(new BlockPos(integer2, integer3 - 1, integer4)).getBlock();
            final BlockPathTypes cnn8 = this.getBlockPathTypeRaw(bhb, integer2, integer3 - 1, integer4);
            if (cnn8 == BlockPathTypes.WALKABLE || cnn8 == BlockPathTypes.OPEN || cnn8 == BlockPathTypes.LAVA) {
                cnn6 = BlockPathTypes.OPEN;
            }
            else {
                cnn6 = BlockPathTypes.WALKABLE;
            }
            if (cnn8 == BlockPathTypes.DAMAGE_FIRE || bmv7 == Blocks.MAGMA_BLOCK || bmv7 == Blocks.CAMPFIRE) {
                cnn6 = BlockPathTypes.DAMAGE_FIRE;
            }
            if (cnn8 == BlockPathTypes.DAMAGE_CACTUS) {
                cnn6 = BlockPathTypes.DAMAGE_CACTUS;
            }
            if (cnn8 == BlockPathTypes.DAMAGE_OTHER) {
                cnn6 = BlockPathTypes.DAMAGE_OTHER;
            }
        }
        cnn6 = this.checkNeighbourBlocks(bhb, integer2, integer3, integer4, cnn6);
        return cnn6;
    }
}

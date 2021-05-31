package net.minecraft.world.level.pathfinder;

import java.util.EnumSet;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
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
            for (Block bmv4 = this.level.getBlockState(a3).getBlock(); bmv4 == Blocks.WATER; bmv4 = this.level.getBlockState(a3).getBlock()) {
                ++integer2;
                a3.set(this.mob.x, integer2, this.mob.z);
            }
        }
        else {
            integer2 = Mth.floor(this.mob.getBoundingBox().minY + 0.5);
        }
        final BlockPos ew3 = new BlockPos(this.mob);
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
                    return super.getNode(ew4.getX(), ew4.getY(), ew4.getZ());
                }
            }
        }
        return super.getNode(ew3.getX(), integer2, ew3.getZ());
    }
    
    @Override
    public Target getGoal(final double double1, final double double2, final double double3) {
        return new Target(super.getNode(Mth.floor(double1), Mth.floor(double2), Mth.floor(double3)));
    }
    
    @Override
    public int getNeighbors(final Node[] arr, final Node cnp) {
        int integer4 = 0;
        final Node cnp2 = this.getNode(cnp.x, cnp.y, cnp.z + 1);
        final Node cnp3 = this.getNode(cnp.x - 1, cnp.y, cnp.z);
        final Node cnp4 = this.getNode(cnp.x + 1, cnp.y, cnp.z);
        final Node cnp5 = this.getNode(cnp.x, cnp.y, cnp.z - 1);
        final Node cnp6 = this.getNode(cnp.x, cnp.y + 1, cnp.z);
        final Node cnp7 = this.getNode(cnp.x, cnp.y - 1, cnp.z);
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
        final boolean boolean11 = cnp5 == null || cnp5.costMalus != 0.0f;
        final boolean boolean12 = cnp2 == null || cnp2.costMalus != 0.0f;
        final boolean boolean13 = cnp4 == null || cnp4.costMalus != 0.0f;
        final boolean boolean14 = cnp3 == null || cnp3.costMalus != 0.0f;
        final boolean boolean15 = cnp6 == null || cnp6.costMalus != 0.0f;
        final boolean boolean16 = cnp7 == null || cnp7.costMalus != 0.0f;
        if (boolean11 && boolean14) {
            final Node cnp8 = this.getNode(cnp.x - 1, cnp.y, cnp.z - 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean11 && boolean13) {
            final Node cnp8 = this.getNode(cnp.x + 1, cnp.y, cnp.z - 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean12 && boolean14) {
            final Node cnp8 = this.getNode(cnp.x - 1, cnp.y, cnp.z + 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean12 && boolean13) {
            final Node cnp8 = this.getNode(cnp.x + 1, cnp.y, cnp.z + 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean11 && boolean15) {
            final Node cnp8 = this.getNode(cnp.x, cnp.y + 1, cnp.z - 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean12 && boolean15) {
            final Node cnp8 = this.getNode(cnp.x, cnp.y + 1, cnp.z + 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean13 && boolean15) {
            final Node cnp8 = this.getNode(cnp.x + 1, cnp.y + 1, cnp.z);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean14 && boolean15) {
            final Node cnp8 = this.getNode(cnp.x - 1, cnp.y + 1, cnp.z);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean11 && boolean16) {
            final Node cnp8 = this.getNode(cnp.x, cnp.y - 1, cnp.z - 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean12 && boolean16) {
            final Node cnp8 = this.getNode(cnp.x, cnp.y - 1, cnp.z + 1);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean13 && boolean16) {
            final Node cnp8 = this.getNode(cnp.x + 1, cnp.y - 1, cnp.z);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        if (boolean14 && boolean16) {
            final Node cnp8 = this.getNode(cnp.x - 1, cnp.y - 1, cnp.z);
            if (cnp8 != null && !cnp8.closed) {
                arr[integer4++] = cnp8;
            }
        }
        return integer4;
    }
    
    @Nullable
    @Override
    protected Node getNode(final int integer1, final int integer2, final int integer3) {
        Node cnp5 = null;
        final BlockPathTypes cnn6 = this.getBlockPathType(this.mob, integer1, integer2, integer3);
        final float float7 = this.mob.getPathfindingMalus(cnn6);
        if (float7 >= 0.0f) {
            cnp5 = super.getNode(integer1, integer2, integer3);
            cnp5.type = cnn6;
            cnp5.costMalus = Math.max(cnp5.costMalus, float7);
            if (cnn6 == BlockPathTypes.WALKABLE) {
                final Node node = cnp5;
                ++node.costMalus;
            }
        }
        if (cnn6 == BlockPathTypes.OPEN || cnn6 == BlockPathTypes.WALKABLE) {
            return cnp5;
        }
        return cnp5;
    }
    
    @Override
    public BlockPathTypes getBlockPathType(final BlockGetter bhb, final int integer2, final int integer3, final int integer4, final Mob aiy, final int integer6, final int integer7, final int integer8, final boolean boolean9, final boolean boolean10) {
        final EnumSet<BlockPathTypes> enumSet12 = (EnumSet<BlockPathTypes>)EnumSet.noneOf((Class)BlockPathTypes.class);
        BlockPathTypes cnn13 = BlockPathTypes.BLOCKED;
        final BlockPos ew14 = new BlockPos(aiy);
        cnn13 = this.getBlockPathTypes(bhb, integer2, integer3, integer4, integer6, integer7, integer8, boolean9, boolean10, enumSet12, cnn13, ew14);
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
    
    @Override
    public BlockPathTypes getBlockPathType(final BlockGetter bhb, final int integer2, final int integer3, final int integer4) {
        BlockPathTypes cnn6 = this.getBlockPathTypeRaw(bhb, integer2, integer3, integer4);
        if (cnn6 == BlockPathTypes.OPEN && integer3 >= 1) {
            final Block bmv7 = bhb.getBlockState(new BlockPos(integer2, integer3 - 1, integer4)).getBlock();
            final BlockPathTypes cnn7 = this.getBlockPathTypeRaw(bhb, integer2, integer3 - 1, integer4);
            if (cnn7 == BlockPathTypes.DAMAGE_FIRE || bmv7 == Blocks.MAGMA_BLOCK || cnn7 == BlockPathTypes.LAVA || bmv7 == Blocks.CAMPFIRE) {
                cnn6 = BlockPathTypes.DAMAGE_FIRE;
            }
            else if (cnn7 == BlockPathTypes.DAMAGE_CACTUS) {
                cnn6 = BlockPathTypes.DAMAGE_CACTUS;
            }
            else if (cnn7 == BlockPathTypes.DAMAGE_OTHER) {
                cnn6 = BlockPathTypes.DAMAGE_OTHER;
            }
            else {
                cnn6 = ((cnn7 == BlockPathTypes.WALKABLE || cnn7 == BlockPathTypes.OPEN || cnn7 == BlockPathTypes.WATER) ? BlockPathTypes.OPEN : BlockPathTypes.WALKABLE);
            }
        }
        cnn6 = this.checkNeighbourBlocks(bhb, integer2, integer3, integer4, cnn6);
        return cnn6;
    }
    
    private BlockPathTypes getBlockPathType(final Mob aiy, final BlockPos ew) {
        return this.getBlockPathType(aiy, ew.getX(), ew.getY(), ew.getZ());
    }
    
    private BlockPathTypes getBlockPathType(final Mob aiy, final int integer2, final int integer3, final int integer4) {
        return this.getBlockPathType(this.level, integer2, integer3, integer4, aiy, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
    }
}

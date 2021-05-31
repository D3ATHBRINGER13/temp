package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import java.util.Iterator;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Set;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class AbstractTreeFeature<T extends FeatureConfiguration> extends Feature<T> {
    public AbstractTreeFeature(final Function<Dynamic<?>, ? extends T> function, final boolean boolean2) {
        super(function, boolean2);
    }
    
    protected static boolean isFree(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> {
            final Block bmv2 = bvt.getBlock();
            return bvt.isAir() || bvt.is(BlockTags.LEAVES) || bmv2 == Blocks.GRASS_BLOCK || Block.equalsDirt(bmv2) || bmv2.is(BlockTags.LOGS) || bmv2.is(BlockTags.SAPLINGS) || bmv2 == Blocks.VINE;
        }));
    }
    
    protected static boolean isAir(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)BlockState::isAir);
    }
    
    protected static boolean isDirt(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> Block.equalsDirt(bvt.getBlock())));
    }
    
    protected static boolean isBlockWater(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> bvt.getBlock() == Blocks.WATER));
    }
    
    protected static boolean isLeaves(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> bvt.is(BlockTags.LEAVES)));
    }
    
    protected static boolean isAirOrLeaves(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> bvt.isAir() || bvt.is(BlockTags.LEAVES)));
    }
    
    protected static boolean isGrassOrDirt(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> {
            final Block bmv2 = bvt.getBlock();
            return Block.equalsDirt(bmv2) || bmv2 == Blocks.GRASS_BLOCK;
        }));
    }
    
    protected static boolean isGrassOrDirtOrFarmland(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> {
            final Block bmv2 = bvt.getBlock();
            return Block.equalsDirt(bmv2) || bmv2 == Blocks.GRASS_BLOCK || bmv2 == Blocks.FARMLAND;
        }));
    }
    
    protected static boolean isReplaceablePlant(final LevelSimulatedReader bhx, final BlockPos ew) {
        return bhx.isStateAtPosition(ew, (Predicate<BlockState>)(bvt -> {
            final Material clo2 = bvt.getMaterial();
            return clo2 == Material.REPLACEABLE_PLANT;
        }));
    }
    
    protected void setDirtAt(final LevelSimulatedRW bhw, final BlockPos ew) {
        if (!isDirt(bhw, ew)) {
            this.setBlock(bhw, ew, Blocks.DIRT.defaultBlockState());
        }
    }
    
    @Override
    protected void setBlock(final LevelWriter bhz, final BlockPos ew, final BlockState bvt) {
        this.setBlockKnownShape(bhz, ew, bvt);
    }
    
    protected final void setBlock(final Set<BlockPos> set, final LevelWriter bhz, final BlockPos ew, final BlockState bvt, final BoundingBox cic) {
        this.setBlockKnownShape(bhz, ew, bvt);
        cic.expand(new BoundingBox(ew, ew));
        if (BlockTags.LOGS.contains(bvt.getBlock())) {
            set.add(ew.immutable());
        }
    }
    
    private void setBlockKnownShape(final LevelWriter bhz, final BlockPos ew, final BlockState bvt) {
        if (this.doUpdate) {
            bhz.setBlock(ew, bvt, 19);
        }
        else {
            bhz.setBlock(ew, bvt, 18);
        }
    }
    
    @Override
    public final boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final T cbo) {
        final Set<BlockPos> set7 = (Set<BlockPos>)Sets.newHashSet();
        final BoundingBox cic8 = BoundingBox.getUnknownBox();
        final boolean boolean9 = this.doPlace(set7, bhs, random, ew, cic8);
        if (cic8.x0 > cic8.x1) {
            return false;
        }
        final List<Set<BlockPos>> list10 = (List<Set<BlockPos>>)Lists.newArrayList();
        final int integer11 = 6;
        for (int integer12 = 0; integer12 < 6; ++integer12) {
            list10.add(Sets.newHashSet());
        }
        final DiscreteVoxelShape csr12 = new BitSetDiscreteVoxelShape(cic8.getXSpan(), cic8.getYSpan(), cic8.getZSpan());
        try (final BlockPos.PooledMutableBlockPos b13 = BlockPos.PooledMutableBlockPos.acquire()) {
            if (boolean9 && !set7.isEmpty()) {
                for (final BlockPos ew2 : Lists.newArrayList((Iterable)set7)) {
                    if (cic8.isInside(ew2)) {
                        csr12.setFull(ew2.getX() - cic8.x0, ew2.getY() - cic8.y0, ew2.getZ() - cic8.z0, true, true);
                    }
                    for (final Direction fb20 : Direction.values()) {
                        b13.set(ew2).move(fb20);
                        if (!set7.contains(b13)) {
                            final BlockState bvt21 = bhs.getBlockState(b13);
                            if (bvt21.<Comparable>hasProperty((Property<Comparable>)BlockStateProperties.DISTANCE)) {
                                ((Set)list10.get(0)).add(b13.immutable());
                                this.setBlockKnownShape(bhs, b13, ((AbstractStateHolder<O, BlockState>)bvt21).<Comparable, Integer>setValue((Property<Comparable>)BlockStateProperties.DISTANCE, 1));
                                if (cic8.isInside(b13)) {
                                    csr12.setFull(b13.getX() - cic8.x0, b13.getY() - cic8.y0, b13.getZ() - cic8.z0, true, true);
                                }
                            }
                        }
                    }
                }
            }
            for (int integer13 = 1; integer13 < 6; ++integer13) {
                final Set<BlockPos> set8 = (Set<BlockPos>)list10.get(integer13 - 1);
                final Set<BlockPos> set9 = (Set<BlockPos>)list10.get(integer13);
                for (final BlockPos ew3 : set8) {
                    if (cic8.isInside(ew3)) {
                        csr12.setFull(ew3.getX() - cic8.x0, ew3.getY() - cic8.y0, ew3.getZ() - cic8.z0, true, true);
                    }
                    for (final Direction fb21 : Direction.values()) {
                        b13.set(ew3).move(fb21);
                        if (!set8.contains(b13)) {
                            if (!set9.contains(b13)) {
                                final BlockState bvt22 = bhs.getBlockState(b13);
                                if (bvt22.<Comparable>hasProperty((Property<Comparable>)BlockStateProperties.DISTANCE)) {
                                    final int integer14 = bvt22.<Integer>getValue((Property<Integer>)BlockStateProperties.DISTANCE);
                                    if (integer14 > integer13 + 1) {
                                        final BlockState bvt23 = ((AbstractStateHolder<O, BlockState>)bvt22).<Comparable, Integer>setValue((Property<Comparable>)BlockStateProperties.DISTANCE, integer13 + 1);
                                        this.setBlockKnownShape(bhs, b13, bvt23);
                                        if (cic8.isInside(b13)) {
                                            csr12.setFull(b13.getX() - cic8.x0, b13.getY() - cic8.y0, b13.getZ() - cic8.z0, true, true);
                                        }
                                        set9.add(b13.immutable());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        StructureTemplate.updateShapeAtEdge(bhs, 3, csr12, cic8.x0, cic8.y0, cic8.z0);
        return boolean9;
    }
    
    protected abstract boolean doPlace(final Set<BlockPos> set, final LevelSimulatedRW bhw, final Random random, final BlockPos ew, final BoundingBox cic);
}

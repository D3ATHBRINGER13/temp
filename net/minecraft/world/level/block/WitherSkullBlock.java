package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.material.Material;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.item.Items;
import java.util.Iterator;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public class WitherSkullBlock extends SkullBlock {
    @Nullable
    private static BlockPattern witherPatternFull;
    @Nullable
    private static BlockPattern witherPatternBase;
    
    protected WitherSkullBlock(final Properties c) {
        super(Types.WITHER_SKELETON, c);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        super.setPlacedBy(bhr, ew, bvt, aix, bcj);
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof SkullBlockEntity) {
            checkSpawn(bhr, ew, (SkullBlockEntity)btw7);
        }
    }
    
    public static void checkSpawn(final Level bhr, final BlockPos ew, final SkullBlockEntity but) {
        if (bhr.isClientSide) {
            return;
        }
        final Block bmv4 = but.getBlockState().getBlock();
        final boolean boolean5 = bmv4 == Blocks.WITHER_SKELETON_SKULL || bmv4 == Blocks.WITHER_SKELETON_WALL_SKULL;
        if (!boolean5 || ew.getY() < 2 || bhr.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        final BlockPattern bvy6 = getOrCreateWitherFull();
        final BlockPattern.BlockPatternMatch b7 = bvy6.find(bhr, ew);
        if (b7 == null) {
            return;
        }
        for (int integer8 = 0; integer8 < bvy6.getWidth(); ++integer8) {
            for (int integer9 = 0; integer9 < bvy6.getHeight(); ++integer9) {
                final BlockInWorld bvx10 = b7.getBlock(integer8, integer9, 0);
                bhr.setBlock(bvx10.getPos(), Blocks.AIR.defaultBlockState(), 2);
                bhr.levelEvent(2001, bvx10.getPos(), Block.getId(bvx10.getState()));
            }
        }
        final WitherBoss atj8 = EntityType.WITHER.create(bhr);
        final BlockPos ew2 = b7.getBlock(1, 2, 0).getPos();
        atj8.moveTo(ew2.getX() + 0.5, ew2.getY() + 0.55, ew2.getZ() + 0.5, (b7.getForwards().getAxis() == Direction.Axis.X) ? 0.0f : 90.0f, 0.0f);
        atj8.yBodyRot = ((b7.getForwards().getAxis() == Direction.Axis.X) ? 0.0f : 90.0f);
        atj8.makeInvulnerable();
        for (final ServerPlayer vl11 : bhr.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)ServerPlayer.class, atj8.getBoundingBox().inflate(50.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(vl11, atj8);
        }
        bhr.addFreshEntity(atj8);
        for (int integer10 = 0; integer10 < bvy6.getWidth(); ++integer10) {
            for (int integer11 = 0; integer11 < bvy6.getHeight(); ++integer11) {
                bhr.blockUpdated(b7.getBlock(integer10, integer11, 0).getPos(), Blocks.AIR);
            }
        }
    }
    
    public static boolean canSpawnMob(final Level bhr, final BlockPos ew, final ItemStack bcj) {
        return bcj.getItem() == Items.WITHER_SKELETON_SKULL && ew.getY() >= 2 && bhr.getDifficulty() != Difficulty.PEACEFUL && !bhr.isClientSide && getOrCreateWitherBase().find(bhr, ew) != null;
    }
    
    private static BlockPattern getOrCreateWitherFull() {
        if (WitherSkullBlock.witherPatternFull == null) {
            WitherSkullBlock.witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.SOUL_SAND))).where('^', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or((Predicate)BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', BlockInWorld.hasState((Predicate<BlockState>)BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return WitherSkullBlock.witherPatternFull;
    }
    
    private static BlockPattern getOrCreateWitherBase() {
        if (WitherSkullBlock.witherPatternBase == null) {
            WitherSkullBlock.witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.SOUL_SAND))).where('~', BlockInWorld.hasState((Predicate<BlockState>)BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return WitherSkullBlock.witherPatternBase;
    }
}

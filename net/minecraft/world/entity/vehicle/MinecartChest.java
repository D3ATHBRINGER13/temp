package net.minecraft.world.entity.vehicle;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class MinecartChest extends AbstractMinecartContainer {
    public MinecartChest(final EntityType<? extends MinecartChest> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public MinecartChest(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.CHEST_MINECART, double2, double3, double4, bhr);
    }
    
    @Override
    public void destroy(final DamageSource ahx) {
        super.destroy(ahx);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.CHEST);
        }
    }
    
    @Override
    public int getContainerSize() {
        return 27;
    }
    
    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }
    
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return ((AbstractStateHolder<O, BlockState>)Blocks.CHEST.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)ChestBlock.FACING, Direction.NORTH);
    }
    
    @Override
    public int getDefaultDisplayOffset() {
        return 8;
    }
    
    public AbstractContainerMenu createMenu(final int integer, final Inventory awf) {
        return ChestMenu.threeRows(integer, awf, this);
    }
}

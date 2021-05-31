package net.minecraft.world.level.block.entity;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.NonNullList;
import java.util.List;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity {
    @Nullable
    protected ResourceLocation lootTable;
    protected long lootTableSeed;
    
    protected RandomizableContainerBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
    }
    
    public static void setLootTable(final BlockGetter bhb, final Random random, final BlockPos ew, final ResourceLocation qv) {
        final BlockEntity btw5 = bhb.getBlockEntity(ew);
        if (btw5 instanceof RandomizableContainerBlockEntity) {
            ((RandomizableContainerBlockEntity)btw5).setLootTable(qv, random.nextLong());
        }
    }
    
    protected boolean tryLoadLootTable(final CompoundTag id) {
        if (id.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(id.getString("LootTable"));
            this.lootTableSeed = id.getLong("LootTableSeed");
            return true;
        }
        return false;
    }
    
    protected boolean trySaveLootTable(final CompoundTag id) {
        if (this.lootTable == null) {
            return false;
        }
        id.putString("LootTable", this.lootTable.toString());
        if (this.lootTableSeed != 0L) {
            id.putLong("LootTableSeed", this.lootTableSeed);
        }
        return true;
    }
    
    public void unpackLootTable(@Nullable final Player awg) {
        if (this.lootTable != null && this.level.getServer() != null) {
            final LootTable cpb3 = this.level.getServer().getLootTables().get(this.lootTable);
            this.lootTable = null;
            final LootContext.Builder a4 = new LootContext.Builder((ServerLevel)this.level).<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(this.worldPosition)).withOptionalRandomSeed(this.lootTableSeed);
            if (awg != null) {
                a4.withLuck(awg.getLuck()).<Entity>withParameter(LootContextParams.THIS_ENTITY, awg);
            }
            cpb3.fill(this, a4.create(LootContextParamSets.CHEST));
        }
    }
    
    public void setLootTable(final ResourceLocation qv, final long long2) {
        this.lootTable = qv;
        this.lootTableSeed = long2;
    }
    
    @Override
    public ItemStack getItem(final int integer) {
        this.unpackLootTable(null);
        return this.getItems().get(integer);
    }
    
    @Override
    public ItemStack removeItem(final int integer1, final int integer2) {
        this.unpackLootTable(null);
        final ItemStack bcj4 = ContainerHelper.removeItem((List<ItemStack>)this.getItems(), integer1, integer2);
        if (!bcj4.isEmpty()) {
            this.setChanged();
        }
        return bcj4;
    }
    
    @Override
    public ItemStack removeItemNoUpdate(final int integer) {
        this.unpackLootTable(null);
        return ContainerHelper.takeItem((List<ItemStack>)this.getItems(), integer);
    }
    
    @Override
    public void setItem(final int integer, final ItemStack bcj) {
        this.unpackLootTable(null);
        this.getItems().set(integer, bcj);
        if (bcj.getCount() > this.getMaxStackSize()) {
            bcj.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.level.getBlockEntity(this.worldPosition) == this && awg.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }
    
    public void clearContent() {
        this.getItems().clear();
    }
    
    protected abstract NonNullList<ItemStack> getItems();
    
    protected abstract void setItems(final NonNullList<ItemStack> fk);
    
    @Override
    public boolean canOpen(final Player awg) {
        return super.canOpen(awg) && (this.lootTable == null || !awg.isSpectator());
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int integer, final Inventory awf, final Player awg) {
        if (this.canOpen(awg)) {
            this.unpackLootTable(awf.player);
            return this.createMenu(integer, awf);
        }
        return null;
    }
}

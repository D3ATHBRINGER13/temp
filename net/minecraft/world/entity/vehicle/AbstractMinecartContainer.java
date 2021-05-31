package net.minecraft.world.entity.vehicle;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.List;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.Containers;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Container;

public abstract class AbstractMinecartContainer extends AbstractMinecart implements Container, MenuProvider {
    private NonNullList<ItemStack> itemStacks;
    private boolean dropEquipment;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;
    
    protected AbstractMinecartContainer(final EntityType<?> ais, final Level bhr) {
        super(ais, bhr);
        this.itemStacks = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
        this.dropEquipment = true;
    }
    
    protected AbstractMinecartContainer(final EntityType<?> ais, final double double2, final double double3, final double double4, final Level bhr) {
        super(ais, bhr, double2, double3, double4);
        this.itemStacks = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
        this.dropEquipment = true;
    }
    
    @Override
    public void destroy(final DamageSource ahx) {
        super.destroy(ahx);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Containers.dropContents(this.level, this, this);
        }
    }
    
    @Override
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.itemStacks) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getItem(final int integer) {
        this.unpackLootTable(null);
        return this.itemStacks.get(integer);
    }
    
    @Override
    public ItemStack removeItem(final int integer1, final int integer2) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem((List<ItemStack>)this.itemStacks, integer1, integer2);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(final int integer) {
        this.unpackLootTable(null);
        final ItemStack bcj3 = this.itemStacks.get(integer);
        if (bcj3.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.itemStacks.set(integer, ItemStack.EMPTY);
        return bcj3;
    }
    
    @Override
    public void setItem(final int integer, final ItemStack bcj) {
        this.unpackLootTable(null);
        this.itemStacks.set(integer, bcj);
        if (!bcj.isEmpty() && bcj.getCount() > this.getMaxStackSize()) {
            bcj.setCount(this.getMaxStackSize());
        }
    }
    
    @Override
    public boolean setSlot(final int integer, final ItemStack bcj) {
        if (integer >= 0 && integer < this.getContainerSize()) {
            this.setItem(integer, bcj);
            return true;
        }
        return false;
    }
    
    @Override
    public void setChanged() {
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return !this.removed && awg.distanceToSqr(this) <= 64.0;
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final DimensionType byn) {
        this.dropEquipment = false;
        return super.changeDimension(byn);
    }
    
    @Override
    public void remove() {
        if (!this.level.isClientSide && this.dropEquipment) {
            Containers.dropContents(this.level, this, this);
        }
        super.remove();
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        if (this.lootTable != null) {
            id.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                id.putLong("LootTableSeed", this.lootTableSeed);
            }
        }
        else {
            ContainerHelper.saveAllItems(id, this.itemStacks);
        }
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.itemStacks = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (id.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(id.getString("LootTable"));
            this.lootTableSeed = id.getLong("LootTableSeed");
        }
        else {
            ContainerHelper.loadAllItems(id, this.itemStacks);
        }
    }
    
    @Override
    public boolean interact(final Player awg, final InteractionHand ahi) {
        awg.openMenu(this);
        return true;
    }
    
    @Override
    protected void applyNaturalSlowdown() {
        float float2 = 0.98f;
        if (this.lootTable == null) {
            final int integer3 = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
            float2 += integer3 * 0.001f;
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(float2, 0.0, float2));
    }
    
    public void unpackLootTable(@Nullable final Player awg) {
        if (this.lootTable != null && this.level.getServer() != null) {
            final LootTable cpb3 = this.level.getServer().getLootTables().get(this.lootTable);
            this.lootTable = null;
            final LootContext.Builder a4 = new LootContext.Builder((ServerLevel)this.level).<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(this)).withOptionalRandomSeed(this.lootTableSeed);
            if (awg != null) {
                a4.withLuck(awg.getLuck()).<Entity>withParameter(LootContextParams.THIS_ENTITY, awg);
            }
            cpb3.fill(this, a4.create(LootContextParamSets.CHEST));
        }
    }
    
    public void clearContent() {
        this.unpackLootTable(null);
        this.itemStacks.clear();
    }
    
    public void setLootTable(final ResourceLocation qv, final long long2) {
        this.lootTable = qv;
        this.lootTableSeed = long2;
    }
    
    @Nullable
    public AbstractContainerMenu createMenu(final int integer, final Inventory awf, final Player awg) {
        if (this.lootTable == null || !awg.isSpectator()) {
            this.unpackLootTable(awf.player);
            return this.createMenu(integer, awf);
        }
        return null;
    }
    
    protected abstract AbstractContainerMenu createMenu(final int integer, final Inventory awf);
}

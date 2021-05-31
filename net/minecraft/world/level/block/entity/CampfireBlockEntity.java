package net.minecraft.world.level.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Clearable;

public class CampfireBlockEntity extends BlockEntity implements Clearable, TickableBlockEntity {
    private final NonNullList<ItemStack> items;
    private final int[] cookingProgress;
    private final int[] cookingTime;
    
    public CampfireBlockEntity() {
        super(BlockEntityType.CAMPFIRE);
        this.items = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
        this.cookingProgress = new int[4];
        this.cookingTime = new int[4];
    }
    
    @Override
    public void tick() {
        final boolean boolean2 = this.getBlockState().<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT);
        final boolean boolean3 = this.level.isClientSide;
        if (boolean3) {
            if (boolean2) {
                this.makeParticles();
            }
            return;
        }
        if (boolean2) {
            this.cook();
        }
        else {
            for (int integer4 = 0; integer4 < this.items.size(); ++integer4) {
                if (this.cookingProgress[integer4] > 0) {
                    this.cookingProgress[integer4] = Mth.clamp(this.cookingProgress[integer4] - 2, 0, this.cookingTime[integer4]);
                }
            }
        }
    }
    
    private void cook() {
        for (int integer2 = 0; integer2 < this.items.size(); ++integer2) {
            final ItemStack bcj3 = this.items.get(integer2);
            if (!bcj3.isEmpty()) {
                final int[] cookingProgress = this.cookingProgress;
                final int n = integer2;
                ++cookingProgress[n];
                if (this.cookingProgress[integer2] >= this.cookingTime[integer2]) {
                    final Container ahc4 = new SimpleContainer(new ItemStack[] { bcj3 });
                    final ItemStack bcj4 = (ItemStack)this.level.getRecipeManager().<Container, CampfireCookingRecipe>getRecipeFor(RecipeType.CAMPFIRE_COOKING, ahc4, this.level).map(bei -> bei.assemble(ahc4)).orElse(bcj3);
                    final BlockPos ew6 = this.getBlockPos();
                    Containers.dropItemStack(this.level, ew6.getX(), ew6.getY(), ew6.getZ(), bcj4);
                    this.items.set(integer2, ItemStack.EMPTY);
                    this.markUpdated();
                }
            }
        }
    }
    
    private void makeParticles() {
        final Level bhr2 = this.getLevel();
        if (bhr2 == null) {
            return;
        }
        final BlockPos ew3 = this.getBlockPos();
        final Random random4 = bhr2.random;
        if (random4.nextFloat() < 0.11f) {
            for (int integer5 = 0; integer5 < random4.nextInt(2) + 2; ++integer5) {
                CampfireBlock.makeParticles(bhr2, ew3, this.getBlockState().<Boolean>getValue((Property<Boolean>)CampfireBlock.SIGNAL_FIRE), false);
            }
        }
        int integer5 = this.getBlockState().<Direction>getValue((Property<Direction>)CampfireBlock.FACING).get2DDataValue();
        for (int integer6 = 0; integer6 < this.items.size(); ++integer6) {
            if (!this.items.get(integer6).isEmpty() && random4.nextFloat() < 0.2f) {
                final Direction fb7 = Direction.from2DDataValue(Math.floorMod(integer6 + integer5, 4));
                final float float8 = 0.3125f;
                final double double9 = ew3.getX() + 0.5 - fb7.getStepX() * 0.3125f + fb7.getClockWise().getStepX() * 0.3125f;
                final double double10 = ew3.getY() + 0.5;
                final double double11 = ew3.getZ() + 0.5 - fb7.getStepZ() * 0.3125f + fb7.getClockWise().getStepZ() * 0.3125f;
                for (int integer7 = 0; integer7 < 4; ++integer7) {
                    bhr2.addParticle(ParticleTypes.SMOKE, double9, double10, double11, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }
    
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.items.clear();
        ContainerHelper.loadAllItems(id, this.items);
        if (id.contains("CookingTimes", 11)) {
            final int[] arr3 = id.getIntArray("CookingTimes");
            System.arraycopy(arr3, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, arr3.length));
        }
        if (id.contains("CookingTotalTimes", 11)) {
            final int[] arr3 = id.getIntArray("CookingTotalTimes");
            System.arraycopy(arr3, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, arr3.length));
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        this.saveMetadataAndItems(id);
        id.putIntArray("CookingTimes", this.cookingProgress);
        id.putIntArray("CookingTotalTimes", this.cookingTime);
        return id;
    }
    
    private CompoundTag saveMetadataAndItems(final CompoundTag id) {
        super.save(id);
        ContainerHelper.saveAllItems(id, this.items, true);
        return id;
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 13, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveMetadataAndItems(new CompoundTag());
    }
    
    public Optional<CampfireCookingRecipe> getCookableRecipe(final ItemStack bcj) {
        if (this.items.stream().noneMatch(ItemStack::isEmpty)) {
            return (Optional<CampfireCookingRecipe>)Optional.empty();
        }
        return this.level.getRecipeManager().<SimpleContainer, CampfireCookingRecipe>getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(new ItemStack[] { bcj }), this.level);
    }
    
    public boolean placeFood(final ItemStack bcj, final int integer) {
        for (int integer2 = 0; integer2 < this.items.size(); ++integer2) {
            final ItemStack bcj2 = this.items.get(integer2);
            if (bcj2.isEmpty()) {
                this.cookingTime[integer2] = integer;
                this.cookingProgress[integer2] = 0;
                this.items.set(integer2, bcj.split(1));
                this.markUpdated();
                return true;
            }
        }
        return false;
    }
    
    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
    
    @Override
    public void clearContent() {
        this.items.clear();
    }
    
    public void dowse() {
        if (!this.getLevel().isClientSide) {
            Containers.dropContents(this.getLevel(), this.getBlockPos(), this.getItems());
        }
        this.markUpdated();
    }
}

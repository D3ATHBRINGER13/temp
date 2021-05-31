package net.minecraft.world.item;

import com.google.common.collect.Maps;
import com.google.common.collect.Iterables;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import java.util.Objects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import java.util.Map;

public class SpawnEggItem extends Item {
    private static final Map<EntityType<?>, SpawnEggItem> BY_ID;
    private final int color1;
    private final int color2;
    private final EntityType<?> defaultType;
    
    public SpawnEggItem(final EntityType<?> ais, final int integer2, final int integer3, final Properties a) {
        super(a);
        this.defaultType = ais;
        this.color1 = integer2;
        this.color2 = integer3;
        SpawnEggItem.BY_ID.put(ais, this);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        if (bhr3.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        final ItemStack bcj4 = bdu.getItemInHand();
        final BlockPos ew5 = bdu.getClickedPos();
        final Direction fb6 = bdu.getClickedFace();
        final BlockState bvt7 = bhr3.getBlockState(ew5);
        final Block bmv8 = bvt7.getBlock();
        if (bmv8 == Blocks.SPAWNER) {
            final BlockEntity btw9 = bhr3.getBlockEntity(ew5);
            if (btw9 instanceof SpawnerBlockEntity) {
                final BaseSpawner bgy10 = ((SpawnerBlockEntity)btw9).getSpawner();
                final EntityType<?> ais11 = this.getType(bcj4.getTag());
                bgy10.setEntityId(ais11);
                btw9.setChanged();
                bhr3.sendBlockUpdated(ew5, bvt7, bvt7, 3);
                bcj4.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        BlockPos ew6;
        if (bvt7.getCollisionShape(bhr3, ew5).isEmpty()) {
            ew6 = ew5;
        }
        else {
            ew6 = ew5.relative(fb6);
        }
        final EntityType<?> ais12 = this.getType(bcj4.getTag());
        if (ais12.spawn(bhr3, bcj4, bdu.getPlayer(), ew6, MobSpawnType.SPAWN_EGG, true, !Objects.equals(ew5, ew6) && fb6 == Direction.UP) != null) {
            bcj4.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (bhr.isClientSide) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        final HitResult csf6 = Item.getPlayerPOVHitResult(bhr, awg, ClipContext.Fluid.SOURCE_ONLY);
        if (csf6.getType() != HitResult.Type.BLOCK) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        final BlockHitResult csd7 = (BlockHitResult)csf6;
        final BlockPos ew8 = csd7.getBlockPos();
        if (!(bhr.getBlockState(ew8).getBlock() instanceof LiquidBlock)) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        if (!bhr.mayInteract(awg, ew8) || !awg.mayUseItemAt(ew8, csd7.getDirection(), bcj5)) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
        }
        final EntityType<?> ais9 = this.getType(bcj5.getTag());
        if (ais9.spawn(bhr, bcj5, awg, ew8, MobSpawnType.SPAWN_EGG, false, false) == null) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        if (!awg.abilities.instabuild) {
            bcj5.shrink(1);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    public boolean spawnsEntity(@Nullable final CompoundTag id, final EntityType<?> ais) {
        return Objects.equals(this.getType(id), ais);
    }
    
    public int getColor(final int integer) {
        return (integer == 0) ? this.color1 : this.color2;
    }
    
    public static SpawnEggItem byId(@Nullable final EntityType<?> ais) {
        return (SpawnEggItem)SpawnEggItem.BY_ID.get(ais);
    }
    
    public static Iterable<SpawnEggItem> eggs() {
        return (Iterable<SpawnEggItem>)Iterables.unmodifiableIterable((Iterable)SpawnEggItem.BY_ID.values());
    }
    
    public EntityType<?> getType(@Nullable final CompoundTag id) {
        if (id != null && id.contains("EntityTag", 10)) {
            final CompoundTag id2 = id.getCompound("EntityTag");
            if (id2.contains("id", 8)) {
                return EntityType.byString(id2.getString("id")).orElse(this.defaultType);
            }
        }
        return this.defaultType;
    }
    
    static {
        BY_ID = (Map)Maps.newIdentityHashMap();
    }
}

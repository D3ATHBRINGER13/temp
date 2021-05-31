package net.minecraft.world.entity.animal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import java.util.function.Consumer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.global.LightningBolt;
import java.util.Random;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.network.syncher.EntityDataAccessor;

public class MushroomCow extends Cow {
    private static final EntityDataAccessor<String> DATA_TYPE;
    private MobEffect effect;
    private int effectDuration;
    private UUID lastLightningBoltUUID;
    
    public MushroomCow(final EntityType<? extends MushroomCow> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        if (bhu.getBlockState(ew.below()).getBlock() == Blocks.MYCELIUM) {
            return 10.0f;
        }
        return bhu.getBrightness(ew) - 0.5f;
    }
    
    public static boolean checkMushroomSpawnRules(final EntityType<MushroomCow> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        return bhs.getBlockState(ew.below()).getBlock() == Blocks.MYCELIUM && bhs.getRawBrightness(ew, 0) > 8;
    }
    
    @Override
    public void thunderHit(final LightningBolt atu) {
        final UUID uUID3 = atu.getUUID();
        if (!uUID3.equals(this.lastLightningBoltUUID)) {
            this.setMushroomType((this.getMushroomType() == MushroomType.RED) ? MushroomType.BROWN : MushroomType.RED);
            this.lastLightningBoltUUID = uUID3;
            this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0f, 1.0f);
        }
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<String>define(MushroomCow.DATA_TYPE, MushroomType.RED.type);
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.BOWL && this.getAge() >= 0 && !awg.abilities.instabuild) {
            bcj4.shrink(1);
            boolean boolean6 = false;
            ItemStack bcj5;
            if (this.effect != null) {
                boolean6 = true;
                bcj5 = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.saveMobEffect(bcj5, this.effect, this.effectDuration);
                this.effect = null;
                this.effectDuration = 0;
            }
            else {
                bcj5 = new ItemStack(Items.MUSHROOM_STEW);
            }
            if (bcj4.isEmpty()) {
                awg.setItemInHand(ahi, bcj5);
            }
            else if (!awg.inventory.add(bcj5)) {
                awg.drop(bcj5, false);
            }
            SoundEvent yo7;
            if (boolean6) {
                yo7 = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
            }
            else {
                yo7 = SoundEvents.MOOSHROOM_MILK;
            }
            this.playSound(yo7, 1.0f, 1.0f);
            return true;
        }
        if (bcj4.getItem() == Items.SHEARS && this.getAge() >= 0) {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + this.getBbHeight() / 2.0f, this.z, 0.0, 0.0, 0.0);
            if (!this.level.isClientSide) {
                this.remove();
                final Cow are5 = EntityType.COW.create(this.level);
                are5.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
                are5.setHealth(this.getHealth());
                are5.yBodyRot = this.yBodyRot;
                if (this.hasCustomName()) {
                    are5.setCustomName(this.getCustomName());
                }
                this.level.addFreshEntity(are5);
                for (int integer6 = 0; integer6 < 5; ++integer6) {
                    this.level.addFreshEntity(new ItemEntity(this.level, this.x, this.y + this.getBbHeight(), this.z, new ItemStack(this.getMushroomType().blockState.getBlock())));
                }
                bcj4.<Player>hurtAndBreak(1, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
                this.playSound(SoundEvents.MOOSHROOM_SHEAR, 1.0f, 1.0f);
            }
            return true;
        }
        if (this.getMushroomType() == MushroomType.BROWN && bcj4.getItem().is(ItemTags.SMALL_FLOWERS)) {
            if (this.effect != null) {
                for (int integer7 = 0; integer7 < 2; ++integer7) {
                    this.level.addParticle(ParticleTypes.SMOKE, this.x + this.random.nextFloat() / 2.0f, this.y + this.getBbHeight() / 2.0f, this.z + this.random.nextFloat() / 2.0f, 0.0, this.random.nextFloat() / 5.0f, 0.0);
                }
            }
            else {
                final Pair<MobEffect, Integer> pair5 = this.getEffectFromItemStack(bcj4);
                if (!awg.abilities.instabuild) {
                    bcj4.shrink(1);
                }
                for (int integer6 = 0; integer6 < 4; ++integer6) {
                    this.level.addParticle(ParticleTypes.EFFECT, this.x + this.random.nextFloat() / 2.0f, this.y + this.getBbHeight() / 2.0f, this.z + this.random.nextFloat() / 2.0f, 0.0, this.random.nextFloat() / 5.0f, 0.0);
                }
                this.effect = (MobEffect)pair5.getLeft();
                this.effectDuration = (int)pair5.getRight();
                this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0f, 1.0f);
            }
        }
        return super.mobInteract(awg, ahi);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putString("Type", this.getMushroomType().type);
        if (this.effect != null) {
            id.putByte("EffectId", (byte)MobEffect.getId(this.effect));
            id.putInt("EffectDuration", this.effectDuration);
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setMushroomType(byType(id.getString("Type")));
        if (id.contains("EffectId", 1)) {
            this.effect = MobEffect.byId(id.getByte("EffectId"));
        }
        if (id.contains("EffectDuration", 3)) {
            this.effectDuration = id.getInt("EffectDuration");
        }
    }
    
    private Pair<MobEffect, Integer> getEffectFromItemStack(final ItemStack bcj) {
        final FlowerBlock boy3 = (FlowerBlock)((BlockItem)bcj.getItem()).getBlock();
        return (Pair<MobEffect, Integer>)Pair.of(boy3.getSuspiciousStewEffect(), boy3.getEffectDuration());
    }
    
    private void setMushroomType(final MushroomType a) {
        this.entityData.<String>set(MushroomCow.DATA_TYPE, a.type);
    }
    
    public MushroomType getMushroomType() {
        return byType(this.entityData.<String>get(MushroomCow.DATA_TYPE));
    }
    
    @Override
    public MushroomCow getBreedOffspring(final AgableMob aim) {
        final MushroomCow arj3 = EntityType.MOOSHROOM.create(this.level);
        arj3.setMushroomType(this.getOffspringType((MushroomCow)aim));
        return arj3;
    }
    
    private MushroomType getOffspringType(final MushroomCow arj) {
        final MushroomType a3 = this.getMushroomType();
        final MushroomType a4 = arj.getMushroomType();
        MushroomType a5;
        if (a3 == a4 && this.random.nextInt(1024) == 0) {
            a5 = ((a3 == MushroomType.BROWN) ? MushroomType.RED : MushroomType.BROWN);
        }
        else {
            a5 = (this.random.nextBoolean() ? a3 : a4);
        }
        return a5;
    }
    
    static {
        DATA_TYPE = SynchedEntityData.<String>defineId(MushroomCow.class, EntityDataSerializers.STRING);
    }
    
    public enum MushroomType {
        RED("red", Blocks.RED_MUSHROOM.defaultBlockState()), 
        BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());
        
        private final String type;
        private final BlockState blockState;
        
        private MushroomType(final String string3, final BlockState bvt) {
            this.type = string3;
            this.blockState = bvt;
        }
        
        public BlockState getBlockState() {
            return this.blockState;
        }
        
        private static MushroomType byType(final String string) {
            for (final MushroomType a5 : values()) {
                if (a5.type.equals(string)) {
                    return a5;
                }
            }
            return MushroomType.RED;
        }
    }
}

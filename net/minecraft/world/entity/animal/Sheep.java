package net.minecraft.world.entity.animal;

import java.util.stream.Collectors;
import java.util.Arrays;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.Blocks;
import java.util.EnumMap;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeType;
import javax.annotation.Nullable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.AgableMob;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.DyeColor;
import java.util.Map;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Sheep extends Animal {
    private static final EntityDataAccessor<Byte> DATA_WOOL_ID;
    private static final Map<DyeColor, ItemLike> ITEM_BY_DYE;
    private static final Map<DyeColor, float[]> COLORARRAY_BY_COLOR;
    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;
    
    private static float[] createSheepColor(final DyeColor bbg) {
        if (bbg == DyeColor.WHITE) {
            return new float[] { 0.9019608f, 0.9019608f, 0.9019608f };
        }
        final float[] arr2 = bbg.getTextureDiffuseColors();
        final float float3 = 0.75f;
        return new float[] { arr2[0] * 0.75f, arr2[1] * 0.75f, arr2[2] * 0.75f };
    }
    
    public static float[] getColorArray(final DyeColor bbg) {
        return (float[])Sheep.COLORARRAY_BY_COLOR.get(bbg);
    }
    
    public Sheep(final EntityType<? extends Sheep> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    protected void registerGoals() {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }
    
    @Override
    protected void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }
    
    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }
        super.aiStep();
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(Sheep.DATA_WOOL_ID, (Byte)0);
    }
    
    public ResourceLocation getDefaultLootTable() {
        if (this.isSheared()) {
            return this.getType().getDefaultLootTable();
        }
        switch (this.getColor()) {
            default: {
                return BuiltInLootTables.SHEEP_WHITE;
            }
            case ORANGE: {
                return BuiltInLootTables.SHEEP_ORANGE;
            }
            case MAGENTA: {
                return BuiltInLootTables.SHEEP_MAGENTA;
            }
            case LIGHT_BLUE: {
                return BuiltInLootTables.SHEEP_LIGHT_BLUE;
            }
            case YELLOW: {
                return BuiltInLootTables.SHEEP_YELLOW;
            }
            case LIME: {
                return BuiltInLootTables.SHEEP_LIME;
            }
            case PINK: {
                return BuiltInLootTables.SHEEP_PINK;
            }
            case GRAY: {
                return BuiltInLootTables.SHEEP_GRAY;
            }
            case LIGHT_GRAY: {
                return BuiltInLootTables.SHEEP_LIGHT_GRAY;
            }
            case CYAN: {
                return BuiltInLootTables.SHEEP_CYAN;
            }
            case PURPLE: {
                return BuiltInLootTables.SHEEP_PURPLE;
            }
            case BLUE: {
                return BuiltInLootTables.SHEEP_BLUE;
            }
            case BROWN: {
                return BuiltInLootTables.SHEEP_BROWN;
            }
            case GREEN: {
                return BuiltInLootTables.SHEEP_GREEN;
            }
            case RED: {
                return BuiltInLootTables.SHEEP_RED;
            }
            case BLACK: {
                return BuiltInLootTables.SHEEP_BLACK;
            }
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 10) {
            this.eatAnimationTick = 40;
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    public float getHeadEatPositionScale(final float float1) {
        if (this.eatAnimationTick <= 0) {
            return 0.0f;
        }
        if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0f;
        }
        if (this.eatAnimationTick < 4) {
            return (this.eatAnimationTick - float1) / 4.0f;
        }
        return -(this.eatAnimationTick - 40 - float1) / 4.0f;
    }
    
    public float getHeadEatAngleScale(final float float1) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            final float float2 = (this.eatAnimationTick - 4 - float1) / 32.0f;
            return 0.62831855f + 0.21991149f * Mth.sin(float2 * 28.7f);
        }
        if (this.eatAnimationTick > 0) {
            return 0.62831855f;
        }
        return this.xRot * 0.017453292f;
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
            this.shear();
            if (!this.level.isClientSide) {
                bcj4.<Player>hurtAndBreak(1, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
            }
        }
        return super.mobInteract(awg, ahi);
    }
    
    public void shear() {
        if (!this.level.isClientSide) {
            this.setSheared(true);
            for (int integer2 = 1 + this.random.nextInt(3), integer3 = 0; integer3 < integer2; ++integer3) {
                final ItemEntity atx4 = this.spawnAtLocation((ItemLike)Sheep.ITEM_BY_DYE.get(this.getColor()), 1);
                if (atx4 != null) {
                    atx4.setDeltaMovement(atx4.getDeltaMovement().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
                }
            }
        }
        this.playSound(SoundEvents.SHEEP_SHEAR, 1.0f, 1.0f);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("Sheared", this.isSheared());
        id.putByte("Color", (byte)this.getColor().getId());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setSheared(id.getBoolean("Sheared"));
        this.setColor(DyeColor.byId(id.getByte("Color")));
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.SHEEP_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }
    
    @Override
    protected void playStepSound(final BlockPos ew, final BlockState bvt) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15f, 1.0f);
    }
    
    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.<Byte>get(Sheep.DATA_WOOL_ID) & 0xF);
    }
    
    public void setColor(final DyeColor bbg) {
        final byte byte3 = this.entityData.<Byte>get(Sheep.DATA_WOOL_ID);
        this.entityData.<Byte>set(Sheep.DATA_WOOL_ID, (byte)((byte3 & 0xF0) | (bbg.getId() & 0xF)));
    }
    
    public boolean isSheared() {
        return (this.entityData.<Byte>get(Sheep.DATA_WOOL_ID) & 0x10) != 0x0;
    }
    
    public void setSheared(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(Sheep.DATA_WOOL_ID);
        if (boolean1) {
            this.entityData.<Byte>set(Sheep.DATA_WOOL_ID, (byte)(byte3 | 0x10));
        }
        else {
            this.entityData.<Byte>set(Sheep.DATA_WOOL_ID, (byte)(byte3 & 0xFFFFFFEF));
        }
    }
    
    public static DyeColor getRandomSheepColor(final Random random) {
        final int integer2 = random.nextInt(100);
        if (integer2 < 5) {
            return DyeColor.BLACK;
        }
        if (integer2 < 10) {
            return DyeColor.GRAY;
        }
        if (integer2 < 15) {
            return DyeColor.LIGHT_GRAY;
        }
        if (integer2 < 18) {
            return DyeColor.BROWN;
        }
        if (random.nextInt(500) == 0) {
            return DyeColor.PINK;
        }
        return DyeColor.WHITE;
    }
    
    @Override
    public Sheep getBreedOffspring(final AgableMob aim) {
        final Sheep ars3 = (Sheep)aim;
        final Sheep ars4 = EntityType.SHEEP.create(this.level);
        ars4.setColor(this.getOffspringColor(this, ars3));
        return ars4;
    }
    
    @Override
    public void ate() {
        this.setSheared(false);
        if (this.isBaby()) {
            this.ageUp(60);
        }
    }
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable SpawnGroupData ajj, @Nullable final CompoundTag id) {
        ajj = super.finalizeSpawn(bhs, ahh, aja, ajj, id);
        this.setColor(getRandomSheepColor(bhs.getRandom()));
        return ajj;
    }
    
    private DyeColor getOffspringColor(final Animal ara1, final Animal ara2) {
        final DyeColor bbg4 = ((Sheep)ara1).getColor();
        final DyeColor bbg5 = ((Sheep)ara2).getColor();
        final CraftingContainer ayw6 = makeContainer(bbg4, bbg5);
        return (DyeColor)this.level.getRecipeManager().<CraftingContainer, CraftingRecipe>getRecipeFor(RecipeType.CRAFTING, ayw6, this.level).map(bej -> bej.assemble(ayw6)).map(ItemStack::getItem).filter(DyeItem.class::isInstance).map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> this.level.random.nextBoolean() ? bbg4 : bbg5);
    }
    
    private static CraftingContainer makeContainer(final DyeColor bbg1, final DyeColor bbg2) {
        final CraftingContainer ayw3 = new CraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public boolean stillValid(final Player awg) {
                return false;
            }
        }, 2, 1);
        ayw3.setItem(0, new ItemStack(DyeItem.byColor(bbg1)));
        ayw3.setItem(1, new ItemStack(DyeItem.byColor(bbg2)));
        return ayw3;
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return 0.95f * aip.height;
    }
    
    static {
        DATA_WOOL_ID = SynchedEntityData.<Byte>defineId(Sheep.class, EntityDataSerializers.BYTE);
        ITEM_BY_DYE = Util.<Map>make((Map)Maps.newEnumMap((Class)DyeColor.class), (java.util.function.Consumer<Map>)(enumMap -> {
            enumMap.put((Enum)DyeColor.WHITE, Blocks.WHITE_WOOL);
            enumMap.put((Enum)DyeColor.ORANGE, Blocks.ORANGE_WOOL);
            enumMap.put((Enum)DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
            enumMap.put((Enum)DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
            enumMap.put((Enum)DyeColor.YELLOW, Blocks.YELLOW_WOOL);
            enumMap.put((Enum)DyeColor.LIME, Blocks.LIME_WOOL);
            enumMap.put((Enum)DyeColor.PINK, Blocks.PINK_WOOL);
            enumMap.put((Enum)DyeColor.GRAY, Blocks.GRAY_WOOL);
            enumMap.put((Enum)DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
            enumMap.put((Enum)DyeColor.CYAN, Blocks.CYAN_WOOL);
            enumMap.put((Enum)DyeColor.PURPLE, Blocks.PURPLE_WOOL);
            enumMap.put((Enum)DyeColor.BLUE, Blocks.BLUE_WOOL);
            enumMap.put((Enum)DyeColor.BROWN, Blocks.BROWN_WOOL);
            enumMap.put((Enum)DyeColor.GREEN, Blocks.GREEN_WOOL);
            enumMap.put((Enum)DyeColor.RED, Blocks.RED_WOOL);
            enumMap.put((Enum)DyeColor.BLACK, Blocks.BLACK_WOOL);
        }));
        COLORARRAY_BY_COLOR = (Map)Maps.newEnumMap((Map)Arrays.stream((Object[])DyeColor.values()).collect(Collectors.toMap(bbg -> bbg, Sheep::createSheepColor)));
    }
}

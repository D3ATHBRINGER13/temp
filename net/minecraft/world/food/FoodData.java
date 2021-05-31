package net.minecraft.world.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class FoodData {
    private int foodLevel;
    private float saturationLevel;
    private float exhaustionLevel;
    private int tickTimer;
    private int lastFoodLevel;
    
    public FoodData() {
        this.foodLevel = 20;
        this.lastFoodLevel = 20;
        this.saturationLevel = 5.0f;
    }
    
    public void eat(final int integer, final float float2) {
        this.foodLevel = Math.min(integer + this.foodLevel, 20);
        this.saturationLevel = Math.min(this.saturationLevel + integer * float2 * 2.0f, (float)this.foodLevel);
    }
    
    public void eat(final Item bce, final ItemStack bcj) {
        if (bce.isEdible()) {
            final FoodProperties ayh4 = bce.getFoodProperties();
            this.eat(ayh4.getNutrition(), ayh4.getSaturationModifier());
        }
    }
    
    public void tick(final Player awg) {
        final Difficulty ahg3 = awg.level.getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0f) {
            this.exhaustionLevel -= 4.0f;
            if (this.saturationLevel > 0.0f) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0f, 0.0f);
            }
            else if (ahg3 != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        final boolean boolean4 = awg.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (boolean4 && this.saturationLevel > 0.0f && awg.isHurt() && this.foodLevel >= 20) {
            ++this.tickTimer;
            if (this.tickTimer >= 10) {
                final float float5 = Math.min(this.saturationLevel, 6.0f);
                awg.heal(float5 / 6.0f);
                this.addExhaustion(float5);
                this.tickTimer = 0;
            }
        }
        else if (boolean4 && this.foodLevel >= 18 && awg.isHurt()) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                awg.heal(1.0f);
                this.addExhaustion(6.0f);
                this.tickTimer = 0;
            }
        }
        else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (awg.getHealth() > 10.0f || ahg3 == Difficulty.HARD || (awg.getHealth() > 1.0f && ahg3 == Difficulty.NORMAL)) {
                    awg.hurt(DamageSource.STARVE, 1.0f);
                }
                this.tickTimer = 0;
            }
        }
        else {
            this.tickTimer = 0;
        }
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        if (id.contains("foodLevel", 99)) {
            this.foodLevel = id.getInt("foodLevel");
            this.tickTimer = id.getInt("foodTickTimer");
            this.saturationLevel = id.getFloat("foodSaturationLevel");
            this.exhaustionLevel = id.getFloat("foodExhaustionLevel");
        }
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        id.putInt("foodLevel", this.foodLevel);
        id.putInt("foodTickTimer", this.tickTimer);
        id.putFloat("foodSaturationLevel", this.saturationLevel);
        id.putFloat("foodExhaustionLevel", this.exhaustionLevel);
    }
    
    public int getFoodLevel() {
        return this.foodLevel;
    }
    
    public boolean needsFood() {
        return this.foodLevel < 20;
    }
    
    public void addExhaustion(final float float1) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + float1, 40.0f);
    }
    
    public float getSaturationLevel() {
        return this.saturationLevel;
    }
    
    public void setFoodLevel(final int integer) {
        this.foodLevel = integer;
    }
    
    public void setSaturation(final float float1) {
        this.saturationLevel = float1;
    }
}

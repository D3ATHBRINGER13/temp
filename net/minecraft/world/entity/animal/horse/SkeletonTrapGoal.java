package net.minecraft.world.entity.animal.horse;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

public class SkeletonTrapGoal extends Goal {
    private final SkeletonHorse horse;
    
    public SkeletonTrapGoal(final SkeletonHorse asg) {
        this.horse = asg;
    }
    
    @Override
    public boolean canUse() {
        return this.horse.level.hasNearbyAlivePlayer(this.horse.x, this.horse.y, this.horse.z, 10.0);
    }
    
    @Override
    public void tick() {
        final DifficultyInstance ahh2 = this.horse.level.getCurrentDifficultyAt(new BlockPos(this.horse));
        this.horse.setTrap(false);
        this.horse.setTamed(true);
        this.horse.setAge(0);
        ((ServerLevel)this.horse.level).addGlobalEntity(new LightningBolt(this.horse.level, this.horse.x, this.horse.y, this.horse.z, true));
        final Skeleton avd3 = this.createSkeleton(ahh2, this.horse);
        avd3.startRiding(this.horse);
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            final AbstractHorse asb5 = this.createHorse(ahh2);
            final Skeleton avd4 = this.createSkeleton(ahh2, asb5);
            avd4.startRiding(asb5);
            asb5.push(this.horse.getRandom().nextGaussian() * 0.5, 0.0, this.horse.getRandom().nextGaussian() * 0.5);
        }
    }
    
    private AbstractHorse createHorse(final DifficultyInstance ahh) {
        final SkeletonHorse asg3 = EntityType.SKELETON_HORSE.create(this.horse.level);
        asg3.finalizeSpawn(this.horse.level, ahh, MobSpawnType.TRIGGERED, null, null);
        asg3.setPos(this.horse.x, this.horse.y, this.horse.z);
        asg3.invulnerableTime = 60;
        asg3.setPersistenceRequired();
        asg3.setTamed(true);
        asg3.setAge(0);
        asg3.level.addFreshEntity(asg3);
        return asg3;
    }
    
    private Skeleton createSkeleton(final DifficultyInstance ahh, final AbstractHorse asb) {
        final Skeleton avd4 = EntityType.SKELETON.create(asb.level);
        avd4.finalizeSpawn(asb.level, ahh, MobSpawnType.TRIGGERED, null, null);
        avd4.setPos(asb.x, asb.y, asb.z);
        avd4.invulnerableTime = 60;
        avd4.setPersistenceRequired();
        if (avd4.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            avd4.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }
        avd4.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(avd4.getRandom(), avd4.getMainHandItem(), (int)(5.0f + ahh.getSpecialMultiplier() * avd4.getRandom().nextInt(18)), false));
        avd4.setItemSlot(EquipmentSlot.HEAD, EnchantmentHelper.enchantItem(avd4.getRandom(), avd4.getItemBySlot(EquipmentSlot.HEAD), (int)(5.0f + ahh.getSpecialMultiplier() * avd4.getRandom().nextInt(18)), false));
        avd4.level.addFreshEntity(avd4);
        return avd4;
    }
}

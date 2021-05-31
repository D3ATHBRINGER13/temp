package net.minecraft.core.dispenser;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.entity.vehicle.Boat;
import java.util.Random;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.Snowball;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockSource;

public interface DispenseItemBehavior {
    public static final DispenseItemBehavior NOOP = (ex, bcj) -> bcj;
    
    ItemStack dispense(final BlockSource ex, final ItemStack bcj);
    
    default void bootStrap() {
        DispenserBlock.registerBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                final Arrow awm5 = new Arrow(bhr, fl.x(), fl.y(), fl.z());
                awm5.pickup = AbstractArrow.Pickup.ALLOWED;
                return awm5;
            }
        });
        DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                final Arrow awm5 = new Arrow(bhr, fl.x(), fl.y(), fl.z());
                awm5.setEffectsFromItem(bcj);
                awm5.pickup = AbstractArrow.Pickup.ALLOWED;
                return awm5;
            }
        });
        DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                final AbstractArrow awk5 = new SpectralArrow(bhr, fl.x(), fl.y(), fl.z());
                awk5.pickup = AbstractArrow.Pickup.ALLOWED;
                return awk5;
            }
        });
        DispenserBlock.registerBehavior(Items.EGG, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                return Util.<ThrownEgg>make(new ThrownEgg(bhr, fl.x(), fl.y(), fl.z()), (java.util.function.Consumer<ThrownEgg>)(axd -> axd.setItem(bcj)));
            }
        });
        DispenserBlock.registerBehavior(Items.SNOWBALL, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                return Util.<Snowball>make(new Snowball(bhr, fl.x(), fl.y(), fl.z()), (java.util.function.Consumer<Snowball>)(awz -> awz.setItem(bcj)));
            }
        });
        DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                return Util.<ThrownExperienceBottle>make(new ThrownExperienceBottle(bhr, fl.x(), fl.y(), fl.z()), (java.util.function.Consumer<ThrownExperienceBottle>)(axf -> axf.setItem(bcj)));
            }
            
            @Override
            protected float getUncertainty() {
                return super.getUncertainty() * 0.5f;
            }
            
            @Override
            protected float getPower() {
                return super.getPower() * 1.25f;
            }
        });
        DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenseItemBehavior() {
            public ItemStack dispense(final BlockSource ex, final ItemStack bcj) {
                return new AbstractProjectileDispenseBehavior() {
                    @Override
                    protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                        return Util.<ThrownPotion>make(new ThrownPotion(bhr, fl.x(), fl.y(), fl.z()), (java.util.function.Consumer<ThrownPotion>)(axg -> axg.setItem(bcj)));
                    }
                    
                    @Override
                    protected float getUncertainty() {
                        return super.getUncertainty() * 0.5f;
                    }
                    
                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25f;
                    }
                }.dispense(ex, bcj);
            }
        });
        DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenseItemBehavior() {
            public ItemStack dispense(final BlockSource ex, final ItemStack bcj) {
                return new AbstractProjectileDispenseBehavior() {
                    @Override
                    protected Projectile getProjectile(final Level bhr, final Position fl, final ItemStack bcj) {
                        return Util.<ThrownPotion>make(new ThrownPotion(bhr, fl.x(), fl.y(), fl.z()), (java.util.function.Consumer<ThrownPotion>)(axg -> axg.setItem(bcj)));
                    }
                    
                    @Override
                    protected float getUncertainty() {
                        return super.getUncertainty() * 0.5f;
                    }
                    
                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25f;
                    }
                }.dispense(ex, bcj);
            }
        });
        final DefaultDispenseItemBehavior fw1 = new DefaultDispenseItemBehavior() {
            public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Direction fb4 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
                final EntityType<?> ais5 = ((SpawnEggItem)bcj.getItem()).getType(bcj.getTag());
                ais5.spawn(ex.getLevel(), bcj, null, ex.getPos().relative(fb4), MobSpawnType.DISPENSER, fb4 != Direction.UP, false);
                bcj.shrink(1);
                return bcj;
            }
        };
        for (final SpawnEggItem bdh3 : SpawnEggItem.eggs()) {
            DispenserBlock.registerBehavior(bdh3, fw1);
        }
        DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior() {
            public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Direction fb4 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
                final double double5 = ex.x() + fb4.getStepX();
                final double double6 = ex.getPos().getY() + 0.2f;
                final double double7 = ex.z() + fb4.getStepZ();
                ex.getLevel().addFreshEntity(new FireworkRocketEntity(ex.getLevel(), double5, double6, double7, bcj));
                bcj.shrink(1);
                return bcj;
            }
            
            @Override
            protected void playSound(final BlockSource ex) {
                ex.getLevel().levelEvent(1004, ex.getPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior() {
            public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Direction fb4 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
                final Position fl5 = DispenserBlock.getDispensePosition(ex);
                final double double6 = fl5.x() + fb4.getStepX() * 0.3f;
                final double double7 = fl5.y() + fb4.getStepY() * 0.3f;
                final double double8 = fl5.z() + fb4.getStepZ() * 0.3f;
                final Level bhr12 = ex.getLevel();
                final Random random13 = bhr12.random;
                final double double9 = random13.nextGaussian() * 0.05 + fb4.getStepX();
                final double double10 = random13.nextGaussian() * 0.05 + fb4.getStepY();
                final double double11 = random13.nextGaussian() * 0.05 + fb4.getStepZ();
                bhr12.addFreshEntity(Util.<SmallFireball>make(new SmallFireball(bhr12, double6, double7, double8, double9, double10, double11), (java.util.function.Consumer<SmallFireball>)(awy -> awy.setItem(bcj))));
                bcj.shrink(1);
                return bcj;
            }
            
            @Override
            protected void playSound(final BlockSource ex) {
                ex.getLevel().levelEvent(1018, ex.getPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK));
        DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE));
        DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH));
        DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE));
        DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK));
        DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA));
        final DispenseItemBehavior fx2 = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
            
            public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final BucketItem bau4 = (BucketItem)bcj.getItem();
                final BlockPos ew5 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
                final Level bhr6 = ex.getLevel();
                if (bau4.emptyBucket(null, bhr6, ew5, null)) {
                    bau4.checkExtraContent(bhr6, bcj, ew5);
                    return new ItemStack(Items.BUCKET);
                }
                return this.defaultDispenseItemBehavior.dispense(ex, bcj);
            }
        };
        DispenserBlock.registerBehavior(Items.LAVA_BUCKET, fx2);
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, fx2);
        DispenserBlock.registerBehavior(Items.SALMON_BUCKET, fx2);
        DispenserBlock.registerBehavior(Items.COD_BUCKET, fx2);
        DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, fx2);
        DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, fx2);
        DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
            
            public ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final LevelAccessor bhs4 = ex.getLevel();
                final BlockPos ew5 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
                final BlockState bvt6 = bhs4.getBlockState(ew5);
                final Block bmv7 = bvt6.getBlock();
                if (!(bmv7 instanceof BucketPickup)) {
                    return super.execute(ex, bcj);
                }
                final Fluid clj9 = ((BucketPickup)bmv7).takeLiquid(bhs4, ew5, bvt6);
                if (!(clj9 instanceof FlowingFluid)) {
                    return super.execute(ex, bcj);
                }
                final Item bce8 = clj9.getBucket();
                bcj.shrink(1);
                if (bcj.isEmpty()) {
                    return new ItemStack(bce8);
                }
                if (ex.<DispenserBlockEntity>getEntity().addItem(new ItemStack(bce8)) < 0) {
                    this.defaultDispenseItemBehavior.dispense(ex, new ItemStack(bce8));
                }
                return bcj;
            }
        });
        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Level bhr4 = ex.getLevel();
                this.success = true;
                final BlockPos ew5 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
                final BlockState bvt6 = bhr4.getBlockState(ew5);
                if (FlintAndSteelItem.canUse(bvt6, bhr4, ew5)) {
                    bhr4.setBlockAndUpdate(ew5, Blocks.FIRE.defaultBlockState());
                }
                else if (FlintAndSteelItem.canLightCampFire(bvt6)) {
                    bhr4.setBlockAndUpdate(ew5, ((AbstractStateHolder<O, BlockState>)bvt6).<Comparable, Boolean>setValue((Property<Comparable>)BlockStateProperties.LIT, true));
                }
                else if (bvt6.getBlock() instanceof TntBlock) {
                    TntBlock.explode(bhr4, ew5);
                    bhr4.removeBlock(ew5, false);
                }
                else {
                    this.success = false;
                }
                if (this.success && bcj.hurt(1, bhr4.random, null)) {
                    bcj.setCount(0);
                }
                return bcj;
            }
        });
        DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                this.success = true;
                final Level bhr4 = ex.getLevel();
                final BlockPos ew5 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
                if (BoneMealItem.growCrop(bcj, bhr4, ew5) || BoneMealItem.growWaterPlant(bcj, bhr4, ew5, null)) {
                    if (!bhr4.isClientSide) {
                        bhr4.levelEvent(2005, ew5, 0);
                    }
                }
                else {
                    this.success = false;
                }
                return bcj;
            }
        });
        DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Level bhr4 = ex.getLevel();
                final BlockPos ew5 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
                final PrimedTnt aty6 = new PrimedTnt(bhr4, ew5.getX() + 0.5, ew5.getY(), ew5.getZ() + 0.5, null);
                bhr4.addFreshEntity(aty6);
                bhr4.playSound(null, aty6.x, aty6.y, aty6.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
                bcj.shrink(1);
                return bcj;
            }
        });
        final DispenseItemBehavior fx3 = new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                this.success = !ArmorItem.dispenseArmor(ex, bcj).isEmpty();
                return bcj;
            }
        };
        DispenserBlock.registerBehavior(Items.CREEPER_HEAD, fx3);
        DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, fx3);
        DispenserBlock.registerBehavior(Items.DRAGON_HEAD, fx3);
        DispenserBlock.registerBehavior(Items.SKELETON_SKULL, fx3);
        DispenserBlock.registerBehavior(Items.PLAYER_HEAD, fx3);
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Level bhr4 = ex.getLevel();
                final Direction fb5 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
                final BlockPos ew6 = ex.getPos().relative(fb5);
                this.success = true;
                if (bhr4.isEmptyBlock(ew6) && WitherSkullBlock.canSpawnMob(bhr4, ew6, bcj)) {
                    bhr4.setBlock(ew6, ((AbstractStateHolder<O, BlockState>)Blocks.WITHER_SKELETON_SKULL.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)SkullBlock.ROTATION, (fb5.getAxis() == Direction.Axis.Y) ? 0 : (fb5.getOpposite().get2DDataValue() * 4)), 3);
                    final BlockEntity btw7 = bhr4.getBlockEntity(ew6);
                    if (btw7 instanceof SkullBlockEntity) {
                        WitherSkullBlock.checkSpawn(bhr4, ew6, (SkullBlockEntity)btw7);
                    }
                    bcj.shrink(1);
                }
                else if (ArmorItem.dispenseArmor(ex, bcj).isEmpty()) {
                    this.success = false;
                }
                return bcj;
            }
        });
        DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Level bhr4 = ex.getLevel();
                final BlockPos ew5 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
                final CarvedPumpkinBlock bni6 = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
                this.success = true;
                if (bhr4.isEmptyBlock(ew5) && bni6.canSpawnGolem(bhr4, ew5)) {
                    if (!bhr4.isClientSide) {
                        bhr4.setBlock(ew5, bni6.defaultBlockState(), 3);
                    }
                    bcj.shrink(1);
                }
                else {
                    final ItemStack bcj2 = ArmorItem.dispenseArmor(ex, bcj);
                    if (bcj2.isEmpty()) {
                        this.success = false;
                    }
                }
                return bcj;
            }
        });
        DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());
        for (final DyeColor bbg7 : DyeColor.values()) {
            DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(bbg7).asItem(), new ShulkerBoxDispenseBehavior());
        }
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
                final Level bhr4 = ex.getLevel();
                if (!bhr4.isClientSide()) {
                    this.success = false;
                    final BlockPos ew5 = ex.getPos().relative(ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING));
                    final List<Sheep> list6 = bhr4.<Sheep>getEntitiesOfClass((java.lang.Class<? extends Sheep>)Sheep.class, new AABB(ew5));
                    for (final Sheep ars8 : list6) {
                        if (ars8.isAlive() && !ars8.isSheared() && !ars8.isBaby()) {
                            ars8.shear();
                            if (bcj.hurt(1, bhr4.random, null)) {
                                bcj.setCount(0);
                            }
                            this.success = true;
                            break;
                        }
                    }
                }
                return bcj;
            }
        });
    }
}

package net.minecraft.world.entity;

import net.minecraft.util.datafix.fixes.References;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixers;
import org.apache.logging.log4j.LogManager;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.Tag;
import java.util.function.Function;
import java.util.function.Consumer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.Util;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.stream.Stream;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.core.Direction;
import java.util.Set;
import java.util.Collections;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import javax.annotation.Nullable;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> {
    private static final Logger LOGGER;
    public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD;
    public static final EntityType<ArmorStand> ARMOR_STAND;
    public static final EntityType<Arrow> ARROW;
    public static final EntityType<Bat> BAT;
    public static final EntityType<Blaze> BLAZE;
    public static final EntityType<Boat> BOAT;
    public static final EntityType<Cat> CAT;
    public static final EntityType<CaveSpider> CAVE_SPIDER;
    public static final EntityType<Chicken> CHICKEN;
    public static final EntityType<Cod> COD;
    public static final EntityType<Cow> COW;
    public static final EntityType<Creeper> CREEPER;
    public static final EntityType<Donkey> DONKEY;
    public static final EntityType<Dolphin> DOLPHIN;
    public static final EntityType<DragonFireball> DRAGON_FIREBALL;
    public static final EntityType<Drowned> DROWNED;
    public static final EntityType<ElderGuardian> ELDER_GUARDIAN;
    public static final EntityType<EndCrystal> END_CRYSTAL;
    public static final EntityType<EnderDragon> ENDER_DRAGON;
    public static final EntityType<EnderMan> ENDERMAN;
    public static final EntityType<Endermite> ENDERMITE;
    public static final EntityType<EvokerFangs> EVOKER_FANGS;
    public static final EntityType<Evoker> EVOKER;
    public static final EntityType<ExperienceOrb> EXPERIENCE_ORB;
    public static final EntityType<EyeOfEnder> EYE_OF_ENDER;
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK;
    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET;
    public static final EntityType<Fox> FOX;
    public static final EntityType<Ghast> GHAST;
    public static final EntityType<Giant> GIANT;
    public static final EntityType<Guardian> GUARDIAN;
    public static final EntityType<Horse> HORSE;
    public static final EntityType<Husk> HUSK;
    public static final EntityType<Illusioner> ILLUSIONER;
    public static final EntityType<ItemEntity> ITEM;
    public static final EntityType<ItemFrame> ITEM_FRAME;
    public static final EntityType<LargeFireball> FIREBALL;
    public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT;
    public static final EntityType<Llama> LLAMA;
    public static final EntityType<LlamaSpit> LLAMA_SPIT;
    public static final EntityType<MagmaCube> MAGMA_CUBE;
    public static final EntityType<Minecart> MINECART;
    public static final EntityType<MinecartChest> CHEST_MINECART;
    public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART;
    public static final EntityType<MinecartFurnace> FURNACE_MINECART;
    public static final EntityType<MinecartHopper> HOPPER_MINECART;
    public static final EntityType<MinecartSpawner> SPAWNER_MINECART;
    public static final EntityType<MinecartTNT> TNT_MINECART;
    public static final EntityType<Mule> MULE;
    public static final EntityType<MushroomCow> MOOSHROOM;
    public static final EntityType<Ocelot> OCELOT;
    public static final EntityType<Painting> PAINTING;
    public static final EntityType<Panda> PANDA;
    public static final EntityType<Parrot> PARROT;
    public static final EntityType<Pig> PIG;
    public static final EntityType<Pufferfish> PUFFERFISH;
    public static final EntityType<PigZombie> ZOMBIE_PIGMAN;
    public static final EntityType<PolarBear> POLAR_BEAR;
    public static final EntityType<PrimedTnt> TNT;
    public static final EntityType<Rabbit> RABBIT;
    public static final EntityType<Salmon> SALMON;
    public static final EntityType<Sheep> SHEEP;
    public static final EntityType<Shulker> SHULKER;
    public static final EntityType<ShulkerBullet> SHULKER_BULLET;
    public static final EntityType<Silverfish> SILVERFISH;
    public static final EntityType<Skeleton> SKELETON;
    public static final EntityType<SkeletonHorse> SKELETON_HORSE;
    public static final EntityType<Slime> SLIME;
    public static final EntityType<SmallFireball> SMALL_FIREBALL;
    public static final EntityType<SnowGolem> SNOW_GOLEM;
    public static final EntityType<Snowball> SNOWBALL;
    public static final EntityType<SpectralArrow> SPECTRAL_ARROW;
    public static final EntityType<Spider> SPIDER;
    public static final EntityType<Squid> SQUID;
    public static final EntityType<Stray> STRAY;
    public static final EntityType<TraderLlama> TRADER_LLAMA;
    public static final EntityType<TropicalFish> TROPICAL_FISH;
    public static final EntityType<Turtle> TURTLE;
    public static final EntityType<ThrownEgg> EGG;
    public static final EntityType<ThrownEnderpearl> ENDER_PEARL;
    public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE;
    public static final EntityType<ThrownPotion> POTION;
    public static final EntityType<ThrownTrident> TRIDENT;
    public static final EntityType<Vex> VEX;
    public static final EntityType<Villager> VILLAGER;
    public static final EntityType<IronGolem> IRON_GOLEM;
    public static final EntityType<Vindicator> VINDICATOR;
    public static final EntityType<Pillager> PILLAGER;
    public static final EntityType<WanderingTrader> WANDERING_TRADER;
    public static final EntityType<Witch> WITCH;
    public static final EntityType<WitherBoss> WITHER;
    public static final EntityType<WitherSkeleton> WITHER_SKELETON;
    public static final EntityType<WitherSkull> WITHER_SKULL;
    public static final EntityType<Wolf> WOLF;
    public static final EntityType<Zombie> ZOMBIE;
    public static final EntityType<ZombieHorse> ZOMBIE_HORSE;
    public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER;
    public static final EntityType<Phantom> PHANTOM;
    public static final EntityType<Ravager> RAVAGER;
    public static final EntityType<LightningBolt> LIGHTNING_BOLT;
    public static final EntityType<Player> PLAYER;
    public static final EntityType<FishingHook> FISHING_BOBBER;
    private final EntityFactory<T> factory;
    private final MobCategory category;
    private final boolean serialize;
    private final boolean summon;
    private final boolean fireImmune;
    private final boolean canSpawnFarFromPlayer;
    @Nullable
    private String descriptionId;
    @Nullable
    private Component description;
    @Nullable
    private ResourceLocation lootTable;
    private final EntityDimensions dimensions;
    
    private static <T extends Entity> EntityType<T> register(final String string, final Builder<T> a) {
        return Registry.<EntityType<T>>register(Registry.ENTITY_TYPE, string, a.build(string));
    }
    
    public static ResourceLocation getKey(final EntityType<?> ais) {
        return Registry.ENTITY_TYPE.getKey(ais);
    }
    
    public static Optional<EntityType<?>> byString(final String string) {
        return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(string));
    }
    
    public EntityType(final EntityFactory<T> b, final MobCategory aiz, final boolean boolean3, final boolean boolean4, final boolean boolean5, final boolean boolean6, final EntityDimensions aip) {
        this.factory = b;
        this.category = aiz;
        this.canSpawnFarFromPlayer = boolean6;
        this.serialize = boolean3;
        this.summon = boolean4;
        this.fireImmune = boolean5;
        this.dimensions = aip;
    }
    
    @Nullable
    public Entity spawn(final Level bhr, @Nullable final ItemStack bcj, @Nullable final Player awg, final BlockPos ew, final MobSpawnType aja, final boolean boolean6, final boolean boolean7) {
        return this.spawn(bhr, (bcj == null) ? null : bcj.getTag(), (bcj != null && bcj.hasCustomHoverName()) ? bcj.getHoverName() : null, awg, ew, aja, boolean6, boolean7);
    }
    
    @Nullable
    public T spawn(final Level bhr, @Nullable final CompoundTag id, @Nullable final Component jo, @Nullable final Player awg, final BlockPos ew, final MobSpawnType aja, final boolean boolean7, final boolean boolean8) {
        final T aio10 = this.create(bhr, id, jo, awg, ew, aja, boolean7, boolean8);
        bhr.addFreshEntity(aio10);
        return aio10;
    }
    
    @Nullable
    public T create(final Level bhr, @Nullable final CompoundTag id, @Nullable final Component jo, @Nullable final Player awg, final BlockPos ew, final MobSpawnType aja, final boolean boolean7, final boolean boolean8) {
        final T aio10 = this.create(bhr);
        if (aio10 == null) {
            return null;
        }
        double double11;
        if (boolean7) {
            aio10.setPos(ew.getX() + 0.5, ew.getY() + 1, ew.getZ() + 0.5);
            double11 = getYOffset(bhr, ew, boolean8, aio10.getBoundingBox());
        }
        else {
            double11 = 0.0;
        }
        aio10.moveTo(ew.getX() + 0.5, ew.getY() + double11, ew.getZ() + 0.5, Mth.wrapDegrees(bhr.random.nextFloat() * 360.0f), 0.0f);
        if (aio10 instanceof Mob) {
            final Mob aiy13 = (Mob)aio10;
            aiy13.yHeadRot = aiy13.yRot;
            aiy13.yBodyRot = aiy13.yRot;
            aiy13.finalizeSpawn(bhr, bhr.getCurrentDifficultyAt(new BlockPos(aiy13)), aja, null, id);
            aiy13.playAmbientSound();
        }
        if (jo != null && aio10 instanceof LivingEntity) {
            aio10.setCustomName(jo);
        }
        updateCustomEntityTag(bhr, awg, aio10, id);
        return aio10;
    }
    
    protected static double getYOffset(final LevelReader bhu, final BlockPos ew, final boolean boolean3, final AABB csc) {
        AABB csc2 = new AABB(ew);
        if (boolean3) {
            csc2 = csc2.expandTowards(0.0, -1.0, 0.0);
        }
        final Stream<VoxelShape> stream6 = bhu.getCollisions(null, csc2, (Set<Entity>)Collections.emptySet());
        return 1.0 + Shapes.collide(Direction.Axis.Y, csc, stream6, boolean3 ? -2.0 : -1.0);
    }
    
    public static void updateCustomEntityTag(final Level bhr, @Nullable final Player awg, @Nullable final Entity aio, @Nullable final CompoundTag id) {
        if (id == null || !id.contains("EntityTag", 10)) {
            return;
        }
        final MinecraftServer minecraftServer5 = bhr.getServer();
        if (minecraftServer5 == null || aio == null) {
            return;
        }
        if (!bhr.isClientSide && aio.onlyOpCanSetNbt() && (awg == null || !minecraftServer5.getPlayerList().isOp(awg.getGameProfile()))) {
            return;
        }
        final CompoundTag id2 = aio.saveWithoutId(new CompoundTag());
        final UUID uUID7 = aio.getUUID();
        id2.merge(id.getCompound("EntityTag"));
        aio.setUUID(uUID7);
        aio.load(id2);
    }
    
    public boolean canSerialize() {
        return this.serialize;
    }
    
    public boolean canSummon() {
        return this.summon;
    }
    
    public boolean fireImmune() {
        return this.fireImmune;
    }
    
    public boolean canSpawnFarFromPlayer() {
        return this.canSpawnFarFromPlayer;
    }
    
    public MobCategory getCategory() {
        return this.category;
    }
    
    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("entity", Registry.ENTITY_TYPE.getKey(this));
        }
        return this.descriptionId;
    }
    
    public Component getDescription() {
        if (this.description == null) {
            this.description = new TranslatableComponent(this.getDescriptionId(), new Object[0]);
        }
        return this.description;
    }
    
    public ResourceLocation getDefaultLootTable() {
        if (this.lootTable == null) {
            final ResourceLocation qv2 = Registry.ENTITY_TYPE.getKey(this);
            this.lootTable = new ResourceLocation(qv2.getNamespace(), "entities/" + qv2.getPath());
        }
        return this.lootTable;
    }
    
    public float getWidth() {
        return this.dimensions.width;
    }
    
    public float getHeight() {
        return this.dimensions.height;
    }
    
    @Nullable
    public T create(final Level bhr) {
        return this.factory.create(this, bhr);
    }
    
    @Nullable
    public static Entity create(final int integer, final Level bhr) {
        return create(bhr, Registry.ENTITY_TYPE.byId(integer));
    }
    
    public static Optional<Entity> create(final CompoundTag id, final Level bhr) {
        return Util.<Entity>ifElse((java.util.Optional<Entity>)by(id).map(ais -> ais.create(bhr)), (java.util.function.Consumer<Entity>)(aio -> aio.load(id)), () -> EntityType.LOGGER.warn("Skipping Entity with id {}", id.getString("id")));
    }
    
    @Nullable
    private static Entity create(final Level bhr, @Nullable final EntityType<?> ais) {
        return (Entity)((ais == null) ? null : ais.create(bhr));
    }
    
    public AABB getAABB(final double double1, final double double2, final double double3) {
        final float float8 = this.getWidth() / 2.0f;
        return new AABB(double1 - float8, double2, double3 - float8, double1 + float8, double2 + this.getHeight(), double3 + float8);
    }
    
    public EntityDimensions getDimensions() {
        return this.dimensions;
    }
    
    public static Optional<EntityType<?>> by(final CompoundTag id) {
        return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(id.getString("id")));
    }
    
    @Nullable
    public static Entity loadEntityRecursive(final CompoundTag id, final Level bhr, final Function<Entity, Entity> function) {
        return (Entity)loadStaticEntity(id, bhr).map((Function)function).map(aio -> {
            if (id.contains("Passengers", 9)) {
                final ListTag ik5 = id.getList("Passengers", 10);
                for (int integer6 = 0; integer6 < ik5.size(); ++integer6) {
                    final Entity aio2 = loadEntityRecursive(ik5.getCompound(integer6), bhr, function);
                    if (aio2 != null) {
                        aio2.startRiding(aio, true);
                    }
                }
            }
            return aio;
        }).orElse(null);
    }
    
    private static Optional<Entity> loadStaticEntity(final CompoundTag id, final Level bhr) {
        try {
            return create(id, bhr);
        }
        catch (RuntimeException runtimeException3) {
            EntityType.LOGGER.warn("Exception loading entity: ", (Throwable)runtimeException3);
            return (Optional<Entity>)Optional.empty();
        }
    }
    
    public int chunkRange() {
        if (this == EntityType.PLAYER) {
            return 32;
        }
        if (this == EntityType.END_CRYSTAL) {
            return 16;
        }
        if (this == EntityType.ENDER_DRAGON || this == EntityType.TNT || this == EntityType.FALLING_BLOCK || this == EntityType.ITEM_FRAME || this == EntityType.LEASH_KNOT || this == EntityType.PAINTING || this == EntityType.ARMOR_STAND || this == EntityType.EXPERIENCE_ORB || this == EntityType.AREA_EFFECT_CLOUD || this == EntityType.EVOKER_FANGS) {
            return 10;
        }
        if (this == EntityType.FISHING_BOBBER || this == EntityType.ARROW || this == EntityType.SPECTRAL_ARROW || this == EntityType.TRIDENT || this == EntityType.SMALL_FIREBALL || this == EntityType.DRAGON_FIREBALL || this == EntityType.FIREBALL || this == EntityType.WITHER_SKULL || this == EntityType.SNOWBALL || this == EntityType.LLAMA_SPIT || this == EntityType.ENDER_PEARL || this == EntityType.EYE_OF_ENDER || this == EntityType.EGG || this == EntityType.POTION || this == EntityType.EXPERIENCE_BOTTLE || this == EntityType.FIREWORK_ROCKET || this == EntityType.ITEM) {
            return 4;
        }
        return 5;
    }
    
    public int updateInterval() {
        if (this == EntityType.PLAYER || this == EntityType.EVOKER_FANGS) {
            return 2;
        }
        if (this == EntityType.EYE_OF_ENDER) {
            return 4;
        }
        if (this == EntityType.FISHING_BOBBER) {
            return 5;
        }
        if (this == EntityType.SMALL_FIREBALL || this == EntityType.DRAGON_FIREBALL || this == EntityType.FIREBALL || this == EntityType.WITHER_SKULL || this == EntityType.SNOWBALL || this == EntityType.LLAMA_SPIT || this == EntityType.ENDER_PEARL || this == EntityType.EGG || this == EntityType.POTION || this == EntityType.EXPERIENCE_BOTTLE || this == EntityType.FIREWORK_ROCKET || this == EntityType.TNT) {
            return 10;
        }
        if (this == EntityType.ARROW || this == EntityType.SPECTRAL_ARROW || this == EntityType.TRIDENT || this == EntityType.ITEM || this == EntityType.FALLING_BLOCK || this == EntityType.EXPERIENCE_ORB) {
            return 20;
        }
        if (this == EntityType.ITEM_FRAME || this == EntityType.LEASH_KNOT || this == EntityType.PAINTING || this == EntityType.AREA_EFFECT_CLOUD || this == EntityType.END_CRYSTAL) {
            return Integer.MAX_VALUE;
        }
        return 3;
    }
    
    public boolean trackDeltas() {
        return this != EntityType.PLAYER && this != EntityType.LLAMA_SPIT && this != EntityType.WITHER && this != EntityType.BAT && this != EntityType.ITEM_FRAME && this != EntityType.LEASH_KNOT && this != EntityType.PAINTING && this != EntityType.END_CRYSTAL && this != EntityType.EVOKER_FANGS;
    }
    
    public boolean is(final Tag<EntityType<?>> zg) {
        return zg.contains(this);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        AREA_EFFECT_CLOUD = EntityType.<AreaEffectCloud>register("area_effect_cloud", Builder.<AreaEffectCloud>of(AreaEffectCloud::new, MobCategory.MISC).fireImmune().sized(6.0f, 0.5f));
        ARMOR_STAND = EntityType.<ArmorStand>register("armor_stand", Builder.<ArmorStand>of(ArmorStand::new, MobCategory.MISC).sized(0.5f, 1.975f));
        ARROW = EntityType.<Arrow>register("arrow", Builder.<Arrow>of(Arrow::new, MobCategory.MISC).sized(0.5f, 0.5f));
        BAT = EntityType.<Bat>register("bat", Builder.<Bat>of(Bat::new, MobCategory.AMBIENT).sized(0.5f, 0.9f));
        BLAZE = EntityType.<Blaze>register("blaze", Builder.<Blaze>of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6f, 1.8f));
        BOAT = EntityType.<Boat>register("boat", Builder.<Boat>of(Boat::new, MobCategory.MISC).sized(1.375f, 0.5625f));
        CAT = EntityType.<Cat>register("cat", Builder.<Cat>of(Cat::new, MobCategory.CREATURE).sized(0.6f, 0.7f));
        CAVE_SPIDER = EntityType.<CaveSpider>register("cave_spider", Builder.<CaveSpider>of(CaveSpider::new, MobCategory.MONSTER).sized(0.7f, 0.5f));
        CHICKEN = EntityType.<Chicken>register("chicken", Builder.<Chicken>of(Chicken::new, MobCategory.CREATURE).sized(0.4f, 0.7f));
        COD = EntityType.<Cod>register("cod", Builder.<Cod>of(Cod::new, MobCategory.WATER_CREATURE).sized(0.5f, 0.3f));
        COW = EntityType.<Cow>register("cow", Builder.<Cow>of(Cow::new, MobCategory.CREATURE).sized(0.9f, 1.4f));
        CREEPER = EntityType.<Creeper>register("creeper", Builder.<Creeper>of(Creeper::new, MobCategory.MONSTER).sized(0.6f, 1.7f));
        DONKEY = EntityType.<Donkey>register("donkey", Builder.<Donkey>of(Donkey::new, MobCategory.CREATURE).sized(1.3964844f, 1.5f));
        DOLPHIN = EntityType.<Dolphin>register("dolphin", Builder.<Dolphin>of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9f, 0.6f));
        DRAGON_FIREBALL = EntityType.<DragonFireball>register("dragon_fireball", Builder.<DragonFireball>of(DragonFireball::new, MobCategory.MISC).sized(1.0f, 1.0f));
        DROWNED = EntityType.<Drowned>register("drowned", Builder.<Drowned>of(Drowned::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        ELDER_GUARDIAN = EntityType.<ElderGuardian>register("elder_guardian", Builder.<ElderGuardian>of(ElderGuardian::new, MobCategory.MONSTER).sized(1.9975f, 1.9975f));
        END_CRYSTAL = EntityType.<EndCrystal>register("end_crystal", Builder.<EndCrystal>of(EndCrystal::new, MobCategory.MISC).sized(2.0f, 2.0f));
        ENDER_DRAGON = EntityType.<EnderDragon>register("ender_dragon", Builder.<EnderDragon>of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0f, 8.0f));
        ENDERMAN = EntityType.<EnderMan>register("enderman", Builder.<EnderMan>of(EnderMan::new, MobCategory.MONSTER).sized(0.6f, 2.9f));
        ENDERMITE = EntityType.<Endermite>register("endermite", Builder.<Endermite>of(Endermite::new, MobCategory.MONSTER).sized(0.4f, 0.3f));
        EVOKER_FANGS = EntityType.<EvokerFangs>register("evoker_fangs", Builder.<EvokerFangs>of(EvokerFangs::new, MobCategory.MISC).sized(0.5f, 0.8f));
        EVOKER = EntityType.<Evoker>register("evoker", Builder.<Evoker>of(Evoker::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        EXPERIENCE_ORB = EntityType.<ExperienceOrb>register("experience_orb", Builder.<ExperienceOrb>of(ExperienceOrb::new, MobCategory.MISC).sized(0.5f, 0.5f));
        EYE_OF_ENDER = EntityType.<EyeOfEnder>register("eye_of_ender", Builder.<EyeOfEnder>of(EyeOfEnder::new, MobCategory.MISC).sized(0.25f, 0.25f));
        FALLING_BLOCK = EntityType.<FallingBlockEntity>register("falling_block", Builder.<FallingBlockEntity>of(FallingBlockEntity::new, MobCategory.MISC).sized(0.98f, 0.98f));
        FIREWORK_ROCKET = EntityType.<FireworkRocketEntity>register("firework_rocket", Builder.<FireworkRocketEntity>of(FireworkRocketEntity::new, MobCategory.MISC).sized(0.25f, 0.25f));
        FOX = EntityType.<Fox>register("fox", Builder.<Fox>of(Fox::new, MobCategory.CREATURE).sized(0.6f, 0.7f));
        GHAST = EntityType.<Ghast>register("ghast", Builder.<Ghast>of(Ghast::new, MobCategory.MONSTER).fireImmune().sized(4.0f, 4.0f));
        GIANT = EntityType.<Giant>register("giant", Builder.<Giant>of(Giant::new, MobCategory.MONSTER).sized(3.6f, 12.0f));
        GUARDIAN = EntityType.<Guardian>register("guardian", Builder.<Guardian>of(Guardian::new, MobCategory.MONSTER).sized(0.85f, 0.85f));
        HORSE = EntityType.<Horse>register("horse", Builder.<Horse>of(Horse::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f));
        HUSK = EntityType.<Husk>register("husk", Builder.<Husk>of(Husk::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        ILLUSIONER = EntityType.<Illusioner>register("illusioner", Builder.<Illusioner>of(Illusioner::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        ITEM = EntityType.<ItemEntity>register("item", Builder.<ItemEntity>of(ItemEntity::new, MobCategory.MISC).sized(0.25f, 0.25f));
        ITEM_FRAME = EntityType.<ItemFrame>register("item_frame", Builder.<ItemFrame>of(ItemFrame::new, MobCategory.MISC).sized(0.5f, 0.5f));
        FIREBALL = EntityType.<LargeFireball>register("fireball", Builder.<LargeFireball>of(LargeFireball::new, MobCategory.MISC).sized(1.0f, 1.0f));
        LEASH_KNOT = EntityType.<LeashFenceKnotEntity>register("leash_knot", Builder.<LeashFenceKnotEntity>of(LeashFenceKnotEntity::new, MobCategory.MISC).noSave().sized(0.5f, 0.5f));
        LLAMA = EntityType.<Llama>register("llama", Builder.<Llama>of(Llama::new, MobCategory.CREATURE).sized(0.9f, 1.87f));
        LLAMA_SPIT = EntityType.<LlamaSpit>register("llama_spit", Builder.<LlamaSpit>of(LlamaSpit::new, MobCategory.MISC).sized(0.25f, 0.25f));
        MAGMA_CUBE = EntityType.<MagmaCube>register("magma_cube", Builder.<MagmaCube>of(MagmaCube::new, MobCategory.MONSTER).fireImmune().sized(2.04f, 2.04f));
        MINECART = EntityType.<Minecart>register("minecart", Builder.<Minecart>of(Minecart::new, MobCategory.MISC).sized(0.98f, 0.7f));
        CHEST_MINECART = EntityType.<MinecartChest>register("chest_minecart", Builder.<MinecartChest>of(MinecartChest::new, MobCategory.MISC).sized(0.98f, 0.7f));
        COMMAND_BLOCK_MINECART = EntityType.<MinecartCommandBlock>register("command_block_minecart", Builder.<MinecartCommandBlock>of(MinecartCommandBlock::new, MobCategory.MISC).sized(0.98f, 0.7f));
        FURNACE_MINECART = EntityType.<MinecartFurnace>register("furnace_minecart", Builder.<MinecartFurnace>of(MinecartFurnace::new, MobCategory.MISC).sized(0.98f, 0.7f));
        HOPPER_MINECART = EntityType.<MinecartHopper>register("hopper_minecart", Builder.<MinecartHopper>of(MinecartHopper::new, MobCategory.MISC).sized(0.98f, 0.7f));
        SPAWNER_MINECART = EntityType.<MinecartSpawner>register("spawner_minecart", Builder.<MinecartSpawner>of(MinecartSpawner::new, MobCategory.MISC).sized(0.98f, 0.7f));
        TNT_MINECART = EntityType.<MinecartTNT>register("tnt_minecart", Builder.<MinecartTNT>of(MinecartTNT::new, MobCategory.MISC).sized(0.98f, 0.7f));
        MULE = EntityType.<Mule>register("mule", Builder.<Mule>of(Mule::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f));
        MOOSHROOM = EntityType.<MushroomCow>register("mooshroom", Builder.<MushroomCow>of(MushroomCow::new, MobCategory.CREATURE).sized(0.9f, 1.4f));
        OCELOT = EntityType.<Ocelot>register("ocelot", Builder.<Ocelot>of(Ocelot::new, MobCategory.CREATURE).sized(0.6f, 0.7f));
        PAINTING = EntityType.<Painting>register("painting", Builder.<Painting>of(Painting::new, MobCategory.MISC).sized(0.5f, 0.5f));
        PANDA = EntityType.<Panda>register("panda", Builder.<Panda>of(Panda::new, MobCategory.CREATURE).sized(1.3f, 1.25f));
        PARROT = EntityType.<Parrot>register("parrot", Builder.<Parrot>of(Parrot::new, MobCategory.CREATURE).sized(0.5f, 0.9f));
        PIG = EntityType.<Pig>register("pig", Builder.<Pig>of(Pig::new, MobCategory.CREATURE).sized(0.9f, 0.9f));
        PUFFERFISH = EntityType.<Pufferfish>register("pufferfish", Builder.<Pufferfish>of(Pufferfish::new, MobCategory.WATER_CREATURE).sized(0.7f, 0.7f));
        ZOMBIE_PIGMAN = EntityType.<PigZombie>register("zombie_pigman", Builder.<PigZombie>of(PigZombie::new, MobCategory.MONSTER).fireImmune().sized(0.6f, 1.95f));
        POLAR_BEAR = EntityType.<PolarBear>register("polar_bear", Builder.<PolarBear>of(PolarBear::new, MobCategory.CREATURE).sized(1.4f, 1.4f));
        TNT = EntityType.<PrimedTnt>register("tnt", Builder.<PrimedTnt>of(PrimedTnt::new, MobCategory.MISC).fireImmune().sized(0.98f, 0.98f));
        RABBIT = EntityType.<Rabbit>register("rabbit", Builder.<Rabbit>of(Rabbit::new, MobCategory.CREATURE).sized(0.4f, 0.5f));
        SALMON = EntityType.<Salmon>register("salmon", Builder.<Salmon>of(Salmon::new, MobCategory.WATER_CREATURE).sized(0.7f, 0.4f));
        SHEEP = EntityType.<Sheep>register("sheep", Builder.<Sheep>of(Sheep::new, MobCategory.CREATURE).sized(0.9f, 1.3f));
        SHULKER = EntityType.<Shulker>register("shulker", Builder.<Shulker>of(Shulker::new, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0f, 1.0f));
        SHULKER_BULLET = EntityType.<ShulkerBullet>register("shulker_bullet", Builder.<ShulkerBullet>of(ShulkerBullet::new, MobCategory.MISC).sized(0.3125f, 0.3125f));
        SILVERFISH = EntityType.<Silverfish>register("silverfish", Builder.<Silverfish>of(Silverfish::new, MobCategory.MONSTER).sized(0.4f, 0.3f));
        SKELETON = EntityType.<Skeleton>register("skeleton", Builder.<Skeleton>of(Skeleton::new, MobCategory.MONSTER).sized(0.6f, 1.99f));
        SKELETON_HORSE = EntityType.<SkeletonHorse>register("skeleton_horse", Builder.<SkeletonHorse>of(SkeletonHorse::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f));
        SLIME = EntityType.<Slime>register("slime", Builder.<Slime>of(Slime::new, MobCategory.MONSTER).sized(2.04f, 2.04f));
        SMALL_FIREBALL = EntityType.<SmallFireball>register("small_fireball", Builder.<SmallFireball>of(SmallFireball::new, MobCategory.MISC).sized(0.3125f, 0.3125f));
        SNOW_GOLEM = EntityType.<SnowGolem>register("snow_golem", Builder.<SnowGolem>of(SnowGolem::new, MobCategory.MISC).sized(0.7f, 1.9f));
        SNOWBALL = EntityType.<Snowball>register("snowball", Builder.<Snowball>of(Snowball::new, MobCategory.MISC).sized(0.25f, 0.25f));
        SPECTRAL_ARROW = EntityType.<SpectralArrow>register("spectral_arrow", Builder.<SpectralArrow>of(SpectralArrow::new, MobCategory.MISC).sized(0.5f, 0.5f));
        SPIDER = EntityType.<Spider>register("spider", Builder.<Spider>of(Spider::new, MobCategory.MONSTER).sized(1.4f, 0.9f));
        SQUID = EntityType.<Squid>register("squid", Builder.<Squid>of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8f, 0.8f));
        STRAY = EntityType.<Stray>register("stray", Builder.<Stray>of(Stray::new, MobCategory.MONSTER).sized(0.6f, 1.99f));
        TRADER_LLAMA = EntityType.<TraderLlama>register("trader_llama", Builder.<TraderLlama>of(TraderLlama::new, MobCategory.CREATURE).sized(0.9f, 1.87f));
        TROPICAL_FISH = EntityType.<TropicalFish>register("tropical_fish", Builder.<TropicalFish>of(TropicalFish::new, MobCategory.WATER_CREATURE).sized(0.5f, 0.4f));
        TURTLE = EntityType.<Turtle>register("turtle", Builder.<Turtle>of(Turtle::new, MobCategory.CREATURE).sized(1.2f, 0.4f));
        EGG = EntityType.<ThrownEgg>register("egg", Builder.<ThrownEgg>of(ThrownEgg::new, MobCategory.MISC).sized(0.25f, 0.25f));
        ENDER_PEARL = EntityType.<ThrownEnderpearl>register("ender_pearl", Builder.<ThrownEnderpearl>of(ThrownEnderpearl::new, MobCategory.MISC).sized(0.25f, 0.25f));
        EXPERIENCE_BOTTLE = EntityType.<ThrownExperienceBottle>register("experience_bottle", Builder.<ThrownExperienceBottle>of(ThrownExperienceBottle::new, MobCategory.MISC).sized(0.25f, 0.25f));
        POTION = EntityType.<ThrownPotion>register("potion", Builder.<ThrownPotion>of(ThrownPotion::new, MobCategory.MISC).sized(0.25f, 0.25f));
        TRIDENT = EntityType.<ThrownTrident>register("trident", Builder.<ThrownTrident>of(ThrownTrident::new, MobCategory.MISC).sized(0.5f, 0.5f));
        VEX = EntityType.<Vex>register("vex", Builder.<Vex>of(Vex::new, MobCategory.MONSTER).fireImmune().sized(0.4f, 0.8f));
        VILLAGER = EntityType.<Villager>register("villager", Builder.<Villager>of(Villager::new, MobCategory.MISC).sized(0.6f, 1.95f));
        IRON_GOLEM = EntityType.<IronGolem>register("iron_golem", Builder.<IronGolem>of(IronGolem::new, MobCategory.MISC).sized(1.4f, 2.7f));
        VINDICATOR = EntityType.<Vindicator>register("vindicator", Builder.<Vindicator>of(Vindicator::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        PILLAGER = EntityType.<Pillager>register("pillager", Builder.<Pillager>of(Pillager::new, MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6f, 1.95f));
        WANDERING_TRADER = EntityType.<WanderingTrader>register("wandering_trader", Builder.<WanderingTrader>of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6f, 1.95f));
        WITCH = EntityType.<Witch>register("witch", Builder.<Witch>of(Witch::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        WITHER = EntityType.<WitherBoss>register("wither", Builder.<WitherBoss>of(WitherBoss::new, MobCategory.MONSTER).fireImmune().sized(0.9f, 3.5f));
        WITHER_SKELETON = EntityType.<WitherSkeleton>register("wither_skeleton", Builder.<WitherSkeleton>of(WitherSkeleton::new, MobCategory.MONSTER).fireImmune().sized(0.7f, 2.4f));
        WITHER_SKULL = EntityType.<WitherSkull>register("wither_skull", Builder.<WitherSkull>of(WitherSkull::new, MobCategory.MISC).sized(0.3125f, 0.3125f));
        WOLF = EntityType.<Wolf>register("wolf", Builder.<Wolf>of(Wolf::new, MobCategory.CREATURE).sized(0.6f, 0.85f));
        ZOMBIE = EntityType.<Zombie>register("zombie", Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        ZOMBIE_HORSE = EntityType.<ZombieHorse>register("zombie_horse", Builder.<ZombieHorse>of(ZombieHorse::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f));
        ZOMBIE_VILLAGER = EntityType.<ZombieVillager>register("zombie_villager", Builder.<ZombieVillager>of(ZombieVillager::new, MobCategory.MONSTER).sized(0.6f, 1.95f));
        PHANTOM = EntityType.<Phantom>register("phantom", Builder.<Phantom>of(Phantom::new, MobCategory.MONSTER).sized(0.9f, 0.5f));
        RAVAGER = EntityType.<Ravager>register("ravager", Builder.<Ravager>of(Ravager::new, MobCategory.MONSTER).sized(1.95f, 2.2f));
        LIGHTNING_BOLT = EntityType.<LightningBolt>register("lightning_bolt", (Builder<LightningBolt>)Builder.<T>createNothing(MobCategory.MISC).noSave().sized(0.0f, 0.0f));
        PLAYER = EntityType.<Player>register("player", (Builder<Player>)Builder.<T>createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6f, 1.8f));
        FISHING_BOBBER = EntityType.<FishingHook>register("fishing_bobber", (Builder<FishingHook>)Builder.<T>createNothing(MobCategory.MISC).noSave().noSummon().sized(0.25f, 0.25f));
    }
    
    public static class Builder<T extends Entity> {
        private final EntityFactory<T> factory;
        private final MobCategory category;
        private boolean serialize;
        private boolean summon;
        private boolean fireImmune;
        private boolean canSpawnFarFromPlayer;
        private EntityDimensions dimensions;
        
        private Builder(final EntityFactory<T> b, final MobCategory aiz) {
            this.serialize = true;
            this.summon = true;
            this.dimensions = EntityDimensions.scalable(0.6f, 1.8f);
            this.factory = b;
            this.category = aiz;
            this.canSpawnFarFromPlayer = (aiz == MobCategory.CREATURE || aiz == MobCategory.MISC);
        }
        
        public static <T extends Entity> Builder<T> of(final EntityFactory<T> b, final MobCategory aiz) {
            return new Builder<T>(b, aiz);
        }
        
        public static <T extends Entity> Builder<T> createNothing(final MobCategory aiz) {
            return new Builder<T>((ais, bhr) -> null, aiz);
        }
        
        public Builder<T> sized(final float float1, final float float2) {
            this.dimensions = EntityDimensions.scalable(float1, float2);
            return this;
        }
        
        public Builder<T> noSummon() {
            this.summon = false;
            return this;
        }
        
        public Builder<T> noSave() {
            this.serialize = false;
            return this;
        }
        
        public Builder<T> fireImmune() {
            this.fireImmune = true;
            return this;
        }
        
        public Builder<T> canSpawnFarFromPlayer() {
            this.canSpawnFarFromPlayer = true;
            return this;
        }
        
        public EntityType<T> build(final String string) {
            if (this.serialize) {
                try {
                    DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(References.ENTITY_TREE, string);
                }
                catch (IllegalStateException illegalStateException3) {
                    if (SharedConstants.IS_RUNNING_IN_IDE) {
                        throw illegalStateException3;
                    }
                    EntityType.LOGGER.warn("No data fixer registered for entity {}", string);
                }
            }
            return new EntityType<T>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.dimensions);
        }
    }
    
    public interface EntityFactory<T extends Entity> {
        T create(final EntityType<T> ais, final Level bhr);
    }
}

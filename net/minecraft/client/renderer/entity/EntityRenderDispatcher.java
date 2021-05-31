package net.minecraft.client.renderer.entity;

import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.CaveSpider;
import com.google.common.collect.Maps;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.client.Options;
import net.minecraft.client.Camera;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import java.util.Map;

public class EntityRenderDispatcher {
    private final Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers;
    private final Map<String, PlayerRenderer> playerRenderers;
    private final PlayerRenderer defaultPlayerRenderer;
    private Font font;
    private double xOff;
    private double yOff;
    private double zOff;
    public final TextureManager textureManager;
    public Level level;
    public Camera camera;
    public Entity crosshairPickEntity;
    public float playerRotY;
    public float playerRotX;
    public Options options;
    private boolean solidRender;
    private boolean shouldRenderShadow;
    private boolean renderHitBoxes;
    
    private <T extends Entity> void register(final Class<T> class1, final EntityRenderer<? super T> dsb) {
        this.renderers.put(class1, dsb);
    }
    
    public EntityRenderDispatcher(final TextureManager dxc, final ItemRenderer dsv, final ReloadableResourceManager xg) {
        this.renderers = (Map<Class<? extends Entity>, EntityRenderer<? extends Entity>>)Maps.newHashMap();
        this.playerRenderers = (Map<String, PlayerRenderer>)Maps.newHashMap();
        this.shouldRenderShadow = true;
        this.textureManager = dxc;
        this.<CaveSpider>register(CaveSpider.class, new CaveSpiderRenderer(this));
        this.<Spider>register(Spider.class, new SpiderRenderer(this));
        this.<Pig>register(Pig.class, new PigRenderer(this));
        this.<Sheep>register(Sheep.class, new SheepRenderer(this));
        this.<Cow>register(Cow.class, new CowRenderer(this));
        this.<MushroomCow>register(MushroomCow.class, new MushroomCowRenderer(this));
        this.<Wolf>register(Wolf.class, new WolfRenderer(this));
        this.<Chicken>register(Chicken.class, new ChickenRenderer(this));
        this.<Ocelot>register(Ocelot.class, new OcelotRenderer(this));
        this.<Rabbit>register(Rabbit.class, new RabbitRenderer(this));
        this.<Parrot>register(Parrot.class, new ParrotRenderer(this));
        this.<Turtle>register(Turtle.class, new TurtleRenderer(this));
        this.<Silverfish>register(Silverfish.class, new SilverfishRenderer(this));
        this.<Endermite>register(Endermite.class, new EndermiteRenderer(this));
        this.<Creeper>register(Creeper.class, new CreeperRenderer(this));
        this.<EnderMan>register(EnderMan.class, new EndermanRenderer(this));
        this.<SnowGolem>register(SnowGolem.class, new SnowGolemRenderer(this));
        this.<Skeleton>register(Skeleton.class, new SkeletonRenderer(this));
        this.<WitherSkeleton>register(WitherSkeleton.class, new WitherSkeletonRenderer(this));
        this.<Stray>register(Stray.class, new StrayRenderer(this));
        this.<Witch>register(Witch.class, new WitchRenderer(this));
        this.<Blaze>register(Blaze.class, new BlazeRenderer(this));
        this.<PigZombie>register(PigZombie.class, new PigZombieRenderer(this));
        this.<Zombie>register(Zombie.class, new ZombieRenderer(this));
        this.<ZombieVillager>register(ZombieVillager.class, new ZombieVillagerRenderer(this, xg));
        this.<Husk>register(Husk.class, new HuskRenderer(this));
        this.<Drowned>register(Drowned.class, new DrownedRenderer(this));
        this.<Slime>register(Slime.class, new SlimeRenderer(this));
        this.<MagmaCube>register(MagmaCube.class, new LavaSlimeRenderer(this));
        this.<Giant>register(Giant.class, new GiantMobRenderer(this, 6.0f));
        this.<Ghast>register(Ghast.class, new GhastRenderer(this));
        this.<Squid>register(Squid.class, new SquidRenderer(this));
        this.<Villager>register(Villager.class, new VillagerRenderer(this, xg));
        this.<WanderingTrader>register(WanderingTrader.class, new WanderingTraderRenderer(this));
        this.<IronGolem>register(IronGolem.class, new IronGolemRenderer(this));
        this.<Bat>register(Bat.class, new BatRenderer(this));
        this.<Guardian>register(Guardian.class, new GuardianRenderer(this));
        this.<ElderGuardian>register(ElderGuardian.class, new ElderGuardianRenderer(this));
        this.<Shulker>register(Shulker.class, new ShulkerRenderer(this));
        this.<PolarBear>register(PolarBear.class, new PolarBearRenderer(this));
        this.<Evoker>register(Evoker.class, new EvokerRenderer(this));
        this.<Vindicator>register(Vindicator.class, new VindicatorRenderer(this));
        this.<Pillager>register(Pillager.class, new PillagerRenderer(this));
        this.<Ravager>register(Ravager.class, new RavagerRenderer(this));
        this.<Vex>register(Vex.class, new VexRenderer(this));
        this.<Illusioner>register(Illusioner.class, new IllusionerRenderer(this));
        this.<Phantom>register(Phantom.class, new PhantomRenderer(this));
        this.<Pufferfish>register(Pufferfish.class, new PufferfishRenderer(this));
        this.<Salmon>register(Salmon.class, new SalmonRenderer(this));
        this.<Cod>register(Cod.class, new CodRenderer(this));
        this.<TropicalFish>register(TropicalFish.class, new TropicalFishRenderer(this));
        this.<Dolphin>register(Dolphin.class, new DolphinRenderer(this));
        this.<Panda>register(Panda.class, new PandaRenderer(this));
        this.<Cat>register(Cat.class, new CatRenderer(this));
        this.<Fox>register(Fox.class, new FoxRenderer(this));
        this.<EnderDragon>register(EnderDragon.class, new EnderDragonRenderer(this));
        this.<EndCrystal>register(EndCrystal.class, new EndCrystalRenderer(this));
        this.<WitherBoss>register(WitherBoss.class, new WitherBossRenderer(this));
        this.<Entity>register(Entity.class, new DefaultRenderer(this));
        this.<Painting>register(Painting.class, new PaintingRenderer(this));
        this.<ItemFrame>register(ItemFrame.class, new ItemFrameRenderer(this, dsv));
        this.<LeashFenceKnotEntity>register(LeashFenceKnotEntity.class, new LeashKnotRenderer(this));
        this.<Arrow>register(Arrow.class, new TippableArrowRenderer(this));
        this.<SpectralArrow>register(SpectralArrow.class, new SpectralArrowRenderer(this));
        this.<ThrownTrident>register(ThrownTrident.class, new ThrownTridentRenderer(this));
        this.<Snowball>register(Snowball.class, new ThrownItemRenderer<>(this, dsv));
        this.<ThrownEnderpearl>register(ThrownEnderpearl.class, new ThrownItemRenderer<>(this, dsv));
        this.<EyeOfEnder>register(EyeOfEnder.class, new ThrownItemRenderer<>(this, dsv));
        this.<ThrownEgg>register(ThrownEgg.class, new ThrownItemRenderer<>(this, dsv));
        this.<ThrownPotion>register(ThrownPotion.class, new ThrownItemRenderer<>(this, dsv));
        this.<ThrownExperienceBottle>register(ThrownExperienceBottle.class, new ThrownItemRenderer<>(this, dsv));
        this.<FireworkRocketEntity>register(FireworkRocketEntity.class, new FireworkEntityRenderer(this, dsv));
        this.<LargeFireball>register(LargeFireball.class, new ThrownItemRenderer<>(this, dsv, 3.0f));
        this.<SmallFireball>register(SmallFireball.class, new ThrownItemRenderer<>(this, dsv, 0.75f));
        this.<DragonFireball>register(DragonFireball.class, new DragonFireballRenderer(this));
        this.<WitherSkull>register(WitherSkull.class, new WitherSkullRenderer(this));
        this.<ShulkerBullet>register(ShulkerBullet.class, new ShulkerBulletRenderer(this));
        this.<ItemEntity>register(ItemEntity.class, new ItemEntityRenderer(this, dsv));
        this.<ExperienceOrb>register(ExperienceOrb.class, new ExperienceOrbRenderer(this));
        this.<PrimedTnt>register(PrimedTnt.class, new TntRenderer(this));
        this.<FallingBlockEntity>register(FallingBlockEntity.class, new FallingBlockRenderer(this));
        this.<ArmorStand>register(ArmorStand.class, new ArmorStandRenderer(this));
        this.<EvokerFangs>register(EvokerFangs.class, new EvokerFangsRenderer(this));
        this.<MinecartTNT>register(MinecartTNT.class, new TntMinecartRenderer(this));
        this.<MinecartSpawner>register(MinecartSpawner.class, new MinecartRenderer<>(this));
        this.<AbstractMinecart>register(AbstractMinecart.class, new MinecartRenderer<>(this));
        this.<Boat>register(Boat.class, new BoatRenderer(this));
        this.<FishingHook>register(FishingHook.class, new FishingHookRenderer(this));
        this.<AreaEffectCloud>register(AreaEffectCloud.class, new AreaEffectCloudRenderer(this));
        this.<Horse>register(Horse.class, new HorseRenderer(this));
        this.<SkeletonHorse>register(SkeletonHorse.class, new UndeadHorseRenderer(this));
        this.<ZombieHorse>register(ZombieHorse.class, new UndeadHorseRenderer(this));
        this.<Mule>register(Mule.class, new ChestedHorseRenderer(this, 0.92f));
        this.<Donkey>register(Donkey.class, new ChestedHorseRenderer(this, 0.87f));
        this.<Llama>register(Llama.class, new LlamaRenderer(this));
        this.<TraderLlama>register(TraderLlama.class, new LlamaRenderer(this));
        this.<LlamaSpit>register(LlamaSpit.class, new LlamaSpitRenderer(this));
        this.<LightningBolt>register(LightningBolt.class, new LightningBoltRenderer(this));
        this.defaultPlayerRenderer = new PlayerRenderer(this);
        this.playerRenderers.put("default", this.defaultPlayerRenderer);
        this.playerRenderers.put("slim", new PlayerRenderer(this, true));
    }
    
    public void setPosition(final double double1, final double double2, final double double3) {
        this.xOff = double1;
        this.yOff = double2;
        this.zOff = double3;
    }
    
    public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(final Class<? extends Entity> class1) {
        EntityRenderer<? extends Entity> dsb3 = this.renderers.get(class1);
        if (dsb3 == null && class1 != Entity.class) {
            dsb3 = this.<Entity, EntityRenderer<? extends Entity>>getRenderer(class1.getSuperclass());
            this.renderers.put(class1, dsb3);
        }
        return (U)dsb3;
    }
    
    @Nullable
    public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(final T aio) {
        if (!(aio instanceof AbstractClientPlayer)) {
            return this.<Entity, U>getRenderer(aio.getClass());
        }
        final String string3 = ((AbstractClientPlayer)aio).getModelName();
        final PlayerRenderer dwn4 = (PlayerRenderer)this.playerRenderers.get(string3);
        if (dwn4 != null) {
            return (U)dwn4;
        }
        return (U)this.defaultPlayerRenderer;
    }
    
    public void prepare(final Level bhr, final Font cyu, final Camera cxq, final Entity aio, final Options cyg) {
        this.level = bhr;
        this.options = cyg;
        this.camera = cxq;
        this.crosshairPickEntity = aio;
        this.font = cyu;
        if (cxq.getEntity() instanceof LivingEntity && ((LivingEntity)cxq.getEntity()).isSleeping()) {
            final Direction fb7 = ((LivingEntity)cxq.getEntity()).getBedOrientation();
            if (fb7 != null) {
                this.playerRotY = fb7.getOpposite().toYRot();
                this.playerRotX = 0.0f;
            }
        }
        else {
            this.playerRotY = cxq.getYRot();
            this.playerRotX = cxq.getXRot();
        }
    }
    
    public void setPlayerRotY(final float float1) {
        this.playerRotY = float1;
    }
    
    public boolean shouldRenderShadow() {
        return this.shouldRenderShadow;
    }
    
    public void setRenderShadow(final boolean boolean1) {
        this.shouldRenderShadow = boolean1;
    }
    
    public void setRenderHitBoxes(final boolean boolean1) {
        this.renderHitBoxes = boolean1;
    }
    
    public boolean shouldRenderHitBoxes() {
        return this.renderHitBoxes;
    }
    
    public boolean hasSecondPass(final Entity aio) {
        return this.<Entity, EntityRenderer>getRenderer(aio).hasSecondPass();
    }
    
    public boolean shouldRender(final Entity aio, final Culler dqe, final double double3, final double double4, final double double5) {
        final EntityRenderer<Entity> dsb10 = this.<Entity, EntityRenderer<Entity>>getRenderer(aio);
        return dsb10 != null && dsb10.shouldRender(aio, dqe, double3, double4, double5);
    }
    
    public void render(final Entity aio, final float float2, final boolean boolean3) {
        if (aio.tickCount == 0) {
            aio.xOld = aio.x;
            aio.yOld = aio.y;
            aio.zOld = aio.z;
        }
        final double double5 = Mth.lerp(float2, aio.xOld, aio.x);
        final double double6 = Mth.lerp(float2, aio.yOld, aio.y);
        final double double7 = Mth.lerp(float2, aio.zOld, aio.z);
        final float float3 = Mth.lerp(float2, aio.yRotO, aio.yRot);
        int integer12 = aio.getLightColor();
        if (aio.isOnFire()) {
            integer12 = 15728880;
        }
        final int integer13 = integer12 % 65536;
        final int integer14 = integer12 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer13, (float)integer14);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.render(aio, double5 - this.xOff, double6 - this.yOff, double7 - this.zOff, float3, float2, boolean3);
    }
    
    public void render(final Entity aio, final double double2, final double double3, final double double4, final float float5, final float float6, final boolean boolean7) {
        EntityRenderer<Entity> dsb12 = null;
        try {
            dsb12 = this.<Entity, EntityRenderer<Entity>>getRenderer(aio);
            if (dsb12 != null && this.textureManager != null) {
                try {
                    dsb12.setSolidRender(this.solidRender);
                    dsb12.render(aio, double2, double3, double4, float5, float6);
                }
                catch (Throwable throwable13) {
                    throw new ReportedException(CrashReport.forThrowable(throwable13, "Rendering entity in world"));
                }
                try {
                    if (!this.solidRender) {
                        dsb12.postRender(aio, double2, double3, double4, float5, float6);
                    }
                }
                catch (Throwable throwable13) {
                    throw new ReportedException(CrashReport.forThrowable(throwable13, "Post-rendering entity in world"));
                }
                if (this.renderHitBoxes && !aio.isInvisible() && !boolean7 && !Minecraft.getInstance().showOnlyReducedInfo()) {
                    try {
                        this.renderHitbox(aio, double2, double3, double4, float5, float6);
                    }
                    catch (Throwable throwable13) {
                        throw new ReportedException(CrashReport.forThrowable(throwable13, "Rendering entity hitbox in world"));
                    }
                }
            }
        }
        catch (Throwable throwable13) {
            final CrashReport d14 = CrashReport.forThrowable(throwable13, "Rendering entity in world");
            final CrashReportCategory e15 = d14.addCategory("Entity being rendered");
            aio.fillCrashReportCategory(e15);
            final CrashReportCategory e16 = d14.addCategory("Renderer details");
            e16.setDetail("Assigned renderer", dsb12);
            e16.setDetail("Location", CrashReportCategory.formatLocation(double2, double3, double4));
            e16.setDetail("Rotation", float5);
            e16.setDetail("Delta", float6);
            throw new ReportedException(d14);
        }
    }
    
    public void renderSecondPass(final Entity aio, final float float2) {
        if (aio.tickCount == 0) {
            aio.xOld = aio.x;
            aio.yOld = aio.y;
            aio.zOld = aio.z;
        }
        final double double4 = Mth.lerp(float2, aio.xOld, aio.x);
        final double double5 = Mth.lerp(float2, aio.yOld, aio.y);
        final double double6 = Mth.lerp(float2, aio.zOld, aio.z);
        final float float3 = Mth.lerp(float2, aio.yRotO, aio.yRot);
        int integer11 = aio.getLightColor();
        if (aio.isOnFire()) {
            integer11 = 15728880;
        }
        final int integer12 = integer11 % 65536;
        final int integer13 = integer11 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer12, (float)integer13);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final EntityRenderer<Entity> dsb14 = this.<Entity, EntityRenderer<Entity>>getRenderer(aio);
        if (dsb14 != null && this.textureManager != null) {
            dsb14.renderSecondPass(aio, double4 - this.xOff, double5 - this.yOff, double6 - this.zOff, float3, float2);
        }
    }
    
    private void renderHitbox(final Entity aio, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        final float float7 = aio.getBbWidth() / 2.0f;
        final AABB csc12 = aio.getBoundingBox();
        LevelRenderer.renderLineBox(csc12.minX - aio.x + double2, csc12.minY - aio.y + double3, csc12.minZ - aio.z + double4, csc12.maxX - aio.x + double2, csc12.maxY - aio.y + double3, csc12.maxZ - aio.z + double4, 1.0f, 1.0f, 1.0f, 1.0f);
        if (aio instanceof EnderDragon) {
            for (final EnderDragonPart asn16 : ((EnderDragon)aio).getSubEntities()) {
                final double double5 = (asn16.x - asn16.xo) * float6;
                final double double6 = (asn16.y - asn16.yo) * float6;
                final double double7 = (asn16.z - asn16.zo) * float6;
                final AABB csc13 = asn16.getBoundingBox();
                LevelRenderer.renderLineBox(csc13.minX - this.xOff + double5, csc13.minY - this.yOff + double6, csc13.minZ - this.zOff + double7, csc13.maxX - this.xOff + double5, csc13.maxY - this.yOff + double6, csc13.maxZ - this.zOff + double7, 0.25f, 1.0f, 0.0f, 1.0f);
            }
        }
        if (aio instanceof LivingEntity) {
            final float float8 = 0.01f;
            LevelRenderer.renderLineBox(double2 - float7, double3 + aio.getEyeHeight() - 0.009999999776482582, double4 - float7, double2 + float7, double3 + aio.getEyeHeight() + 0.009999999776482582, double4 + float7, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        final Tesselator cuz13 = Tesselator.getInstance();
        final BufferBuilder cuw14 = cuz13.getBuilder();
        final Vec3 csi15 = aio.getViewVector(float6);
        cuw14.begin(3, DefaultVertexFormat.POSITION_COLOR);
        cuw14.vertex(double2, double3 + aio.getEyeHeight(), double4).color(0, 0, 255, 255).endVertex();
        cuw14.vertex(double2 + csi15.x * 2.0, double3 + aio.getEyeHeight() + csi15.y * 2.0, double4 + csi15.z * 2.0).color(0, 0, 255, 255).endVertex();
        cuz13.end();
        GlStateManager.enableTexture();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
    
    public void setLevel(@Nullable final Level bhr) {
        this.level = bhr;
        if (bhr == null) {
            this.camera = null;
        }
    }
    
    public double distanceToSqr(final double double1, final double double2, final double double3) {
        return this.camera.getPosition().distanceToSqr(double1, double2, double3);
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public void setSolidRendering(final boolean boolean1) {
        this.solidRender = boolean1;
    }
}

package net.minecraft.world.damagesource;

import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import javax.annotation.Nullable;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DamageSource {
    public static final DamageSource IN_FIRE;
    public static final DamageSource LIGHTNING_BOLT;
    public static final DamageSource ON_FIRE;
    public static final DamageSource LAVA;
    public static final DamageSource HOT_FLOOR;
    public static final DamageSource IN_WALL;
    public static final DamageSource CRAMMING;
    public static final DamageSource DROWN;
    public static final DamageSource STARVE;
    public static final DamageSource CACTUS;
    public static final DamageSource FALL;
    public static final DamageSource FLY_INTO_WALL;
    public static final DamageSource OUT_OF_WORLD;
    public static final DamageSource GENERIC;
    public static final DamageSource MAGIC;
    public static final DamageSource WITHER;
    public static final DamageSource ANVIL;
    public static final DamageSource FALLING_BLOCK;
    public static final DamageSource DRAGON_BREATH;
    public static final DamageSource FIREWORKS;
    public static final DamageSource DRY_OUT;
    public static final DamageSource SWEET_BERRY_BUSH;
    private boolean bypassArmor;
    private boolean bypassInvul;
    private boolean bypassMagic;
    private float exhaustion;
    private boolean isFireSource;
    private boolean isProjectile;
    private boolean scalesWithDifficulty;
    private boolean isMagic;
    private boolean isExplosion;
    public final String msgId;
    
    public static DamageSource mobAttack(final LivingEntity aix) {
        return new EntityDamageSource("mob", (Entity)aix);
    }
    
    public static DamageSource indirectMobAttack(final Entity aio, final LivingEntity aix) {
        return new IndirectEntityDamageSource("mob", aio, (Entity)aix);
    }
    
    public static DamageSource playerAttack(final Player awg) {
        return new EntityDamageSource("player", (Entity)awg);
    }
    
    public static DamageSource arrow(final AbstractArrow awk, @Nullable final Entity aio) {
        return new IndirectEntityDamageSource("arrow", (Entity)awk, aio).setProjectile();
    }
    
    public static DamageSource trident(final Entity aio1, @Nullable final Entity aio2) {
        return new IndirectEntityDamageSource("trident", aio1, aio2).setProjectile();
    }
    
    public static DamageSource fireball(final AbstractHurtingProjectile awl, @Nullable final Entity aio) {
        if (aio == null) {
            return new IndirectEntityDamageSource("onFire", (Entity)awl, (Entity)awl).setIsFire().setProjectile();
        }
        return new IndirectEntityDamageSource("fireball", (Entity)awl, aio).setIsFire().setProjectile();
    }
    
    public static DamageSource thrown(final Entity aio1, @Nullable final Entity aio2) {
        return new IndirectEntityDamageSource("thrown", aio1, aio2).setProjectile();
    }
    
    public static DamageSource indirectMagic(final Entity aio1, @Nullable final Entity aio2) {
        return new IndirectEntityDamageSource("indirectMagic", aio1, aio2).bypassArmor().setMagic();
    }
    
    public static DamageSource thorns(final Entity aio) {
        return new EntityDamageSource("thorns", aio).setThorns().setMagic();
    }
    
    public static DamageSource explosion(@Nullable final Explosion bhk) {
        if (bhk != null && bhk.getSourceMob() != null) {
            return new EntityDamageSource("explosion.player", (Entity)bhk.getSourceMob()).setScalesWithDifficulty().setExplosion();
        }
        return new DamageSource("explosion").setScalesWithDifficulty().setExplosion();
    }
    
    public static DamageSource explosion(@Nullable final LivingEntity aix) {
        if (aix != null) {
            return new EntityDamageSource("explosion.player", (Entity)aix).setScalesWithDifficulty().setExplosion();
        }
        return new DamageSource("explosion").setScalesWithDifficulty().setExplosion();
    }
    
    public static DamageSource netherBedExplosion() {
        return new NetherBedDamage();
    }
    
    public boolean isProjectile() {
        return this.isProjectile;
    }
    
    public DamageSource setProjectile() {
        this.isProjectile = true;
        return this;
    }
    
    public boolean isExplosion() {
        return this.isExplosion;
    }
    
    public DamageSource setExplosion() {
        this.isExplosion = true;
        return this;
    }
    
    public boolean isBypassArmor() {
        return this.bypassArmor;
    }
    
    public float getFoodExhaustion() {
        return this.exhaustion;
    }
    
    public boolean isBypassInvul() {
        return this.bypassInvul;
    }
    
    public boolean isBypassMagic() {
        return this.bypassMagic;
    }
    
    protected DamageSource(final String string) {
        this.exhaustion = 0.1f;
        this.msgId = string;
    }
    
    @Nullable
    public Entity getDirectEntity() {
        return this.getEntity();
    }
    
    @Nullable
    public Entity getEntity() {
        return null;
    }
    
    protected DamageSource bypassArmor() {
        this.bypassArmor = true;
        this.exhaustion = 0.0f;
        return this;
    }
    
    protected DamageSource bypassInvul() {
        this.bypassInvul = true;
        return this;
    }
    
    protected DamageSource bypassMagic() {
        this.bypassMagic = true;
        this.exhaustion = 0.0f;
        return this;
    }
    
    protected DamageSource setIsFire() {
        this.isFireSource = true;
        return this;
    }
    
    public Component getLocalizedDeathMessage(final LivingEntity aix) {
        final LivingEntity aix2 = aix.getKillCredit();
        final String string4 = "death.attack." + this.msgId;
        final String string5 = string4 + ".player";
        if (aix2 != null) {
            return new TranslatableComponent(string5, new Object[] { aix.getDisplayName(), aix2.getDisplayName() });
        }
        return new TranslatableComponent(string4, new Object[] { aix.getDisplayName() });
    }
    
    public boolean isFire() {
        return this.isFireSource;
    }
    
    public String getMsgId() {
        return this.msgId;
    }
    
    public DamageSource setScalesWithDifficulty() {
        this.scalesWithDifficulty = true;
        return this;
    }
    
    public boolean scalesWithDifficulty() {
        return this.scalesWithDifficulty;
    }
    
    public boolean isMagic() {
        return this.isMagic;
    }
    
    public DamageSource setMagic() {
        this.isMagic = true;
        return this;
    }
    
    public boolean isCreativePlayer() {
        final Entity aio2 = this.getEntity();
        return aio2 instanceof Player && ((Player)aio2).abilities.instabuild;
    }
    
    @Nullable
    public Vec3 getSourcePosition() {
        return null;
    }
    
    static {
        IN_FIRE = new DamageSource("inFire").setIsFire();
        LIGHTNING_BOLT = new DamageSource("lightningBolt");
        ON_FIRE = new DamageSource("onFire").bypassArmor().setIsFire();
        LAVA = new DamageSource("lava").setIsFire();
        HOT_FLOOR = new DamageSource("hotFloor").setIsFire();
        IN_WALL = new DamageSource("inWall").bypassArmor();
        CRAMMING = new DamageSource("cramming").bypassArmor();
        DROWN = new DamageSource("drown").bypassArmor();
        STARVE = new DamageSource("starve").bypassArmor().bypassMagic();
        CACTUS = new DamageSource("cactus");
        FALL = new DamageSource("fall").bypassArmor();
        FLY_INTO_WALL = new DamageSource("flyIntoWall").bypassArmor();
        OUT_OF_WORLD = new DamageSource("outOfWorld").bypassArmor().bypassInvul();
        GENERIC = new DamageSource("generic").bypassArmor();
        MAGIC = new DamageSource("magic").bypassArmor().setMagic();
        WITHER = new DamageSource("wither").bypassArmor();
        ANVIL = new DamageSource("anvil");
        FALLING_BLOCK = new DamageSource("fallingBlock");
        DRAGON_BREATH = new DamageSource("dragonBreath").bypassArmor();
        FIREWORKS = new DamageSource("fireworks").setExplosion();
        DRY_OUT = new DamageSource("dryout");
        SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
    }
}

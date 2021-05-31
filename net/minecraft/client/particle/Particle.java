package net.minecraft.client.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RewindableStream;
import java.util.stream.Stream;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.util.Mth;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class Particle {
    private static final AABB INITIAL_AABB;
    protected final Level level;
    protected double xo;
    protected double yo;
    protected double zo;
    protected double x;
    protected double y;
    protected double z;
    protected double xd;
    protected double yd;
    protected double zd;
    private AABB bb;
    protected boolean onGround;
    protected boolean hasPhysics;
    protected boolean removed;
    protected float bbWidth;
    protected float bbHeight;
    protected final Random random;
    protected int age;
    protected int lifetime;
    protected float gravity;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    protected float alpha;
    protected float roll;
    protected float oRoll;
    public static double xOff;
    public static double yOff;
    public static double zOff;
    
    protected Particle(final Level bhr, final double double2, final double double3, final double double4) {
        this.bb = Particle.INITIAL_AABB;
        this.hasPhysics = true;
        this.bbWidth = 0.6f;
        this.bbHeight = 1.8f;
        this.random = new Random();
        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;
        this.alpha = 1.0f;
        this.level = bhr;
        this.setSize(0.2f, 0.2f);
        this.setPos(double2, double3, double4);
        this.xo = double2;
        this.yo = double3;
        this.zo = double4;
        this.lifetime = (int)(4.0f / (this.random.nextFloat() * 0.9f + 0.1f));
    }
    
    public Particle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        this(bhr, double2, double3, double4);
        this.xd = double5 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.yd = double6 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.zd = double7 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        final float float15 = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        final float float16 = Mth.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / float16 * float15 * 0.4000000059604645;
        this.yd = this.yd / float16 * float15 * 0.4000000059604645 + 0.10000000149011612;
        this.zd = this.zd / float16 * float15 * 0.4000000059604645;
    }
    
    public Particle setPower(final float float1) {
        this.xd *= float1;
        this.yd = (this.yd - 0.10000000149011612) * float1 + 0.10000000149011612;
        this.zd *= float1;
        return this;
    }
    
    public Particle scale(final float float1) {
        this.setSize(0.2f * float1, 0.2f * float1);
        return this;
    }
    
    public void setColor(final float float1, final float float2, final float float3) {
        this.rCol = float1;
        this.gCol = float2;
        this.bCol = float3;
    }
    
    protected void setAlpha(final float float1) {
        this.alpha = float1;
    }
    
    public void setLifetime(final int integer) {
        this.lifetime = integer;
    }
    
    public int getLifetime() {
        return this.lifetime;
    }
    
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.yd -= 0.04 * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9800000190734863;
        this.yd *= 0.9800000190734863;
        this.zd *= 0.9800000190734863;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
    
    public abstract void render(final BufferBuilder cuw, final Camera cxq, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8);
    
    public abstract ParticleRenderType getRenderType();
    
    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
    }
    
    public void remove() {
        this.removed = true;
    }
    
    protected void setSize(final float float1, final float float2) {
        if (float1 != this.bbWidth || float2 != this.bbHeight) {
            this.bbWidth = float1;
            this.bbHeight = float2;
            final AABB csc4 = this.getBoundingBox();
            final double double5 = (csc4.minX + csc4.maxX - float1) / 2.0;
            final double double6 = (csc4.minZ + csc4.maxZ - float1) / 2.0;
            this.setBoundingBox(new AABB(double5, csc4.minY, double6, double5 + this.bbWidth, csc4.minY + this.bbHeight, double6 + this.bbWidth));
        }
    }
    
    public void setPos(final double double1, final double double2, final double double3) {
        this.x = double1;
        this.y = double2;
        this.z = double3;
        final float float8 = this.bbWidth / 2.0f;
        final float float9 = this.bbHeight;
        this.setBoundingBox(new AABB(double1 - float8, double2, double3 - float8, double1 + float8, double2 + float9, double3 + float8));
    }
    
    public void move(double double1, double double2, double double3) {
        final double double4 = double1;
        final double double5 = double2;
        final double double6 = double3;
        if (this.hasPhysics && (double1 != 0.0 || double2 != 0.0 || double3 != 0.0)) {
            final Vec3 csi14 = Entity.collideBoundingBoxHeuristically(null, new Vec3(double1, double2, double3), this.getBoundingBox(), this.level, CollisionContext.empty(), new RewindableStream<VoxelShape>((java.util.stream.Stream<VoxelShape>)Stream.empty()));
            double1 = csi14.x;
            double2 = csi14.y;
            double3 = csi14.z;
        }
        if (double1 != 0.0 || double2 != 0.0 || double3 != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(double1, double2, double3));
            this.setLocationFromBoundingbox();
        }
        this.onGround = (double5 != double2 && double5 < 0.0);
        if (double4 != double1) {
            this.xd = 0.0;
        }
        if (double6 != double3) {
            this.zd = 0.0;
        }
    }
    
    protected void setLocationFromBoundingbox() {
        final AABB csc2 = this.getBoundingBox();
        this.x = (csc2.minX + csc2.maxX) / 2.0;
        this.y = csc2.minY;
        this.z = (csc2.minZ + csc2.maxZ) / 2.0;
    }
    
    protected int getLightColor(final float float1) {
        final BlockPos ew3 = new BlockPos(this.x, this.y, this.z);
        if (this.level.hasChunkAt(ew3)) {
            return this.level.getLightColor(ew3, 0);
        }
        return 0;
    }
    
    public boolean isAlive() {
        return !this.removed;
    }
    
    public AABB getBoundingBox() {
        return this.bb;
    }
    
    public void setBoundingBox(final AABB csc) {
        this.bb = csc;
    }
    
    static {
        INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
}

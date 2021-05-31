package net.minecraft.world.level.border;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import net.minecraft.world.level.storage.LevelData;
import java.util.Iterator;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Lists;
import java.util.List;

public class WorldBorder {
    private final List<BorderChangeListener> listeners;
    private double damagePerBlock;
    private double damageSafeZone;
    private int warningTime;
    private int warningBlocks;
    private double centerX;
    private double centerZ;
    private int absoluteMaxSize;
    private BorderExtent extent;
    
    public WorldBorder() {
        this.listeners = (List<BorderChangeListener>)Lists.newArrayList();
        this.damagePerBlock = 0.2;
        this.damageSafeZone = 5.0;
        this.warningTime = 15;
        this.warningBlocks = 5;
        this.absoluteMaxSize = 29999984;
        this.extent = new StaticBorderExtent(6.0E7);
    }
    
    public boolean isWithinBounds(final BlockPos ew) {
        return ew.getX() + 1 > this.getMinX() && ew.getX() < this.getMaxX() && ew.getZ() + 1 > this.getMinZ() && ew.getZ() < this.getMaxZ();
    }
    
    public boolean isWithinBounds(final ChunkPos bhd) {
        return bhd.getMaxBlockX() > this.getMinX() && bhd.getMinBlockX() < this.getMaxX() && bhd.getMaxBlockZ() > this.getMinZ() && bhd.getMinBlockZ() < this.getMaxZ();
    }
    
    public boolean isWithinBounds(final AABB csc) {
        return csc.maxX > this.getMinX() && csc.minX < this.getMaxX() && csc.maxZ > this.getMinZ() && csc.minZ < this.getMaxZ();
    }
    
    public double getDistanceToBorder(final Entity aio) {
        return this.getDistanceToBorder(aio.x, aio.z);
    }
    
    public VoxelShape getCollisionShape() {
        return this.extent.getCollisionShape();
    }
    
    public double getDistanceToBorder(final double double1, final double double2) {
        final double double3 = double2 - this.getMinZ();
        final double double4 = this.getMaxZ() - double2;
        final double double5 = double1 - this.getMinX();
        final double double6 = this.getMaxX() - double1;
        double double7 = Math.min(double5, double6);
        double7 = Math.min(double7, double3);
        return Math.min(double7, double4);
    }
    
    public BorderStatus getStatus() {
        return this.extent.getStatus();
    }
    
    public double getMinX() {
        return this.extent.getMinX();
    }
    
    public double getMinZ() {
        return this.extent.getMinZ();
    }
    
    public double getMaxX() {
        return this.extent.getMaxX();
    }
    
    public double getMaxZ() {
        return this.extent.getMaxZ();
    }
    
    public double getCenterX() {
        return this.centerX;
    }
    
    public double getCenterZ() {
        return this.centerZ;
    }
    
    public void setCenter(final double double1, final double double2) {
        this.centerX = double1;
        this.centerZ = double2;
        this.extent.onCenterChange();
        for (final BorderChangeListener bxd7 : this.getListeners()) {
            bxd7.onBorderCenterSet(this, double1, double2);
        }
    }
    
    public double getSize() {
        return this.extent.getSize();
    }
    
    public long getLerpRemainingTime() {
        return this.extent.getLerpRemainingTime();
    }
    
    public double getLerpTarget() {
        return this.extent.getLerpTarget();
    }
    
    public void setSize(final double double1) {
        this.extent = new StaticBorderExtent(double1);
        for (final BorderChangeListener bxd5 : this.getListeners()) {
            bxd5.onBorderSizeSet(this, double1);
        }
    }
    
    public void lerpSizeBetween(final double double1, final double double2, final long long3) {
        this.extent = ((double1 == double2) ? new StaticBorderExtent(double2) : new MovingBorderExtent(double1, double2, long3));
        for (final BorderChangeListener bxd9 : this.getListeners()) {
            bxd9.onBorderSizeLerping(this, double1, double2, long3);
        }
    }
    
    protected List<BorderChangeListener> getListeners() {
        return (List<BorderChangeListener>)Lists.newArrayList((Iterable)this.listeners);
    }
    
    public void addListener(final BorderChangeListener bxd) {
        this.listeners.add(bxd);
    }
    
    public void setAbsoluteMaxSize(final int integer) {
        this.absoluteMaxSize = integer;
        this.extent.onAbsoluteMaxSizeChange();
    }
    
    public int getAbsoluteMaxSize() {
        return this.absoluteMaxSize;
    }
    
    public double getDamageSafeZone() {
        return this.damageSafeZone;
    }
    
    public void setDamageSafeZone(final double double1) {
        this.damageSafeZone = double1;
        for (final BorderChangeListener bxd5 : this.getListeners()) {
            bxd5.onBorderSetDamageSafeZOne(this, double1);
        }
    }
    
    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }
    
    public void setDamagePerBlock(final double double1) {
        this.damagePerBlock = double1;
        for (final BorderChangeListener bxd5 : this.getListeners()) {
            bxd5.onBorderSetDamagePerBlock(this, double1);
        }
    }
    
    public double getLerpSpeed() {
        return this.extent.getLerpSpeed();
    }
    
    public int getWarningTime() {
        return this.warningTime;
    }
    
    public void setWarningTime(final int integer) {
        this.warningTime = integer;
        for (final BorderChangeListener bxd4 : this.getListeners()) {
            bxd4.onBorderSetWarningTime(this, integer);
        }
    }
    
    public int getWarningBlocks() {
        return this.warningBlocks;
    }
    
    public void setWarningBlocks(final int integer) {
        this.warningBlocks = integer;
        for (final BorderChangeListener bxd4 : this.getListeners()) {
            bxd4.onBorderSetWarningBlocks(this, integer);
        }
    }
    
    public void tick() {
        this.extent = this.extent.update();
    }
    
    public void saveWorldBorderData(final LevelData com) {
        com.setBorderSize(this.getSize());
        com.setBorderX(this.getCenterX());
        com.setBorderZ(this.getCenterZ());
        com.setBorderSafeZone(this.getDamageSafeZone());
        com.setBorderDamagePerBlock(this.getDamagePerBlock());
        com.setBorderWarningBlocks(this.getWarningBlocks());
        com.setBorderWarningTime(this.getWarningTime());
        com.setBorderSizeLerpTarget(this.getLerpTarget());
        com.setBorderSizeLerpTime(this.getLerpRemainingTime());
    }
    
    public void readBorderData(final LevelData com) {
        this.setCenter(com.getBorderX(), com.getBorderZ());
        this.setDamagePerBlock(com.getBorderDamagePerBlock());
        this.setDamageSafeZone(com.getBorderSafeZone());
        this.setWarningBlocks(com.getBorderWarningBlocks());
        this.setWarningTime(com.getBorderWarningTime());
        if (com.getBorderSizeLerpTime() > 0L) {
            this.lerpSizeBetween(com.getBorderSize(), com.getBorderSizeLerpTarget(), com.getBorderSizeLerpTime());
        }
        else {
            this.setSize(com.getBorderSize());
        }
    }
    
    class MovingBorderExtent implements BorderExtent {
        private final double from;
        private final double to;
        private final long lerpEnd;
        private final long lerpBegin;
        private final double lerpDuration;
        
        private MovingBorderExtent(final double double2, final double double3, final long long4) {
            this.from = double2;
            this.to = double3;
            this.lerpDuration = (double)long4;
            this.lerpBegin = Util.getMillis();
            this.lerpEnd = this.lerpBegin + long4;
        }
        
        public double getMinX() {
            return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize));
        }
        
        public double getMinZ() {
            return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize));
        }
        
        public double getMaxX() {
            return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)WorldBorder.this.absoluteMaxSize);
        }
        
        public double getMaxZ() {
            return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)WorldBorder.this.absoluteMaxSize);
        }
        
        public double getSize() {
            final double double2 = (Util.getMillis() - this.lerpBegin) / this.lerpDuration;
            return (double2 < 1.0) ? Mth.lerp(double2, this.from, this.to) : this.to;
        }
        
        public double getLerpSpeed() {
            return Math.abs(this.from - this.to) / (this.lerpEnd - this.lerpBegin);
        }
        
        public long getLerpRemainingTime() {
            return this.lerpEnd - Util.getMillis();
        }
        
        public double getLerpTarget() {
            return this.to;
        }
        
        public BorderStatus getStatus() {
            return (this.to < this.from) ? BorderStatus.SHRINKING : BorderStatus.GROWING;
        }
        
        public void onCenterChange() {
        }
        
        public void onAbsoluteMaxSizeChange() {
        }
        
        public BorderExtent update() {
            if (this.getLerpRemainingTime() <= 0L) {
                return new StaticBorderExtent(this.to);
            }
            return this;
        }
        
        public VoxelShape getCollisionShape() {
            return Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
        }
    }
    
    class StaticBorderExtent implements BorderExtent {
        private final double size;
        private double minX;
        private double minZ;
        private double maxX;
        private double maxZ;
        private VoxelShape shape;
        
        public StaticBorderExtent(final double double2) {
            this.size = double2;
            this.updateBox();
        }
        
        public double getMinX() {
            return this.minX;
        }
        
        public double getMaxX() {
            return this.maxX;
        }
        
        public double getMinZ() {
            return this.minZ;
        }
        
        public double getMaxZ() {
            return this.maxZ;
        }
        
        public double getSize() {
            return this.size;
        }
        
        public BorderStatus getStatus() {
            return BorderStatus.STATIONARY;
        }
        
        public double getLerpSpeed() {
            return 0.0;
        }
        
        public long getLerpRemainingTime() {
            return 0L;
        }
        
        public double getLerpTarget() {
            return this.size;
        }
        
        private void updateBox() {
            this.minX = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize));
            this.minZ = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize));
            this.maxX = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0, (double)WorldBorder.this.absoluteMaxSize);
            this.maxZ = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0, (double)WorldBorder.this.absoluteMaxSize);
            this.shape = Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
        }
        
        public void onAbsoluteMaxSizeChange() {
            this.updateBox();
        }
        
        public void onCenterChange() {
            this.updateBox();
        }
        
        public BorderExtent update() {
            return this;
        }
        
        public VoxelShape getCollisionShape() {
            return this.shape;
        }
    }
    
    interface BorderExtent {
        double getMinX();
        
        double getMaxX();
        
        double getMinZ();
        
        double getMaxZ();
        
        double getSize();
        
        double getLerpSpeed();
        
        long getLerpRemainingTime();
        
        double getLerpTarget();
        
        BorderStatus getStatus();
        
        void onAbsoluteMaxSizeChange();
        
        void onCenterChange();
        
        BorderExtent update();
        
        VoxelShape getCollisionShape();
    }
}

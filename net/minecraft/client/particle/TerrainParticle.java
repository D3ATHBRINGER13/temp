package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.BlockAndBiomeGetter;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainParticle extends TextureSheetParticle {
    private final BlockState blockState;
    private BlockPos pos;
    private final float uo;
    private final float vo;
    
    public TerrainParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final BlockState bvt) {
        super(bhr, double2, double3, double4, double5, double6, double7);
        this.blockState = bvt;
        this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(bvt));
        this.gravity = 1.0f;
        this.rCol = 0.6f;
        this.gCol = 0.6f;
        this.bCol = 0.6f;
        this.quadSize /= 2.0f;
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }
    
    public TerrainParticle init(final BlockPos ew) {
        this.pos = ew;
        if (this.blockState.getBlock() == Blocks.GRASS_BLOCK) {
            return this;
        }
        this.multiplyColor(ew);
        return this;
    }
    
    public TerrainParticle init() {
        this.pos = new BlockPos(this.x, this.y, this.z);
        final Block bmv2 = this.blockState.getBlock();
        if (bmv2 == Blocks.GRASS_BLOCK) {
            return this;
        }
        this.multiplyColor(this.pos);
        return this;
    }
    
    protected void multiplyColor(@Nullable final BlockPos ew) {
        final int integer3 = Minecraft.getInstance().getBlockColors().getColor(this.blockState, this.level, ew, 0);
        this.rCol *= (integer3 >> 16 & 0xFF) / 255.0f;
        this.gCol *= (integer3 >> 8 & 0xFF) / 255.0f;
        this.bCol *= (integer3 & 0xFF) / 255.0f;
    }
    
    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0f) / 4.0f * 16.0f);
    }
    
    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0f * 16.0f);
    }
    
    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0f * 16.0f);
    }
    
    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0f) / 4.0f * 16.0f);
    }
    
    public int getLightColor(final float float1) {
        final int integer3 = super.getLightColor(float1);
        int integer4 = 0;
        if (this.level.hasChunkAt(this.pos)) {
            integer4 = this.level.getLightColor(this.pos, 0);
        }
        return (integer3 == 0) ? integer4 : integer3;
    }
    
    public static class Provider implements ParticleProvider<BlockParticleOption> {
        public Particle createParticle(final BlockParticleOption gc, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final BlockState bvt16 = gc.getState();
            if (bvt16.isAir() || bvt16.getBlock() == Blocks.MOVING_PISTON) {
                return null;
            }
            return new TerrainParticle(bhr, double3, double4, double5, double6, double7, double8, bvt16).init();
        }
    }
}

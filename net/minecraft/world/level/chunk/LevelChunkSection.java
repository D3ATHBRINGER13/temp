package net.minecraft.world.level.chunk;

import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class LevelChunkSection {
    private static final Palette<BlockState> GLOBAL_BLOCKSTATE_PALETTE;
    private final int bottomBlockY;
    private short nonEmptyBlockCount;
    private short tickingBlockCount;
    private short tickingFluidCount;
    private final PalettedContainer<BlockState> states;
    
    public LevelChunkSection(final int integer) {
        this(integer, (short)0, (short)0, (short)0);
    }
    
    public LevelChunkSection(final int integer, final short short2, final short short3, final short short4) {
        this.bottomBlockY = integer;
        this.nonEmptyBlockCount = short2;
        this.tickingBlockCount = short3;
        this.tickingFluidCount = short4;
        this.states = new PalettedContainer<BlockState>(LevelChunkSection.GLOBAL_BLOCKSTATE_PALETTE, Block.BLOCK_STATE_REGISTRY, (java.util.function.Function<CompoundTag, BlockState>)NbtUtils::readBlockState, (java.util.function.Function<BlockState, CompoundTag>)NbtUtils::writeBlockState, Blocks.AIR.defaultBlockState());
    }
    
    public BlockState getBlockState(final int integer1, final int integer2, final int integer3) {
        return this.states.get(integer1, integer2, integer3);
    }
    
    public FluidState getFluidState(final int integer1, final int integer2, final int integer3) {
        return this.states.get(integer1, integer2, integer3).getFluidState();
    }
    
    public void acquire() {
        this.states.acquire();
    }
    
    public void release() {
        this.states.release();
    }
    
    public BlockState setBlockState(final int integer1, final int integer2, final int integer3, final BlockState bvt) {
        return this.setBlockState(integer1, integer2, integer3, bvt, true);
    }
    
    public BlockState setBlockState(final int integer1, final int integer2, final int integer3, final BlockState bvt, final boolean boolean5) {
        BlockState bvt2;
        if (boolean5) {
            bvt2 = this.states.getAndSet(integer1, integer2, integer3, bvt);
        }
        else {
            bvt2 = this.states.getAndSetUnchecked(integer1, integer2, integer3, bvt);
        }
        final FluidState clk8 = bvt2.getFluidState();
        final FluidState clk9 = bvt.getFluidState();
        if (!bvt2.isAir()) {
            --this.nonEmptyBlockCount;
            if (bvt2.isRandomlyTicking()) {
                --this.tickingBlockCount;
            }
        }
        if (!clk8.isEmpty()) {
            --this.tickingFluidCount;
        }
        if (!bvt.isAir()) {
            ++this.nonEmptyBlockCount;
            if (bvt.isRandomlyTicking()) {
                ++this.tickingBlockCount;
            }
        }
        if (!clk9.isEmpty()) {
            ++this.tickingFluidCount;
        }
        return bvt2;
    }
    
    public boolean isEmpty() {
        return this.nonEmptyBlockCount == 0;
    }
    
    public static boolean isEmpty(@Nullable final LevelChunkSection bxu) {
        return bxu == LevelChunk.EMPTY_SECTION || bxu.isEmpty();
    }
    
    public boolean isRandomlyTicking() {
        return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
    }
    
    public boolean isRandomlyTickingBlocks() {
        return this.tickingBlockCount > 0;
    }
    
    public boolean isRandomlyTickingFluids() {
        return this.tickingFluidCount > 0;
    }
    
    public int bottomBlockY() {
        return this.bottomBlockY;
    }
    
    public void recalcBlockCounts() {
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;
        this.tickingFluidCount = 0;
        final FluidState clk4;
        this.states.count((bvt, integer) -> {
            clk4 = bvt.getFluidState();
            if (!bvt.isAir()) {
                this.nonEmptyBlockCount += integer;
                if (bvt.isRandomlyTicking()) {
                    this.tickingBlockCount += integer;
                }
            }
            if (!clk4.isEmpty()) {
                this.nonEmptyBlockCount += integer;
                if (clk4.isRandomlyTicking()) {
                    this.tickingFluidCount += integer;
                }
            }
        });
    }
    
    public PalettedContainer<BlockState> getStates() {
        return this.states;
    }
    
    public void read(final FriendlyByteBuf je) {
        this.nonEmptyBlockCount = je.readShort();
        this.states.read(je);
    }
    
    public void write(final FriendlyByteBuf je) {
        je.writeShort(this.nonEmptyBlockCount);
        this.states.write(je);
    }
    
    public int getSerializedSize() {
        return 2 + this.states.getSerializedSize();
    }
    
    public boolean maybeHas(final BlockState bvt) {
        return this.states.maybeHas(bvt);
    }
    
    static {
        GLOBAL_BLOCKSTATE_PALETTE = new GlobalPalette<BlockState>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState());
    }
}

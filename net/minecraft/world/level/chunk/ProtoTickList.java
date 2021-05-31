package net.minecraft.world.level.chunk;

import net.minecraft.world.level.TickNextTickData;
import java.util.stream.Stream;
import net.minecraft.world.level.TickPriority;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.core.BlockPos;
import java.util.function.Function;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.nbt.ListTag;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.world.level.ChunkPos;
import java.util.function.Predicate;
import net.minecraft.world.level.TickList;

public class ProtoTickList<T> implements TickList<T> {
    protected final Predicate<T> ignore;
    private final ChunkPos chunkPos;
    private final ShortList[] toBeTicked;
    
    public ProtoTickList(final Predicate<T> predicate, final ChunkPos bhd) {
        this(predicate, bhd, new ListTag());
    }
    
    public ProtoTickList(final Predicate<T> predicate, final ChunkPos bhd, final ListTag ik) {
        this.toBeTicked = new ShortList[16];
        this.ignore = predicate;
        this.chunkPos = bhd;
        for (int integer5 = 0; integer5 < ik.size(); ++integer5) {
            final ListTag ik2 = ik.getList(integer5);
            for (int integer6 = 0; integer6 < ik2.size(); ++integer6) {
                ChunkAccess.getOrCreateOffsetList(this.toBeTicked, integer5).add(ik2.getShort(integer6));
            }
        }
    }
    
    public ListTag save() {
        return ChunkSerializer.packOffsets(this.toBeTicked);
    }
    
    public void copyOut(final TickList<T> big, final Function<BlockPos, T> function) {
        for (int integer4 = 0; integer4 < this.toBeTicked.length; ++integer4) {
            if (this.toBeTicked[integer4] != null) {
                for (final Short short6 : this.toBeTicked[integer4]) {
                    final BlockPos ew7 = ProtoChunk.unpackOffsetCoordinates(short6, integer4, this.chunkPos);
                    big.scheduleTick(ew7, (T)function.apply(ew7), 0);
                }
                this.toBeTicked[integer4].clear();
            }
        }
    }
    
    public boolean hasScheduledTick(final BlockPos ew, final T object) {
        return false;
    }
    
    public void scheduleTick(final BlockPos ew, final T object, final int integer, final TickPriority bii) {
        ChunkAccess.getOrCreateOffsetList(this.toBeTicked, ew.getY() >> 4).add(ProtoChunk.packOffsetCoordinates(ew));
    }
    
    public boolean willTickThisTick(final BlockPos ew, final T object) {
        return false;
    }
    
    public void addAll(final Stream<TickNextTickData<T>> stream) {
        stream.forEach(bih -> this.scheduleTick(bih.pos, bih.getType(), 0, bih.priority));
    }
}

package net.minecraft.world.entity.ai.village.poi;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.level.chunk.LevelChunkSection;
import java.util.function.BooleanSupplier;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.world.level.ChunkPos;
import java.util.stream.Stream;
import java.util.function.Predicate;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos;
import net.minecraft.util.datafix.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.world.level.chunk.storage.SectionStorage;

public class PoiManager extends SectionStorage<PoiSection> {
    private final DistanceTracker distanceTracker;
    
    public PoiManager(final File file, final DataFixer dataFixer) {
        super(file, PoiSection::new, PoiSection::new, dataFixer, DataFixTypes.POI_CHUNK);
        this.distanceTracker = new DistanceTracker();
    }
    
    public void add(final BlockPos ew, final PoiType aqs) {
        this.getOrCreate(SectionPos.of(ew).asLong()).add(ew, aqs);
    }
    
    public void remove(final BlockPos ew) {
        this.getOrCreate(SectionPos.of(ew).asLong()).remove(ew);
    }
    
    public long getCountInRange(final Predicate<PoiType> predicate, final BlockPos ew, final int integer, final Occupancy b) {
        return this.getInRange(predicate, ew, integer, b).count();
    }
    
    public Stream<PoiRecord> getInRange(final Predicate<PoiType> predicate, final BlockPos ew, final int integer, final Occupancy b) {
        final int integer2 = integer * integer;
        return (Stream<PoiRecord>)ChunkPos.rangeClosed(new ChunkPos(ew), Math.floorDiv(integer, 16)).flatMap(bhd -> this.getInChunk(predicate, bhd, b).filter(aqq -> aqq.getPos().distSqr(ew) <= integer2));
    }
    
    public Stream<PoiRecord> getInChunk(final Predicate<PoiType> predicate, final ChunkPos bhd, final Occupancy b) {
        return (Stream<PoiRecord>)IntStream.range(0, 16).boxed().flatMap(integer -> this.getInSection(predicate, SectionPos.of(bhd, integer).asLong(), b));
    }
    
    private Stream<PoiRecord> getInSection(final Predicate<PoiType> predicate, final long long2, final Occupancy b) {
        return (Stream<PoiRecord>)this.getOrLoad(long2).map(aqr -> aqr.getRecords(predicate, b)).orElseGet(Stream::empty);
    }
    
    public Stream<BlockPos> findAll(final Predicate<PoiType> predicate1, final Predicate<BlockPos> predicate2, final BlockPos ew, final int integer, final Occupancy b) {
        return (Stream<BlockPos>)this.getInRange(predicate1, ew, integer, b).map(PoiRecord::getPos).filter((Predicate)predicate2);
    }
    
    public Optional<BlockPos> find(final Predicate<PoiType> predicate1, final Predicate<BlockPos> predicate2, final BlockPos ew, final int integer, final Occupancy b) {
        return (Optional<BlockPos>)this.findAll(predicate1, predicate2, ew, integer, b).findFirst();
    }
    
    public Optional<BlockPos> findClosest(final Predicate<PoiType> predicate1, final Predicate<BlockPos> predicate2, final BlockPos ew, final int integer, final Occupancy b) {
        return (Optional<BlockPos>)this.getInRange(predicate1, ew, integer, b).map(PoiRecord::getPos).sorted(Comparator.comparingDouble(ew2 -> ew2.distSqr(ew))).filter((Predicate)predicate2).findFirst();
    }
    
    public Optional<BlockPos> take(final Predicate<PoiType> predicate1, final Predicate<BlockPos> predicate2, final BlockPos ew, final int integer) {
        return (Optional<BlockPos>)this.getInRange(predicate1, ew, integer, Occupancy.HAS_SPACE).filter(aqq -> predicate2.test(aqq.getPos())).findFirst().map(aqq -> {
            aqq.acquireTicket();
            return aqq.getPos();
        });
    }
    
    public Optional<BlockPos> getRandom(final Predicate<PoiType> predicate1, final Predicate<BlockPos> predicate2, final Occupancy b, final BlockPos ew, final int integer, final Random random) {
        final List<PoiRecord> list8 = (List<PoiRecord>)this.getInRange(predicate1, ew, integer, b).collect(Collectors.toList());
        Collections.shuffle((List)list8, random);
        return (Optional<BlockPos>)list8.stream().filter(aqq -> predicate2.test(aqq.getPos())).findFirst().map(PoiRecord::getPos);
    }
    
    public boolean release(final BlockPos ew) {
        return this.getOrCreate(SectionPos.of(ew).asLong()).release(ew);
    }
    
    public boolean exists(final BlockPos ew, final Predicate<PoiType> predicate) {
        return (boolean)this.getOrLoad(SectionPos.of(ew).asLong()).map(aqr -> aqr.exists(ew, predicate)).orElse(false);
    }
    
    public Optional<PoiType> getType(final BlockPos ew) {
        final PoiSection aqr3 = this.getOrCreate(SectionPos.of(ew).asLong());
        return aqr3.getType(ew);
    }
    
    public int sectionsToVillage(final SectionPos fp) {
        this.distanceTracker.runAllUpdates();
        return this.distanceTracker.getLevel(fp.asLong());
    }
    
    private boolean isVillageCenter(final long long1) {
        final Optional<PoiSection> optional4 = this.get(long1);
        return optional4 != null && (boolean)optional4.map(aqr -> aqr.getRecords(PoiType.ALL, Occupancy.IS_OCCUPIED).count() > 0L).orElse(false);
    }
    
    public void tick(final BooleanSupplier booleanSupplier) {
        super.tick(booleanSupplier);
        this.distanceTracker.runAllUpdates();
    }
    
    @Override
    protected void setDirty(final long long1) {
        super.setDirty(long1);
        this.distanceTracker.update(long1, this.distanceTracker.getLevelFromSource(long1), false);
    }
    
    @Override
    protected void onSectionLoad(final long long1) {
        this.distanceTracker.update(long1, this.distanceTracker.getLevelFromSource(long1), false);
    }
    
    public void checkConsistencyWithBlocks(final ChunkPos bhd, final LevelChunkSection bxu) {
        final SectionPos fp4 = SectionPos.of(bhd, bxu.bottomBlockY() >> 4);
        Util.<PoiSection>ifElse(this.getOrLoad(fp4.asLong()), (java.util.function.Consumer<PoiSection>)(aqr -> aqr.refresh((Consumer<BiConsumer<BlockPos, PoiType>>)(biConsumer -> {
            if (mayHavePoi(bxu)) {
                this.updateFromSection(bxu, fp4, (BiConsumer<BlockPos, PoiType>)biConsumer);
            }
        }))), () -> {
            if (mayHavePoi(bxu)) {
                final PoiSection aqr4 = this.getOrCreate(fp4.asLong());
                this.updateFromSection(bxu, fp4, (BiConsumer<BlockPos, PoiType>)aqr4::add);
            }
        });
    }
    
    private static boolean mayHavePoi(final LevelChunkSection bxu) {
        return PoiType.allPoiStates().anyMatch(bxu::maybeHas);
    }
    
    private void updateFromSection(final LevelChunkSection bxu, final SectionPos fp, final BiConsumer<BlockPos, PoiType> biConsumer) {
        fp.blocksInside().forEach(ew -> {
            final BlockState bvt4 = bxu.getBlockState(SectionPos.sectionRelative(ew.getX()), SectionPos.sectionRelative(ew.getY()), SectionPos.sectionRelative(ew.getZ()));
            PoiType.forState(bvt4).ifPresent(aqs -> biConsumer.accept(ew, aqs));
        });
    }
    
    public enum Occupancy {
        HAS_SPACE(PoiRecord::hasSpace), 
        IS_OCCUPIED(PoiRecord::isOccupied), 
        ANY((aqq -> true));
        
        private final Predicate<? super PoiRecord> test;
        
        private Occupancy(final Predicate<? super PoiRecord> predicate) {
            this.test = predicate;
        }
        
        public Predicate<? super PoiRecord> getTest() {
            return this.test;
        }
    }
    
    final class DistanceTracker extends SectionTracker {
        private final Long2ByteMap levels;
        
        protected DistanceTracker() {
            super(7, 16, 256);
            (this.levels = (Long2ByteMap)new Long2ByteOpenHashMap()).defaultReturnValue((byte)7);
        }
        
        @Override
        protected int getLevelFromSource(final long long1) {
            return PoiManager.this.isVillageCenter(long1) ? 0 : 7;
        }
        
        @Override
        protected int getLevel(final long long1) {
            return this.levels.get(long1);
        }
        
        @Override
        protected void setLevel(final long long1, final int integer) {
            if (integer > 6) {
                this.levels.remove(long1);
            }
            else {
                this.levels.put(long1, (byte)integer);
            }
        }
        
        public void runAllUpdates() {
            super.runUpdates(Integer.MAX_VALUE);
        }
    }
}

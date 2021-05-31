package net.minecraft.util.datafix.fixes;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import java.util.Arrays;
import java.util.stream.LongStream;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.BitStorage;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.stream.Collectors;
import java.util.Map;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import com.mojang.datafixers.DataFix;

public class LeavesFix extends DataFix {
    private static final int[][] DIRECTIONS;
    private static final Object2IntMap<String> LEAVES;
    private static final Set<String> LOGS;
    
    public LeavesFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type2 = this.getInputSchema().getType(References.CHUNK);
        final OpticFinder<?> opticFinder3 = type2.findField("Level");
        final OpticFinder<?> opticFinder4 = opticFinder3.type().findField("Sections");
        final Type<?> type3 = opticFinder4.type();
        if (!(type3 instanceof List.ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        }
        final Type<?> type4 = ((List.ListType)type3).getElement();
        final OpticFinder<?> opticFinder5 = DSL.typeFinder((Type)type4);
        return this.fixTypeEverywhereTyped("Leaves fix", (Type)type2, typed -> typed.updateTyped(opticFinder3, typed -> {
            final int[] arr5 = { 0 };
            Typed<?> typed2 = typed.updateTyped(opticFinder4, typed -> {
                final Int2ObjectMap<LeavesSection> int2ObjectMap5 = (Int2ObjectMap<LeavesSection>)new Int2ObjectOpenHashMap((Map)typed.getAllTyped(opticFinder5).stream().map(typed -> new LeavesSection(typed, this.getInputSchema())).collect(Collectors.toMap(Section::getIndex, a -> a)));
                if (int2ObjectMap5.values().stream().allMatch(Section::isSkippable)) {
                    return typed;
                }
                final java.util.List<IntSet> list6 = (java.util.List<IntSet>)Lists.newArrayList();
                for (int integer7 = 0; integer7 < 7; ++integer7) {
                    list6.add(new IntOpenHashSet());
                }
                for (final LeavesSection a8 : int2ObjectMap5.values()) {
                    if (a8.isSkippable()) {
                        continue;
                    }
                    for (int integer8 = 0; integer8 < 4096; ++integer8) {
                        final int integer9 = a8.getBlock(integer8);
                        if (a8.isLog(integer9)) {
                            ((IntSet)list6.get(0)).add(a8.getIndex() << 12 | integer8);
                        }
                        else if (a8.isLeaf(integer9)) {
                            final int integer10 = this.getX(integer8);
                            final int integer11 = this.getZ(integer8);
                            final int n = 0;
                            arr5[n] |= getSideMask(integer10 == 0, integer10 == 15, integer11 == 0, integer11 == 15);
                        }
                    }
                }
                for (int integer7 = 1; integer7 < 7; ++integer7) {
                    final IntSet intSet8 = (IntSet)list6.get(integer7 - 1);
                    final IntSet intSet9 = (IntSet)list6.get(integer7);
                    final IntIterator intIterator10 = intSet8.iterator();
                    while (intIterator10.hasNext()) {
                        final int integer10 = intIterator10.nextInt();
                        final int integer11 = this.getX(integer10);
                        final int integer12 = this.getY(integer10);
                        final int integer13 = this.getZ(integer10);
                        for (final int[] arr2 : LeavesFix.DIRECTIONS) {
                            final int integer14 = integer11 + arr2[0];
                            final int integer15 = integer12 + arr2[1];
                            final int integer16 = integer13 + arr2[2];
                            if (integer14 >= 0 && integer14 <= 15 && integer16 >= 0 && integer16 <= 15 && integer15 >= 0) {
                                if (integer15 <= 255) {
                                    final LeavesSection a9 = (LeavesSection)int2ObjectMap5.get(integer15 >> 4);
                                    if (a9 != null) {
                                        if (!a9.isSkippable()) {
                                            final int integer17 = getIndex(integer14, integer15 & 0xF, integer16);
                                            final int integer18 = a9.getBlock(integer17);
                                            if (a9.isLeaf(integer18)) {
                                                final int integer19 = a9.getDistance(integer18);
                                                if (integer19 > integer7) {
                                                    a9.setDistance(integer17, integer18, integer7);
                                                    intSet9.add(getIndex(integer14, integer15, integer16));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return typed.updateTyped(opticFinder5, typed -> ((LeavesSection)int2ObjectMap5.get(((Dynamic)typed.get(DSL.remainderFinder())).get("Y").asInt(0))).write(typed));
            });
            if (arr5[0] != 0) {
                typed2 = typed2.update(DSL.remainderFinder(), dynamic -> {
                    final Dynamic<?> dynamic2 = DataFixUtils.orElse(dynamic.get("UpgradeData").get(), dynamic.emptyMap());
                    return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.get("Sides").asByte((byte)0) | arr5[0]))));
                });
            }
            return typed2;
        }));
    }
    
    public static int getIndex(final int integer1, final int integer2, final int integer3) {
        return integer2 << 8 | integer3 << 4 | integer1;
    }
    
    private int getX(final int integer) {
        return integer & 0xF;
    }
    
    private int getY(final int integer) {
        return integer >> 8 & 0xFF;
    }
    
    private int getZ(final int integer) {
        return integer >> 4 & 0xF;
    }
    
    public static int getSideMask(final boolean boolean1, final boolean boolean2, final boolean boolean3, final boolean boolean4) {
        int integer5 = 0;
        if (boolean3) {
            if (boolean2) {
                integer5 |= 0x2;
            }
            else if (boolean1) {
                integer5 |= 0x80;
            }
            else {
                integer5 |= 0x1;
            }
        }
        else if (boolean4) {
            if (boolean1) {
                integer5 |= 0x20;
            }
            else if (boolean2) {
                integer5 |= 0x8;
            }
            else {
                integer5 |= 0x10;
            }
        }
        else if (boolean2) {
            integer5 |= 0x4;
        }
        else if (boolean1) {
            integer5 |= 0x40;
        }
        return integer5;
    }
    
    static {
        DIRECTIONS = new int[][] { { -1, 0, 0 }, { 1, 0, 0 }, { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 } };
        LEAVES = (Object2IntMap)DataFixUtils.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
            object2IntOpenHashMap.put("minecraft:acacia_leaves", 0);
            object2IntOpenHashMap.put("minecraft:birch_leaves", 1);
            object2IntOpenHashMap.put("minecraft:dark_oak_leaves", 2);
            object2IntOpenHashMap.put("minecraft:jungle_leaves", 3);
            object2IntOpenHashMap.put("minecraft:oak_leaves", 4);
            object2IntOpenHashMap.put("minecraft:spruce_leaves", 5);
        });
        LOGS = (Set)ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", (Object[])new String[] { "minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log" });
    }
    
    public abstract static class Section {
        private final Type<Pair<String, Dynamic<?>>> blockStateType;
        protected final OpticFinder<java.util.List<Pair<String, Dynamic<?>>>> paletteFinder;
        protected final java.util.List<Dynamic<?>> palette;
        protected final int index;
        @Nullable
        protected BitStorage storage;
        
        public Section(final Typed<?> typed, final Schema schema) {
            this.blockStateType = (Type<Pair<String, Dynamic<?>>>)DSL.named(References.BLOCK_STATE.typeName(), DSL.remainderType());
            this.paletteFinder = (OpticFinder<java.util.List<Pair<String, Dynamic<?>>>>)DSL.fieldFinder("Palette", (Type)DSL.list((Type)this.blockStateType));
            if (!Objects.equals(schema.getType(References.BLOCK_STATE), this.blockStateType)) {
                throw new IllegalStateException("Block state type is not what was expected.");
            }
            final Optional<java.util.List<Pair<String, Dynamic<?>>>> optional4 = (Optional<java.util.List<Pair<String, Dynamic<?>>>>)typed.getOptional((OpticFinder)this.paletteFinder);
            this.palette = (java.util.List<Dynamic<?>>)optional4.map(list -> (java.util.List)list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse(ImmutableList.of());
            final Dynamic<?> dynamic5 = typed.get(DSL.remainderFinder());
            this.index = dynamic5.get("Y").asInt(0);
            this.readStorage(dynamic5);
        }
        
        protected void readStorage(final Dynamic<?> dynamic) {
            if (this.skippable()) {
                this.storage = null;
            }
            else {
                final long[] arr3 = ((LongStream)dynamic.get("BlockStates").asLongStreamOpt().get()).toArray();
                final int integer4 = Math.max(4, DataFixUtils.ceillog2(this.palette.size()));
                this.storage = new BitStorage(integer4, 4096, arr3);
            }
        }
        
        public Typed<?> write(final Typed<?> typed) {
            if (this.isSkippable()) {
                return typed;
            }
            return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.storage.getRaw())))).set((OpticFinder)this.paletteFinder, this.palette.stream().map(dynamic -> Pair.of(References.BLOCK_STATE.typeName(), dynamic)).collect(Collectors.toList()));
        }
        
        public boolean isSkippable() {
            return this.storage == null;
        }
        
        public int getBlock(final int integer) {
            return this.storage.get(integer);
        }
        
        protected int getStateId(final String string, final boolean boolean2, final int integer) {
            return LeavesFix.LEAVES.get(string) << 5 | (boolean2 ? 16 : 0) | integer;
        }
        
        int getIndex() {
            return this.index;
        }
        
        protected abstract boolean skippable();
    }
    
    public static final class LeavesSection extends Section {
        @Nullable
        private IntSet leaveIds;
        @Nullable
        private IntSet logIds;
        @Nullable
        private Int2IntMap stateToIdMap;
        
        public LeavesSection(final Typed<?> typed, final Schema schema) {
            super(typed, schema);
        }
        
        @Override
        protected boolean skippable() {
            this.leaveIds = (IntSet)new IntOpenHashSet();
            this.logIds = (IntSet)new IntOpenHashSet();
            this.stateToIdMap = (Int2IntMap)new Int2IntOpenHashMap();
            for (int integer2 = 0; integer2 < this.palette.size(); ++integer2) {
                final Dynamic<?> dynamic3 = this.palette.get(integer2);
                final String string4 = dynamic3.get("Name").asString("");
                if (LeavesFix.LEAVES.containsKey(string4)) {
                    final boolean boolean5 = Objects.equals(dynamic3.get("Properties").get("decayable").asString(""), "false");
                    this.leaveIds.add(integer2);
                    this.stateToIdMap.put(this.getStateId(string4, boolean5, 7), integer2);
                    this.palette.set(integer2, this.makeLeafTag(dynamic3, string4, boolean5, 7));
                }
                if (LeavesFix.LOGS.contains(string4)) {
                    this.logIds.add(integer2);
                }
            }
            return this.leaveIds.isEmpty() && this.logIds.isEmpty();
        }
        
        private Dynamic<?> makeLeafTag(final Dynamic<?> dynamic, final String string, final boolean boolean3, final int integer) {
            Dynamic<?> dynamic2 = dynamic.emptyMap();
            dynamic2 = dynamic2.set("persistent", dynamic2.createString(boolean3 ? "true" : "false"));
            dynamic2 = dynamic2.set("distance", dynamic2.createString(Integer.toString(integer)));
            Dynamic<?> dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Properties", (Dynamic)dynamic2);
            dynamic3 = dynamic3.set("Name", dynamic3.createString(string));
            return dynamic3;
        }
        
        public boolean isLog(final int integer) {
            return this.logIds.contains(integer);
        }
        
        public boolean isLeaf(final int integer) {
            return this.leaveIds.contains(integer);
        }
        
        private int getDistance(final int integer) {
            if (this.isLog(integer)) {
                return 0;
            }
            return Integer.parseInt(((Dynamic)this.palette.get(integer)).get("Properties").get("distance").asString(""));
        }
        
        private void setDistance(final int integer1, final int integer2, final int integer3) {
            final Dynamic<?> dynamic5 = this.palette.get(integer2);
            final String string6 = dynamic5.get("Name").asString("");
            final boolean boolean7 = Objects.equals(dynamic5.get("Properties").get("persistent").asString(""), "true");
            final int integer4 = this.getStateId(string6, boolean7, integer3);
            if (!this.stateToIdMap.containsKey(integer4)) {
                final int integer5 = this.palette.size();
                this.leaveIds.add(integer5);
                this.stateToIdMap.put(integer4, integer5);
                this.palette.add(this.makeLeafTag(dynamic5, string6, boolean7, integer3));
            }
            final int integer5 = this.stateToIdMap.get(integer4);
            if (1 << this.storage.getBits() <= integer5) {
                final BitStorage zk10 = new BitStorage(this.storage.getBits() + 1, 4096);
                for (int integer6 = 0; integer6 < 4096; ++integer6) {
                    zk10.set(integer6, this.storage.get(integer6));
                }
                this.storage = zk10;
            }
            this.storage.set(integer1, integer5);
        }
    }
}

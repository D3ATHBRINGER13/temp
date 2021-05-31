package net.minecraft.world.entity.ai.village.poi;

import net.minecraft.sounds.SoundEvents;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Blocks;
import java.util.stream.Collectors;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.BedBlock;
import java.util.stream.Stream;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import java.util.Map;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import java.util.function.Predicate;

public class PoiType {
    private static final Predicate<PoiType> ALL_JOBS;
    public static final Predicate<PoiType> ALL;
    private static final Set<BlockState> BEDS;
    private static final Map<BlockState, PoiType> TYPE_BY_STATE;
    public static final PoiType UNEMPLOYED;
    public static final PoiType ARMORER;
    public static final PoiType BUTCHER;
    public static final PoiType CARTOGRAPHER;
    public static final PoiType CLERIC;
    public static final PoiType FARMER;
    public static final PoiType FISHERMAN;
    public static final PoiType FLETCHER;
    public static final PoiType LEATHERWORKER;
    public static final PoiType LIBRARIAN;
    public static final PoiType MASON;
    public static final PoiType NITWIT;
    public static final PoiType SHEPHERD;
    public static final PoiType TOOLSMITH;
    public static final PoiType WEAPONSMITH;
    public static final PoiType HOME;
    public static final PoiType MEETING;
    private final String name;
    private final Set<BlockState> matchingStates;
    private final int maxTickets;
    @Nullable
    private final SoundEvent soundEvent;
    private final Predicate<PoiType> predicate;
    private final int validRange;
    
    private static Set<BlockState> getBlockStates(final Block bmv) {
        return (Set<BlockState>)ImmutableSet.copyOf((Collection)bmv.getStateDefinition().getPossibleStates());
    }
    
    private PoiType(final String string, final Set<BlockState> set, final int integer3, @Nullable final SoundEvent yo, final Predicate<PoiType> predicate, final int integer6) {
        this.name = string;
        this.matchingStates = (Set<BlockState>)ImmutableSet.copyOf((Collection)set);
        this.maxTickets = integer3;
        this.soundEvent = yo;
        this.predicate = predicate;
        this.validRange = integer6;
    }
    
    private PoiType(final String string, final Set<BlockState> set, final int integer3, @Nullable final SoundEvent yo, final int integer5) {
        this.name = string;
        this.matchingStates = (Set<BlockState>)ImmutableSet.copyOf((Collection)set);
        this.maxTickets = integer3;
        this.soundEvent = yo;
        this.predicate = (Predicate<PoiType>)(aqs -> aqs == this);
        this.validRange = integer5;
    }
    
    public int getMaxTickets() {
        return this.maxTickets;
    }
    
    public Predicate<PoiType> getPredicate() {
        return this.predicate;
    }
    
    public int getValidRange() {
        return this.validRange;
    }
    
    public String toString() {
        return this.name;
    }
    
    @Nullable
    public SoundEvent getUseSound() {
        return this.soundEvent;
    }
    
    private static PoiType register(final String string, final Set<BlockState> set, final int integer3, @Nullable final SoundEvent yo, final int integer5) {
        return registerBlockStates(Registry.POINT_OF_INTEREST_TYPE.<PoiType>register(new ResourceLocation(string), new PoiType(string, set, integer3, yo, integer5)));
    }
    
    private static PoiType register(final String string, final Set<BlockState> set, final int integer3, @Nullable final SoundEvent yo, final Predicate<PoiType> predicate, final int integer6) {
        return registerBlockStates(Registry.POINT_OF_INTEREST_TYPE.<PoiType>register(new ResourceLocation(string), new PoiType(string, set, integer3, yo, predicate, integer6)));
    }
    
    private static PoiType registerBlockStates(final PoiType aqs) {
        aqs.matchingStates.forEach(bvt -> {
            final PoiType aqs2 = (PoiType)PoiType.TYPE_BY_STATE.put(bvt, aqs);
            if (aqs2 != null) {
                throw new IllegalStateException(String.format("%s is defined in too many tags", new Object[] { bvt }));
            }
        });
        return aqs;
    }
    
    public static Optional<PoiType> forState(final BlockState bvt) {
        return (Optional<PoiType>)Optional.ofNullable(PoiType.TYPE_BY_STATE.get(bvt));
    }
    
    public static Stream<BlockState> allPoiStates() {
        return (Stream<BlockState>)PoiType.TYPE_BY_STATE.keySet().stream();
    }
    
    static {
        ALL_JOBS = (aqs -> ((Set)Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getJobPoiType).collect(Collectors.toSet())).contains(aqs));
        ALL = (aqs -> true);
        BEDS = (Set)ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, (Object[])new Block[] { Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED }).stream().flatMap(bmv -> bmv.getStateDefinition().getPossibleStates().stream()).filter(bvt -> bvt.<BedPart>getValue(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
        TYPE_BY_STATE = (Map)Maps.newHashMap();
        UNEMPLOYED = register("unemployed", (Set<BlockState>)ImmutableSet.of(), 1, (SoundEvent)null, PoiType.ALL_JOBS, 1);
        ARMORER = register("armorer", getBlockStates(Blocks.BLAST_FURNACE), 1, SoundEvents.VILLAGER_WORK_ARMORER, 1);
        BUTCHER = register("butcher", getBlockStates(Blocks.SMOKER), 1, SoundEvents.VILLAGER_WORK_BUTCHER, 1);
        CARTOGRAPHER = register("cartographer", getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, SoundEvents.VILLAGER_WORK_CARTOGRAPHER, 1);
        CLERIC = register("cleric", getBlockStates(Blocks.BREWING_STAND), 1, SoundEvents.VILLAGER_WORK_CLERIC, 1);
        FARMER = register("farmer", getBlockStates(Blocks.COMPOSTER), 1, SoundEvents.VILLAGER_WORK_FARMER, 1);
        FISHERMAN = register("fisherman", getBlockStates(Blocks.BARREL), 1, SoundEvents.VILLAGER_WORK_FISHERMAN, 1);
        FLETCHER = register("fletcher", getBlockStates(Blocks.FLETCHING_TABLE), 1, SoundEvents.VILLAGER_WORK_FLETCHER, 1);
        LEATHERWORKER = register("leatherworker", getBlockStates(Blocks.CAULDRON), 1, SoundEvents.VILLAGER_WORK_LEATHERWORKER, 1);
        LIBRARIAN = register("librarian", getBlockStates(Blocks.LECTERN), 1, SoundEvents.VILLAGER_WORK_LIBRARIAN, 1);
        MASON = register("mason", getBlockStates(Blocks.STONECUTTER), 1, SoundEvents.VILLAGER_WORK_MASON, 1);
        NITWIT = register("nitwit", (Set<BlockState>)ImmutableSet.of(), 1, (SoundEvent)null, 1);
        SHEPHERD = register("shepherd", getBlockStates(Blocks.LOOM), 1, SoundEvents.VILLAGER_WORK_SHEPHERD, 1);
        TOOLSMITH = register("toolsmith", getBlockStates(Blocks.SMITHING_TABLE), 1, SoundEvents.VILLAGER_WORK_TOOLSMITH, 1);
        WEAPONSMITH = register("weaponsmith", getBlockStates(Blocks.GRINDSTONE), 1, SoundEvents.VILLAGER_WORK_WEAPONSMITH, 1);
        HOME = register("home", PoiType.BEDS, 1, (SoundEvent)null, 1);
        MEETING = register("meeting", getBlockStates(Blocks.BELL), 32, (SoundEvent)null, 6);
    }
}

package net.minecraft.world.entity.npc;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class VillagerProfession {
    public static final VillagerProfession NONE;
    public static final VillagerProfession ARMORER;
    public static final VillagerProfession BUTCHER;
    public static final VillagerProfession CARTOGRAPHER;
    public static final VillagerProfession CLERIC;
    public static final VillagerProfession FARMER;
    public static final VillagerProfession FISHERMAN;
    public static final VillagerProfession FLETCHER;
    public static final VillagerProfession LEATHERWORKER;
    public static final VillagerProfession LIBRARIAN;
    public static final VillagerProfession MASON;
    public static final VillagerProfession NITWIT;
    public static final VillagerProfession SHEPHERD;
    public static final VillagerProfession TOOLSMITH;
    public static final VillagerProfession WEAPONSMITH;
    private final String name;
    private final PoiType jobPoiType;
    private final ImmutableSet<Item> requestedItems;
    private final ImmutableSet<Block> secondaryPoi;
    
    private VillagerProfession(final String string, final PoiType aqs, final ImmutableSet<Item> immutableSet3, final ImmutableSet<Block> immutableSet4) {
        this.name = string;
        this.jobPoiType = aqs;
        this.requestedItems = immutableSet3;
        this.secondaryPoi = immutableSet4;
    }
    
    public PoiType getJobPoiType() {
        return this.jobPoiType;
    }
    
    public ImmutableSet<Item> getRequestedItems() {
        return this.requestedItems;
    }
    
    public ImmutableSet<Block> getSecondaryPoi() {
        return this.secondaryPoi;
    }
    
    public String toString() {
        return this.name;
    }
    
    static VillagerProfession register(final String string, final PoiType aqs) {
        return register(string, aqs, (ImmutableSet<Item>)ImmutableSet.of(), (ImmutableSet<Block>)ImmutableSet.of());
    }
    
    static VillagerProfession register(final String string, final PoiType aqs, final ImmutableSet<Item> immutableSet3, final ImmutableSet<Block> immutableSet4) {
        return Registry.<VillagerProfession>register(Registry.VILLAGER_PROFESSION, new ResourceLocation(string), new VillagerProfession(string, aqs, immutableSet3, immutableSet4));
    }
    
    static {
        NONE = register("none", PoiType.UNEMPLOYED);
        ARMORER = register("armorer", PoiType.ARMORER);
        BUTCHER = register("butcher", PoiType.BUTCHER);
        CARTOGRAPHER = register("cartographer", PoiType.CARTOGRAPHER);
        CLERIC = register("cleric", PoiType.CLERIC);
        FARMER = register("farmer", PoiType.FARMER, (ImmutableSet<Item>)ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS), (ImmutableSet<Block>)ImmutableSet.of(Blocks.FARMLAND));
        FISHERMAN = register("fisherman", PoiType.FISHERMAN);
        FLETCHER = register("fletcher", PoiType.FLETCHER);
        LEATHERWORKER = register("leatherworker", PoiType.LEATHERWORKER);
        LIBRARIAN = register("librarian", PoiType.LIBRARIAN);
        MASON = register("mason", PoiType.MASON);
        NITWIT = register("nitwit", PoiType.NITWIT);
        SHEPHERD = register("shepherd", PoiType.SHEPHERD);
        TOOLSMITH = register("toolsmith", PoiType.TOOLSMITH);
        WEAPONSMITH = register("weaponsmith", PoiType.WEAPONSMITH);
    }
}

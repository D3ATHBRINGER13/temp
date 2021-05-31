package net.minecraft.world.entity.npc;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;

public class VillagerData {
    private static final int[] NEXT_LEVEL_XP_THRESHOLDS;
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;
    
    public VillagerData(final VillagerType avy, final VillagerProfession avw, final int integer) {
        this.type = avy;
        this.profession = avw;
        this.level = Math.max(1, integer);
    }
    
    public VillagerData(final Dynamic<?> dynamic) {
        this(Registry.VILLAGER_TYPE.get(ResourceLocation.tryParse(dynamic.get("type").asString(""))), Registry.VILLAGER_PROFESSION.get(ResourceLocation.tryParse(dynamic.get("profession").asString(""))), dynamic.get("level").asInt(1));
    }
    
    public VillagerType getType() {
        return this.type;
    }
    
    public VillagerProfession getProfession() {
        return this.profession;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public VillagerData setType(final VillagerType avy) {
        return new VillagerData(avy, this.profession, this.level);
    }
    
    public VillagerData setProfession(final VillagerProfession avw) {
        return new VillagerData(this.type, avw, this.level);
    }
    
    public VillagerData setLevel(final int integer) {
        return new VillagerData(this.type, this.profession, integer);
    }
    
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString(Registry.VILLAGER_TYPE.getKey(this.type).toString()), dynamicOps.createString("profession"), dynamicOps.createString(Registry.VILLAGER_PROFESSION.getKey(this.profession).toString()), dynamicOps.createString("level"), dynamicOps.createInt(this.level)));
    }
    
    public static int getMinXpPerLevel(final int integer) {
        return canLevelUp(integer) ? VillagerData.NEXT_LEVEL_XP_THRESHOLDS[integer - 1] : 0;
    }
    
    public static int getMaxXpPerLevel(final int integer) {
        return canLevelUp(integer) ? VillagerData.NEXT_LEVEL_XP_THRESHOLDS[integer] : 0;
    }
    
    public static boolean canLevelUp(final int integer) {
        return integer >= 1 && integer < 5;
    }
    
    static {
        NEXT_LEVEL_XP_THRESHOLDS = new int[] { 0, 10, 70, 150, 250 };
    }
}

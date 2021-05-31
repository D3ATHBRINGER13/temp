package net.minecraft.util.datafix.fixes;

import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class ZombieVillagerRebuildXpFix extends NamedEntityFix {
    public ZombieVillagerRebuildXpFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "Zombie Villager XP rebuild", References.ENTITY, "minecraft:zombie_villager");
    }
    
    @Override
    protected Typed<?> fix(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            final Optional<Number> optional2 = (Optional<Number>)dynamic.get("Xp").asNumber();
            if (!optional2.isPresent()) {
                final int integer3 = ((Number)dynamic.get("VillagerData").get("level").asNumber().orElse(1)).intValue();
                return dynamic.set("Xp", dynamic.createInt(VillagerRebuildLevelAndXpFix.getMinXpPerLevel(integer3)));
            }
            return dynamic;
        });
    }
}

package net.minecraft.util.datafix.fixes;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class EntityRedundantChanceTagsFix extends DataFix {
    public EntityRedundantChanceTagsFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(References.ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            final Dynamic<?> dynamic2 = dynamic;
            if (Objects.equals(dynamic.get("HandDropChances"), Optional.of((Object)dynamic.createList(Stream.generate(() -> dynamic2.createFloat(0.0f)).limit(2L))))) {
                dynamic = dynamic.remove("HandDropChances");
            }
            if (Objects.equals(dynamic.get("ArmorDropChances"), Optional.of((Object)dynamic.createList(Stream.generate(() -> dynamic2.createFloat(0.0f)).limit(4L))))) {
                dynamic = dynamic.remove("ArmorDropChances");
            }
            return dynamic;
        }));
    }
}

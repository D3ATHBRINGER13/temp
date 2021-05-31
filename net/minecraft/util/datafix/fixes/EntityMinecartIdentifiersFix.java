package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import java.util.Optional;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import com.mojang.datafixers.DataFix;

public class EntityMinecartIdentifiersFix extends DataFix {
    private static final List<String> MINECART_BY_ID;
    
    public EntityMinecartIdentifiersFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoice.TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.ENTITY);
        final TaggedChoice.TaggedChoiceType<String> taggedChoiceType3 = (TaggedChoice.TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(References.ENTITY);
        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", (Type)taggedChoiceType2, (Type)taggedChoiceType3, dynamicOps -> pair -> {
            if (Objects.equals(pair.getFirst(), "Minecart")) {
                final Typed<? extends Pair<String, ?>> typed5 = taggedChoiceType2.point(dynamicOps, "Minecart", pair.getSecond()).orElseThrow(IllegalStateException::new);
                final Dynamic<?> dynamic6 = typed5.getOrCreate(DSL.remainderFinder());
                final int integer8 = dynamic6.get("Type").asInt(0);
                String string7;
                if (integer8 > 0 && integer8 < EntityMinecartIdentifiersFix.MINECART_BY_ID.size()) {
                    string7 = (String)EntityMinecartIdentifiersFix.MINECART_BY_ID.get(integer8);
                }
                else {
                    string7 = "MinecartRideable";
                }
                return Pair.of(string7, ((Optional)((Type)taggedChoiceType3.types().get(string7)).read(typed5.write()).getSecond()).orElseThrow(() -> new IllegalStateException("Could not read the new minecart.")));
            }
            return pair;
        });
    }
    
    static {
        MINECART_BY_ID = (List)Lists.newArrayList((Object[])new String[] { "MinecartRideable", "MinecartChest", "MinecartFurnace" });
    }
}

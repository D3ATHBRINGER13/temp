package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import java.util.stream.Stream;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
    public ItemWrittenBookPagesStrictJsonFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public Dynamic<?> fixTag(final Dynamic<?> dynamic) {
        return dynamic.update("pages", dynamic2 -> (Dynamic)DataFixUtils.orElse(dynamic2.asStreamOpt().map(stream -> stream.map(dynamic -> {
            if (!dynamic.asString().isPresent()) {
                return dynamic;
            }
            final String string2 = dynamic.asString("");
            Component jo3 = null;
            if ("null".equals(string2) || StringUtils.isEmpty((CharSequence)string2)) {
                jo3 = new TextComponent("");
            }
            else {
                if (string2.charAt(0) != '\"' || string2.charAt(string2.length() - 1) != '\"') {
                    if (string2.charAt(0) != '{' || string2.charAt(string2.length() - 1) != '}') {
                        jo3 = new TextComponent(string2);
                        return dynamic.createString(Component.Serializer.toJson(jo3));
                    }
                }
                try {
                    jo3 = GsonHelper.<Component>fromJson(BlockEntitySignTextStrictJsonFix.GSON, string2, Component.class, true);
                    if (jo3 == null) {
                        jo3 = new TextComponent("");
                    }
                }
                catch (JsonParseException ex) {}
                if (jo3 == null) {
                    try {
                        jo3 = Component.Serializer.fromJson(string2);
                    }
                    catch (JsonParseException ex2) {}
                }
                if (jo3 == null) {
                    try {
                        jo3 = Component.Serializer.fromJsonLenient(string2);
                    }
                    catch (JsonParseException ex3) {}
                }
                if (jo3 == null) {
                    jo3 = new TextComponent(string2);
                }
            }
            return dynamic.createString(Component.Serializer.toJson(jo3));
        })).map(dynamic::createList), dynamic.emptyList()));
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type2 = this.getInputSchema().getType(References.ITEM_STACK);
        final OpticFinder<?> opticFinder3 = type2.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", (Type)type2, typed -> typed.updateTyped(opticFinder3, typed -> typed.update(DSL.remainderFinder(), this::fixTag)));
    }
}

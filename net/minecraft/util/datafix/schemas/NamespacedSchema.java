package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.schemas.Schema;

public class NamespacedSchema extends Schema {
    public NamespacedSchema(final int integer, final Schema schema) {
        super(integer, schema);
    }
    
    public static String ensureNamespaced(final String string) {
        final ResourceLocation qv2 = ResourceLocation.tryParse(string);
        if (qv2 != null) {
            return qv2.toString();
        }
        return string;
    }
    
    public Type<?> getChoiceType(final DSL.TypeReference typeReference, final String string) {
        return super.getChoiceType(typeReference, ensureNamespaced(string));
    }
}

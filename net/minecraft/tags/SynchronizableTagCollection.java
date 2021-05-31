package net.minecraft.tags;

import com.google.common.collect.Maps;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Registry;

public class SynchronizableTagCollection<T> extends TagCollection<T> {
    private final Registry<T> registry;
    
    public SynchronizableTagCollection(final Registry<T> fn, final String string2, final String string3) {
        super(fn::getOptional, string2, false, string3);
        this.registry = fn;
    }
    
    public void serializeToNetwork(final FriendlyByteBuf je) {
        final Map<ResourceLocation, Tag<T>> map3 = this.getAllTags();
        je.writeVarInt(map3.size());
        for (final Map.Entry<ResourceLocation, Tag<T>> entry5 : map3.entrySet()) {
            je.writeResourceLocation((ResourceLocation)entry5.getKey());
            je.writeVarInt(((Tag)entry5.getValue()).getValues().size());
            for (final T object7 : ((Tag)entry5.getValue()).getValues()) {
                je.writeVarInt(this.registry.getId(object7));
            }
        }
    }
    
    public void loadFromNetwork(final FriendlyByteBuf je) {
        final Map<ResourceLocation, Tag<T>> map3 = (Map<ResourceLocation, Tag<T>>)Maps.newHashMap();
        for (int integer4 = je.readVarInt(), integer5 = 0; integer5 < integer4; ++integer5) {
            final ResourceLocation qv6 = je.readResourceLocation();
            final int integer6 = je.readVarInt();
            final Tag.Builder<T> a8 = Tag.Builder.<T>tag();
            for (int integer7 = 0; integer7 < integer6; ++integer7) {
                a8.add(this.registry.byId(je.readVarInt()));
            }
            map3.put(qv6, a8.build(qv6));
        }
        this.replace(map3);
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.world.item.crafting.Recipe;
import java.util.List;
import net.minecraft.network.protocol.Packet;

public class ClientboundUpdateRecipesPacket implements Packet<ClientGamePacketListener> {
    private List<Recipe<?>> recipes;
    
    public ClientboundUpdateRecipesPacket() {
    }
    
    public ClientboundUpdateRecipesPacket(final Collection<Recipe<?>> collection) {
        this.recipes = (List<Recipe<?>>)Lists.newArrayList((Iterable)collection);
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleUpdateRecipes(this);
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        this.recipes = (List<Recipe<?>>)Lists.newArrayList();
        for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            this.recipes.add(fromNetwork(je));
        }
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        je.writeVarInt(this.recipes.size());
        for (final Recipe<?> ber4 : this.recipes) {
            ClientboundUpdateRecipesPacket.<Recipe<?>>toNetwork(ber4, je);
        }
    }
    
    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }
    
    public static Recipe<?> fromNetwork(final FriendlyByteBuf je) {
        final ResourceLocation qv2 = je.readResourceLocation();
        final ResourceLocation qv3 = je.readResourceLocation();
        return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOptional(qv2).orElseThrow(() -> new IllegalArgumentException(new StringBuilder().append("Unknown recipe serializer ").append(qv2).toString()))).fromNetwork(qv3, je);
    }
    
    public static <T extends Recipe<?>> void toNetwork(final T ber, final FriendlyByteBuf je) {
        je.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(ber.getSerializer()));
        je.writeResourceLocation(ber.getId());
        ber.getSerializer().toNetwork(je, ber);
    }
}

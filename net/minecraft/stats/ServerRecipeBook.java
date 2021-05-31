package net.minecraft.stats;

import org.apache.logging.log4j.LogManager;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import java.util.function.Consumer;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.advancements.CriteriaTriggers;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import java.util.Collection;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
    private static final Logger LOGGER;
    private final RecipeManager manager;
    
    public ServerRecipeBook(final RecipeManager bes) {
        this.manager = bes;
    }
    
    public int addRecipes(final Collection<Recipe<?>> collection, final ServerPlayer vl) {
        final List<ResourceLocation> list4 = (List<ResourceLocation>)Lists.newArrayList();
        int integer5 = 0;
        for (final Recipe<?> ber7 : collection) {
            final ResourceLocation qv8 = ber7.getId();
            if (!this.known.contains(qv8) && !ber7.isSpecial()) {
                this.add(qv8);
                this.addHighlight(qv8);
                list4.add(qv8);
                CriteriaTriggers.RECIPE_UNLOCKED.trigger(vl, ber7);
                ++integer5;
            }
        }
        this.sendRecipes(ClientboundRecipePacket.State.ADD, vl, list4);
        return integer5;
    }
    
    public int removeRecipes(final Collection<Recipe<?>> collection, final ServerPlayer vl) {
        final List<ResourceLocation> list4 = (List<ResourceLocation>)Lists.newArrayList();
        int integer5 = 0;
        for (final Recipe<?> ber7 : collection) {
            final ResourceLocation qv8 = ber7.getId();
            if (this.known.contains(qv8)) {
                this.remove(qv8);
                list4.add(qv8);
                ++integer5;
            }
        }
        this.sendRecipes(ClientboundRecipePacket.State.REMOVE, vl, list4);
        return integer5;
    }
    
    private void sendRecipes(final ClientboundRecipePacket.State a, final ServerPlayer vl, final List<ResourceLocation> list) {
        vl.connection.send(new ClientboundRecipePacket(a, (Collection<ResourceLocation>)list, (Collection<ResourceLocation>)Collections.emptyList(), this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
    }
    
    public CompoundTag toNbt() {
        final CompoundTag id2 = new CompoundTag();
        id2.putBoolean("isGuiOpen", this.guiOpen);
        id2.putBoolean("isFilteringCraftable", this.filteringCraftable);
        id2.putBoolean("isFurnaceGuiOpen", this.furnaceGuiOpen);
        id2.putBoolean("isFurnaceFilteringCraftable", this.furnaceFilteringCraftable);
        final ListTag ik3 = new ListTag();
        for (final ResourceLocation qv5 : this.known) {
            ik3.add(new StringTag(qv5.toString()));
        }
        id2.put("recipes", (Tag)ik3);
        final ListTag ik4 = new ListTag();
        for (final ResourceLocation qv6 : this.highlight) {
            ik4.add(new StringTag(qv6.toString()));
        }
        id2.put("toBeDisplayed", (Tag)ik4);
        return id2;
    }
    
    public void fromNbt(final CompoundTag id) {
        this.guiOpen = id.getBoolean("isGuiOpen");
        this.filteringCraftable = id.getBoolean("isFilteringCraftable");
        this.furnaceGuiOpen = id.getBoolean("isFurnaceGuiOpen");
        this.furnaceFilteringCraftable = id.getBoolean("isFurnaceFilteringCraftable");
        final ListTag ik3 = id.getList("recipes", 8);
        this.loadRecipes(ik3, (Consumer<Recipe<?>>)this::add);
        final ListTag ik4 = id.getList("toBeDisplayed", 8);
        this.loadRecipes(ik4, (Consumer<Recipe<?>>)this::addHighlight);
    }
    
    private void loadRecipes(final ListTag ik, final Consumer<Recipe<?>> consumer) {
        for (int integer4 = 0; integer4 < ik.size(); ++integer4) {
            final String string5 = ik.getString(integer4);
            try {
                final ResourceLocation qv6 = new ResourceLocation(string5);
                final Optional<? extends Recipe<?>> optional7 = this.manager.byKey(qv6);
                if (!optional7.isPresent()) {
                    ServerRecipeBook.LOGGER.error("Tried to load unrecognized recipe: {} removed now.", qv6);
                }
                else {
                    consumer.accept(optional7.get());
                }
            }
            catch (ResourceLocationException n6) {
                ServerRecipeBook.LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", string5);
            }
        }
    }
    
    public void sendInitialRecipeBook(final ServerPlayer vl) {
        vl.connection.send(new ClientboundRecipePacket(ClientboundRecipePacket.State.INIT, (Collection<ResourceLocation>)this.known, (Collection<ResourceLocation>)this.highlight, this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

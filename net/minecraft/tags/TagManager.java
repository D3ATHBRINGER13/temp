package net.minecraft.tags;

import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import com.mojang.datafixers.util.Pair;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class TagManager implements PreparableReloadListener {
    private final SynchronizableTagCollection<Block> blocks;
    private final SynchronizableTagCollection<Item> items;
    private final SynchronizableTagCollection<Fluid> fluids;
    private final SynchronizableTagCollection<EntityType<?>> entityTypes;
    
    public TagManager() {
        this.blocks = new SynchronizableTagCollection<Block>((Registry<Block>)Registry.BLOCK, "tags/blocks", "block");
        this.items = new SynchronizableTagCollection<Item>((Registry<Item>)Registry.ITEM, "tags/items", "item");
        this.fluids = new SynchronizableTagCollection<Fluid>((Registry<Fluid>)Registry.FLUID, "tags/fluids", "fluid");
        this.entityTypes = new SynchronizableTagCollection<EntityType<?>>((Registry<EntityType<?>>)Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");
    }
    
    public SynchronizableTagCollection<Block> getBlocks() {
        return this.blocks;
    }
    
    public SynchronizableTagCollection<Item> getItems() {
        return this.items;
    }
    
    public SynchronizableTagCollection<Fluid> getFluids() {
        return this.fluids;
    }
    
    public SynchronizableTagCollection<EntityType<?>> getEntityTypes() {
        return this.entityTypes;
    }
    
    public void serializeToNetwork(final FriendlyByteBuf je) {
        this.blocks.serializeToNetwork(je);
        this.items.serializeToNetwork(je);
        this.fluids.serializeToNetwork(je);
        this.entityTypes.serializeToNetwork(je);
    }
    
    public static TagManager deserializeFromNetwork(final FriendlyByteBuf je) {
        final TagManager zi2 = new TagManager();
        zi2.getBlocks().loadFromNetwork(je);
        zi2.getItems().loadFromNetwork(je);
        zi2.getFluids().loadFromNetwork(je);
        zi2.getEntityTypes().loadFromNetwork(je);
        return zi2;
    }
    
    public CompletableFuture<Void> reload(final PreparationBarrier a, final ResourceManager xi, final ProfilerFiller agn3, final ProfilerFiller agn4, final Executor executor5, final Executor executor6) {
        final CompletableFuture<Map<ResourceLocation, Tag.Builder<Block>>> completableFuture8 = this.blocks.prepare(xi, executor5);
        final CompletableFuture<Map<ResourceLocation, Tag.Builder<Item>>> completableFuture9 = this.items.prepare(xi, executor5);
        final CompletableFuture<Map<ResourceLocation, Tag.Builder<Fluid>>> completableFuture10 = this.fluids.prepare(xi, executor5);
        final CompletableFuture<Map<ResourceLocation, Tag.Builder<EntityType<?>>>> completableFuture11 = this.entityTypes.prepare(xi, executor5);
        return (CompletableFuture<Void>)completableFuture8.thenCombine((CompletionStage)completableFuture9, Pair::of).thenCombine((CompletionStage)completableFuture10.thenCombine((CompletionStage)completableFuture11, Pair::of), (pair1, pair2) -> new Preparations((Map<ResourceLocation, Tag.Builder<Block>>)pair1.getFirst(), (Map<ResourceLocation, Tag.Builder<Item>>)pair1.getSecond(), (Map<ResourceLocation, Tag.Builder<Fluid>>)pair2.getFirst(), (Map<ResourceLocation, Tag.Builder<EntityType<?>>>)pair2.getSecond())).thenCompose(a::wait).thenAcceptAsync(a -> {
            this.blocks.load(a.blocks);
            this.items.load(a.items);
            this.fluids.load(a.fluids);
            this.entityTypes.load(a.entityTypes);
            BlockTags.reset(this.blocks);
            ItemTags.reset(this.items);
            FluidTags.reset(this.fluids);
            EntityTypeTags.reset(this.entityTypes);
        }, executor6);
    }
    
    public static class Preparations {
        final Map<ResourceLocation, Tag.Builder<Block>> blocks;
        final Map<ResourceLocation, Tag.Builder<Item>> items;
        final Map<ResourceLocation, Tag.Builder<Fluid>> fluids;
        final Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes;
        
        public Preparations(final Map<ResourceLocation, Tag.Builder<Block>> map1, final Map<ResourceLocation, Tag.Builder<Item>> map2, final Map<ResourceLocation, Tag.Builder<Fluid>> map3, final Map<ResourceLocation, Tag.Builder<EntityType<?>>> map4) {
            this.blocks = map1;
            this.items = map2;
            this.fluids = map3;
            this.entityTypes = map4;
        }
    }
}

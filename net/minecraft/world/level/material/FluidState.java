package net.minecraft.world.level.material;

import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Iterator;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.core.Direction;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockLayer;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.StateHolder;

public interface FluidState extends StateHolder<FluidState> {
    Fluid getType();
    
    default boolean isSource() {
        return this.getType().isSource(this);
    }
    
    default boolean isEmpty() {
        return this.getType().isEmpty();
    }
    
    default float getHeight(final BlockGetter bhb, final BlockPos ew) {
        return this.getType().getHeight(this, bhb, ew);
    }
    
    default float getOwnHeight() {
        return this.getType().getOwnHeight(this);
    }
    
    default int getAmount() {
        return this.getType().getAmount(this);
    }
    
    default boolean shouldRenderBackwardUpFace(final BlockGetter bhb, final BlockPos ew) {
        for (int integer4 = -1; integer4 <= 1; ++integer4) {
            for (int integer5 = -1; integer5 <= 1; ++integer5) {
                final BlockPos ew2 = ew.offset(integer4, 0, integer5);
                final FluidState clk7 = bhb.getFluidState(ew2);
                if (!clk7.getType().isSame(this.getType()) && !bhb.getBlockState(ew2).isSolidRender(bhb, ew2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    default void tick(final Level bhr, final BlockPos ew) {
        this.getType().tick(bhr, ew, this);
    }
    
    default void animateTick(final Level bhr, final BlockPos ew, final Random random) {
        this.getType().animateTick(bhr, ew, this, random);
    }
    
    default boolean isRandomlyTicking() {
        return this.getType().isRandomlyTicking();
    }
    
    default void randomTick(final Level bhr, final BlockPos ew, final Random random) {
        this.getType().randomTick(bhr, ew, this, random);
    }
    
    default Vec3 getFlow(final BlockGetter bhb, final BlockPos ew) {
        return this.getType().getFlow(bhb, ew, this);
    }
    
    default BlockState createLegacyBlock() {
        return this.getType().createLegacyBlock(this);
    }
    
    @Nullable
    default ParticleOptions getDripParticle() {
        return this.getType().getDripParticle();
    }
    
    default BlockLayer getRenderLayer() {
        return this.getType().getRenderLayer();
    }
    
    default boolean is(final Tag<Fluid> zg) {
        return this.getType().is(zg);
    }
    
    default float getExplosionResistance() {
        return this.getType().getExplosionResistance();
    }
    
    default boolean canBeReplacedWith(final BlockGetter bhb, final BlockPos ew, final Fluid clj, final Direction fb) {
        return this.getType().canBeReplacedWith(this, bhb, ew, clj, fb);
    }
    
    default <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps, final FluidState clk) {
        final ImmutableMap<Property<?>, Comparable<?>> immutableMap3 = clk.getValues();
        T object4;
        if (immutableMap3.isEmpty()) {
            object4 = (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.FLUID.getKey(clk.getType()).toString())));
        }
        else {
            object4 = (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.FLUID.getKey(clk.getType()).toString()), dynamicOps.createString("Properties"), dynamicOps.createMap((Map)immutableMap3.entrySet().stream().map(entry -> Pair.of(dynamicOps.createString(((Property)entry.getKey()).getName()), dynamicOps.createString(StateHolder.<Comparable>getName((Property<Comparable>)entry.getKey(), entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object4);
    }
    
    default <T> FluidState deserialize(final Dynamic<T> dynamic) {
        final Fluid clj2 = Registry.FLUID.get(new ResourceLocation((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:empty")));
        final Map<String, String> map3 = (Map<String, String>)dynamic.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        FluidState clk4 = clj2.defaultFluidState();
        final StateDefinition<Fluid, FluidState> bvu5 = clj2.getStateDefinition();
        for (final Map.Entry<String, String> entry7 : map3.entrySet()) {
            final String string8 = (String)entry7.getKey();
            final Property<?> bww9 = bvu5.getProperty(string8);
            if (bww9 != null) {
                clk4 = StateHolder.setValueHelper(clk4, bww9, string8, dynamic.toString(), (String)entry7.getValue());
            }
        }
        return clk4;
    }
    
    default VoxelShape getShape(final BlockGetter bhb, final BlockPos ew) {
        return this.getType().getShape(this, bhb, ew);
    }
}

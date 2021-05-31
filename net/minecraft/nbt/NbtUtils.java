package net.minecraft.nbt;

import org.apache.logging.log4j.LogManager;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.StringUtil;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.logging.log4j.Logger;

public final class NbtUtils {
    private static final Logger LOGGER;
    
    @Nullable
    public static GameProfile readGameProfile(final CompoundTag id) {
        String string2 = null;
        String string3 = null;
        if (id.contains("Name", 8)) {
            string2 = id.getString("Name");
        }
        Label_0040: {
            if (!id.contains("Id", 8)) {
                break Label_0040;
            }
            string3 = id.getString("Id");
            try {
                UUID uUID4;
                try {
                    uUID4 = UUID.fromString(string3);
                }
                catch (Throwable throwable5) {
                    uUID4 = null;
                }
                final GameProfile gameProfile5 = new GameProfile(uUID4, string2);
                if (id.contains("Properties", 10)) {
                    final CompoundTag id2 = id.getCompound("Properties");
                    for (final String string4 : id2.getAllKeys()) {
                        final ListTag ik9 = id2.getList(string4, 10);
                        for (int integer10 = 0; integer10 < ik9.size(); ++integer10) {
                            final CompoundTag id3 = ik9.getCompound(integer10);
                            final String string5 = id3.getString("Value");
                            if (id3.contains("Signature", 8)) {
                                gameProfile5.getProperties().put(string4, new Property(string4, string5, id3.getString("Signature")));
                            }
                            else {
                                gameProfile5.getProperties().put(string4, new Property(string4, string5));
                            }
                        }
                    }
                }
                return gameProfile5;
            }
            catch (Throwable t) {
                return null;
            }
        }
    }
    
    public static CompoundTag writeGameProfile(final CompoundTag id, final GameProfile gameProfile) {
        if (!StringUtil.isNullOrEmpty(gameProfile.getName())) {
            id.putString("Name", gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            id.putString("Id", gameProfile.getId().toString());
        }
        if (!gameProfile.getProperties().isEmpty()) {
            final CompoundTag id2 = new CompoundTag();
            for (final String string5 : gameProfile.getProperties().keySet()) {
                final ListTag ik6 = new ListTag();
                for (final Property property8 : gameProfile.getProperties().get(string5)) {
                    final CompoundTag id3 = new CompoundTag();
                    id3.putString("Value", property8.getValue());
                    if (property8.hasSignature()) {
                        id3.putString("Signature", property8.getSignature());
                    }
                    ik6.add(id3);
                }
                id2.put(string5, ik6);
            }
            id.put("Properties", (Tag)id2);
        }
        return id;
    }
    
    @VisibleForTesting
    public static boolean compareNbt(@Nullable final Tag iu1, @Nullable final Tag iu2, final boolean boolean3) {
        if (iu1 == iu2) {
            return true;
        }
        if (iu1 == null) {
            return true;
        }
        if (iu2 == null) {
            return false;
        }
        if (!iu1.getClass().equals(iu2.getClass())) {
            return false;
        }
        if (iu1 instanceof CompoundTag) {
            final CompoundTag id4 = (CompoundTag)iu1;
            final CompoundTag id5 = (CompoundTag)iu2;
            for (final String string7 : id4.getAllKeys()) {
                final Tag iu3 = id4.get(string7);
                if (!compareNbt(iu3, id5.get(string7), boolean3)) {
                    return false;
                }
            }
            return true;
        }
        if (!(iu1 instanceof ListTag) || !boolean3) {
            return iu1.equals(iu2);
        }
        final ListTag ik4 = (ListTag)iu1;
        final ListTag ik5 = (ListTag)iu2;
        if (ik4.isEmpty()) {
            return ik5.isEmpty();
        }
        for (int integer6 = 0; integer6 < ik4.size(); ++integer6) {
            final Tag iu4 = ik4.get(integer6);
            boolean boolean4 = false;
            for (int integer7 = 0; integer7 < ik5.size(); ++integer7) {
                if (compareNbt(iu4, ik5.get(integer7), boolean3)) {
                    boolean4 = true;
                    break;
                }
            }
            if (!boolean4) {
                return false;
            }
        }
        return true;
    }
    
    public static CompoundTag createUUIDTag(final UUID uUID) {
        final CompoundTag id2 = new CompoundTag();
        id2.putLong("M", uUID.getMostSignificantBits());
        id2.putLong("L", uUID.getLeastSignificantBits());
        return id2;
    }
    
    public static UUID loadUUIDTag(final CompoundTag id) {
        return new UUID(id.getLong("M"), id.getLong("L"));
    }
    
    public static BlockPos readBlockPos(final CompoundTag id) {
        return new BlockPos(id.getInt("X"), id.getInt("Y"), id.getInt("Z"));
    }
    
    public static CompoundTag writeBlockPos(final BlockPos ew) {
        final CompoundTag id2 = new CompoundTag();
        id2.putInt("X", ew.getX());
        id2.putInt("Y", ew.getY());
        id2.putInt("Z", ew.getZ());
        return id2;
    }
    
    public static BlockState readBlockState(final CompoundTag id) {
        if (!id.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        }
        final Block bmv2 = Registry.BLOCK.get(new ResourceLocation(id.getString("Name")));
        BlockState bvt3 = bmv2.defaultBlockState();
        if (id.contains("Properties", 10)) {
            final CompoundTag id2 = id.getCompound("Properties");
            final StateDefinition<Block, BlockState> bvu5 = bmv2.getStateDefinition();
            for (final String string7 : id2.getAllKeys()) {
                final net.minecraft.world.level.block.state.properties.Property<?> bww8 = bvu5.getProperty(string7);
                if (bww8 != null) {
                    bvt3 = NbtUtils.setValueHelper(bvt3, bww8, string7, id2, id);
                }
            }
        }
        return bvt3;
    }
    
    private static <S extends StateHolder<S>, T extends Comparable<T>> S setValueHelper(final S bvv, final net.minecraft.world.level.block.state.properties.Property<T> bww, final String string, final CompoundTag id4, final CompoundTag id5) {
        final Optional<T> optional6 = bww.getValue(id4.getString(string));
        if (optional6.isPresent()) {
            return bvv.<T, Comparable>setValue(bww, optional6.get());
        }
        NbtUtils.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", string, id4.getString(string), id5.toString());
        return bvv;
    }
    
    public static CompoundTag writeBlockState(final BlockState bvt) {
        final CompoundTag id2 = new CompoundTag();
        id2.putString("Name", Registry.BLOCK.getKey(bvt.getBlock()).toString());
        final ImmutableMap<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> immutableMap3 = bvt.getValues();
        if (!immutableMap3.isEmpty()) {
            final CompoundTag id3 = new CompoundTag();
            for (final Map.Entry<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> entry6 : immutableMap3.entrySet()) {
                final net.minecraft.world.level.block.state.properties.Property<?> bww7 = entry6.getKey();
                id3.putString(bww7.getName(), NbtUtils.getName(bww7, entry6.getValue()));
            }
            id2.put("Properties", (Tag)id3);
        }
        return id2;
    }
    
    private static <T extends Comparable<T>> String getName(final net.minecraft.world.level.block.state.properties.Property<T> bww, final Comparable<?> comparable) {
        return bww.getName((T)comparable);
    }
    
    public static CompoundTag update(final DataFixer dataFixer, final DataFixTypes aaj, final CompoundTag id, final int integer) {
        return update(dataFixer, aaj, id, integer, SharedConstants.getCurrentVersion().getWorldVersion());
    }
    
    public static CompoundTag update(final DataFixer dataFixer, final DataFixTypes aaj, final CompoundTag id, final int integer4, final int integer5) {
        return (CompoundTag)dataFixer.update(aaj.getType(), new Dynamic((DynamicOps)NbtOps.INSTANCE, id), integer4, integer5).getValue();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.world.item;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import com.google.common.collect.LinkedHashMultiset;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class MapItem extends ComplexItem {
    public MapItem(final Properties a) {
        super(a);
    }
    
    public static ItemStack create(final Level bhr, final int integer2, final int integer3, final byte byte4, final boolean boolean5, final boolean boolean6) {
        final ItemStack bcj7 = new ItemStack(Items.FILLED_MAP);
        createAndStoreSavedData(bcj7, bhr, integer2, integer3, byte4, boolean5, boolean6, bhr.dimension.getType());
        return bcj7;
    }
    
    @Nullable
    public static MapItemSavedData getSavedData(final ItemStack bcj, final Level bhr) {
        return bhr.getMapData(makeKey(getMapId(bcj)));
    }
    
    @Nullable
    public static MapItemSavedData getOrCreateSavedData(final ItemStack bcj, final Level bhr) {
        MapItemSavedData coh3 = getSavedData(bcj, bhr);
        if (coh3 == null && !bhr.isClientSide) {
            coh3 = createAndStoreSavedData(bcj, bhr, bhr.getLevelData().getXSpawn(), bhr.getLevelData().getZSpawn(), 3, false, false, bhr.dimension.getType());
        }
        return coh3;
    }
    
    public static int getMapId(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTag();
        return (id2 != null && id2.contains("map", 99)) ? id2.getInt("map") : 0;
    }
    
    private static MapItemSavedData createAndStoreSavedData(final ItemStack bcj, final Level bhr, final int integer3, final int integer4, final int integer5, final boolean boolean6, final boolean boolean7, final DimensionType byn) {
        final int integer6 = bhr.getFreeMapId();
        final MapItemSavedData coh10 = new MapItemSavedData(makeKey(integer6));
        coh10.setProperties(integer3, integer4, integer5, boolean6, boolean7, byn);
        bhr.setMapData(coh10);
        bcj.getOrCreateTag().putInt("map", integer6);
        return coh10;
    }
    
    public static String makeKey(final int integer) {
        return new StringBuilder().append("map_").append(integer).toString();
    }
    
    public void update(final Level bhr, final Entity aio, final MapItemSavedData coh) {
        if (bhr.dimension.getType() != coh.dimension || !(aio instanceof Player)) {
            return;
        }
        final int integer5 = 1 << coh.scale;
        final int integer6 = coh.x;
        final int integer7 = coh.z;
        final int integer8 = Mth.floor(aio.x - integer6) / integer5 + 64;
        final int integer9 = Mth.floor(aio.z - integer7) / integer5 + 64;
        int integer10 = 128 / integer5;
        if (bhr.dimension.isHasCeiling()) {
            integer10 /= 2;
        }
        final MapItemSavedData.HoldingPlayer holdingPlayer;
        final MapItemSavedData.HoldingPlayer a11 = holdingPlayer = coh.getHoldingPlayer((Player)aio);
        ++holdingPlayer.step;
        boolean boolean12 = false;
        for (int integer11 = integer8 - integer10 + 1; integer11 < integer8 + integer10; ++integer11) {
            if ((integer11 & 0xF) == (a11.step & 0xF) || boolean12) {
                boolean12 = false;
                double double14 = 0.0;
                for (int integer12 = integer9 - integer10 - 1; integer12 < integer9 + integer10; ++integer12) {
                    if (integer11 >= 0 && integer12 >= -1 && integer11 < 128) {
                        if (integer12 < 128) {
                            final int integer13 = integer11 - integer8;
                            final int integer14 = integer12 - integer9;
                            final boolean boolean13 = integer13 * integer13 + integer14 * integer14 > (integer10 - 2) * (integer10 - 2);
                            final int integer15 = (integer6 / integer5 + integer11 - 64) * integer5;
                            final int integer16 = (integer7 / integer5 + integer12 - 64) * integer5;
                            final Multiset<MaterialColor> multiset22 = (Multiset<MaterialColor>)LinkedHashMultiset.create();
                            final LevelChunk bxt23 = bhr.getChunkAt(new BlockPos(integer15, 0, integer16));
                            if (!bxt23.isEmpty()) {
                                final ChunkPos bhd24 = bxt23.getPos();
                                final int integer17 = integer15 & 0xF;
                                final int integer18 = integer16 & 0xF;
                                int integer19 = 0;
                                double double15 = 0.0;
                                if (bhr.dimension.isHasCeiling()) {
                                    int integer20 = integer15 + integer16 * 231871;
                                    integer20 = integer20 * integer20 * 31287121 + integer20 * 11;
                                    if ((integer20 >> 20 & 0x1) == 0x0) {
                                        multiset22.add(Blocks.DIRT.defaultBlockState().getMapColor(bhr, BlockPos.ZERO), 10);
                                    }
                                    else {
                                        multiset22.add(Blocks.STONE.defaultBlockState().getMapColor(bhr, BlockPos.ZERO), 100);
                                    }
                                    double15 = 100.0;
                                }
                                else {
                                    final BlockPos.MutableBlockPos a12 = new BlockPos.MutableBlockPos();
                                    final BlockPos.MutableBlockPos a13 = new BlockPos.MutableBlockPos();
                                    for (int integer21 = 0; integer21 < integer5; ++integer21) {
                                        for (int integer22 = 0; integer22 < integer5; ++integer22) {
                                            int integer23 = bxt23.getHeight(Heightmap.Types.WORLD_SURFACE, integer21 + integer17, integer22 + integer18) + 1;
                                            BlockState bvt35;
                                            if (integer23 > 1) {
                                                do {
                                                    --integer23;
                                                    a12.set(bhd24.getMinBlockX() + integer21 + integer17, integer23, bhd24.getMinBlockZ() + integer22 + integer18);
                                                    bvt35 = bxt23.getBlockState(a12);
                                                } while (bvt35.getMapColor(bhr, a12) == MaterialColor.NONE && integer23 > 0);
                                                if (integer23 > 0 && !bvt35.getFluidState().isEmpty()) {
                                                    int integer24 = integer23 - 1;
                                                    a13.set(a12);
                                                    BlockState bvt36;
                                                    do {
                                                        a13.setY(integer24--);
                                                        bvt36 = bxt23.getBlockState(a13);
                                                        ++integer19;
                                                    } while (integer24 > 0 && !bvt36.getFluidState().isEmpty());
                                                    bvt35 = this.getCorrectStateForFluidBlock(bhr, bvt35, a12);
                                                }
                                            }
                                            else {
                                                bvt35 = Blocks.BEDROCK.defaultBlockState();
                                            }
                                            coh.checkBanners(bhr, bhd24.getMinBlockX() + integer21 + integer17, bhd24.getMinBlockZ() + integer22 + integer18);
                                            double15 += integer23 / (double)(integer5 * integer5);
                                            multiset22.add(bvt35.getMapColor(bhr, a12));
                                        }
                                    }
                                }
                                integer19 /= integer5 * integer5;
                                double double16 = (double15 - double14) * 4.0 / (integer5 + 4) + ((integer11 + integer12 & 0x1) - 0.5) * 0.4;
                                int integer21 = 1;
                                if (double16 > 0.6) {
                                    integer21 = 2;
                                }
                                if (double16 < -0.6) {
                                    integer21 = 0;
                                }
                                final MaterialColor clp33 = (MaterialColor)Iterables.getFirst((Iterable)Multisets.copyHighestCountFirst((Multiset)multiset22), MaterialColor.NONE);
                                if (clp33 == MaterialColor.WATER) {
                                    double16 = integer19 * 0.1 + (integer11 + integer12 & 0x1) * 0.2;
                                    integer21 = 1;
                                    if (double16 < 0.5) {
                                        integer21 = 2;
                                    }
                                    if (double16 > 0.9) {
                                        integer21 = 0;
                                    }
                                }
                                double14 = double15;
                                if (integer12 >= 0) {
                                    if (integer13 * integer13 + integer14 * integer14 < integer10 * integer10) {
                                        if (!boolean13 || (integer11 + integer12 & 0x1) != 0x0) {
                                            final byte byte34 = coh.colors[integer11 + integer12 * 128];
                                            final byte byte35 = (byte)(clp33.id * 4 + integer21);
                                            if (byte34 != byte35) {
                                                coh.colors[integer11 + integer12 * 128] = byte35;
                                                coh.setDirty(integer11, integer12);
                                                boolean12 = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private BlockState getCorrectStateForFluidBlock(final Level bhr, final BlockState bvt, final BlockPos ew) {
        final FluidState clk5 = bvt.getFluidState();
        if (!clk5.isEmpty() && !bvt.isFaceSturdy(bhr, ew, Direction.UP)) {
            return clk5.createLegacyBlock();
        }
        return bvt;
    }
    
    private static boolean isLand(final Biome[] arr, final int integer2, final int integer3, final int integer4) {
        return arr[integer3 * integer2 + integer4 * integer2 * 128 * integer2].getDepth() >= 0.0f;
    }
    
    public static void renderBiomePreviewMap(final Level bhr, final ItemStack bcj) {
        final MapItemSavedData coh3 = getOrCreateSavedData(bcj, bhr);
        if (coh3 == null) {
            return;
        }
        if (bhr.dimension.getType() != coh3.dimension) {
            return;
        }
        final int integer4 = 1 << coh3.scale;
        final int integer5 = coh3.x;
        final int integer6 = coh3.z;
        final Biome[] arr7 = bhr.getChunkSource().getGenerator().getBiomeSource().getBiomeBlock((integer5 / integer4 - 64) * integer4, (integer6 / integer4 - 64) * integer4, 128 * integer4, 128 * integer4, false);
        for (int integer7 = 0; integer7 < 128; ++integer7) {
            for (int integer8 = 0; integer8 < 128; ++integer8) {
                if (integer7 > 0 && integer8 > 0 && integer7 < 127 && integer8 < 127) {
                    final Biome bio10 = arr7[integer7 * integer4 + integer8 * integer4 * 128 * integer4];
                    int integer9 = 8;
                    if (isLand(arr7, integer4, integer7 - 1, integer8 - 1)) {
                        --integer9;
                    }
                    if (isLand(arr7, integer4, integer7 - 1, integer8 + 1)) {
                        --integer9;
                    }
                    if (isLand(arr7, integer4, integer7 - 1, integer8)) {
                        --integer9;
                    }
                    if (isLand(arr7, integer4, integer7 + 1, integer8 - 1)) {
                        --integer9;
                    }
                    if (isLand(arr7, integer4, integer7 + 1, integer8 + 1)) {
                        --integer9;
                    }
                    if (isLand(arr7, integer4, integer7 + 1, integer8)) {
                        --integer9;
                    }
                    if (isLand(arr7, integer4, integer7, integer8 - 1)) {
                        --integer9;
                    }
                    if (isLand(arr7, integer4, integer7, integer8 + 1)) {
                        --integer9;
                    }
                    int integer10 = 3;
                    MaterialColor clp13 = MaterialColor.NONE;
                    if (bio10.getDepth() < 0.0f) {
                        clp13 = MaterialColor.COLOR_ORANGE;
                        if (integer9 > 7 && integer8 % 2 == 0) {
                            integer10 = (integer7 + (int)(Mth.sin(integer8 + 0.0f) * 7.0f)) / 8 % 5;
                            if (integer10 == 3) {
                                integer10 = 1;
                            }
                            else if (integer10 == 4) {
                                integer10 = 0;
                            }
                        }
                        else if (integer9 > 7) {
                            clp13 = MaterialColor.NONE;
                        }
                        else if (integer9 > 5) {
                            integer10 = 1;
                        }
                        else if (integer9 > 3) {
                            integer10 = 0;
                        }
                        else if (integer9 > 1) {
                            integer10 = 0;
                        }
                    }
                    else if (integer9 > 0) {
                        clp13 = MaterialColor.COLOR_BROWN;
                        if (integer9 > 3) {
                            integer10 = 1;
                        }
                        else {
                            integer10 = 3;
                        }
                    }
                    if (clp13 != MaterialColor.NONE) {
                        coh3.colors[integer7 + integer8 * 128] = (byte)(clp13.id * 4 + integer10);
                        coh3.setDirty(integer7, integer8);
                    }
                }
            }
        }
    }
    
    @Override
    public void inventoryTick(final ItemStack bcj, final Level bhr, final Entity aio, final int integer, final boolean boolean5) {
        if (bhr.isClientSide) {
            return;
        }
        final MapItemSavedData coh7 = getOrCreateSavedData(bcj, bhr);
        if (coh7 == null) {
            return;
        }
        if (aio instanceof Player) {
            final Player awg8 = (Player)aio;
            coh7.tickCarriedBy(awg8, bcj);
        }
        if (!coh7.locked && (boolean5 || (aio instanceof Player && ((Player)aio).getOffhandItem() == bcj))) {
            this.update(bhr, aio, coh7);
        }
    }
    
    @Nullable
    @Override
    public Packet<?> getUpdatePacket(final ItemStack bcj, final Level bhr, final Player awg) {
        return getOrCreateSavedData(bcj, bhr).getUpdatePacket(bcj, bhr, awg);
    }
    
    @Override
    public void onCraftedBy(final ItemStack bcj, final Level bhr, final Player awg) {
        final CompoundTag id5 = bcj.getTag();
        if (id5 != null && id5.contains("map_scale_direction", 99)) {
            scaleMap(bcj, bhr, id5.getInt("map_scale_direction"));
            id5.remove("map_scale_direction");
        }
    }
    
    protected static void scaleMap(final ItemStack bcj, final Level bhr, final int integer) {
        final MapItemSavedData coh4 = getOrCreateSavedData(bcj, bhr);
        if (coh4 != null) {
            createAndStoreSavedData(bcj, bhr, coh4.x, coh4.z, Mth.clamp(coh4.scale + integer, 0, 4), coh4.trackingPosition, coh4.unlimitedTracking, coh4.dimension);
        }
    }
    
    @Nullable
    public static ItemStack lockMap(final Level bhr, final ItemStack bcj) {
        final MapItemSavedData coh3 = getOrCreateSavedData(bcj, bhr);
        if (coh3 != null) {
            final ItemStack bcj2 = bcj.copy();
            final MapItemSavedData coh4 = createAndStoreSavedData(bcj2, bhr, 0, 0, coh3.scale, coh3.trackingPosition, coh3.unlimitedTracking, coh3.dimension);
            coh4.lockData(coh3);
            return bcj2;
        }
        return null;
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        final MapItemSavedData coh6 = (bhr == null) ? null : getOrCreateSavedData(bcj, bhr);
        if (coh6 != null && coh6.locked) {
            list.add(new TranslatableComponent("filled_map.locked", new Object[] { getMapId(bcj) }).withStyle(ChatFormatting.GRAY));
        }
        if (bdr.isAdvanced()) {
            if (coh6 != null) {
                list.add(new TranslatableComponent("filled_map.id", new Object[] { getMapId(bcj) }).withStyle(ChatFormatting.GRAY));
                list.add(new TranslatableComponent("filled_map.scale", new Object[] { 1 << coh6.scale }).withStyle(ChatFormatting.GRAY));
                list.add(new TranslatableComponent("filled_map.level", new Object[] { coh6.scale, 4 }).withStyle(ChatFormatting.GRAY));
            }
            else {
                list.add(new TranslatableComponent("filled_map.unknown", new Object[0]).withStyle(ChatFormatting.GRAY));
            }
        }
    }
    
    public static int getColor(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTagElement("display");
        if (id2 != null && id2.contains("MapColor", 99)) {
            final int integer3 = id2.getInt("MapColor");
            return 0xFF000000 | (integer3 & 0xFFFFFF);
        }
        return -12173266;
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final BlockState bvt3 = bdu.getLevel().getBlockState(bdu.getClickedPos());
        if (bvt3.is(BlockTags.BANNERS)) {
            if (!bdu.level.isClientSide) {
                final MapItemSavedData coh4 = getOrCreateSavedData(bdu.getItemInHand(), bdu.getLevel());
                coh4.toggleBanner(bdu.getLevel(), bdu.getClickedPos());
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(bdu);
    }
}

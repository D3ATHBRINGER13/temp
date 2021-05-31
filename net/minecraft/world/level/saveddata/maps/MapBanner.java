package net.minecraft.world.level.saveddata.maps;

import net.minecraft.nbt.Tag;
import java.util.Objects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;

public class MapBanner {
    private final BlockPos pos;
    private final DyeColor color;
    @Nullable
    private final Component name;
    
    public MapBanner(final BlockPos ew, final DyeColor bbg, @Nullable final Component jo) {
        this.pos = ew;
        this.color = bbg;
        this.name = jo;
    }
    
    public static MapBanner load(final CompoundTag id) {
        final BlockPos ew2 = NbtUtils.readBlockPos(id.getCompound("Pos"));
        final DyeColor bbg3 = DyeColor.byName(id.getString("Color"), DyeColor.WHITE);
        final Component jo4 = id.contains("Name") ? Component.Serializer.fromJson(id.getString("Name")) : null;
        return new MapBanner(ew2, bbg3, jo4);
    }
    
    @Nullable
    public static MapBanner fromWorld(final BlockGetter bhb, final BlockPos ew) {
        final BlockEntity btw3 = bhb.getBlockEntity(ew);
        if (btw3 instanceof BannerBlockEntity) {
            final BannerBlockEntity bto4 = (BannerBlockEntity)btw3;
            final DyeColor bbg5 = bto4.getBaseColor((Supplier<BlockState>)(() -> bhb.getBlockState(ew)));
            final Component jo6 = bto4.hasCustomName() ? bto4.getCustomName() : null;
            return new MapBanner(ew, bbg5, jo6);
        }
        return null;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public MapDecoration.Type getDecoration() {
        switch (this.color) {
            case WHITE: {
                return MapDecoration.Type.BANNER_WHITE;
            }
            case ORANGE: {
                return MapDecoration.Type.BANNER_ORANGE;
            }
            case MAGENTA: {
                return MapDecoration.Type.BANNER_MAGENTA;
            }
            case LIGHT_BLUE: {
                return MapDecoration.Type.BANNER_LIGHT_BLUE;
            }
            case YELLOW: {
                return MapDecoration.Type.BANNER_YELLOW;
            }
            case LIME: {
                return MapDecoration.Type.BANNER_LIME;
            }
            case PINK: {
                return MapDecoration.Type.BANNER_PINK;
            }
            case GRAY: {
                return MapDecoration.Type.BANNER_GRAY;
            }
            case LIGHT_GRAY: {
                return MapDecoration.Type.BANNER_LIGHT_GRAY;
            }
            case CYAN: {
                return MapDecoration.Type.BANNER_CYAN;
            }
            case PURPLE: {
                return MapDecoration.Type.BANNER_PURPLE;
            }
            case BLUE: {
                return MapDecoration.Type.BANNER_BLUE;
            }
            case BROWN: {
                return MapDecoration.Type.BANNER_BROWN;
            }
            case GREEN: {
                return MapDecoration.Type.BANNER_GREEN;
            }
            case RED: {
                return MapDecoration.Type.BANNER_RED;
            }
            default: {
                return MapDecoration.Type.BANNER_BLACK;
            }
        }
    }
    
    @Nullable
    public Component getName() {
        return this.name;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        final MapBanner cod3 = (MapBanner)object;
        return Objects.equals(this.pos, cod3.pos) && this.color == cod3.color && Objects.equals(this.name, cod3.name);
    }
    
    public int hashCode() {
        return Objects.hash(new Object[] { this.pos, this.color, this.name });
    }
    
    public CompoundTag save() {
        final CompoundTag id2 = new CompoundTag();
        id2.put("Pos", (Tag)NbtUtils.writeBlockPos(this.pos));
        id2.putString("Color", this.color.getName());
        if (this.name != null) {
            id2.putString("Name", Component.Serializer.toJson(this.name));
        }
        return id2;
    }
    
    public String getId() {
        return new StringBuilder().append("banner-").append(this.pos.getX()).append(",").append(this.pos.getY()).append(",").append(this.pos.getZ()).toString();
    }
}

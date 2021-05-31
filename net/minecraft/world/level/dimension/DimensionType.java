package net.minecraft.world.level.dimension;

import net.minecraft.world.level.dimension.end.TheEndDimension;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import java.io.File;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.Dynamic;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import java.util.function.BiFunction;
import net.minecraft.util.Serializable;

public class DimensionType implements Serializable {
    public static final DimensionType OVERWORLD;
    public static final DimensionType NETHER;
    public static final DimensionType THE_END;
    private final int id;
    private final String fileSuffix;
    private final String folder;
    private final BiFunction<Level, DimensionType, ? extends Dimension> factory;
    private final boolean hasSkylight;
    
    private static DimensionType register(final String string, final DimensionType byn) {
        return Registry.<DimensionType>registerMapping(Registry.DIMENSION_TYPE, byn.id, string, byn);
    }
    
    protected DimensionType(final int integer, final String string2, final String string3, final BiFunction<Level, DimensionType, ? extends Dimension> biFunction, final boolean boolean5) {
        this.id = integer;
        this.fileSuffix = string2;
        this.folder = string3;
        this.factory = biFunction;
        this.hasSkylight = boolean5;
    }
    
    public static DimensionType of(final Dynamic<?> dynamic) {
        return Registry.DIMENSION_TYPE.get(new ResourceLocation(dynamic.asString("")));
    }
    
    public static Iterable<DimensionType> getAllTypes() {
        return (Iterable<DimensionType>)Registry.DIMENSION_TYPE;
    }
    
    public int getId() {
        return this.id - 1;
    }
    
    public String getFileSuffix() {
        return this.fileSuffix;
    }
    
    public File getStorageFolder(final File file) {
        if (this.folder.isEmpty()) {
            return file;
        }
        return new File(file, this.folder);
    }
    
    public Dimension create(final Level bhr) {
        return (Dimension)this.factory.apply(bhr, this);
    }
    
    public String toString() {
        return getName(this).toString();
    }
    
    @Nullable
    public static DimensionType getById(final int integer) {
        return Registry.DIMENSION_TYPE.byId(integer + 1);
    }
    
    @Nullable
    public static DimensionType getByName(final ResourceLocation qv) {
        return Registry.DIMENSION_TYPE.get(qv);
    }
    
    @Nullable
    public static ResourceLocation getName(final DimensionType byn) {
        return Registry.DIMENSION_TYPE.getKey(byn);
    }
    
    public boolean hasSkyLight() {
        return this.hasSkylight;
    }
    
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createString(Registry.DIMENSION_TYPE.getKey(this).toString());
    }
    
    static {
        OVERWORLD = register("overworld", new DimensionType(1, "", "", NormalDimension::new, true));
        NETHER = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new, false));
        THE_END = register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false));
    }
}

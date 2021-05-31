package net.minecraft.client.renderer.block.model;

import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.BlockModelRotation;
import java.util.Objects;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import java.util.stream.Collectors;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.client.resources.model.ModelBakery;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.model.BakedModel;
import java.util.List;

public class ItemOverrides {
    public static final ItemOverrides EMPTY;
    private final List<ItemOverride> overrides;
    private final List<BakedModel> overrideModels;
    
    private ItemOverrides() {
        this.overrides = (List<ItemOverride>)Lists.newArrayList();
        this.overrideModels = (List<BakedModel>)Collections.emptyList();
    }
    
    public ItemOverrides(final ModelBakery dys, final BlockModel doe, final Function<ResourceLocation, UnbakedModel> function, final List<ItemOverride> list) {
        this.overrides = (List<ItemOverride>)Lists.newArrayList();
        Collections.reverse((List)(this.overrideModels = (List<BakedModel>)list.stream().map(doj -> {
            final UnbakedModel dyy5 = (UnbakedModel)function.apply(doj.getModel());
            if (Objects.equals(dyy5, doe)) {
                return null;
            }
            return dys.bake(doj.getModel(), BlockModelRotation.X0_Y0);
        }).collect(Collectors.toList())));
        for (int integer6 = list.size() - 1; integer6 >= 0; --integer6) {
            this.overrides.add(list.get(integer6));
        }
    }
    
    @Nullable
    public BakedModel resolve(final BakedModel dyp, final ItemStack bcj, @Nullable final Level bhr, @Nullable final LivingEntity aix) {
        if (!this.overrides.isEmpty()) {
            int integer6 = 0;
            while (integer6 < this.overrides.size()) {
                final ItemOverride doj7 = (ItemOverride)this.overrides.get(integer6);
                if (doj7.test(bcj, bhr, aix)) {
                    final BakedModel dyp2 = (BakedModel)this.overrideModels.get(integer6);
                    if (dyp2 == null) {
                        return dyp;
                    }
                    return dyp2;
                }
                else {
                    ++integer6;
                }
            }
        }
        return dyp;
    }
    
    static {
        EMPTY = new ItemOverrides();
    }
}

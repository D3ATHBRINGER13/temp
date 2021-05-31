package net.minecraft.world.item;

import org.apache.logging.log4j.LogManager;
import java.util.Optional;
import net.minecraft.world.item.crafting.RecipeManager;
import java.util.List;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.crafting.Recipe;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Lists;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem extends Item {
    private static final Logger LOGGER;
    
    public KnowledgeBookItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final CompoundTag id6 = bcj5.getTag();
        if (!awg.abilities.instabuild) {
            awg.setItemInHand(ahi, ItemStack.EMPTY);
        }
        if (id6 == null || !id6.contains("Recipes", 9)) {
            KnowledgeBookItem.LOGGER.error("Tag not valid: {}", id6);
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
        }
        if (!bhr.isClientSide) {
            final ListTag ik7 = id6.getList("Recipes", 8);
            final List<Recipe<?>> list8 = (List<Recipe<?>>)Lists.newArrayList();
            final RecipeManager bes9 = bhr.getServer().getRecipeManager();
            for (int integer10 = 0; integer10 < ik7.size(); ++integer10) {
                final String string11 = ik7.getString(integer10);
                final Optional<? extends Recipe<?>> optional12 = bes9.byKey(new ResourceLocation(string11));
                if (!optional12.isPresent()) {
                    KnowledgeBookItem.LOGGER.error("Invalid recipe: {}", string11);
                    return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
                }
                list8.add(optional12.get());
            }
            awg.awardRecipes((Collection<Recipe<?>>)list8);
            awg.awardStat(Stats.ITEM_USED.get(this));
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

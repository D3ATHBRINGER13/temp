package net.minecraft.world.inventory;

import net.minecraft.world.level.GameRules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.Recipe;

public interface RecipeHolder {
    void setRecipeUsed(@Nullable final Recipe<?> ber);
    
    @Nullable
    Recipe<?> getRecipeUsed();
    
    default void awardAndReset(final Player awg) {
        final Recipe<?> ber3 = this.getRecipeUsed();
        if (ber3 != null && !ber3.isSpecial()) {
            awg.awardRecipes((Collection<Recipe<?>>)Collections.singleton(ber3));
            this.setRecipeUsed(null);
        }
    }
    
    default boolean setRecipeUsed(final Level bhr, final ServerPlayer vl, final Recipe<?> ber) {
        if (ber.isSpecial() || !bhr.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) || vl.getRecipeBook().contains(ber)) {
            this.setRecipeUsed(ber);
            return true;
        }
        return false;
    }
}

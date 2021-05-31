package net.minecraft.commands.arguments;

import java.util.Arrays;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.Recipe;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import com.mojang.brigadier.arguments.ArgumentType;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_ID;
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT;
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE;
    
    public static ResourceLocationArgument id() {
        return new ResourceLocationArgument();
    }
    
    public static Advancement getAdvancement(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        final ResourceLocation qv3 = (ResourceLocation)commandContext.getArgument(string, (Class)ResourceLocation.class);
        final Advancement q4 = ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAdvancement(qv3);
        if (q4 == null) {
            throw ResourceLocationArgument.ERROR_UNKNOWN_ADVANCEMENT.create(qv3);
        }
        return q4;
    }
    
    public static Recipe<?> getRecipe(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        final RecipeManager bes3 = ((CommandSourceStack)commandContext.getSource()).getServer().getRecipeManager();
        final ResourceLocation qv4 = (ResourceLocation)commandContext.getArgument(string, (Class)ResourceLocation.class);
        return bes3.byKey(qv4).orElseThrow(() -> ResourceLocationArgument.ERROR_UNKNOWN_RECIPE.create(qv4));
    }
    
    public static ResourceLocation getId(final CommandContext<CommandSourceStack> commandContext, final String string) {
        return (ResourceLocation)commandContext.getArgument(string, (Class)ResourceLocation.class);
    }
    
    public ResourceLocation parse(final StringReader stringReader) throws CommandSyntaxException {
        return ResourceLocation.read(stringReader);
    }
    
    public Collection<String> getExamples() {
        return ResourceLocationArgument.EXAMPLES;
    }
    
    static {
        EXAMPLES = (Collection)Arrays.asList((Object[])new String[] { "foo", "foo:bar", "012" });
        ERROR_UNKNOWN_ID = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.id.unknown", new Object[] { object }));
        ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType(object -> new TranslatableComponent("advancement.advancementNotFound", new Object[] { object }));
        ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType(object -> new TranslatableComponent("recipe.notFound", new Object[] { object }));
    }
}

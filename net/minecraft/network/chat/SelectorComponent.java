package net.minecraft.network.chat;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.apache.logging.log4j.Logger;

public class SelectorComponent extends BaseComponent implements ContextAwareComponent {
    private static final Logger LOGGER;
    private final String pattern;
    @Nullable
    private final EntitySelector selector;
    
    public SelectorComponent(final String string) {
        this.pattern = string;
        EntitySelector ec3 = null;
        try {
            final EntitySelectorParser ed4 = new EntitySelectorParser(new StringReader(string));
            ec3 = ed4.parse();
        }
        catch (CommandSyntaxException commandSyntaxException4) {
            SelectorComponent.LOGGER.warn("Invalid selector component: {}", string, commandSyntaxException4.getMessage());
        }
        this.selector = ec3;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    @Override
    public Component resolve(@Nullable final CommandSourceStack cd, @Nullable final Entity aio, final int integer) throws CommandSyntaxException {
        if (cd == null || this.selector == null) {
            return new TextComponent("");
        }
        return EntitySelector.joinNames(this.selector.findEntities(cd));
    }
    
    public String getContents() {
        return this.pattern;
    }
    
    public SelectorComponent copy() {
        return new SelectorComponent(this.pattern);
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof SelectorComponent) {
            final SelectorComponent jv3 = (SelectorComponent)object;
            return this.pattern.equals(jv3.pattern) && super.equals(object);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

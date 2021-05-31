package net.minecraft.network.chat;

import java.util.List;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtil;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.selector.EntitySelector;

public class ScoreComponent extends BaseComponent implements ContextAwareComponent {
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;
    private String value;
    
    public ScoreComponent(final String string1, final String string2) {
        this.value = "";
        this.name = string1;
        this.objective = string2;
        EntitySelector ec4 = null;
        try {
            final EntitySelectorParser ed5 = new EntitySelectorParser(new StringReader(string1));
            ec4 = ed5.parse();
        }
        catch (CommandSyntaxException ex) {}
        this.selector = ec4;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getObjective() {
        return this.objective;
    }
    
    public void setValue(final String string) {
        this.value = string;
    }
    
    public String getContents() {
        return this.value;
    }
    
    private void resolve(final CommandSourceStack cd) {
        final MinecraftServer minecraftServer3 = cd.getServer();
        if (minecraftServer3 != null && minecraftServer3.isInitialized() && StringUtil.isNullOrEmpty(this.value)) {
            final Scoreboard cti4 = minecraftServer3.getScoreboard();
            final Objective ctf5 = cti4.getObjective(this.objective);
            if (cti4.hasPlayerScore(this.name, ctf5)) {
                final Score cth6 = cti4.getOrCreatePlayerScore(this.name, ctf5);
                this.setValue(String.format("%d", new Object[] { cth6.getScore() }));
            }
            else {
                this.value = "";
            }
        }
    }
    
    public ScoreComponent copy() {
        final ScoreComponent ju2 = new ScoreComponent(this.name, this.objective);
        ju2.setValue(this.value);
        return ju2;
    }
    
    @Override
    public Component resolve(@Nullable final CommandSourceStack cd, @Nullable final Entity aio, final int integer) throws CommandSyntaxException {
        if (cd == null) {
            return this.copy();
        }
        String string5;
        if (this.selector != null) {
            final List<? extends Entity> list6 = this.selector.findEntities(cd);
            if (list6.isEmpty()) {
                string5 = this.name;
            }
            else {
                if (list6.size() != 1) {
                    throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
                }
                string5 = ((Entity)list6.get(0)).getScoreboardName();
            }
        }
        else {
            string5 = this.name;
        }
        final String string6 = (aio != null && string5.equals("*")) ? aio.getScoreboardName() : string5;
        final ScoreComponent ju7 = new ScoreComponent(string6, this.objective);
        ju7.setValue(this.value);
        ju7.resolve(cd);
        return ju7;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ScoreComponent) {
            final ScoreComponent ju3 = (ScoreComponent)object;
            return this.name.equals(ju3.name) && this.objective.equals(ju3.objective) && super.equals(object);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}

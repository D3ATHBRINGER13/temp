package net.minecraft.world.scores;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Objective {
    private final Scoreboard scoreboard;
    private final String name;
    private final ObjectiveCriteria criteria;
    private Component displayName;
    private ObjectiveCriteria.RenderType renderType;
    
    public Objective(final Scoreboard cti, final String string, final ObjectiveCriteria ctl, final Component jo, final ObjectiveCriteria.RenderType a) {
        this.scoreboard = cti;
        this.name = string;
        this.criteria = ctl;
        this.displayName = jo;
        this.renderType = a;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ObjectiveCriteria getCriteria() {
        return this.criteria;
    }
    
    public Component getDisplayName() {
        return this.displayName;
    }
    
    public Component getFormattedDisplayName() {
        return ComponentUtils.wrapInSquareBrackets(this.displayName.deepCopy().withStyle((Consumer<Style>)(jw -> jw.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.getName()))))));
    }
    
    public void setDisplayName(final Component jo) {
        this.displayName = jo;
        this.scoreboard.onObjectiveChanged(this);
    }
    
    public ObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }
    
    public void setRenderType(final ObjectiveCriteria.RenderType a) {
        this.renderType = a;
        this.scoreboard.onObjectiveChanged(this);
    }
}

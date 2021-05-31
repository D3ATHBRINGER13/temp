package net.minecraft.world.scores;

import net.minecraft.network.chat.HoverEvent;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.network.chat.TextComponent;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.Set;

public class PlayerTeam extends Team {
    private final Scoreboard scoreboard;
    private final String name;
    private final Set<String> players;
    private Component displayName;
    private Component playerPrefix;
    private Component playerSuffix;
    private boolean allowFriendlyFire;
    private boolean seeFriendlyInvisibles;
    private Visibility nameTagVisibility;
    private Visibility deathMessageVisibility;
    private ChatFormatting color;
    private CollisionRule collisionRule;
    
    public PlayerTeam(final Scoreboard cti, final String string) {
        this.players = (Set<String>)Sets.newHashSet();
        this.playerPrefix = new TextComponent("");
        this.playerSuffix = new TextComponent("");
        this.allowFriendlyFire = true;
        this.seeFriendlyInvisibles = true;
        this.nameTagVisibility = Visibility.ALWAYS;
        this.deathMessageVisibility = Visibility.ALWAYS;
        this.color = ChatFormatting.RESET;
        this.collisionRule = CollisionRule.ALWAYS;
        this.scoreboard = cti;
        this.name = string;
        this.displayName = new TextComponent(string);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public Component getDisplayName() {
        return this.displayName;
    }
    
    public Component getFormattedDisplayName() {
        final Component jo2 = ComponentUtils.wrapInSquareBrackets(this.displayName.deepCopy().withStyle((Consumer<Style>)(jw -> jw.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.name))))));
        final ChatFormatting c3 = this.getColor();
        if (c3 != ChatFormatting.RESET) {
            jo2.withStyle(c3);
        }
        return jo2;
    }
    
    public void setDisplayName(final Component jo) {
        if (jo == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.displayName = jo;
        this.scoreboard.onTeamChanged(this);
    }
    
    public void setPlayerPrefix(@Nullable final Component jo) {
        this.playerPrefix = ((jo == null) ? new TextComponent("") : jo.deepCopy());
        this.scoreboard.onTeamChanged(this);
    }
    
    public Component getPlayerPrefix() {
        return this.playerPrefix;
    }
    
    public void setPlayerSuffix(@Nullable final Component jo) {
        this.playerSuffix = ((jo == null) ? new TextComponent("") : jo.deepCopy());
        this.scoreboard.onTeamChanged(this);
    }
    
    public Component getPlayerSuffix() {
        return this.playerSuffix;
    }
    
    @Override
    public Collection<String> getPlayers() {
        return (Collection<String>)this.players;
    }
    
    @Override
    public Component getFormattedName(final Component jo) {
        final Component jo2 = new TextComponent("").append(this.playerPrefix).append(jo).append(this.playerSuffix);
        final ChatFormatting c4 = this.getColor();
        if (c4 != ChatFormatting.RESET) {
            jo2.withStyle(c4);
        }
        return jo2;
    }
    
    public static Component formatNameForTeam(@Nullable final Team ctk, final Component jo) {
        if (ctk == null) {
            return jo.deepCopy();
        }
        return ctk.getFormattedName(jo);
    }
    
    @Override
    public boolean isAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }
    
    public void setAllowFriendlyFire(final boolean boolean1) {
        this.allowFriendlyFire = boolean1;
        this.scoreboard.onTeamChanged(this);
    }
    
    @Override
    public boolean canSeeFriendlyInvisibles() {
        return this.seeFriendlyInvisibles;
    }
    
    public void setSeeFriendlyInvisibles(final boolean boolean1) {
        this.seeFriendlyInvisibles = boolean1;
        this.scoreboard.onTeamChanged(this);
    }
    
    @Override
    public Visibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }
    
    @Override
    public Visibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }
    
    public void setNameTagVisibility(final Visibility b) {
        this.nameTagVisibility = b;
        this.scoreboard.onTeamChanged(this);
    }
    
    public void setDeathMessageVisibility(final Visibility b) {
        this.deathMessageVisibility = b;
        this.scoreboard.onTeamChanged(this);
    }
    
    @Override
    public CollisionRule getCollisionRule() {
        return this.collisionRule;
    }
    
    public void setCollisionRule(final CollisionRule a) {
        this.collisionRule = a;
        this.scoreboard.onTeamChanged(this);
    }
    
    public int packOptions() {
        int integer2 = 0;
        if (this.isAllowFriendlyFire()) {
            integer2 |= 0x1;
        }
        if (this.canSeeFriendlyInvisibles()) {
            integer2 |= 0x2;
        }
        return integer2;
    }
    
    public void unpackOptions(final int integer) {
        this.setAllowFriendlyFire((integer & 0x1) > 0);
        this.setSeeFriendlyInvisibles((integer & 0x2) > 0);
    }
    
    public void setColor(final ChatFormatting c) {
        this.color = c;
        this.scoreboard.onTeamChanged(this);
    }
    
    @Override
    public ChatFormatting getColor() {
        return this.color;
    }
}

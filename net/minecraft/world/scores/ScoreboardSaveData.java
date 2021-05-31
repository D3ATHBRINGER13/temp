package net.minecraft.world.scores;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.saveddata.SavedData;

public class ScoreboardSaveData extends SavedData {
    private static final Logger LOGGER;
    private Scoreboard scoreboard;
    private CompoundTag delayLoad;
    
    public ScoreboardSaveData() {
        super("scoreboard");
    }
    
    public void setScoreboard(final Scoreboard cti) {
        this.scoreboard = cti;
        if (this.delayLoad != null) {
            this.load(this.delayLoad);
        }
    }
    
    @Override
    public void load(final CompoundTag id) {
        if (this.scoreboard == null) {
            this.delayLoad = id;
            return;
        }
        this.loadObjectives(id.getList("Objectives", 10));
        this.scoreboard.loadPlayerScores(id.getList("PlayerScores", 10));
        if (id.contains("DisplaySlots", 10)) {
            this.loadDisplaySlots(id.getCompound("DisplaySlots"));
        }
        if (id.contains("Teams", 9)) {
            this.loadTeams(id.getList("Teams", 10));
        }
    }
    
    protected void loadTeams(final ListTag ik) {
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            final CompoundTag id4 = ik.getCompound(integer3);
            String string5 = id4.getString("Name");
            if (string5.length() > 16) {
                string5 = string5.substring(0, 16);
            }
            final PlayerTeam ctg6 = this.scoreboard.addPlayerTeam(string5);
            final Component jo7 = Component.Serializer.fromJson(id4.getString("DisplayName"));
            if (jo7 != null) {
                ctg6.setDisplayName(jo7);
            }
            if (id4.contains("TeamColor", 8)) {
                ctg6.setColor(ChatFormatting.getByName(id4.getString("TeamColor")));
            }
            if (id4.contains("AllowFriendlyFire", 99)) {
                ctg6.setAllowFriendlyFire(id4.getBoolean("AllowFriendlyFire"));
            }
            if (id4.contains("SeeFriendlyInvisibles", 99)) {
                ctg6.setSeeFriendlyInvisibles(id4.getBoolean("SeeFriendlyInvisibles"));
            }
            if (id4.contains("MemberNamePrefix", 8)) {
                final Component jo8 = Component.Serializer.fromJson(id4.getString("MemberNamePrefix"));
                if (jo8 != null) {
                    ctg6.setPlayerPrefix(jo8);
                }
            }
            if (id4.contains("MemberNameSuffix", 8)) {
                final Component jo8 = Component.Serializer.fromJson(id4.getString("MemberNameSuffix"));
                if (jo8 != null) {
                    ctg6.setPlayerSuffix(jo8);
                }
            }
            if (id4.contains("NameTagVisibility", 8)) {
                final Team.Visibility b8 = Team.Visibility.byName(id4.getString("NameTagVisibility"));
                if (b8 != null) {
                    ctg6.setNameTagVisibility(b8);
                }
            }
            if (id4.contains("DeathMessageVisibility", 8)) {
                final Team.Visibility b8 = Team.Visibility.byName(id4.getString("DeathMessageVisibility"));
                if (b8 != null) {
                    ctg6.setDeathMessageVisibility(b8);
                }
            }
            if (id4.contains("CollisionRule", 8)) {
                final Team.CollisionRule a8 = Team.CollisionRule.byName(id4.getString("CollisionRule"));
                if (a8 != null) {
                    ctg6.setCollisionRule(a8);
                }
            }
            this.loadTeamPlayers(ctg6, id4.getList("Players", 8));
        }
    }
    
    protected void loadTeamPlayers(final PlayerTeam ctg, final ListTag ik) {
        for (int integer4 = 0; integer4 < ik.size(); ++integer4) {
            this.scoreboard.addPlayerToTeam(ik.getString(integer4), ctg);
        }
    }
    
    protected void loadDisplaySlots(final CompoundTag id) {
        for (int integer3 = 0; integer3 < 19; ++integer3) {
            if (id.contains(new StringBuilder().append("slot_").append(integer3).toString(), 8)) {
                final String string4 = id.getString(new StringBuilder().append("slot_").append(integer3).toString());
                final Objective ctf5 = this.scoreboard.getObjective(string4);
                this.scoreboard.setDisplayObjective(integer3, ctf5);
            }
        }
    }
    
    protected void loadObjectives(final ListTag ik) {
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            final CompoundTag id4 = ik.getCompound(integer3);
            ObjectiveCriteria.byName(id4.getString("CriteriaName")).ifPresent(ctl -> {
                String string4 = id4.getString("Name");
                if (string4.length() > 16) {
                    string4 = string4.substring(0, 16);
                }
                final Component jo5 = Component.Serializer.fromJson(id4.getString("DisplayName"));
                final ObjectiveCriteria.RenderType a6 = ObjectiveCriteria.RenderType.byId(id4.getString("RenderType"));
                this.scoreboard.addObjective(string4, ctl, jo5, a6);
            });
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        if (this.scoreboard == null) {
            ScoreboardSaveData.LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
            return id;
        }
        id.put("Objectives", (Tag)this.saveObjectives());
        id.put("PlayerScores", (Tag)this.scoreboard.savePlayerScores());
        id.put("Teams", (Tag)this.saveTeams());
        this.saveDisplaySlots(id);
        return id;
    }
    
    protected ListTag saveTeams() {
        final ListTag ik2 = new ListTag();
        final Collection<PlayerTeam> collection3 = this.scoreboard.getPlayerTeams();
        for (final PlayerTeam ctg5 : collection3) {
            final CompoundTag id6 = new CompoundTag();
            id6.putString("Name", ctg5.getName());
            id6.putString("DisplayName", Component.Serializer.toJson(ctg5.getDisplayName()));
            if (ctg5.getColor().getId() >= 0) {
                id6.putString("TeamColor", ctg5.getColor().getName());
            }
            id6.putBoolean("AllowFriendlyFire", ctg5.isAllowFriendlyFire());
            id6.putBoolean("SeeFriendlyInvisibles", ctg5.canSeeFriendlyInvisibles());
            id6.putString("MemberNamePrefix", Component.Serializer.toJson(ctg5.getPlayerPrefix()));
            id6.putString("MemberNameSuffix", Component.Serializer.toJson(ctg5.getPlayerSuffix()));
            id6.putString("NameTagVisibility", ctg5.getNameTagVisibility().name);
            id6.putString("DeathMessageVisibility", ctg5.getDeathMessageVisibility().name);
            id6.putString("CollisionRule", ctg5.getCollisionRule().name);
            final ListTag ik3 = new ListTag();
            for (final String string9 : ctg5.getPlayers()) {
                ik3.add(new StringTag(string9));
            }
            id6.put("Players", (Tag)ik3);
            ik2.add(id6);
        }
        return ik2;
    }
    
    protected void saveDisplaySlots(final CompoundTag id) {
        final CompoundTag id2 = new CompoundTag();
        boolean boolean4 = false;
        for (int integer5 = 0; integer5 < 19; ++integer5) {
            final Objective ctf6 = this.scoreboard.getDisplayObjective(integer5);
            if (ctf6 != null) {
                id2.putString(new StringBuilder().append("slot_").append(integer5).toString(), ctf6.getName());
                boolean4 = true;
            }
        }
        if (boolean4) {
            id.put("DisplaySlots", (Tag)id2);
        }
    }
    
    protected ListTag saveObjectives() {
        final ListTag ik2 = new ListTag();
        final Collection<Objective> collection3 = this.scoreboard.getObjectives();
        for (final Objective ctf5 : collection3) {
            if (ctf5.getCriteria() == null) {
                continue;
            }
            final CompoundTag id6 = new CompoundTag();
            id6.putString("Name", ctf5.getName());
            id6.putString("CriteriaName", ctf5.getCriteria().getName());
            id6.putString("DisplayName", Component.Serializer.toJson(ctf5.getDisplayName()));
            id6.putString("RenderType", ctf5.getRenderType().getId());
            ik2.add(id6);
        }
        return ik2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

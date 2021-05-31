package net.minecraft.world.scores;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.ChatFormatting;
import java.util.Iterator;
import java.util.Comparator;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import java.util.List;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import java.util.Map;

public class Scoreboard {
    private final Map<String, Objective> objectivesByName;
    private final Map<ObjectiveCriteria, List<Objective>> objectivesByCriteria;
    private final Map<String, Map<Objective, Score>> playerScores;
    private final Objective[] displayObjectives;
    private final Map<String, PlayerTeam> teamsByName;
    private final Map<String, PlayerTeam> teamsByPlayer;
    private static String[] displaySlotNames;
    
    public Scoreboard() {
        this.objectivesByName = (Map<String, Objective>)Maps.newHashMap();
        this.objectivesByCriteria = (Map<ObjectiveCriteria, List<Objective>>)Maps.newHashMap();
        this.playerScores = (Map<String, Map<Objective, Score>>)Maps.newHashMap();
        this.displayObjectives = new Objective[19];
        this.teamsByName = (Map<String, PlayerTeam>)Maps.newHashMap();
        this.teamsByPlayer = (Map<String, PlayerTeam>)Maps.newHashMap();
    }
    
    public boolean hasObjective(final String string) {
        return this.objectivesByName.containsKey(string);
    }
    
    public Objective getOrCreateObjective(final String string) {
        return (Objective)this.objectivesByName.get(string);
    }
    
    @Nullable
    public Objective getObjective(@Nullable final String string) {
        return (Objective)this.objectivesByName.get(string);
    }
    
    public Objective addObjective(final String string, final ObjectiveCriteria ctl, final Component jo, final ObjectiveCriteria.RenderType a) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + string + "' is too long!");
        }
        if (this.objectivesByName.containsKey(string)) {
            throw new IllegalArgumentException("An objective with the name '" + string + "' already exists!");
        }
        final Objective ctf6 = new Objective(this, string, ctl, jo, a);
        ((List)this.objectivesByCriteria.computeIfAbsent(ctl, ctl -> Lists.newArrayList())).add(ctf6);
        this.objectivesByName.put(string, ctf6);
        this.onObjectiveAdded(ctf6);
        return ctf6;
    }
    
    public final void forAllObjectives(final ObjectiveCriteria ctl, final String string, final Consumer<Score> consumer) {
        ((List)this.objectivesByCriteria.getOrDefault(ctl, Collections.emptyList())).forEach(ctf -> consumer.accept(this.getOrCreatePlayerScore(string, ctf)));
    }
    
    public boolean hasPlayerScore(final String string, final Objective ctf) {
        final Map<Objective, Score> map4 = (Map<Objective, Score>)this.playerScores.get(string);
        if (map4 == null) {
            return false;
        }
        final Score cth5 = (Score)map4.get(ctf);
        return cth5 != null;
    }
    
    public Score getOrCreatePlayerScore(final String string, final Objective ctf) {
        if (string.length() > 40) {
            throw new IllegalArgumentException("The player name '" + string + "' is too long!");
        }
        final Map<Objective, Score> map4 = (Map<Objective, Score>)this.playerScores.computeIfAbsent(string, string -> Maps.newHashMap());
        return (Score)map4.computeIfAbsent(ctf, ctf -> {
            final Score cth4 = new Score(this, ctf, string);
            cth4.setScore(0);
            return cth4;
        });
    }
    
    public Collection<Score> getPlayerScores(final Objective ctf) {
        final List<Score> list3 = (List<Score>)Lists.newArrayList();
        for (final Map<Objective, Score> map5 : this.playerScores.values()) {
            final Score cth6 = (Score)map5.get(ctf);
            if (cth6 != null) {
                list3.add(cth6);
            }
        }
        Collections.sort((List)list3, (Comparator)Score.SCORE_COMPARATOR);
        return (Collection<Score>)list3;
    }
    
    public Collection<Objective> getObjectives() {
        return (Collection<Objective>)this.objectivesByName.values();
    }
    
    public Collection<String> getObjectiveNames() {
        return (Collection<String>)this.objectivesByName.keySet();
    }
    
    public Collection<String> getTrackedPlayers() {
        return (Collection<String>)Lists.newArrayList((Iterable)this.playerScores.keySet());
    }
    
    public void resetPlayerScore(final String string, @Nullable final Objective ctf) {
        if (ctf == null) {
            final Map<Objective, Score> map4 = (Map<Objective, Score>)this.playerScores.remove(string);
            if (map4 != null) {
                this.onPlayerRemoved(string);
            }
        }
        else {
            final Map<Objective, Score> map4 = (Map<Objective, Score>)this.playerScores.get(string);
            if (map4 != null) {
                final Score cth5 = (Score)map4.remove(ctf);
                if (map4.size() < 1) {
                    final Map<Objective, Score> map5 = (Map<Objective, Score>)this.playerScores.remove(string);
                    if (map5 != null) {
                        this.onPlayerRemoved(string);
                    }
                }
                else if (cth5 != null) {
                    this.onPlayerScoreRemoved(string, ctf);
                }
            }
        }
    }
    
    public Map<Objective, Score> getPlayerScores(final String string) {
        Map<Objective, Score> map3 = (Map<Objective, Score>)this.playerScores.get(string);
        if (map3 == null) {
            map3 = (Map<Objective, Score>)Maps.newHashMap();
        }
        return map3;
    }
    
    public void removeObjective(final Objective ctf) {
        this.objectivesByName.remove(ctf.getName());
        for (int integer3 = 0; integer3 < 19; ++integer3) {
            if (this.getDisplayObjective(integer3) == ctf) {
                this.setDisplayObjective(integer3, null);
            }
        }
        final List<Objective> list3 = (List<Objective>)this.objectivesByCriteria.get(ctf.getCriteria());
        if (list3 != null) {
            list3.remove(ctf);
        }
        for (final Map<Objective, Score> map5 : this.playerScores.values()) {
            map5.remove(ctf);
        }
        this.onObjectiveRemoved(ctf);
    }
    
    public void setDisplayObjective(final int integer, @Nullable final Objective ctf) {
        this.displayObjectives[integer] = ctf;
    }
    
    @Nullable
    public Objective getDisplayObjective(final int integer) {
        return this.displayObjectives[integer];
    }
    
    public PlayerTeam getPlayerTeam(final String string) {
        return (PlayerTeam)this.teamsByName.get(string);
    }
    
    public PlayerTeam addPlayerTeam(final String string) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The team name '" + string + "' is too long!");
        }
        PlayerTeam ctg3 = this.getPlayerTeam(string);
        if (ctg3 != null) {
            throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
        }
        ctg3 = new PlayerTeam(this, string);
        this.teamsByName.put(string, ctg3);
        this.onTeamAdded(ctg3);
        return ctg3;
    }
    
    public void removePlayerTeam(final PlayerTeam ctg) {
        this.teamsByName.remove(ctg.getName());
        for (final String string4 : ctg.getPlayers()) {
            this.teamsByPlayer.remove(string4);
        }
        this.onTeamRemoved(ctg);
    }
    
    public boolean addPlayerToTeam(final String string, final PlayerTeam ctg) {
        if (string.length() > 40) {
            throw new IllegalArgumentException("The player name '" + string + "' is too long!");
        }
        if (this.getPlayersTeam(string) != null) {
            this.removePlayerFromTeam(string);
        }
        this.teamsByPlayer.put(string, ctg);
        return ctg.getPlayers().add(string);
    }
    
    public boolean removePlayerFromTeam(final String string) {
        final PlayerTeam ctg3 = this.getPlayersTeam(string);
        if (ctg3 != null) {
            this.removePlayerFromTeam(string, ctg3);
            return true;
        }
        return false;
    }
    
    public void removePlayerFromTeam(final String string, final PlayerTeam ctg) {
        if (this.getPlayersTeam(string) != ctg) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + ctg.getName() + "'.");
        }
        this.teamsByPlayer.remove(string);
        ctg.getPlayers().remove(string);
    }
    
    public Collection<String> getTeamNames() {
        return (Collection<String>)this.teamsByName.keySet();
    }
    
    public Collection<PlayerTeam> getPlayerTeams() {
        return (Collection<PlayerTeam>)this.teamsByName.values();
    }
    
    @Nullable
    public PlayerTeam getPlayersTeam(final String string) {
        return (PlayerTeam)this.teamsByPlayer.get(string);
    }
    
    public void onObjectiveAdded(final Objective ctf) {
    }
    
    public void onObjectiveChanged(final Objective ctf) {
    }
    
    public void onObjectiveRemoved(final Objective ctf) {
    }
    
    public void onScoreChanged(final Score cth) {
    }
    
    public void onPlayerRemoved(final String string) {
    }
    
    public void onPlayerScoreRemoved(final String string, final Objective ctf) {
    }
    
    public void onTeamAdded(final PlayerTeam ctg) {
    }
    
    public void onTeamChanged(final PlayerTeam ctg) {
    }
    
    public void onTeamRemoved(final PlayerTeam ctg) {
    }
    
    public static String getDisplaySlotName(final int integer) {
        switch (integer) {
            case 0: {
                return "list";
            }
            case 1: {
                return "sidebar";
            }
            case 2: {
                return "belowName";
            }
            default: {
                if (integer >= 3 && integer <= 18) {
                    final ChatFormatting c2 = ChatFormatting.getById(integer - 3);
                    if (c2 != null && c2 != ChatFormatting.RESET) {
                        return "sidebar.team." + c2.getName();
                    }
                }
                return null;
            }
        }
    }
    
    public static int getDisplaySlotByName(final String string) {
        if ("list".equalsIgnoreCase(string)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase(string)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase(string)) {
            return 2;
        }
        if (string.startsWith("sidebar.team.")) {
            final String string2 = string.substring("sidebar.team.".length());
            final ChatFormatting c3 = ChatFormatting.getByName(string2);
            if (c3 != null && c3.getId() >= 0) {
                return c3.getId() + 3;
            }
        }
        return -1;
    }
    
    public static String[] getDisplaySlotNames() {
        if (Scoreboard.displaySlotNames == null) {
            Scoreboard.displaySlotNames = new String[19];
            for (int integer1 = 0; integer1 < 19; ++integer1) {
                Scoreboard.displaySlotNames[integer1] = getDisplaySlotName(integer1);
            }
        }
        return Scoreboard.displaySlotNames;
    }
    
    public void entityRemoved(final Entity aio) {
        if (aio == null || aio instanceof Player || aio.isAlive()) {
            return;
        }
        final String string3 = aio.getStringUUID();
        this.resetPlayerScore(string3, null);
        this.removePlayerFromTeam(string3);
    }
    
    protected ListTag savePlayerScores() {
        final ListTag ik2 = new ListTag();
        this.playerScores.values().stream().map(Map::values).forEach(collection -> collection.stream().filter(cth -> cth.getObjective() != null).forEach(cth -> {
            final CompoundTag id3 = new CompoundTag();
            id3.putString("Name", cth.getOwner());
            id3.putString("Objective", cth.getObjective().getName());
            id3.putInt("Score", cth.getScore());
            id3.putBoolean("Locked", cth.isLocked());
            ik2.add(id3);
        }));
        return ik2;
    }
    
    protected void loadPlayerScores(final ListTag ik) {
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            final CompoundTag id4 = ik.getCompound(integer3);
            final Objective ctf5 = this.getOrCreateObjective(id4.getString("Objective"));
            String string6 = id4.getString("Name");
            if (string6.length() > 40) {
                string6 = string6.substring(0, 40);
            }
            final Score cth7 = this.getOrCreatePlayerScore(string6, ctf5);
            cth7.setScore(id4.getInt("Score"));
            if (id4.contains("Locked")) {
                cth7.setLocked(id4.getBoolean("Locked"));
            }
        }
    }
}

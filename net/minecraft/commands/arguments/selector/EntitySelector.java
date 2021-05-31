package net.minecraft.commands.arguments.selector;

import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Component;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.server.level.ServerLevel;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collections;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import java.util.UUID;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.function.Function;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.Entity;
import java.util.function.Predicate;

public class EntitySelector {
    private final int maxResults;
    private final boolean includesEntities;
    private final boolean worldLimited;
    private final Predicate<Entity> predicate;
    private final MinMaxBounds.Floats range;
    private final Function<Vec3, Vec3> position;
    @Nullable
    private final AABB aabb;
    private final BiConsumer<Vec3, List<? extends Entity>> order;
    private final boolean currentEntity;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID entityUUID;
    @Nullable
    private final EntityType<?> type;
    private final boolean usesSelector;
    
    public EntitySelector(final int integer, final boolean boolean2, final boolean boolean3, final Predicate<Entity> predicate, final MinMaxBounds.Floats c, final Function<Vec3, Vec3> function, @Nullable final AABB csc, final BiConsumer<Vec3, List<? extends Entity>> biConsumer, final boolean boolean9, @Nullable final String string, @Nullable final UUID uUID, @Nullable final EntityType<?> ais, final boolean boolean13) {
        this.maxResults = integer;
        this.includesEntities = boolean2;
        this.worldLimited = boolean3;
        this.predicate = predicate;
        this.range = c;
        this.position = function;
        this.aabb = csc;
        this.order = biConsumer;
        this.currentEntity = boolean9;
        this.playerName = string;
        this.entityUUID = uUID;
        this.type = ais;
        this.usesSelector = boolean13;
    }
    
    public int getMaxResults() {
        return this.maxResults;
    }
    
    public boolean includesEntities() {
        return this.includesEntities;
    }
    
    public boolean isSelfSelector() {
        return this.currentEntity;
    }
    
    public boolean isWorldLimited() {
        return this.worldLimited;
    }
    
    private void checkPermissions(final CommandSourceStack cd) throws CommandSyntaxException {
        if (this.usesSelector && !cd.hasPermission(2)) {
            throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
        }
    }
    
    public Entity findSingleEntity(final CommandSourceStack cd) throws CommandSyntaxException {
        this.checkPermissions(cd);
        final List<? extends Entity> list3 = this.findEntities(cd);
        if (list3.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }
        if (list3.size() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
        }
        return (Entity)list3.get(0);
    }
    
    public List<? extends Entity> findEntities(final CommandSourceStack cd) throws CommandSyntaxException {
        this.checkPermissions(cd);
        if (!this.includesEntities) {
            return this.findPlayers(cd);
        }
        if (this.playerName != null) {
            final ServerPlayer vl3 = cd.getServer().getPlayerList().getPlayerByName(this.playerName);
            if (vl3 == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayer[] { vl3 });
        }
        else {
            if (this.entityUUID != null) {
                for (final ServerLevel vk4 : cd.getServer().getAllLevels()) {
                    final Entity aio5 = vk4.getEntity(this.entityUUID);
                    if (aio5 != null) {
                        return Lists.newArrayList((Object[])new Entity[] { aio5 });
                    }
                }
                return Collections.emptyList();
            }
            final Vec3 csi3 = (Vec3)this.position.apply(cd.getPosition());
            final Predicate<Entity> predicate4 = this.getPredicate(csi3);
            if (!this.currentEntity) {
                final List<Entity> list5 = (List<Entity>)Lists.newArrayList();
                if (this.isWorldLimited()) {
                    this.addEntities(list5, cd.getLevel(), csi3, predicate4);
                }
                else {
                    for (final ServerLevel vk5 : cd.getServer().getAllLevels()) {
                        this.addEntities(list5, vk5, csi3, predicate4);
                    }
                }
                return this.sortAndLimit(csi3, (java.util.List<? extends Entity>)list5);
            }
            if (cd.getEntity() != null && predicate4.test(cd.getEntity())) {
                return Lists.newArrayList((Object[])new Entity[] { cd.getEntity() });
            }
            return Collections.emptyList();
        }
    }
    
    private void addEntities(final List<Entity> list, final ServerLevel vk, final Vec3 csi, final Predicate<Entity> predicate) {
        if (this.aabb != null) {
            list.addAll((Collection)vk.getEntities(this.type, this.aabb.move(csi), predicate));
        }
        else {
            list.addAll((Collection)vk.getEntities(this.type, predicate));
        }
    }
    
    public ServerPlayer findSinglePlayer(final CommandSourceStack cd) throws CommandSyntaxException {
        this.checkPermissions(cd);
        final List<ServerPlayer> list3 = this.findPlayers(cd);
        if (list3.size() != 1) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        return (ServerPlayer)list3.get(0);
    }
    
    public List<ServerPlayer> findPlayers(final CommandSourceStack cd) throws CommandSyntaxException {
        this.checkPermissions(cd);
        if (this.playerName != null) {
            final ServerPlayer vl3 = cd.getServer().getPlayerList().getPlayerByName(this.playerName);
            if (vl3 == null) {
                return (List<ServerPlayer>)Collections.emptyList();
            }
            return (List<ServerPlayer>)Lists.newArrayList((Object[])new ServerPlayer[] { vl3 });
        }
        else if (this.entityUUID != null) {
            final ServerPlayer vl3 = cd.getServer().getPlayerList().getPlayer(this.entityUUID);
            if (vl3 == null) {
                return (List<ServerPlayer>)Collections.emptyList();
            }
            return (List<ServerPlayer>)Lists.newArrayList((Object[])new ServerPlayer[] { vl3 });
        }
        else {
            final Vec3 csi3 = (Vec3)this.position.apply(cd.getPosition());
            final Predicate<Entity> predicate4 = this.getPredicate(csi3);
            if (this.currentEntity) {
                if (cd.getEntity() instanceof ServerPlayer) {
                    final ServerPlayer vl4 = (ServerPlayer)cd.getEntity();
                    if (predicate4.test(vl4)) {
                        return (List<ServerPlayer>)Lists.newArrayList((Object[])new ServerPlayer[] { vl4 });
                    }
                }
                return (List<ServerPlayer>)Collections.emptyList();
            }
            List<ServerPlayer> list5;
            if (this.isWorldLimited()) {
                list5 = cd.getLevel().getPlayers(predicate4::test);
            }
            else {
                list5 = (List<ServerPlayer>)Lists.newArrayList();
                for (final ServerPlayer vl5 : cd.getServer().getPlayerList().getPlayers()) {
                    if (predicate4.test(vl5)) {
                        list5.add(vl5);
                    }
                }
            }
            return this.<ServerPlayer>sortAndLimit(csi3, list5);
        }
    }
    
    private Predicate<Entity> getPredicate(final Vec3 csi) {
        Predicate<Entity> predicate3 = this.predicate;
        if (this.aabb != null) {
            final AABB csc4 = this.aabb.move(csi);
            predicate3 = (Predicate<Entity>)predicate3.and(aio -> csc4.intersects(aio.getBoundingBox()));
        }
        if (!this.range.isAny()) {
            predicate3 = (Predicate<Entity>)predicate3.and(aio -> this.range.matchesSqr(aio.distanceToSqr(csi)));
        }
        return predicate3;
    }
    
    private <T extends Entity> List<T> sortAndLimit(final Vec3 csi, final List<T> list) {
        if (list.size() > 1) {
            this.order.accept(csi, list);
        }
        return (List<T>)list.subList(0, Math.min(this.maxResults, list.size()));
    }
    
    public static Component joinNames(final List<? extends Entity> list) {
        return ComponentUtils.formatList((java.util.Collection<Object>)list, (java.util.function.Function<Object, Component>)Entity::getDisplayName);
    }
}

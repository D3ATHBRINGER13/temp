package net.minecraft.client.multiplayer;

import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Locale;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.player.LocalPlayer;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import java.util.Collections;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collection;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;

public class ClientSuggestionProvider implements SharedSuggestionProvider {
    private final ClientPacketListener connection;
    private final Minecraft minecraft;
    private int pendingSuggestionsId;
    private CompletableFuture<Suggestions> pendingSuggestionsFuture;
    
    public ClientSuggestionProvider(final ClientPacketListener dkc, final Minecraft cyc) {
        this.pendingSuggestionsId = -1;
        this.connection = dkc;
        this.minecraft = cyc;
    }
    
    public Collection<String> getOnlinePlayerNames() {
        final List<String> list2 = (List<String>)Lists.newArrayList();
        for (final PlayerInfo dkg4 : this.connection.getOnlinePlayers()) {
            list2.add(dkg4.getProfile().getName());
        }
        return (Collection<String>)list2;
    }
    
    public Collection<String> getSelectedEntities() {
        if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY) {
            return (Collection<String>)Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID());
        }
        return (Collection<String>)Collections.emptyList();
    }
    
    public Collection<String> getAllTeams() {
        return this.connection.getLevel().getScoreboard().getTeamNames();
    }
    
    public Collection<ResourceLocation> getAvailableSoundEvents() {
        return this.minecraft.getSoundManager().getAvailableSounds();
    }
    
    public Stream<ResourceLocation> getRecipeNames() {
        return this.connection.getRecipeManager().getRecipeIds();
    }
    
    public boolean hasPermission(final int integer) {
        final LocalPlayer dmp3 = this.minecraft.player;
        return (dmp3 != null) ? dmp3.hasPermissions(integer) : (integer == 0);
    }
    
    public CompletableFuture<Suggestions> customSuggestion(final CommandContext<SharedSuggestionProvider> commandContext, final SuggestionsBuilder suggestionsBuilder) {
        if (this.pendingSuggestionsFuture != null) {
            this.pendingSuggestionsFuture.cancel(false);
        }
        this.pendingSuggestionsFuture = (CompletableFuture<Suggestions>)new CompletableFuture();
        final int integer4 = ++this.pendingSuggestionsId;
        this.connection.send(new ServerboundCommandSuggestionPacket(integer4, commandContext.getInput()));
        return this.pendingSuggestionsFuture;
    }
    
    private static String prettyPrint(final double double1) {
        return String.format(Locale.ROOT, "%.2f", new Object[] { double1 });
    }
    
    private static String prettyPrint(final int integer) {
        return Integer.toString(integer);
    }
    
    public Collection<TextCoordinates> getRelevantCoordinates() {
        final HitResult csf2 = this.minecraft.hitResult;
        if (csf2 == null || csf2.getType() != HitResult.Type.BLOCK) {
            return super.getRelevantCoordinates();
        }
        final BlockPos ew3 = ((BlockHitResult)csf2).getBlockPos();
        return (Collection<TextCoordinates>)Collections.singleton(new TextCoordinates(prettyPrint(ew3.getX()), prettyPrint(ew3.getY()), prettyPrint(ew3.getZ())));
    }
    
    public Collection<TextCoordinates> getAbsoluteCoordinates() {
        final HitResult csf2 = this.minecraft.hitResult;
        if (csf2 == null || csf2.getType() != HitResult.Type.BLOCK) {
            return super.getAbsoluteCoordinates();
        }
        final Vec3 csi3 = csf2.getLocation();
        return (Collection<TextCoordinates>)Collections.singleton(new TextCoordinates(prettyPrint(csi3.x), prettyPrint(csi3.y), prettyPrint(csi3.z)));
    }
    
    public void completeCustomSuggestions(final int integer, final Suggestions suggestions) {
        if (integer == this.pendingSuggestionsId) {
            this.pendingSuggestionsFuture.complete(suggestions);
            this.pendingSuggestionsFuture = null;
            this.pendingSuggestionsId = -1;
        }
    }
}

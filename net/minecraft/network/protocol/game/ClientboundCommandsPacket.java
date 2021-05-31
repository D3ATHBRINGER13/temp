package net.minecraft.network.protocol.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.PacketListener;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import javax.annotation.Nullable;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.synchronization.ArgumentTypes;
import com.mojang.brigadier.builder.ArgumentBuilder;
import java.util.Map;
import com.mojang.brigadier.tree.CommandNode;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.Deque;
import java.util.ArrayDeque;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.commands.SharedSuggestionProvider;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.network.protocol.Packet;

public class ClientboundCommandsPacket implements Packet<ClientGamePacketListener> {
    private RootCommandNode<SharedSuggestionProvider> root;
    
    public ClientboundCommandsPacket() {
    }
    
    public ClientboundCommandsPacket(final RootCommandNode<SharedSuggestionProvider> rootCommandNode) {
        this.root = rootCommandNode;
    }
    
    public void read(final FriendlyByteBuf je) throws IOException {
        final Entry[] arr3 = new Entry[je.readVarInt()];
        final Deque<Entry> deque4 = (Deque<Entry>)new ArrayDeque(arr3.length);
        for (int integer5 = 0; integer5 < arr3.length; ++integer5) {
            deque4.add((arr3[integer5] = this.readNode(je)));
        }
        while (!deque4.isEmpty()) {
            boolean boolean5 = false;
            final Iterator<Entry> iterator6 = (Iterator<Entry>)deque4.iterator();
            while (iterator6.hasNext()) {
                final Entry a7 = (Entry)iterator6.next();
                if (a7.build(arr3)) {
                    iterator6.remove();
                    boolean5 = true;
                }
            }
            if (!boolean5) {
                throw new IllegalStateException("Server sent an impossible command tree");
            }
        }
        this.root = (RootCommandNode<SharedSuggestionProvider>)arr3[je.readVarInt()].node;
    }
    
    public void write(final FriendlyByteBuf je) throws IOException {
        final Map<CommandNode<SharedSuggestionProvider>, Integer> map3 = (Map<CommandNode<SharedSuggestionProvider>, Integer>)Maps.newHashMap();
        final Deque<CommandNode<SharedSuggestionProvider>> deque4 = (Deque<CommandNode<SharedSuggestionProvider>>)new ArrayDeque();
        deque4.add(this.root);
        while (!deque4.isEmpty()) {
            final CommandNode<SharedSuggestionProvider> commandNode5 = (CommandNode<SharedSuggestionProvider>)deque4.pollFirst();
            if (map3.containsKey(commandNode5)) {
                continue;
            }
            final int integer6 = map3.size();
            map3.put(commandNode5, integer6);
            deque4.addAll(commandNode5.getChildren());
            if (commandNode5.getRedirect() == null) {
                continue;
            }
            deque4.add(commandNode5.getRedirect());
        }
        final CommandNode<SharedSuggestionProvider>[] arr5 = new CommandNode[map3.size()];
        for (final Map.Entry<CommandNode<SharedSuggestionProvider>, Integer> entry7 : map3.entrySet()) {
            arr5[entry7.getValue()] = (CommandNode<SharedSuggestionProvider>)entry7.getKey();
        }
        je.writeVarInt(arr5.length);
        for (final CommandNode<SharedSuggestionProvider> commandNode6 : arr5) {
            this.writeNode(je, commandNode6, map3);
        }
        je.writeVarInt((int)map3.get(this.root));
    }
    
    private Entry readNode(final FriendlyByteBuf je) {
        final byte byte3 = je.readByte();
        final int[] arr4 = je.readVarIntArray();
        final int integer5 = ((byte3 & 0x8) != 0x0) ? je.readVarInt() : 0;
        final ArgumentBuilder<SharedSuggestionProvider, ?> argumentBuilder6 = this.createBuilder(je, byte3);
        return new Entry((ArgumentBuilder)argumentBuilder6, byte3, integer5, arr4);
    }
    
    @Nullable
    private ArgumentBuilder<SharedSuggestionProvider, ?> createBuilder(final FriendlyByteBuf je, final byte byte2) {
        final int integer4 = byte2 & 0x3;
        if (integer4 == 2) {
            final String string5 = je.readUtf(32767);
            final ArgumentType<?> argumentType6 = ArgumentTypes.deserialize(je);
            if (argumentType6 == null) {
                return null;
            }
            final RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredArgumentBuilder7 = RequiredArgumentBuilder.argument(string5, (ArgumentType)argumentType6);
            if ((byte2 & 0x10) != 0x0) {
                requiredArgumentBuilder7.suggests((SuggestionProvider)SuggestionProviders.getProvider(je.readResourceLocation()));
            }
            return requiredArgumentBuilder7;
        }
        else {
            if (integer4 == 1) {
                return LiteralArgumentBuilder.literal(je.readUtf(32767));
            }
            return null;
        }
    }
    
    private void writeNode(final FriendlyByteBuf je, final CommandNode<SharedSuggestionProvider> commandNode, final Map<CommandNode<SharedSuggestionProvider>, Integer> map) {
        byte byte5 = 0;
        if (commandNode.getRedirect() != null) {
            byte5 |= 0x8;
        }
        if (commandNode.getCommand() != null) {
            byte5 |= 0x4;
        }
        if (commandNode instanceof RootCommandNode) {
            byte5 |= 0x0;
        }
        else if (commandNode instanceof ArgumentCommandNode) {
            byte5 |= 0x2;
            if (((ArgumentCommandNode)commandNode).getCustomSuggestions() != null) {
                byte5 |= 0x10;
            }
        }
        else {
            if (!(commandNode instanceof LiteralCommandNode)) {
                throw new UnsupportedOperationException(new StringBuilder().append("Unknown node type ").append(commandNode).toString());
            }
            byte5 |= 0x1;
        }
        je.writeByte(byte5);
        je.writeVarInt(commandNode.getChildren().size());
        for (final CommandNode<SharedSuggestionProvider> commandNode2 : commandNode.getChildren()) {
            je.writeVarInt((int)map.get(commandNode2));
        }
        if (commandNode.getRedirect() != null) {
            je.writeVarInt((int)map.get(commandNode.getRedirect()));
        }
        if (commandNode instanceof ArgumentCommandNode) {
            final ArgumentCommandNode<SharedSuggestionProvider, ?> argumentCommandNode6 = commandNode;
            je.writeUtf(argumentCommandNode6.getName());
            ArgumentTypes.<ArgumentType>serialize(je, argumentCommandNode6.getType());
            if (argumentCommandNode6.getCustomSuggestions() != null) {
                je.writeResourceLocation(SuggestionProviders.getName((SuggestionProvider<SharedSuggestionProvider>)argumentCommandNode6.getCustomSuggestions()));
            }
        }
        else if (commandNode instanceof LiteralCommandNode) {
            je.writeUtf(((LiteralCommandNode)commandNode).getLiteral());
        }
    }
    
    public void handle(final ClientGamePacketListener kf) {
        kf.handleCommands(this);
    }
    
    public RootCommandNode<SharedSuggestionProvider> getRoot() {
        return this.root;
    }
    
    static class Entry {
        @Nullable
        private final ArgumentBuilder<SharedSuggestionProvider, ?> builder;
        private final byte flags;
        private final int redirect;
        private final int[] children;
        private CommandNode<SharedSuggestionProvider> node;
        
        private Entry(@Nullable final ArgumentBuilder<SharedSuggestionProvider, ?> argumentBuilder, final byte byte2, final int integer, final int[] arr) {
            this.builder = argumentBuilder;
            this.flags = byte2;
            this.redirect = integer;
            this.children = arr;
        }
        
        public boolean build(final Entry[] arr) {
            if (this.node == null) {
                if (this.builder == null) {
                    this.node = (CommandNode<SharedSuggestionProvider>)new RootCommandNode();
                }
                else {
                    if ((this.flags & 0x8) != 0x0) {
                        if (arr[this.redirect].node == null) {
                            return false;
                        }
                        this.builder.redirect((CommandNode)arr[this.redirect].node);
                    }
                    if ((this.flags & 0x4) != 0x0) {
                        this.builder.executes(commandContext -> 0);
                    }
                    this.node = (CommandNode<SharedSuggestionProvider>)this.builder.build();
                }
            }
            for (final int integer6 : this.children) {
                if (arr[integer6].node == null) {
                    return false;
                }
            }
            for (final int integer6 : this.children) {
                final CommandNode<SharedSuggestionProvider> commandNode7 = arr[integer6].node;
                if (!(commandNode7 instanceof RootCommandNode)) {
                    this.node.addChild((CommandNode)commandNode7);
                }
            }
            return true;
        }
    }
}

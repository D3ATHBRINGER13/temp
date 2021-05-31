package net.minecraft.world.level.timers;

import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.tags.Tag;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class FunctionTagCallback implements TimerCallback<MinecraftServer> {
    private final ResourceLocation tagId;
    
    public FunctionTagCallback(final ResourceLocation qv) {
        this.tagId = qv;
    }
    
    public void handle(final MinecraftServer minecraftServer, final TimerQueue<MinecraftServer> crz, final long long3) {
        final ServerFunctionManager rh6 = minecraftServer.getFunctions();
        final Tag<CommandFunction> zg7 = rh6.getTags().getTagOrEmpty(this.tagId);
        for (final CommandFunction ca9 : zg7.getValues()) {
            rh6.execute(ca9, rh6.getGameLoopSender());
        }
    }
    
    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagCallback> {
        public Serializer() {
            super(new ResourceLocation("function_tag"), FunctionTagCallback.class);
        }
        
        @Override
        public void serialize(final CompoundTag id, final FunctionTagCallback crw) {
            id.putString("Name", crw.tagId.toString());
        }
        
        @Override
        public FunctionTagCallback deserialize(final CompoundTag id) {
            final ResourceLocation qv3 = new ResourceLocation(id.getString("Name"));
            return new FunctionTagCallback(qv3);
        }
    }
}

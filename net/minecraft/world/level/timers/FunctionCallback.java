package net.minecraft.world.level.timers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.commands.CommandFunction;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class FunctionCallback implements TimerCallback<MinecraftServer> {
    private final ResourceLocation functionId;
    
    public FunctionCallback(final ResourceLocation qv) {
        this.functionId = qv;
    }
    
    public void handle(final MinecraftServer minecraftServer, final TimerQueue<MinecraftServer> crz, final long long3) {
        final ServerFunctionManager rh6 = minecraftServer.getFunctions();
        rh6.get(this.functionId).ifPresent(ca -> rh6.execute(ca, rh6.getGameLoopSender()));
    }
    
    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionCallback> {
        public Serializer() {
            super(new ResourceLocation("function"), FunctionCallback.class);
        }
        
        @Override
        public void serialize(final CompoundTag id, final FunctionCallback crv) {
            id.putString("Name", crv.functionId.toString());
        }
        
        @Override
        public FunctionCallback deserialize(final CompoundTag id) {
            final ResourceLocation qv3 = new ResourceLocation(id.getString("Name"));
            return new FunctionCallback(qv3);
        }
    }
}

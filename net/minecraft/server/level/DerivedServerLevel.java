package net.minecraft.server.level;

import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorage;
import java.util.concurrent.Executor;
import net.minecraft.server.MinecraftServer;

public class DerivedServerLevel extends ServerLevel {
    public DerivedServerLevel(final ServerLevel vk, final MinecraftServer minecraftServer, final Executor executor, final LevelStorage coo, final DimensionType byn, final ProfilerFiller agn, final ChunkProgressListener vt) {
        super(minecraftServer, executor, coo, new DerivedLevelData(vk.getLevelData()), byn, agn, vt);
        vk.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(this.getWorldBorder()));
    }
    
    @Override
    protected void tickTime() {
    }
}

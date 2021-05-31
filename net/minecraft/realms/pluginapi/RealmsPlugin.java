package net.minecraft.realms.pluginapi;

import com.mojang.datafixers.util.Either;

public interface RealmsPlugin {
    Either<LoadedRealmsPlugin, String> tryLoad(final String string);
}

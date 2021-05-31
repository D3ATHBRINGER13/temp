package net.minecraft.server.packs.repository;

import java.util.Map;

public interface RepositorySource {
     <T extends UnopenedPack> void loadPacks(final Map<String, T> map, final UnopenedPack.UnopenedPackConstructor<T> b);
}

package net.minecraft.server.packs.repository;

import net.minecraft.server.packs.Pack;
import java.util.function.Supplier;
import java.util.Map;
import net.minecraft.server.packs.VanillaPack;

public class ServerPacksSource implements RepositorySource {
    private final VanillaPack vanillaPack;
    
    public ServerPacksSource() {
        this.vanillaPack = new VanillaPack(new String[] { "minecraft" });
    }
    
    public <T extends UnopenedPack> void loadPacks(final Map<String, T> map, final UnopenedPack.UnopenedPackConstructor<T> b) {
        final T xa4 = UnopenedPack.<T>create("vanilla", false, (Supplier<Pack>)(() -> this.vanillaPack), b, UnopenedPack.Position.BOTTOM);
        if (xa4 != null) {
            map.put("vanilla", xa4);
        }
    }
}

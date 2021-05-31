package net.minecraft.server.packs.repository;

import java.util.function.Function;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import net.minecraft.network.chat.HoverEvent;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.Pack;
import java.util.function.Supplier;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.apache.logging.log4j.Logger;

public class UnopenedPack implements AutoCloseable {
    private static final Logger LOGGER;
    private static final PackMetadataSection BROKEN_ASSETS_FALLBACK;
    private final String id;
    private final Supplier<Pack> supplier;
    private final Component title;
    private final Component description;
    private final PackCompatibility compatibility;
    private final Position defaultPosition;
    private final boolean required;
    private final boolean fixedPosition;
    
    @Nullable
    public static <T extends UnopenedPack> T create(final String string, final boolean boolean2, final Supplier<Pack> supplier, final UnopenedPackConstructor<T> b, final Position a) {
        try (final Pack wl6 = (Pack)supplier.get()) {
            PackMetadataSection wq8 = wl6.<PackMetadataSection>getMetadataSection((MetadataSectionSerializer<PackMetadataSection>)PackMetadataSection.SERIALIZER);
            if (boolean2 && wq8 == null) {
                UnopenedPack.LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
                wq8 = UnopenedPack.BROKEN_ASSETS_FALLBACK;
            }
            if (wq8 != null) {
                return b.create(string, boolean2, supplier, wl6, wq8, a);
            }
            UnopenedPack.LOGGER.warn("Couldn't find pack meta for pack {}", string);
        }
        catch (IOException iOException6) {
            UnopenedPack.LOGGER.warn("Couldn't get pack info for: {}", iOException6.toString());
        }
        return null;
    }
    
    public UnopenedPack(final String string, final boolean boolean2, final Supplier<Pack> supplier, final Component jo4, final Component jo5, final PackCompatibility ww, final Position a, final boolean boolean8) {
        this.id = string;
        this.supplier = supplier;
        this.title = jo4;
        this.description = jo5;
        this.compatibility = ww;
        this.required = boolean2;
        this.defaultPosition = a;
        this.fixedPosition = boolean8;
    }
    
    public UnopenedPack(final String string, final boolean boolean2, final Supplier<Pack> supplier, final Pack wl, final PackMetadataSection wq, final Position a) {
        this(string, boolean2, supplier, new TextComponent(wl.getName()), wq.getDescription(), PackCompatibility.forFormat(wq.getPackFormat()), a, false);
    }
    
    public Component getTitle() {
        return this.title;
    }
    
    public Component getDescription() {
        return this.description;
    }
    
    public Component getChatLink(final boolean boolean1) {
        return ComponentUtils.wrapInSquareBrackets(new TextComponent(this.id)).withStyle((Consumer<Style>)(jw -> jw.setColor(boolean1 ? ChatFormatting.GREEN : ChatFormatting.RED).setInsertion(StringArgumentType.escapeIfRequired(this.id)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("").append(this.title).append("\n").append(this.description)))));
    }
    
    public PackCompatibility getCompatibility() {
        return this.compatibility;
    }
    
    public Pack open() {
        return (Pack)this.supplier.get();
    }
    
    public String getId() {
        return this.id;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public boolean isFixedPosition() {
        return this.fixedPosition;
    }
    
    public Position getDefaultPosition() {
        return this.defaultPosition;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof UnopenedPack)) {
            return false;
        }
        final UnopenedPack xa3 = (UnopenedPack)object;
        return this.id.equals(xa3.id);
    }
    
    public int hashCode() {
        return this.id.hashCode();
    }
    
    public void close() {
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BROKEN_ASSETS_FALLBACK = new PackMetadataSection(new TranslatableComponent("resourcePack.broken_assets", new Object[0]).withStyle(ChatFormatting.RED, ChatFormatting.ITALIC), SharedConstants.getCurrentVersion().getPackVersion());
    }
    
    public enum Position {
        TOP, 
        BOTTOM;
        
        public <T, P extends UnopenedPack> int insert(final List<T> list, final T object, final Function<T, P> function, final boolean boolean4) {
            final Position a6 = boolean4 ? this.opposite() : this;
            if (a6 == Position.BOTTOM) {
                int integer7;
                for (integer7 = 0; integer7 < list.size(); ++integer7) {
                    final P xa8 = (P)function.apply(list.get(integer7));
                    if (!xa8.isFixedPosition() || xa8.getDefaultPosition() != this) {
                        break;
                    }
                }
                list.add(integer7, object);
                return integer7;
            }
            int integer7;
            for (integer7 = list.size() - 1; integer7 >= 0; --integer7) {
                final P xa8 = (P)function.apply(list.get(integer7));
                if (!xa8.isFixedPosition() || xa8.getDefaultPosition() != this) {
                    break;
                }
            }
            list.add(integer7 + 1, object);
            return integer7 + 1;
        }
        
        public Position opposite() {
            return (this == Position.TOP) ? Position.BOTTOM : Position.TOP;
        }
    }
    
    @FunctionalInterface
    public interface UnopenedPackConstructor<T extends UnopenedPack> {
        @Nullable
        T create(final String string, final boolean boolean2, final Supplier<Pack> supplier, final Pack wl, final PackMetadataSection wq, final Position a);
    }
}

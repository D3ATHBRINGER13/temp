package net.minecraft.network.chat;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import java.util.Objects;
import java.util.List;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.apache.logging.log4j.LogManager;
import com.google.common.base.Joiner;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.NbtPathArgument;
import org.apache.logging.log4j.Logger;

public abstract class NbtComponent extends BaseComponent implements ContextAwareComponent {
    private static final Logger LOGGER;
    protected final boolean interpreting;
    protected final String nbtPathPattern;
    @Nullable
    protected final NbtPathArgument.NbtPath compiledNbtPath;
    
    @Nullable
    private static NbtPathArgument.NbtPath compileNbtPath(final String string) {
        try {
            return new NbtPathArgument().parse(new StringReader(string));
        }
        catch (CommandSyntaxException commandSyntaxException2) {
            return null;
        }
    }
    
    public NbtComponent(final String string, final boolean boolean2) {
        this(string, compileNbtPath(string), boolean2);
    }
    
    protected NbtComponent(final String string, @Nullable final NbtPathArgument.NbtPath h, final boolean boolean3) {
        this.nbtPathPattern = string;
        this.compiledNbtPath = h;
        this.interpreting = boolean3;
    }
    
    protected abstract Stream<CompoundTag> getData(final CommandSourceStack cd) throws CommandSyntaxException;
    
    public String getContents() {
        return "";
    }
    
    public String getNbtPath() {
        return this.nbtPathPattern;
    }
    
    public boolean isInterpreting() {
        return this.interpreting;
    }
    
    @Override
    public Component resolve(@Nullable final CommandSourceStack cd, @Nullable final Entity aio, final int integer) throws CommandSyntaxException {
        if (cd == null || this.compiledNbtPath == null) {
            return new TextComponent("");
        }
        final Stream<String> stream5 = (Stream<String>)this.getData(cd).flatMap(id -> {
            try {
                return this.compiledNbtPath.get(id).stream();
            }
            catch (CommandSyntaxException commandSyntaxException3) {
                return Stream.empty();
            }
        }).map(Tag::getAsString);
        if (this.interpreting) {
            return (Component)stream5.flatMap(string -> {
                try {
                    final Component jo5 = Component.Serializer.fromJson(string);
                    return Stream.of(ComponentUtils.updateForEntity(cd, jo5, aio, integer));
                }
                catch (Exception exception5) {
                    NbtComponent.LOGGER.warn("Failed to parse component: " + string, (Throwable)exception5);
                    return Stream.of((Object[])new Component[0]);
                }
            }).reduce((jo1, jo2) -> jo1.append(", ").append(jo2)).orElse(new TextComponent(""));
        }
        return new TextComponent(Joiner.on(", ").join(stream5.iterator()));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class EntityNbtComponent extends NbtComponent {
        private final String selectorPattern;
        @Nullable
        private final EntitySelector compiledSelector;
        
        public EntityNbtComponent(final String string1, final boolean boolean2, final String string3) {
            super(string1, boolean2);
            this.selectorPattern = string3;
            this.compiledSelector = compileSelector(string3);
        }
        
        @Nullable
        private static EntitySelector compileSelector(final String string) {
            try {
                final EntitySelectorParser ed2 = new EntitySelectorParser(new StringReader(string));
                return ed2.parse();
            }
            catch (CommandSyntaxException commandSyntaxException2) {
                return null;
            }
        }
        
        private EntityNbtComponent(final String string1, @Nullable final NbtPathArgument.NbtPath h, final boolean boolean3, final String string4, @Nullable final EntitySelector ec) {
            super(string1, h, boolean3);
            this.selectorPattern = string4;
            this.compiledSelector = ec;
        }
        
        public String getSelector() {
            return this.selectorPattern;
        }
        
        public Component copy() {
            return new EntityNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.selectorPattern, this.compiledSelector);
        }
        
        @Override
        protected Stream<CompoundTag> getData(final CommandSourceStack cd) throws CommandSyntaxException {
            if (this.compiledSelector != null) {
                final List<? extends Entity> list3 = this.compiledSelector.findEntities(cd);
                return (Stream<CompoundTag>)list3.stream().map(NbtPredicate::getEntityTagToCompare);
            }
            return (Stream<CompoundTag>)Stream.empty();
        }
        
        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof EntityNbtComponent) {
                final EntityNbtComponent b3 = (EntityNbtComponent)object;
                return Objects.equals(this.selectorPattern, b3.selectorPattern) && Objects.equals(this.nbtPathPattern, b3.nbtPathPattern) && super.equals(object);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.selectorPattern + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }
    
    public static class BlockNbtComponent extends NbtComponent {
        private final String posPattern;
        @Nullable
        private final Coordinates compiledPos;
        
        public BlockNbtComponent(final String string1, final boolean boolean2, final String string3) {
            super(string1, boolean2);
            this.posPattern = string3;
            this.compiledPos = this.compilePos(this.posPattern);
        }
        
        @Nullable
        private Coordinates compilePos(final String string) {
            try {
                return BlockPosArgument.blockPos().parse(new StringReader(string));
            }
            catch (CommandSyntaxException commandSyntaxException3) {
                return null;
            }
        }
        
        private BlockNbtComponent(final String string1, @Nullable final NbtPathArgument.NbtPath h, final boolean boolean3, final String string4, @Nullable final Coordinates dl) {
            super(string1, h, boolean3);
            this.posPattern = string4;
            this.compiledPos = dl;
        }
        
        @Nullable
        public String getPos() {
            return this.posPattern;
        }
        
        public Component copy() {
            return new BlockNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.posPattern, this.compiledPos);
        }
        
        @Override
        protected Stream<CompoundTag> getData(final CommandSourceStack cd) {
            if (this.compiledPos != null) {
                final ServerLevel vk3 = cd.getLevel();
                final BlockPos ew4 = this.compiledPos.getBlockPos(cd);
                if (vk3.isLoaded(ew4)) {
                    final BlockEntity btw5 = vk3.getBlockEntity(ew4);
                    if (btw5 != null) {
                        return (Stream<CompoundTag>)Stream.of(btw5.save(new CompoundTag()));
                    }
                }
            }
            return (Stream<CompoundTag>)Stream.empty();
        }
        
        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof BlockNbtComponent) {
                final BlockNbtComponent a3 = (BlockNbtComponent)object;
                return Objects.equals(this.posPattern, a3.posPattern) && Objects.equals(this.nbtPathPattern, a3.nbtPathPattern) && super.equals(object);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.posPattern + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }
    }
}

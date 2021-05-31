package net.minecraft.network.syncher;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Pose;
import java.util.OptionalInt;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

public class EntityDataSerializers {
    private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS;
    public static final EntityDataSerializer<Byte> BYTE;
    public static final EntityDataSerializer<Integer> INT;
    public static final EntityDataSerializer<Float> FLOAT;
    public static final EntityDataSerializer<String> STRING;
    public static final EntityDataSerializer<Component> COMPONENT;
    public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT;
    public static final EntityDataSerializer<ItemStack> ITEM_STACK;
    public static final EntityDataSerializer<Optional<BlockState>> BLOCK_STATE;
    public static final EntityDataSerializer<Boolean> BOOLEAN;
    public static final EntityDataSerializer<ParticleOptions> PARTICLE;
    public static final EntityDataSerializer<Rotations> ROTATIONS;
    public static final EntityDataSerializer<BlockPos> BLOCK_POS;
    public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS;
    public static final EntityDataSerializer<Direction> DIRECTION;
    public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID;
    public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG;
    public static final EntityDataSerializer<VillagerData> VILLAGER_DATA;
    public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT;
    public static final EntityDataSerializer<Pose> POSE;
    
    public static void registerSerializer(final EntityDataSerializer<?> ql) {
        EntityDataSerializers.SERIALIZERS.add(ql);
    }
    
    @Nullable
    public static EntityDataSerializer<?> getSerializer(final int integer) {
        return EntityDataSerializers.SERIALIZERS.byId(integer);
    }
    
    public static int getSerializedId(final EntityDataSerializer<?> ql) {
        return EntityDataSerializers.SERIALIZERS.getId(ql);
    }
    
    static {
        SERIALIZERS = new CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>>(16);
        BYTE = new EntityDataSerializer<Byte>() {
            public void write(final FriendlyByteBuf je, final Byte byte2) {
                je.writeByte(byte2);
            }
            
            public Byte read(final FriendlyByteBuf je) {
                return je.readByte();
            }
            
            public Byte copy(final Byte byte1) {
                return byte1;
            }
        };
        INT = new EntityDataSerializer<Integer>() {
            public void write(final FriendlyByteBuf je, final Integer integer) {
                je.writeVarInt(integer);
            }
            
            public Integer read(final FriendlyByteBuf je) {
                return je.readVarInt();
            }
            
            public Integer copy(final Integer integer) {
                return integer;
            }
        };
        FLOAT = new EntityDataSerializer<Float>() {
            public void write(final FriendlyByteBuf je, final Float float2) {
                je.writeFloat(float2);
            }
            
            public Float read(final FriendlyByteBuf je) {
                return je.readFloat();
            }
            
            public Float copy(final Float float1) {
                return float1;
            }
        };
        STRING = new EntityDataSerializer<String>() {
            public void write(final FriendlyByteBuf je, final String string) {
                je.writeUtf(string);
            }
            
            public String read(final FriendlyByteBuf je) {
                return je.readUtf(32767);
            }
            
            public String copy(final String string) {
                return string;
            }
        };
        COMPONENT = new EntityDataSerializer<Component>() {
            public void write(final FriendlyByteBuf je, final Component jo) {
                je.writeComponent(jo);
            }
            
            public Component read(final FriendlyByteBuf je) {
                return je.readComponent();
            }
            
            public Component copy(final Component jo) {
                return jo.deepCopy();
            }
        };
        OPTIONAL_COMPONENT = new EntityDataSerializer<Optional<Component>>() {
            public void write(final FriendlyByteBuf je, final Optional<Component> optional) {
                if (optional.isPresent()) {
                    je.writeBoolean(true);
                    je.writeComponent((Component)optional.get());
                }
                else {
                    je.writeBoolean(false);
                }
            }
            
            public Optional<Component> read(final FriendlyByteBuf je) {
                return (Optional<Component>)(je.readBoolean() ? Optional.of(je.readComponent()) : Optional.empty());
            }
            
            public Optional<Component> copy(final Optional<Component> optional) {
                return (Optional<Component>)(optional.isPresent() ? Optional.of(((Component)optional.get()).deepCopy()) : Optional.empty());
            }
        };
        ITEM_STACK = new EntityDataSerializer<ItemStack>() {
            public void write(final FriendlyByteBuf je, final ItemStack bcj) {
                je.writeItem(bcj);
            }
            
            public ItemStack read(final FriendlyByteBuf je) {
                return je.readItem();
            }
            
            public ItemStack copy(final ItemStack bcj) {
                return bcj.copy();
            }
        };
        BLOCK_STATE = new EntityDataSerializer<Optional<BlockState>>() {
            public void write(final FriendlyByteBuf je, final Optional<BlockState> optional) {
                if (optional.isPresent()) {
                    je.writeVarInt(Block.getId((BlockState)optional.get()));
                }
                else {
                    je.writeVarInt(0);
                }
            }
            
            public Optional<BlockState> read(final FriendlyByteBuf je) {
                final int integer3 = je.readVarInt();
                if (integer3 == 0) {
                    return (Optional<BlockState>)Optional.empty();
                }
                return (Optional<BlockState>)Optional.of(Block.stateById(integer3));
            }
            
            public Optional<BlockState> copy(final Optional<BlockState> optional) {
                return optional;
            }
        };
        BOOLEAN = new EntityDataSerializer<Boolean>() {
            public void write(final FriendlyByteBuf je, final Boolean boolean2) {
                je.writeBoolean(boolean2);
            }
            
            public Boolean read(final FriendlyByteBuf je) {
                return je.readBoolean();
            }
            
            public Boolean copy(final Boolean boolean1) {
                return boolean1;
            }
        };
        PARTICLE = new EntityDataSerializer<ParticleOptions>() {
            public void write(final FriendlyByteBuf je, final ParticleOptions gf) {
                je.writeVarInt(Registry.PARTICLE_TYPE.getId(gf.getType()));
                gf.writeToNetwork(je);
            }
            
            public ParticleOptions read(final FriendlyByteBuf je) {
                return this.<ParticleOptions>readParticle(je, Registry.PARTICLE_TYPE.byId(je.readVarInt()));
            }
            
            private <T extends ParticleOptions> T readParticle(final FriendlyByteBuf je, final ParticleType<T> gg) {
                return gg.getDeserializer().fromNetwork(gg, je);
            }
            
            public ParticleOptions copy(final ParticleOptions gf) {
                return gf;
            }
        };
        ROTATIONS = new EntityDataSerializer<Rotations>() {
            public void write(final FriendlyByteBuf je, final Rotations fo) {
                je.writeFloat(fo.getX());
                je.writeFloat(fo.getY());
                je.writeFloat(fo.getZ());
            }
            
            public Rotations read(final FriendlyByteBuf je) {
                return new Rotations(je.readFloat(), je.readFloat(), je.readFloat());
            }
            
            public Rotations copy(final Rotations fo) {
                return fo;
            }
        };
        BLOCK_POS = new EntityDataSerializer<BlockPos>() {
            public void write(final FriendlyByteBuf je, final BlockPos ew) {
                je.writeBlockPos(ew);
            }
            
            public BlockPos read(final FriendlyByteBuf je) {
                return je.readBlockPos();
            }
            
            public BlockPos copy(final BlockPos ew) {
                return ew;
            }
        };
        OPTIONAL_BLOCK_POS = new EntityDataSerializer<Optional<BlockPos>>() {
            public void write(final FriendlyByteBuf je, final Optional<BlockPos> optional) {
                je.writeBoolean(optional.isPresent());
                if (optional.isPresent()) {
                    je.writeBlockPos((BlockPos)optional.get());
                }
            }
            
            public Optional<BlockPos> read(final FriendlyByteBuf je) {
                if (!je.readBoolean()) {
                    return (Optional<BlockPos>)Optional.empty();
                }
                return (Optional<BlockPos>)Optional.of(je.readBlockPos());
            }
            
            public Optional<BlockPos> copy(final Optional<BlockPos> optional) {
                return optional;
            }
        };
        DIRECTION = new EntityDataSerializer<Direction>() {
            public void write(final FriendlyByteBuf je, final Direction fb) {
                je.writeEnum(fb);
            }
            
            public Direction read(final FriendlyByteBuf je) {
                return je.<Direction>readEnum(Direction.class);
            }
            
            public Direction copy(final Direction fb) {
                return fb;
            }
        };
        OPTIONAL_UUID = new EntityDataSerializer<Optional<UUID>>() {
            public void write(final FriendlyByteBuf je, final Optional<UUID> optional) {
                je.writeBoolean(optional.isPresent());
                if (optional.isPresent()) {
                    je.writeUUID((UUID)optional.get());
                }
            }
            
            public Optional<UUID> read(final FriendlyByteBuf je) {
                if (!je.readBoolean()) {
                    return (Optional<UUID>)Optional.empty();
                }
                return (Optional<UUID>)Optional.of(je.readUUID());
            }
            
            public Optional<UUID> copy(final Optional<UUID> optional) {
                return optional;
            }
        };
        COMPOUND_TAG = new EntityDataSerializer<CompoundTag>() {
            public void write(final FriendlyByteBuf je, final CompoundTag id) {
                je.writeNbt(id);
            }
            
            public CompoundTag read(final FriendlyByteBuf je) {
                return je.readNbt();
            }
            
            public CompoundTag copy(final CompoundTag id) {
                return id.copy();
            }
        };
        VILLAGER_DATA = new EntityDataSerializer<VillagerData>() {
            public void write(final FriendlyByteBuf je, final VillagerData avu) {
                je.writeVarInt(Registry.VILLAGER_TYPE.getId(avu.getType()));
                je.writeVarInt(Registry.VILLAGER_PROFESSION.getId(avu.getProfession()));
                je.writeVarInt(avu.getLevel());
            }
            
            public VillagerData read(final FriendlyByteBuf je) {
                return new VillagerData(Registry.VILLAGER_TYPE.byId(je.readVarInt()), Registry.VILLAGER_PROFESSION.byId(je.readVarInt()), je.readVarInt());
            }
            
            public VillagerData copy(final VillagerData avu) {
                return avu;
            }
        };
        OPTIONAL_UNSIGNED_INT = new EntityDataSerializer<OptionalInt>() {
            public void write(final FriendlyByteBuf je, final OptionalInt optionalInt) {
                je.writeVarInt(optionalInt.orElse(-1) + 1);
            }
            
            public OptionalInt read(final FriendlyByteBuf je) {
                final int integer3 = je.readVarInt();
                return (integer3 == 0) ? OptionalInt.empty() : OptionalInt.of(integer3 - 1);
            }
            
            public OptionalInt copy(final OptionalInt optionalInt) {
                return optionalInt;
            }
        };
        POSE = new EntityDataSerializer<Pose>() {
            public void write(final FriendlyByteBuf je, final Pose ajh) {
                je.writeEnum(ajh);
            }
            
            public Pose read(final FriendlyByteBuf je) {
                return je.<Pose>readEnum(Pose.class);
            }
            
            public Pose copy(final Pose ajh) {
                return ajh;
            }
        };
        registerSerializer(EntityDataSerializers.BYTE);
        registerSerializer(EntityDataSerializers.INT);
        registerSerializer(EntityDataSerializers.FLOAT);
        registerSerializer(EntityDataSerializers.STRING);
        registerSerializer(EntityDataSerializers.COMPONENT);
        registerSerializer(EntityDataSerializers.OPTIONAL_COMPONENT);
        registerSerializer(EntityDataSerializers.ITEM_STACK);
        registerSerializer(EntityDataSerializers.BOOLEAN);
        registerSerializer(EntityDataSerializers.ROTATIONS);
        registerSerializer(EntityDataSerializers.BLOCK_POS);
        registerSerializer(EntityDataSerializers.OPTIONAL_BLOCK_POS);
        registerSerializer(EntityDataSerializers.DIRECTION);
        registerSerializer(EntityDataSerializers.OPTIONAL_UUID);
        registerSerializer(EntityDataSerializers.BLOCK_STATE);
        registerSerializer(EntityDataSerializers.COMPOUND_TAG);
        registerSerializer(EntityDataSerializers.PARTICLE);
        registerSerializer(EntityDataSerializers.VILLAGER_DATA);
        registerSerializer(EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
        registerSerializer(EntityDataSerializers.POSE);
    }
}

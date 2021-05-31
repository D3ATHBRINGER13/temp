package net.minecraft.core.particles;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
    public static final Deserializer<BlockParticleOption> DESERIALIZER;
    private final ParticleType<BlockParticleOption> type;
    private final BlockState state;
    
    public BlockParticleOption(final ParticleType<BlockParticleOption> gg, final BlockState bvt) {
        this.type = gg;
        this.state = bvt;
    }
    
    public void writeToNetwork(final FriendlyByteBuf je) {
        je.writeVarInt(Block.BLOCK_STATE_REGISTRY.getId(this.state));
    }
    
    public String writeToString() {
        return new StringBuilder().append(Registry.PARTICLE_TYPE.getKey(this.getType())).append(" ").append(BlockStateParser.serialize(this.state)).toString();
    }
    
    public ParticleType<BlockParticleOption> getType() {
        return this.type;
    }
    
    public BlockState getState() {
        return this.state;
    }
    
    static {
        DESERIALIZER = new Deserializer<BlockParticleOption>() {
            public BlockParticleOption fromCommand(final ParticleType<BlockParticleOption> gg, final StringReader stringReader) throws CommandSyntaxException {
                stringReader.expect(' ');
                return new BlockParticleOption(gg, new BlockStateParser(stringReader, false).parse(false).getState());
            }
            
            public BlockParticleOption fromNetwork(final ParticleType<BlockParticleOption> gg, final FriendlyByteBuf je) {
                return new BlockParticleOption(gg, Block.BLOCK_STATE_REGISTRY.byId(je.readVarInt()));
            }
        };
    }
}

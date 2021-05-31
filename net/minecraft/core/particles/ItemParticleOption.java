package net.minecraft.core.particles;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemParser;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption implements ParticleOptions {
    public static final Deserializer<ItemParticleOption> DESERIALIZER;
    private final ParticleType<ItemParticleOption> type;
    private final ItemStack itemStack;
    
    public ItemParticleOption(final ParticleType<ItemParticleOption> gg, final ItemStack bcj) {
        this.type = gg;
        this.itemStack = bcj;
    }
    
    public void writeToNetwork(final FriendlyByteBuf je) {
        je.writeItem(this.itemStack);
    }
    
    public String writeToString() {
        return new StringBuilder().append(Registry.PARTICLE_TYPE.getKey(this.getType())).append(" ").append(new ItemInput(this.itemStack.getItem(), this.itemStack.getTag()).serialize()).toString();
    }
    
    public ParticleType<ItemParticleOption> getType() {
        return this.type;
    }
    
    public ItemStack getItem() {
        return this.itemStack;
    }
    
    static {
        DESERIALIZER = new Deserializer<ItemParticleOption>() {
            public ItemParticleOption fromCommand(final ParticleType<ItemParticleOption> gg, final StringReader stringReader) throws CommandSyntaxException {
                stringReader.expect(' ');
                final ItemParser dy4 = new ItemParser(stringReader, false).parse();
                final ItemStack bcj5 = new ItemInput(dy4.getItem(), dy4.getNbt()).createItemStack(1, false);
                return new ItemParticleOption(gg, bcj5);
            }
            
            public ItemParticleOption fromNetwork(final ParticleType<ItemParticleOption> gg, final FriendlyByteBuf je) {
                return new ItemParticleOption(gg, je.readItem());
            }
        };
    }
}

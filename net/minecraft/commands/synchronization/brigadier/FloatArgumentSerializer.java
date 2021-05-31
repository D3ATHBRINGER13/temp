package net.minecraft.commands.synchronization.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;

public class FloatArgumentSerializer implements ArgumentSerializer<FloatArgumentType> {
    public void serializeToNetwork(final FloatArgumentType floatArgumentType, final FriendlyByteBuf je) {
        final boolean boolean4 = floatArgumentType.getMinimum() != -3.4028235E38f;
        final boolean boolean5 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
        je.writeByte(BrigadierArgumentSerializers.createNumberFlags(boolean4, boolean5));
        if (boolean4) {
            je.writeFloat(floatArgumentType.getMinimum());
        }
        if (boolean5) {
            je.writeFloat(floatArgumentType.getMaximum());
        }
    }
    
    public FloatArgumentType deserializeFromNetwork(final FriendlyByteBuf je) {
        final byte byte3 = je.readByte();
        final float float4 = BrigadierArgumentSerializers.numberHasMin(byte3) ? je.readFloat() : -3.4028235E38f;
        final float float5 = BrigadierArgumentSerializers.numberHasMax(byte3) ? je.readFloat() : Float.MAX_VALUE;
        return FloatArgumentType.floatArg(float4, float5);
    }
    
    public void serializeToJson(final FloatArgumentType floatArgumentType, final JsonObject jsonObject) {
        if (floatArgumentType.getMinimum() != -3.4028235E38f) {
            jsonObject.addProperty("min", (Number)floatArgumentType.getMinimum());
        }
        if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)floatArgumentType.getMaximum());
        }
    }
}

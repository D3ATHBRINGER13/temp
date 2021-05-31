package net.minecraft.client.resources.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;

public class SimpleSoundInstance extends AbstractSoundInstance {
    public SimpleSoundInstance(final SoundEvent yo, final SoundSource yq, final float float3, final float float4, final BlockPos ew) {
        this(yo, yq, float3, float4, ew.getX() + 0.5f, ew.getY() + 0.5f, ew.getZ() + 0.5f);
    }
    
    public static SimpleSoundInstance forUI(final SoundEvent yo, final float float2) {
        return forUI(yo, float2, 0.25f);
    }
    
    public static SimpleSoundInstance forUI(final SoundEvent yo, final float float2, final float float3) {
        return new SimpleSoundInstance(yo.getLocation(), SoundSource.MASTER, float3, float2, false, 0, SoundInstance.Attenuation.NONE, 0.0f, 0.0f, 0.0f, true);
    }
    
    public static SimpleSoundInstance forMusic(final SoundEvent yo) {
        return new SimpleSoundInstance(yo.getLocation(), SoundSource.MUSIC, 1.0f, 1.0f, false, 0, SoundInstance.Attenuation.NONE, 0.0f, 0.0f, 0.0f, true);
    }
    
    public static SimpleSoundInstance forRecord(final SoundEvent yo, final float float2, final float float3, final float float4) {
        return new SimpleSoundInstance(yo, SoundSource.RECORDS, 4.0f, 1.0f, false, 0, SoundInstance.Attenuation.LINEAR, float2, float3, float4);
    }
    
    public SimpleSoundInstance(final SoundEvent yo, final SoundSource yq, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this(yo, yq, float3, float4, false, 0, SoundInstance.Attenuation.LINEAR, float5, float6, float7);
    }
    
    private SimpleSoundInstance(final SoundEvent yo, final SoundSource yq, final float float3, final float float4, final boolean boolean5, final int integer, final SoundInstance.Attenuation a, final float float8, final float float9, final float float10) {
        this(yo.getLocation(), yq, float3, float4, boolean5, integer, a, float8, float9, float10, false);
    }
    
    public SimpleSoundInstance(final ResourceLocation qv, final SoundSource yq, final float float3, final float float4, final boolean boolean5, final int integer, final SoundInstance.Attenuation a, final float float8, final float float9, final float float10, final boolean boolean11) {
        super(qv, yq);
        this.volume = float3;
        this.pitch = float4;
        this.x = float8;
        this.y = float9;
        this.z = float10;
        this.looping = boolean5;
        this.delay = integer;
        this.attenuation = a;
        this.relative = boolean11;
    }
}

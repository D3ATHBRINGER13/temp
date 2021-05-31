package net.minecraft.world.entity.player;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;

public class Abilities {
    public boolean invulnerable;
    public boolean flying;
    public boolean mayfly;
    public boolean instabuild;
    public boolean mayBuild;
    private float flyingSpeed;
    private float walkingSpeed;
    
    public Abilities() {
        this.mayBuild = true;
        this.flyingSpeed = 0.05f;
        this.walkingSpeed = 0.1f;
    }
    
    public void addSaveData(final CompoundTag id) {
        final CompoundTag id2 = new CompoundTag();
        id2.putBoolean("invulnerable", this.invulnerable);
        id2.putBoolean("flying", this.flying);
        id2.putBoolean("mayfly", this.mayfly);
        id2.putBoolean("instabuild", this.instabuild);
        id2.putBoolean("mayBuild", this.mayBuild);
        id2.putFloat("flySpeed", this.flyingSpeed);
        id2.putFloat("walkSpeed", this.walkingSpeed);
        id.put("abilities", (Tag)id2);
    }
    
    public void loadSaveData(final CompoundTag id) {
        if (id.contains("abilities", 10)) {
            final CompoundTag id2 = id.getCompound("abilities");
            this.invulnerable = id2.getBoolean("invulnerable");
            this.flying = id2.getBoolean("flying");
            this.mayfly = id2.getBoolean("mayfly");
            this.instabuild = id2.getBoolean("instabuild");
            if (id2.contains("flySpeed", 99)) {
                this.flyingSpeed = id2.getFloat("flySpeed");
                this.walkingSpeed = id2.getFloat("walkSpeed");
            }
            if (id2.contains("mayBuild", 1)) {
                this.mayBuild = id2.getBoolean("mayBuild");
            }
        }
    }
    
    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }
    
    public void setFlyingSpeed(final float float1) {
        this.flyingSpeed = float1;
    }
    
    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }
    
    public void setWalkingSpeed(final float float1) {
        this.walkingSpeed = float1;
    }
}

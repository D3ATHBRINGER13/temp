package net.minecraft.world.level;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;

public enum GameType {
    NOT_SET(-1, ""), 
    SURVIVAL(0, "survival"), 
    CREATIVE(1, "creative"), 
    ADVENTURE(2, "adventure"), 
    SPECTATOR(3, "spectator");
    
    private final int id;
    private final String name;
    
    private GameType(final int integer3, final String string4) {
        this.id = integer3;
        this.name = string4;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Component getDisplayName() {
        return new TranslatableComponent("gameMode." + this.name, new Object[0]);
    }
    
    public void updatePlayerAbilities(final Abilities awd) {
        if (this == GameType.CREATIVE) {
            awd.mayfly = true;
            awd.instabuild = true;
            awd.invulnerable = true;
        }
        else if (this == GameType.SPECTATOR) {
            awd.mayfly = true;
            awd.instabuild = false;
            awd.invulnerable = true;
            awd.flying = true;
        }
        else {
            awd.mayfly = false;
            awd.instabuild = false;
            awd.invulnerable = false;
            awd.flying = false;
        }
        awd.mayBuild = !this.isBlockPlacingRestricted();
    }
    
    public boolean isBlockPlacingRestricted() {
        return this == GameType.ADVENTURE || this == GameType.SPECTATOR;
    }
    
    public boolean isCreative() {
        return this == GameType.CREATIVE;
    }
    
    public boolean isSurvival() {
        return this == GameType.SURVIVAL || this == GameType.ADVENTURE;
    }
    
    public static GameType byId(final int integer) {
        return byId(integer, GameType.SURVIVAL);
    }
    
    public static GameType byId(final int integer, final GameType bho) {
        for (final GameType bho2 : values()) {
            if (bho2.id == integer) {
                return bho2;
            }
        }
        return bho;
    }
    
    public static GameType byName(final String string) {
        return byName(string, GameType.SURVIVAL);
    }
    
    public static GameType byName(final String string, final GameType bho) {
        for (final GameType bho2 : values()) {
            if (bho2.name.equals(string)) {
                return bho2;
            }
        }
        return bho;
    }
}

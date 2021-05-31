package net.minecraft.client.resources.sounds;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.player.LocalPlayer;

public class BubbleColumnAmbientSoundHandler implements AmbientSoundHandler {
    private final LocalPlayer player;
    private boolean wasInBubbleColumn;
    private boolean firstTick;
    
    public BubbleColumnAmbientSoundHandler(final LocalPlayer dmp) {
        this.firstTick = true;
        this.player = dmp;
    }
    
    public void tick() {
        final Level bhr2 = this.player.level;
        final BlockState bvt3 = bhr2.containsBlock(this.player.getBoundingBox().inflate(0.0, -0.4000000059604645, 0.0).deflate(0.001), Blocks.BUBBLE_COLUMN);
        if (bvt3 != null) {
            if (!this.wasInBubbleColumn && !this.firstTick && bvt3.getBlock() == Blocks.BUBBLE_COLUMN && !this.player.isSpectator()) {
                final boolean boolean4 = bvt3.<Boolean>getValue((Property<Boolean>)BubbleColumnBlock.DRAG_DOWN);
                if (boolean4) {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0f, 1.0f);
                }
                else {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0f, 1.0f);
                }
            }
            this.wasInBubbleColumn = true;
        }
        else {
            this.wasInBubbleColumn = false;
        }
        this.firstTick = false;
    }
}

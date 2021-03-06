package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;

public interface RenderChunkFactory {
    RenderChunk create(final Level bhr, final LevelRenderer dng);
}

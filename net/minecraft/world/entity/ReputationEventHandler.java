package net.minecraft.world.entity;

import net.minecraft.world.entity.ai.village.ReputationEventType;

public interface ReputationEventHandler {
    void onReputationEventFrom(final ReputationEventType aqm, final Entity aio);
}

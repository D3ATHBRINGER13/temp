package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;

public interface ClientGamePacketListener extends PacketListener {
    void handleAddEntity(final ClientboundAddEntityPacket kg);
    
    void handleAddExperienceOrb(final ClientboundAddExperienceOrbPacket kh);
    
    void handleAddGlobalEntity(final ClientboundAddGlobalEntityPacket ki);
    
    void handleAddMob(final ClientboundAddMobPacket kj);
    
    void handleAddObjective(final ClientboundSetObjectivePacket mz);
    
    void handleAddPainting(final ClientboundAddPaintingPacket kk);
    
    void handleAddPlayer(final ClientboundAddPlayerPacket kl);
    
    void handleAnimate(final ClientboundAnimatePacket km);
    
    void handleAwardStats(final ClientboundAwardStatsPacket kn);
    
    void handleAddOrRemoveRecipes(final ClientboundRecipePacket mg);
    
    void handleBlockDestruction(final ClientboundBlockDestructionPacket kp);
    
    void handleOpenSignEditor(final ClientboundOpenSignEditorPacket lz);
    
    void handleBlockEntityData(final ClientboundBlockEntityDataPacket kq);
    
    void handleBlockEvent(final ClientboundBlockEventPacket kr);
    
    void handleBlockUpdate(final ClientboundBlockUpdatePacket ks);
    
    void handleChat(final ClientboundChatPacket kv);
    
    void handleChunkBlocksUpdate(final ClientboundChunkBlocksUpdatePacket kw);
    
    void handleMapItemData(final ClientboundMapItemDataPacket lt);
    
    void handleContainerAck(final ClientboundContainerAckPacket kz);
    
    void handleContainerClose(final ClientboundContainerClosePacket la);
    
    void handleContainerContent(final ClientboundContainerSetContentPacket lb);
    
    void handleHorseScreenOpen(final ClientboundHorseScreenOpenPacket lm);
    
    void handleContainerSetData(final ClientboundContainerSetDataPacket lc);
    
    void handleContainerSetSlot(final ClientboundContainerSetSlotPacket ld);
    
    void handleCustomPayload(final ClientboundCustomPayloadPacket lf);
    
    void handleDisconnect(final ClientboundDisconnectPacket lh);
    
    void handleEntityEvent(final ClientboundEntityEventPacket li);
    
    void handleEntityLinkPacket(final ClientboundSetEntityLinkPacket mu);
    
    void handleSetEntityPassengersPacket(final ClientboundSetPassengersPacket na);
    
    void handleExplosion(final ClientboundExplodePacket lj);
    
    void handleGameEvent(final ClientboundGameEventPacket ll);
    
    void handleKeepAlive(final ClientboundKeepAlivePacket ln);
    
    void handleLevelChunk(final ClientboundLevelChunkPacket lo);
    
    void handleForgetLevelChunk(final ClientboundForgetLevelChunkPacket lk);
    
    void handleLevelEvent(final ClientboundLevelEventPacket lp);
    
    void handleLogin(final ClientboundLoginPacket ls);
    
    void handleMoveEntity(final ClientboundMoveEntityPacket lv);
    
    void handleMovePlayer(final ClientboundPlayerPositionPacket mf);
    
    void handleParticleEvent(final ClientboundLevelParticlesPacket lq);
    
    void handlePlayerAbilities(final ClientboundPlayerAbilitiesPacket mb);
    
    void handlePlayerInfo(final ClientboundPlayerInfoPacket md);
    
    void handleRemoveEntity(final ClientboundRemoveEntitiesPacket mh);
    
    void handleRemoveMobEffect(final ClientboundRemoveMobEffectPacket mi);
    
    void handleRespawn(final ClientboundRespawnPacket mk);
    
    void handleRotateMob(final ClientboundRotateHeadPacket ml);
    
    void handleSetCarriedItem(final ClientboundSetCarriedItemPacket mp);
    
    void handleSetDisplayObjective(final ClientboundSetDisplayObjectivePacket ms);
    
    void handleSetEntityData(final ClientboundSetEntityDataPacket mt);
    
    void handleSetEntityMotion(final ClientboundSetEntityMotionPacket mv);
    
    void handleSetEquippedItem(final ClientboundSetEquippedItemPacket mw);
    
    void handleSetExperience(final ClientboundSetExperiencePacket mx);
    
    void handleSetHealth(final ClientboundSetHealthPacket my);
    
    void handleSetPlayerTeamPacket(final ClientboundSetPlayerTeamPacket nb);
    
    void handleSetScore(final ClientboundSetScorePacket nc);
    
    void handleSetSpawn(final ClientboundSetSpawnPositionPacket nd);
    
    void handleSetTime(final ClientboundSetTimePacket ne);
    
    void handleSoundEvent(final ClientboundSoundPacket nh);
    
    void handleSoundEntityEvent(final ClientboundSoundEntityPacket ng);
    
    void handleCustomSoundEvent(final ClientboundCustomSoundPacket lg);
    
    void handleTakeItemEntity(final ClientboundTakeItemEntityPacket nl);
    
    void handleTeleportEntity(final ClientboundTeleportEntityPacket nm);
    
    void handleUpdateAttributes(final ClientboundUpdateAttributesPacket no);
    
    void handleUpdateMobEffect(final ClientboundUpdateMobEffectPacket np);
    
    void handleUpdateTags(final ClientboundUpdateTagsPacket nr);
    
    void handlePlayerCombat(final ClientboundPlayerCombatPacket mc);
    
    void handleChangeDifficulty(final ClientboundChangeDifficultyPacket ku);
    
    void handleSetCamera(final ClientboundSetCameraPacket mo);
    
    void handleSetBorder(final ClientboundSetBorderPacket mn);
    
    void handleSetTitles(final ClientboundSetTitlesPacket nf);
    
    void handleTabListCustomisation(final ClientboundTabListPacket nj);
    
    void handleResourcePack(final ClientboundResourcePackPacket mj);
    
    void handleBossUpdate(final ClientboundBossEventPacket kt);
    
    void handleItemCooldown(final ClientboundCooldownPacket le);
    
    void handleMoveVehicle(final ClientboundMoveVehiclePacket lw);
    
    void handleUpdateAdvancementsPacket(final ClientboundUpdateAdvancementsPacket nn);
    
    void handleSelectAdvancementsTab(final ClientboundSelectAdvancementsTabPacket mm);
    
    void handlePlaceRecipe(final ClientboundPlaceGhostRecipePacket ma);
    
    void handleCommands(final ClientboundCommandsPacket ky);
    
    void handleStopSoundEvent(final ClientboundStopSoundPacket ni);
    
    void handleCommandSuggestions(final ClientboundCommandSuggestionsPacket kx);
    
    void handleUpdateRecipes(final ClientboundUpdateRecipesPacket nq);
    
    void handleLookAt(final ClientboundPlayerLookAtPacket me);
    
    void handleTagQueryPacket(final ClientboundTagQueryPacket nk);
    
    void handleLightUpdatePacked(final ClientboundLightUpdatePacket lr);
    
    void handleOpenBook(final ClientboundOpenBookPacket lx);
    
    void handleOpenScreen(final ClientboundOpenScreenPacket ly);
    
    void handleMerchantOffers(final ClientboundMerchantOffersPacket lu);
    
    void handleSetChunkCacheRadius(final ClientboundSetChunkCacheRadiusPacket mr);
    
    void handleSetChunkCacheCenter(final ClientboundSetChunkCacheCenterPacket mq);
    
    void handleBlockBreakAck(final ClientboundBlockBreakAckPacket ko);
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;

public interface ServerGamePacketListener extends PacketListener {
    void handleAnimate(final ServerboundSwingPacket pi);
    
    void handleChat(final ServerboundChatPacket ny);
    
    void handleClientCommand(final ServerboundClientCommandPacket nz);
    
    void handleClientInformation(final ServerboundClientInformationPacket oa);
    
    void handleContainerAck(final ServerboundContainerAckPacket oc);
    
    void handleContainerButtonClick(final ServerboundContainerButtonClickPacket od);
    
    void handleContainerClick(final ServerboundContainerClickPacket oe);
    
    void handlePlaceRecipe(final ServerboundPlaceRecipePacket oq);
    
    void handleContainerClose(final ServerboundContainerClosePacket of);
    
    void handleCustomPayload(final ServerboundCustomPayloadPacket og);
    
    void handleInteract(final ServerboundInteractPacket oj);
    
    void handleKeepAlive(final ServerboundKeepAlivePacket ok);
    
    void handleMovePlayer(final ServerboundMovePlayerPacket om);
    
    void handlePlayerAbilities(final ServerboundPlayerAbilitiesPacket or);
    
    void handlePlayerAction(final ServerboundPlayerActionPacket os);
    
    void handlePlayerCommand(final ServerboundPlayerCommandPacket ot);
    
    void handlePlayerInput(final ServerboundPlayerInputPacket ou);
    
    void handleSetCarriedItem(final ServerboundSetCarriedItemPacket pb);
    
    void handleSetCreativeModeSlot(final ServerboundSetCreativeModeSlotPacket pe);
    
    void handleSignUpdate(final ServerboundSignUpdatePacket ph);
    
    void handleUseItemOn(final ServerboundUseItemOnPacket pk);
    
    void handleUseItem(final ServerboundUseItemPacket pl);
    
    void handleTeleportToEntityPacket(final ServerboundTeleportToEntityPacket pj);
    
    void handleResourcePackResponse(final ServerboundResourcePackPacket ox);
    
    void handlePaddleBoat(final ServerboundPaddleBoatPacket oo);
    
    void handleMoveVehicle(final ServerboundMoveVehiclePacket on);
    
    void handleAcceptTeleportPacket(final ServerboundAcceptTeleportationPacket nv);
    
    void handleRecipeBookUpdatePacket(final ServerboundRecipeBookUpdatePacket ov);
    
    void handleSeenAdvancements(final ServerboundSeenAdvancementsPacket oy);
    
    void handleCustomCommandSuggestions(final ServerboundCommandSuggestionPacket ob);
    
    void handleSetCommandBlock(final ServerboundSetCommandBlockPacket pc);
    
    void handleSetCommandMinecart(final ServerboundSetCommandMinecartPacket pd);
    
    void handlePickItem(final ServerboundPickItemPacket op);
    
    void handleRenameItem(final ServerboundRenameItemPacket ow);
    
    void handleSetBeaconPacket(final ServerboundSetBeaconPacket pa);
    
    void handleSetStructureBlock(final ServerboundSetStructureBlockPacket pg);
    
    void handleSelectTrade(final ServerboundSelectTradePacket oz);
    
    void handleEditBook(final ServerboundEditBookPacket oh);
    
    void handleEntityTagQuery(final ServerboundEntityTagQuery oi);
    
    void handleBlockEntityTagQuery(final ServerboundBlockEntityTagQuery nw);
    
    void handleSetJigsawBlock(final ServerboundSetJigsawBlockPacket pf);
    
    void handleChangeDifficulty(final ServerboundChangeDifficultyPacket nx);
    
    void handleLockDifficulty(final ServerboundLockDifficultyPacket ol);
}

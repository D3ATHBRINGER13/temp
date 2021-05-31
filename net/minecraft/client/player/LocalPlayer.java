package net.minecraft.client.player;

import java.util.stream.Stream;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import java.util.Set;
import java.util.Collections;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.world.item.Items;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookUpdatePacket;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.util.Mth;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import java.util.Iterator;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.world.level.dimension.DimensionType;
import com.google.common.collect.Lists;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.stats.StatsCounter;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class LocalPlayer extends AbstractClientPlayer {
    public final ClientPacketListener connection;
    private final StatsCounter stats;
    private final ClientRecipeBook recipeBook;
    private final List<AmbientSoundHandler> ambientSoundHandlers;
    private int permissionLevel;
    private double xLast;
    private double yLast1;
    private double zLast;
    private float yRotLast;
    private float xRotLast;
    private boolean lastOnGround;
    private boolean wasTryingToSneak;
    private boolean wasSprinting;
    private int positionReminder;
    private boolean flashOnSetHealth;
    private String serverBrand;
    public Input input;
    protected final Minecraft minecraft;
    protected int sprintTriggerTime;
    public int sprintTime;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;
    private int jumpRidingTicks;
    private float jumpRidingScale;
    public float portalTime;
    public float oPortalTime;
    private boolean startedUsingItem;
    private InteractionHand usingItemHand;
    private boolean handsBusy;
    private boolean autoJumpEnabled;
    private int autoJumpTime;
    private boolean wasFallFlying;
    private int waterVisionTime;
    
    public LocalPlayer(final Minecraft cyc, final MultiPlayerLevel dkf, final ClientPacketListener dkc, final StatsCounter yz, final ClientRecipeBook cxr) {
        super(dkf, dkc.getLocalGameProfile());
        this.ambientSoundHandlers = (List<AmbientSoundHandler>)Lists.newArrayList();
        this.permissionLevel = 0;
        this.autoJumpEnabled = true;
        this.connection = dkc;
        this.stats = yz;
        this.recipeBook = cxr;
        this.minecraft = cyc;
        this.dimension = DimensionType.OVERWORLD;
        this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, cyc.getSoundManager()));
        this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        return false;
    }
    
    @Override
    public void heal(final float float1) {
    }
    
    @Override
    public boolean startRiding(final Entity aio, final boolean boolean2) {
        if (!super.startRiding(aio, boolean2)) {
            return false;
        }
        if (aio instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)aio));
        }
        if (aio instanceof Boat) {
            this.yRotO = aio.yRot;
            this.yRot = aio.yRot;
            this.setYHeadRot(aio.yRot);
        }
        return true;
    }
    
    @Override
    public void stopRiding() {
        super.stopRiding();
        this.handsBusy = false;
    }
    
    @Override
    public float getViewXRot(final float float1) {
        return this.xRot;
    }
    
    @Override
    public float getViewYRot(final float float1) {
        if (this.isPassenger()) {
            return super.getViewYRot(float1);
        }
        return this.yRot;
    }
    
    @Override
    public void tick() {
        if (!this.level.hasChunkAt(new BlockPos(this.x, 0.0, this.z))) {
            return;
        }
        super.tick();
        if (this.isPassenger()) {
            this.connection.send(new ServerboundMovePlayerPacket.Rot(this.yRot, this.xRot, this.onGround));
            this.connection.send(new ServerboundPlayerInputPacket(this.xxa, this.zza, this.input.jumping, this.input.sneakKeyDown));
            final Entity aio2 = this.getRootVehicle();
            if (aio2 != this && aio2.isControlledByLocalInstance()) {
                this.connection.send(new ServerboundMoveVehiclePacket(aio2));
            }
        }
        else {
            this.sendPosition();
        }
        for (final AmbientSoundHandler dze3 : this.ambientSoundHandlers) {
            dze3.tick();
        }
    }
    
    private void sendPosition() {
        final boolean boolean2 = this.isSprinting();
        if (boolean2 != this.wasSprinting) {
            final ServerboundPlayerCommandPacket.Action a3 = boolean2 ? ServerboundPlayerCommandPacket.Action.START_SPRINTING : ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
            this.connection.send(new ServerboundPlayerCommandPacket(this, a3));
            this.wasSprinting = boolean2;
        }
        final boolean boolean3 = this.isTryingToSneak();
        if (boolean3 != this.wasTryingToSneak) {
            final ServerboundPlayerCommandPacket.Action a4 = boolean3 ? ServerboundPlayerCommandPacket.Action.START_SNEAKING : ServerboundPlayerCommandPacket.Action.STOP_SNEAKING;
            this.connection.send(new ServerboundPlayerCommandPacket(this, a4));
            this.wasTryingToSneak = boolean3;
        }
        if (this.isControlledCamera()) {
            final AABB csc4 = this.getBoundingBox();
            final double double5 = this.x - this.xLast;
            final double double6 = csc4.minY - this.yLast1;
            final double double7 = this.z - this.zLast;
            final double double8 = this.yRot - this.yRotLast;
            final double double9 = this.xRot - this.xRotLast;
            ++this.positionReminder;
            boolean boolean4 = double5 * double5 + double6 * double6 + double7 * double7 > 9.0E-4 || this.positionReminder >= 20;
            final boolean boolean5 = double8 != 0.0 || double9 != 0.0;
            if (this.isPassenger()) {
                final Vec3 csi17 = this.getDeltaMovement();
                this.connection.send(new ServerboundMovePlayerPacket.PosRot(csi17.x, -999.0, csi17.z, this.yRot, this.xRot, this.onGround));
                boolean4 = false;
            }
            else if (boolean4 && boolean5) {
                this.connection.send(new ServerboundMovePlayerPacket.PosRot(this.x, csc4.minY, this.z, this.yRot, this.xRot, this.onGround));
            }
            else if (boolean4) {
                this.connection.send(new ServerboundMovePlayerPacket.Pos(this.x, csc4.minY, this.z, this.onGround));
            }
            else if (boolean5) {
                this.connection.send(new ServerboundMovePlayerPacket.Rot(this.yRot, this.xRot, this.onGround));
            }
            else if (this.lastOnGround != this.onGround) {
                this.connection.send(new ServerboundMovePlayerPacket(this.onGround));
            }
            if (boolean4) {
                this.xLast = this.x;
                this.yLast1 = csc4.minY;
                this.zLast = this.z;
                this.positionReminder = 0;
            }
            if (boolean5) {
                this.yRotLast = this.yRot;
                this.xRotLast = this.xRot;
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.minecraft.options.autoJump;
        }
    }
    
    @Nullable
    @Override
    public ItemEntity drop(final boolean boolean1) {
        final ServerboundPlayerActionPacket.Action a3 = boolean1 ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM;
        this.connection.send(new ServerboundPlayerActionPacket(a3, BlockPos.ZERO, Direction.DOWN));
        this.inventory.removeItem(this.inventory.selected, (boolean1 && !this.inventory.getSelected().isEmpty()) ? this.inventory.getSelected().getCount() : 1);
        return null;
    }
    
    public void chat(final String string) {
        this.connection.send(new ServerboundChatPacket(string));
    }
    
    @Override
    public void swing(final InteractionHand ahi) {
        super.swing(ahi);
        this.connection.send(new ServerboundSwingPacket(ahi));
    }
    
    @Override
    public void respawn() {
        this.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
    }
    
    @Override
    protected void actuallyHurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return;
        }
        this.setHealth(this.getHealth() - float2);
    }
    
    public void closeContainer() {
        this.connection.send(new ServerboundContainerClosePacket(this.containerMenu.containerId));
        this.clientSideCloseContainer();
    }
    
    public void clientSideCloseContainer() {
        this.inventory.setCarried(ItemStack.EMPTY);
        super.closeContainer();
        this.minecraft.setScreen(null);
    }
    
    public void hurtTo(final float float1) {
        if (this.flashOnSetHealth) {
            final float float2 = this.getHealth() - float1;
            if (float2 <= 0.0f) {
                this.setHealth(float1);
                if (float2 < 0.0f) {
                    this.invulnerableTime = 10;
                }
            }
            else {
                this.lastHurt = float2;
                this.setHealth(this.getHealth());
                this.invulnerableTime = 20;
                this.actuallyHurt(DamageSource.GENERIC, float2);
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
            }
        }
        else {
            this.setHealth(float1);
            this.flashOnSetHealth = true;
        }
    }
    
    @Override
    public void onUpdateAbilities() {
        this.connection.send(new ServerboundPlayerAbilitiesPacket(this.abilities));
    }
    
    @Override
    public boolean isLocalPlayer() {
        return true;
    }
    
    protected void sendRidingJump() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(this.getJumpRidingScale() * 100.0f)));
    }
    
    public void sendOpenInventory() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
    }
    
    public void setServerBrand(final String string) {
        this.serverBrand = string;
    }
    
    public String getServerBrand() {
        return this.serverBrand;
    }
    
    public StatsCounter getStats() {
        return this.stats;
    }
    
    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }
    
    public void removeRecipeHighlight(final Recipe<?> ber) {
        if (this.recipeBook.willHighlight(ber)) {
            this.recipeBook.removeHighlight(ber);
            this.connection.send(new ServerboundRecipeBookUpdatePacket(ber));
        }
    }
    
    @Override
    protected int getPermissionLevel() {
        return this.permissionLevel;
    }
    
    public void setPermissionLevel(final int integer) {
        this.permissionLevel = integer;
    }
    
    @Override
    public void displayClientMessage(final Component jo, final boolean boolean2) {
        if (boolean2) {
            this.minecraft.gui.setOverlayMessage(jo, false);
        }
        else {
            this.minecraft.gui.getChat().addMessage(jo);
        }
    }
    
    @Override
    protected void checkInBlock(final double double1, final double double2, final double double3) {
        final BlockPos ew8 = new BlockPos(double1, double2, double3);
        if (this.blocked(ew8)) {
            final double double4 = double1 - ew8.getX();
            final double double5 = double3 - ew8.getZ();
            Direction fb13 = null;
            double double6 = 9999.0;
            if (!this.blocked(ew8.west()) && double4 < double6) {
                double6 = double4;
                fb13 = Direction.WEST;
            }
            if (!this.blocked(ew8.east()) && 1.0 - double4 < double6) {
                double6 = 1.0 - double4;
                fb13 = Direction.EAST;
            }
            if (!this.blocked(ew8.north()) && double5 < double6) {
                double6 = double5;
                fb13 = Direction.NORTH;
            }
            if (!this.blocked(ew8.south()) && 1.0 - double5 < double6) {
                double6 = 1.0 - double5;
                fb13 = Direction.SOUTH;
            }
            if (fb13 != null) {
                final Vec3 csi16 = this.getDeltaMovement();
                switch (fb13) {
                    case WEST: {
                        this.setDeltaMovement(-0.1, csi16.y, csi16.z);
                        break;
                    }
                    case EAST: {
                        this.setDeltaMovement(0.1, csi16.y, csi16.z);
                        break;
                    }
                    case NORTH: {
                        this.setDeltaMovement(csi16.x, csi16.y, -0.1);
                        break;
                    }
                    case SOUTH: {
                        this.setDeltaMovement(csi16.x, csi16.y, 0.1);
                        break;
                    }
                }
            }
        }
    }
    
    private boolean blocked(final BlockPos ew) {
        final AABB csc3 = this.getBoundingBox();
        final BlockPos.MutableBlockPos a4 = new BlockPos.MutableBlockPos(ew);
        for (int integer5 = Mth.floor(csc3.minY); integer5 < Mth.ceil(csc3.maxY); ++integer5) {
            a4.setY(integer5);
            if (!this.freeAt(a4)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setSprinting(final boolean boolean1) {
        super.setSprinting(boolean1);
        this.sprintTime = 0;
    }
    
    public void setExperienceValues(final float float1, final int integer2, final int integer3) {
        this.experienceProgress = float1;
        this.totalExperience = integer2;
        this.experienceLevel = integer3;
    }
    
    @Override
    public void sendMessage(final Component jo) {
        this.minecraft.gui.getChat().addMessage(jo);
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 >= 24 && byte1 <= 28) {
            this.setPermissionLevel(byte1 - 24);
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    @Override
    public void playSound(final SoundEvent yo, final float float2, final float float3) {
        this.level.playLocalSound(this.x, this.y, this.z, yo, this.getSoundSource(), float2, float3, false);
    }
    
    @Override
    public void playNotifySound(final SoundEvent yo, final SoundSource yq, final float float3, final float float4) {
        this.level.playLocalSound(this.x, this.y, this.z, yo, yq, float3, float4, false);
    }
    
    @Override
    public boolean isEffectiveAi() {
        return true;
    }
    
    @Override
    public void startUsingItem(final InteractionHand ahi) {
        final ItemStack bcj3 = this.getItemInHand(ahi);
        if (bcj3.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.startUsingItem(ahi);
        this.startedUsingItem = true;
        this.usingItemHand = ahi;
    }
    
    @Override
    public boolean isUsingItem() {
        return this.startedUsingItem;
    }
    
    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        this.startedUsingItem = false;
    }
    
    @Override
    public InteractionHand getUsedItemHand() {
        return this.usingItemHand;
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        super.onSyncedDataUpdated(qk);
        if (LocalPlayer.DATA_LIVING_ENTITY_FLAGS.equals(qk)) {
            final boolean boolean3 = (this.entityData.<Byte>get(LocalPlayer.DATA_LIVING_ENTITY_FLAGS) & 0x1) > 0;
            final InteractionHand ahi4 = ((this.entityData.<Byte>get(LocalPlayer.DATA_LIVING_ENTITY_FLAGS) & 0x2) > 0) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            if (boolean3 && !this.startedUsingItem) {
                this.startUsingItem(ahi4);
            }
            else if (!boolean3 && this.startedUsingItem) {
                this.stopUsingItem();
            }
        }
        if (LocalPlayer.DATA_SHARED_FLAGS_ID.equals(qk) && this.isFallFlying() && !this.wasFallFlying) {
            this.minecraft.getSoundManager().play(new ElytraOnPlayerSoundInstance(this));
        }
    }
    
    public boolean isRidingJumpable() {
        final Entity aio2 = this.getVehicle();
        return this.isPassenger() && aio2 instanceof PlayerRideableJumping && ((PlayerRideableJumping)aio2).canJump();
    }
    
    public float getJumpRidingScale() {
        return this.jumpRidingScale;
    }
    
    @Override
    public void openTextEdit(final SignBlockEntity bus) {
        this.minecraft.setScreen(new SignEditScreen(bus));
    }
    
    @Override
    public void openMinecartCommandBlock(final BaseCommandBlock bgx) {
        this.minecraft.setScreen(new MinecartCommandBlockEditScreen(bgx));
    }
    
    @Override
    public void openCommandBlock(final CommandBlockEntity bub) {
        this.minecraft.setScreen(new CommandBlockEditScreen(bub));
    }
    
    @Override
    public void openStructureBlock(final StructureBlockEntity buw) {
        this.minecraft.setScreen(new StructureBlockEditScreen(buw));
    }
    
    @Override
    public void openJigsawBlock(final JigsawBlockEntity bum) {
        this.minecraft.setScreen(new JigsawBlockEditScreen(bum));
    }
    
    @Override
    public void openItemGui(final ItemStack bcj, final InteractionHand ahi) {
        final Item bce4 = bcj.getItem();
        if (bce4 == Items.WRITABLE_BOOK) {
            this.minecraft.setScreen(new BookEditScreen(this, bcj, ahi));
        }
    }
    
    @Override
    public void crit(final Entity aio) {
        this.minecraft.particleEngine.createTrackingEmitter(aio, ParticleTypes.CRIT);
    }
    
    @Override
    public void magicCrit(final Entity aio) {
        this.minecraft.particleEngine.createTrackingEmitter(aio, ParticleTypes.ENCHANTED_HIT);
    }
    
    @Override
    public boolean isSneaking() {
        return this.isTryingToSneak();
    }
    
    public boolean isTryingToSneak() {
        return this.input != null && this.input.sneakKeyDown;
    }
    
    @Override
    public boolean isVisuallySneaking() {
        return !this.abilities.flying && !this.isSwimming() && this.canEnterPose(Pose.SNEAKING) && (this.isTryingToSneak() || !this.canEnterPose(Pose.STANDING));
    }
    
    public void serverAiStep() {
        super.serverAiStep();
        if (this.isControlledCamera()) {
            this.xxa = this.input.leftImpulse;
            this.zza = this.input.forwardImpulse;
            this.jumping = this.input.jumping;
            this.yBobO = this.yBob;
            this.xBobO = this.xBob;
            this.xBob += (float)((this.xRot - this.xBob) * 0.5);
            this.yBob += (float)((this.yRot - this.yBob) * 0.5);
        }
    }
    
    protected boolean isControlledCamera() {
        return this.minecraft.getCameraEntity() == this;
    }
    
    @Override
    public void aiStep() {
        ++this.sprintTime;
        if (this.sprintTriggerTime > 0) {
            --this.sprintTriggerTime;
        }
        this.handleNetherPortalClient();
        final boolean boolean2 = this.input.jumping;
        final boolean boolean3 = this.input.sneakKeyDown;
        final boolean boolean4 = this.hasEnoughImpulseToStartSprinting();
        final boolean boolean5 = this.isVisuallySneaking() || this.isVisuallyCrawling();
        this.input.tick(boolean5, this.isSpectator());
        this.minecraft.getTutorial().onInput(this.input);
        if (this.isUsingItem() && !this.isPassenger()) {
            final Input input = this.input;
            input.leftImpulse *= 0.2f;
            final Input input2 = this.input;
            input2.forwardImpulse *= 0.2f;
            this.sprintTriggerTime = 0;
        }
        boolean boolean6 = false;
        if (this.autoJumpTime > 0) {
            --this.autoJumpTime;
            boolean6 = true;
            this.input.jumping = true;
        }
        if (!this.noPhysics) {
            final AABB csc7 = this.getBoundingBox();
            this.checkInBlock(this.x - this.getBbWidth() * 0.35, csc7.minY + 0.5, this.z + this.getBbWidth() * 0.35);
            this.checkInBlock(this.x - this.getBbWidth() * 0.35, csc7.minY + 0.5, this.z - this.getBbWidth() * 0.35);
            this.checkInBlock(this.x + this.getBbWidth() * 0.35, csc7.minY + 0.5, this.z - this.getBbWidth() * 0.35);
            this.checkInBlock(this.x + this.getBbWidth() * 0.35, csc7.minY + 0.5, this.z + this.getBbWidth() * 0.35);
        }
        final boolean boolean7 = this.getFoodData().getFoodLevel() > 6.0f || this.abilities.mayfly;
        if ((this.onGround || this.isUnderWater()) && !boolean3 && !boolean4 && this.hasEnoughImpulseToStartSprinting() && !this.isSprinting() && boolean7 && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS)) {
            if (this.sprintTriggerTime > 0 || this.minecraft.options.keySprint.isDown()) {
                this.setSprinting(true);
            }
            else {
                this.sprintTriggerTime = 7;
            }
        }
        if (!this.isSprinting() && (!this.isInWater() || this.isUnderWater()) && this.hasEnoughImpulseToStartSprinting() && boolean7 && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS) && this.minecraft.options.keySprint.isDown()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            final boolean boolean8 = !this.input.hasForwardImpulse() || !boolean7;
            final boolean boolean9 = boolean8 || this.horizontalCollision || (this.isInWater() && !this.isUnderWater());
            if (this.isSwimming()) {
                if ((!this.onGround && !this.input.sneakKeyDown && boolean8) || !this.isInWater()) {
                    this.setSprinting(false);
                }
            }
            else if (boolean9) {
                this.setSprinting(false);
            }
        }
        if (this.abilities.mayfly) {
            if (this.minecraft.gameMode.isAlwaysFlying()) {
                if (!this.abilities.flying) {
                    this.abilities.flying = true;
                    this.onUpdateAbilities();
                }
            }
            else if (!boolean2 && this.input.jumping && !boolean6) {
                if (this.jumpTriggerTime == 0) {
                    this.jumpTriggerTime = 7;
                }
                else if (!this.isSwimming()) {
                    this.abilities.flying = !this.abilities.flying;
                    this.onUpdateAbilities();
                    this.jumpTriggerTime = 0;
                }
            }
        }
        if (this.input.jumping && !boolean2 && !this.onGround && this.getDeltaMovement().y < 0.0 && !this.isFallFlying() && !this.abilities.flying) {
            final ItemStack bcj8 = this.getItemBySlot(EquipmentSlot.CHEST);
            if (bcj8.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(bcj8)) {
                this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }
        }
        this.wasFallFlying = this.isFallFlying();
        if (this.isInWater() && this.input.sneakKeyDown) {
            this.goDownInWater();
        }
        if (this.isUnderLiquid(FluidTags.WATER)) {
            final int integer8 = this.isSpectator() ? 10 : 1;
            this.waterVisionTime = Mth.clamp(this.waterVisionTime + integer8, 0, 600);
        }
        else if (this.waterVisionTime > 0) {
            this.isUnderLiquid(FluidTags.WATER);
            this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
        }
        if (this.abilities.flying && this.isControlledCamera()) {
            int integer8 = 0;
            if (this.input.sneakKeyDown) {
                final Input input3 = this.input;
                input3.leftImpulse /= (float)0.3;
                final Input input4 = this.input;
                input4.forwardImpulse /= (float)0.3;
                --integer8;
            }
            if (this.input.jumping) {
                ++integer8;
            }
            if (integer8 != 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, integer8 * this.abilities.getFlyingSpeed() * 3.0f, 0.0));
            }
        }
        if (this.isRidingJumpable()) {
            final PlayerRideableJumping ajg8 = (PlayerRideableJumping)this.getVehicle();
            if (this.jumpRidingTicks < 0) {
                ++this.jumpRidingTicks;
                if (this.jumpRidingTicks == 0) {
                    this.jumpRidingScale = 0.0f;
                }
            }
            if (boolean2 && !this.input.jumping) {
                this.jumpRidingTicks = -10;
                ajg8.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0f));
                this.sendRidingJump();
            }
            else if (!boolean2 && this.input.jumping) {
                this.jumpRidingTicks = 0;
                this.jumpRidingScale = 0.0f;
            }
            else if (boolean2) {
                ++this.jumpRidingTicks;
                if (this.jumpRidingTicks < 10) {
                    this.jumpRidingScale = this.jumpRidingTicks * 0.1f;
                }
                else {
                    this.jumpRidingScale = 0.8f + 2.0f / (this.jumpRidingTicks - 9) * 0.1f;
                }
            }
        }
        else {
            this.jumpRidingScale = 0.0f;
        }
        super.aiStep();
        if (this.onGround && this.abilities.flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            this.abilities.flying = false;
            this.onUpdateAbilities();
        }
    }
    
    private void handleNetherPortalClient() {
        this.oPortalTime = this.portalTime;
        if (this.isInsidePortal) {
            if (this.minecraft.screen != null && !this.minecraft.screen.isPauseScreen()) {
                if (this.minecraft.screen instanceof AbstractContainerScreen) {
                    this.closeContainer();
                }
                this.minecraft.setScreen(null);
            }
            if (this.portalTime == 0.0f) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4f + 0.8f));
            }
            this.portalTime += 0.0125f;
            if (this.portalTime >= 1.0f) {
                this.portalTime = 1.0f;
            }
            this.isInsidePortal = false;
        }
        else if (this.hasEffect(MobEffects.CONFUSION) && this.getEffect(MobEffects.CONFUSION).getDuration() > 60) {
            this.portalTime += 0.006666667f;
            if (this.portalTime > 1.0f) {
                this.portalTime = 1.0f;
            }
        }
        else {
            if (this.portalTime > 0.0f) {
                this.portalTime -= 0.05f;
            }
            if (this.portalTime < 0.0f) {
                this.portalTime = 0.0f;
            }
        }
        this.processDimensionDelay();
    }
    
    @Override
    public void rideTick() {
        super.rideTick();
        this.handsBusy = false;
        if (this.getVehicle() instanceof Boat) {
            final Boat axw2 = (Boat)this.getVehicle();
            axw2.setInput(this.input.left, this.input.right, this.input.up, this.input.down);
            this.handsBusy |= (this.input.left || this.input.right || this.input.up || this.input.down);
        }
    }
    
    public boolean isHandsBusy() {
        return this.handsBusy;
    }
    
    @Nullable
    @Override
    public MobEffectInstance removeEffectNoUpdate(@Nullable final MobEffect aig) {
        if (aig == MobEffects.CONFUSION) {
            this.oPortalTime = 0.0f;
            this.portalTime = 0.0f;
        }
        return super.removeEffectNoUpdate(aig);
    }
    
    @Override
    public void move(final MoverType ajc, final Vec3 csi) {
        final double double4 = this.x;
        final double double5 = this.z;
        super.move(ajc, csi);
        this.updateAutoJump((float)(this.x - double4), (float)(this.z - double5));
    }
    
    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }
    
    protected void updateAutoJump(final float float1, final float float2) {
        if (!this.isAutoJumpEnabled()) {
            return;
        }
        if (this.autoJumpTime > 0 || !this.onGround || this.isSneaking() || this.isPassenger()) {
            return;
        }
        final Vec2 csh4 = this.input.getMoveVector();
        if (csh4.x == 0.0f && csh4.y == 0.0f) {
            return;
        }
        final Vec3 csi5 = new Vec3(this.x, this.getBoundingBox().minY, this.z);
        final Vec3 csi6 = new Vec3(this.x + float1, this.getBoundingBox().minY, this.z + float2);
        Vec3 csi7 = new Vec3(float1, 0.0, float2);
        final float float3 = this.getSpeed();
        float float4 = (float)csi7.lengthSqr();
        if (float4 <= 0.001f) {
            final float float5 = float3 * csh4.x;
            final float float6 = float3 * csh4.y;
            final float float7 = Mth.sin(this.yRot * 0.017453292f);
            final float float8 = Mth.cos(this.yRot * 0.017453292f);
            csi7 = new Vec3(float5 * float8 - float6 * float7, csi7.y, float6 * float8 + float5 * float7);
            float4 = (float)csi7.lengthSqr();
            if (float4 <= 0.001f) {
                return;
            }
        }
        final float float5 = (float)Mth.fastInvSqrt(float4);
        final Vec3 csi8 = csi7.scale(float5);
        final Vec3 csi9 = this.getForward();
        final float float8 = (float)(csi9.x * csi8.x + csi9.z * csi8.z);
        if (float8 < -0.15f) {
            return;
        }
        final CollisionContext csn14 = CollisionContext.of(this);
        BlockPos ew15 = new BlockPos(this.x, this.getBoundingBox().maxY, this.z);
        final BlockState bvt16 = this.level.getBlockState(ew15);
        if (!bvt16.getCollisionShape(this.level, ew15, csn14).isEmpty()) {
            return;
        }
        ew15 = ew15.above();
        final BlockState bvt17 = this.level.getBlockState(ew15);
        if (!bvt17.getCollisionShape(this.level, ew15, csn14).isEmpty()) {
            return;
        }
        final float float9 = 7.0f;
        float float10 = 1.2f;
        if (this.hasEffect(MobEffects.JUMP)) {
            float10 += (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.75f;
        }
        final float float11 = Math.max(float3 * 7.0f, 1.0f / float5);
        Vec3 csi10 = csi5;
        Vec3 csi11 = csi6.add(csi8.scale(float11));
        final float float12 = this.getBbWidth();
        final float float13 = this.getBbHeight();
        final AABB csc25 = new AABB(csi10, csi11.add(0.0, float13, 0.0)).inflate(float12, 0.0, float12);
        csi10 = csi10.add(0.0, 0.5099999904632568, 0.0);
        csi11 = csi11.add(0.0, 0.5099999904632568, 0.0);
        final Vec3 csi12 = csi8.cross(new Vec3(0.0, 1.0, 0.0));
        final Vec3 csi13 = csi12.scale(float12 * 0.5f);
        final Vec3 csi14 = csi10.subtract(csi13);
        final Vec3 csi15 = csi11.subtract(csi13);
        final Vec3 csi16 = csi10.add(csi13);
        final Vec3 csi17 = csi11.add(csi13);
        final Iterator<AABB> iterator32 = (Iterator<AABB>)this.level.getCollisions(this, csc25, (Set<Entity>)Collections.emptySet()).flatMap(ctc -> ctc.toAabbs().stream()).iterator();
        float float14 = Float.MIN_VALUE;
        while (iterator32.hasNext()) {
            final AABB csc26 = (AABB)iterator32.next();
            if (!csc26.intersects(csi14, csi15) && !csc26.intersects(csi16, csi17)) {
                continue;
            }
            float14 = (float)csc26.maxY;
            final Vec3 csi18 = csc26.getCenter();
            final BlockPos ew16 = new BlockPos(csi18);
            for (int integer38 = 1; integer38 < float10; ++integer38) {
                final BlockPos ew17 = ew16.above(integer38);
                final BlockState bvt18 = this.level.getBlockState(ew17);
                final VoxelShape ctc35;
                if (!(ctc35 = bvt18.getCollisionShape(this.level, ew17, csn14)).isEmpty()) {
                    float14 = (float)ctc35.max(Direction.Axis.Y) + ew17.getY();
                    if (float14 - this.getBoundingBox().minY > float10) {
                        return;
                    }
                }
                if (integer38 > 1) {
                    ew15 = ew15.above();
                    final BlockState bvt19 = this.level.getBlockState(ew15);
                    if (!bvt19.getCollisionShape(this.level, ew15, csn14).isEmpty()) {
                        return;
                    }
                }
            }
            break;
        }
        if (float14 == Float.MIN_VALUE) {
            return;
        }
        final float float15 = (float)(float14 - this.getBoundingBox().minY);
        if (float15 <= 0.5f || float15 > float10) {
            return;
        }
        this.autoJumpTime = 1;
    }
    
    private boolean hasEnoughImpulseToStartSprinting() {
        final double double2 = 0.8;
        return this.isUnderWater() ? this.input.hasForwardImpulse() : (this.input.forwardImpulse >= 0.8);
    }
    
    public float getWaterVision() {
        if (!this.isUnderLiquid(FluidTags.WATER)) {
            return 0.0f;
        }
        final float float2 = 600.0f;
        final float float3 = 100.0f;
        if (this.waterVisionTime >= 600.0f) {
            return 1.0f;
        }
        final float float4 = Mth.clamp(this.waterVisionTime / 100.0f, 0.0f, 1.0f);
        final float float5 = (this.waterVisionTime < 100.0f) ? 0.0f : Mth.clamp((this.waterVisionTime - 100.0f) / 500.0f, 0.0f, 1.0f);
        return float4 * 0.6f + float5 * 0.39999998f;
    }
    
    @Override
    public boolean isUnderWater() {
        return this.wasUnderwater;
    }
    
    @Override
    protected boolean updateIsUnderwater() {
        final boolean boolean2 = this.wasUnderwater;
        final boolean boolean3 = super.updateIsUnderwater();
        if (this.isSpectator()) {
            return this.wasUnderwater;
        }
        if (!boolean2 && boolean3) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.AMBIENT, 1.0f, 1.0f, false);
            this.minecraft.getSoundManager().play(new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(this));
        }
        if (boolean2 && !boolean3) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.wasUnderwater;
    }
}

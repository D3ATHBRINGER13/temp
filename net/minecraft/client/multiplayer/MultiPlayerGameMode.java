package net.minecraft.client.multiplayer;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.util.Mth;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.PosAndRot;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

public class MultiPlayerGameMode {
    private static final Logger LOGGER;
    private final Minecraft minecraft;
    private final ClientPacketListener connection;
    private BlockPos destroyBlockPos;
    private ItemStack destroyingItem;
    private float destroyProgress;
    private float destroyTicks;
    private int destroyDelay;
    private boolean isDestroying;
    private GameType localPlayerMode;
    private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, ServerboundPlayerActionPacket.Action>, PosAndRot> unAckedActions;
    private int carriedIndex;
    
    public MultiPlayerGameMode(final Minecraft cyc, final ClientPacketListener dkc) {
        this.destroyBlockPos = new BlockPos(-1, -1, -1);
        this.destroyingItem = ItemStack.EMPTY;
        this.localPlayerMode = GameType.SURVIVAL;
        this.unAckedActions = (Object2ObjectLinkedOpenHashMap<Pair<BlockPos, ServerboundPlayerActionPacket.Action>, PosAndRot>)new Object2ObjectLinkedOpenHashMap();
        this.minecraft = cyc;
        this.connection = dkc;
    }
    
    public static void creativeDestroyBlock(final Minecraft cyc, final MultiPlayerGameMode dke, final BlockPos ew, final Direction fb) {
        if (!cyc.level.extinguishFire(cyc.player, ew, fb)) {
            dke.destroyBlock(ew);
        }
    }
    
    public void adjustPlayer(final Player awg) {
        this.localPlayerMode.updatePlayerAbilities(awg.abilities);
    }
    
    public void setLocalMode(final GameType bho) {
        (this.localPlayerMode = bho).updatePlayerAbilities(this.minecraft.player.abilities);
    }
    
    public boolean canHurtPlayer() {
        return this.localPlayerMode.isSurvival();
    }
    
    public boolean destroyBlock(final BlockPos ew) {
        if (this.minecraft.player.blockActionRestricted(this.minecraft.level, ew, this.localPlayerMode)) {
            return false;
        }
        final Level bhr3 = this.minecraft.level;
        final BlockState bvt4 = bhr3.getBlockState(ew);
        if (!this.minecraft.player.getMainHandItem().getItem().canAttackBlock(bvt4, bhr3, ew, this.minecraft.player)) {
            return false;
        }
        final Block bmv5 = bvt4.getBlock();
        if ((bmv5 instanceof CommandBlock || bmv5 instanceof StructureBlock || bmv5 instanceof JigsawBlock) && !this.minecraft.player.canUseGameMasterBlocks()) {
            return false;
        }
        if (bvt4.isAir()) {
            return false;
        }
        bmv5.playerWillDestroy(bhr3, ew, bvt4, this.minecraft.player);
        final FluidState clk6 = bhr3.getFluidState(ew);
        final boolean boolean7 = bhr3.setBlock(ew, clk6.createLegacyBlock(), 11);
        if (boolean7) {
            bmv5.destroy(bhr3, ew, bvt4);
        }
        return boolean7;
    }
    
    public boolean startDestroyBlock(final BlockPos ew, final Direction fb) {
        if (this.minecraft.player.blockActionRestricted(this.minecraft.level, ew, this.localPlayerMode)) {
            return false;
        }
        if (!this.minecraft.level.getWorldBorder().isWithinBounds(ew)) {
            return false;
        }
        if (this.localPlayerMode.isCreative()) {
            final BlockState bvt4 = this.minecraft.level.getBlockState(ew);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, ew, bvt4, 1.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, ew, fb);
            creativeDestroyBlock(this.minecraft, this, ew, fb);
            this.destroyDelay = 5;
        }
        else if (!this.isDestroying || !this.sameDestroyTarget(ew)) {
            if (this.isDestroying) {
                this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, fb);
            }
            final BlockState bvt4 = this.minecraft.level.getBlockState(ew);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, ew, bvt4, 0.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, ew, fb);
            final boolean boolean5 = !bvt4.isAir();
            if (boolean5 && this.destroyProgress == 0.0f) {
                bvt4.attack(this.minecraft.level, ew, this.minecraft.player);
            }
            if (boolean5 && bvt4.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, ew) >= 1.0f) {
                this.destroyBlock(ew);
            }
            else {
                this.isDestroying = true;
                this.destroyBlockPos = ew;
                this.destroyingItem = this.minecraft.player.getMainHandItem();
                this.destroyProgress = 0.0f;
                this.destroyTicks = 0.0f;
                this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0f) - 1);
            }
        }
        return true;
    }
    
    public void stopDestroyBlock() {
        if (this.isDestroying) {
            final BlockState bvt2 = this.minecraft.level.getBlockState(this.destroyBlockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, bvt2, -1.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN);
            this.isDestroying = false;
            this.destroyProgress = 0.0f;
            this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
            this.minecraft.player.resetAttackStrengthTicker();
        }
    }
    
    public boolean continueDestroyBlock(final BlockPos ew, final Direction fb) {
        this.ensureHasSentCarriedItem();
        if (this.destroyDelay > 0) {
            --this.destroyDelay;
            return true;
        }
        if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(ew)) {
            this.destroyDelay = 5;
            final BlockState bvt4 = this.minecraft.level.getBlockState(ew);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, ew, bvt4, 1.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, ew, fb);
            creativeDestroyBlock(this.minecraft, this, ew, fb);
            return true;
        }
        if (!this.sameDestroyTarget(ew)) {
            return this.startDestroyBlock(ew, fb);
        }
        final BlockState bvt4 = this.minecraft.level.getBlockState(ew);
        if (bvt4.isAir()) {
            return this.isDestroying = false;
        }
        this.destroyProgress += bvt4.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, ew);
        if (this.destroyTicks % 4.0f == 0.0f) {
            final SoundType bry5 = bvt4.getSoundType();
            this.minecraft.getSoundManager().play(new SimpleSoundInstance(bry5.getHitSound(), SoundSource.NEUTRAL, (bry5.getVolume() + 1.0f) / 8.0f, bry5.getPitch() * 0.5f, ew));
        }
        ++this.destroyTicks;
        this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, ew, bvt4, Mth.clamp(this.destroyProgress, 0.0f, 1.0f));
        if (this.destroyProgress >= 1.0f) {
            this.isDestroying = false;
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, ew, fb);
            this.destroyBlock(ew);
            this.destroyProgress = 0.0f;
            this.destroyTicks = 0.0f;
            this.destroyDelay = 5;
        }
        this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0f) - 1);
        return true;
    }
    
    public float getPickRange() {
        if (this.localPlayerMode.isCreative()) {
            return 5.0f;
        }
        return 4.5f;
    }
    
    public void tick() {
        this.ensureHasSentCarriedItem();
        if (this.connection.getConnection().isConnected()) {
            this.connection.getConnection().tick();
        }
        else {
            this.connection.getConnection().handleDisconnection();
        }
    }
    
    private boolean sameDestroyTarget(final BlockPos ew) {
        final ItemStack bcj3 = this.minecraft.player.getMainHandItem();
        boolean boolean4 = this.destroyingItem.isEmpty() && bcj3.isEmpty();
        if (!this.destroyingItem.isEmpty() && !bcj3.isEmpty()) {
            boolean4 = (bcj3.getItem() == this.destroyingItem.getItem() && ItemStack.tagMatches(bcj3, this.destroyingItem) && (bcj3.isDamageableItem() || bcj3.getDamageValue() == this.destroyingItem.getDamageValue()));
        }
        return ew.equals(this.destroyBlockPos) && boolean4;
    }
    
    private void ensureHasSentCarriedItem() {
        final int integer2 = this.minecraft.player.inventory.selected;
        if (integer2 != this.carriedIndex) {
            this.carriedIndex = integer2;
            this.connection.send(new ServerboundSetCarriedItemPacket(this.carriedIndex));
        }
    }
    
    public InteractionResult useItemOn(final LocalPlayer dmp, final MultiPlayerLevel dkf, final InteractionHand ahi, final BlockHitResult csd) {
        this.ensureHasSentCarriedItem();
        final BlockPos ew6 = csd.getBlockPos();
        final Vec3 csi7 = csd.getLocation();
        if (!this.minecraft.level.getWorldBorder().isWithinBounds(ew6)) {
            return InteractionResult.FAIL;
        }
        final ItemStack bcj8 = dmp.getItemInHand(ahi);
        if (this.localPlayerMode == GameType.SPECTATOR) {
            this.connection.send(new ServerboundUseItemOnPacket(ahi, csd));
            return InteractionResult.SUCCESS;
        }
        final boolean boolean9 = !dmp.getMainHandItem().isEmpty() || !dmp.getOffhandItem().isEmpty();
        final boolean boolean10 = dmp.isSneaking() && boolean9;
        if (!boolean10 && dkf.getBlockState(ew6).use(dkf, dmp, ahi, csd)) {
            this.connection.send(new ServerboundUseItemOnPacket(ahi, csd));
            return InteractionResult.SUCCESS;
        }
        this.connection.send(new ServerboundUseItemOnPacket(ahi, csd));
        if (bcj8.isEmpty() || dmp.getCooldowns().isOnCooldown(bcj8.getItem())) {
            return InteractionResult.PASS;
        }
        final UseOnContext bdu12 = new UseOnContext(dmp, ahi, csd);
        InteractionResult ahj11;
        if (this.localPlayerMode.isCreative()) {
            final int integer13 = bcj8.getCount();
            ahj11 = bcj8.useOn(bdu12);
            bcj8.setCount(integer13);
        }
        else {
            ahj11 = bcj8.useOn(bdu12);
        }
        return ahj11;
    }
    
    public InteractionResult useItem(final Player awg, final Level bhr, final InteractionHand ahi) {
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundUseItemPacket(ahi));
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (awg.getCooldowns().isOnCooldown(bcj5.getItem())) {
            return InteractionResult.PASS;
        }
        final int integer6 = bcj5.getCount();
        final InteractionResultHolder<ItemStack> ahk7 = bcj5.use(bhr, awg, ahi);
        final ItemStack bcj6 = ahk7.getObject();
        if (bcj6 != bcj5 || bcj6.getCount() != integer6) {
            awg.setItemInHand(ahi, bcj6);
        }
        return ahk7.getResult();
    }
    
    public LocalPlayer createPlayer(final MultiPlayerLevel dkf, final StatsCounter yz, final ClientRecipeBook cxr) {
        return new LocalPlayer(this.minecraft, dkf, this.connection, yz, cxr);
    }
    
    public void attack(final Player awg, final Entity aio) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundInteractPacket(aio));
        if (this.localPlayerMode != GameType.SPECTATOR) {
            awg.attack(aio);
            awg.resetAttackStrengthTicker();
        }
    }
    
    public InteractionResult interact(final Player awg, final Entity aio, final InteractionHand ahi) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundInteractPacket(aio, ahi));
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        return awg.interactOn(aio, ahi);
    }
    
    public InteractionResult interactAt(final Player awg, final Entity aio, final EntityHitResult cse, final InteractionHand ahi) {
        this.ensureHasSentCarriedItem();
        final Vec3 csi6 = cse.getLocation().subtract(aio.x, aio.y, aio.z);
        this.connection.send(new ServerboundInteractPacket(aio, ahi, csi6));
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        return aio.interactAt(awg, csi6, ahi);
    }
    
    public ItemStack handleInventoryMouseClick(final int integer1, final int integer2, final int integer3, final ClickType ays, final Player awg) {
        final short short7 = awg.containerMenu.backup(awg.inventory);
        final ItemStack bcj8 = awg.containerMenu.clicked(integer2, integer3, ays, awg);
        this.connection.send(new ServerboundContainerClickPacket(integer1, integer2, integer3, ays, bcj8, short7));
        return bcj8;
    }
    
    public void handlePlaceRecipe(final int integer, final Recipe<?> ber, final boolean boolean3) {
        this.connection.send(new ServerboundPlaceRecipePacket(integer, ber, boolean3));
    }
    
    public void handleInventoryButtonClick(final int integer1, final int integer2) {
        this.connection.send(new ServerboundContainerButtonClickPacket(integer1, integer2));
    }
    
    public void handleCreativeModeItemAdd(final ItemStack bcj, final int integer) {
        if (this.localPlayerMode.isCreative()) {
            this.connection.send(new ServerboundSetCreativeModeSlotPacket(integer, bcj));
        }
    }
    
    public void handleCreativeModeItemDrop(final ItemStack bcj) {
        if (this.localPlayerMode.isCreative() && !bcj.isEmpty()) {
            this.connection.send(new ServerboundSetCreativeModeSlotPacket(-1, bcj));
        }
    }
    
    public void releaseUsingItem(final Player awg) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
        awg.releaseUsingItem();
    }
    
    public boolean hasExperience() {
        return this.localPlayerMode.isSurvival();
    }
    
    public boolean hasMissTime() {
        return !this.localPlayerMode.isCreative();
    }
    
    public boolean hasInfiniteItems() {
        return this.localPlayerMode.isCreative();
    }
    
    public boolean hasFarPickRange() {
        return this.localPlayerMode.isCreative();
    }
    
    public boolean isServerControlledInventory() {
        return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof AbstractHorse;
    }
    
    public boolean isAlwaysFlying() {
        return this.localPlayerMode == GameType.SPECTATOR;
    }
    
    public GameType getPlayerMode() {
        return this.localPlayerMode;
    }
    
    public boolean isDestroying() {
        return this.isDestroying;
    }
    
    public void handlePickItem(final int integer) {
        this.connection.send(new ServerboundPickItemPacket(integer));
    }
    
    private void sendBlockAction(final ServerboundPlayerActionPacket.Action a, final BlockPos ew, final Direction fb) {
        final LocalPlayer dmp5 = this.minecraft.player;
        this.unAckedActions.put(Pair.of((Object)ew, (Object)a), new PosAndRot(dmp5.position(), dmp5.xRot, dmp5.yRot));
        this.connection.send(new ServerboundPlayerActionPacket(a, ew, fb));
    }
    
    public void handleBlockBreakAck(final MultiPlayerLevel dkf, final BlockPos ew, final BlockState bvt, final ServerboundPlayerActionPacket.Action a, final boolean boolean5) {
        final PosAndRot csg7 = (PosAndRot)this.unAckedActions.remove(Pair.of((Object)ew, (Object)a));
        if (csg7 == null || !boolean5 || (a != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK && dkf.getBlockState(ew) != bvt)) {
            dkf.setKnownState(ew, bvt);
            if (csg7 != null) {
                final Vec3 csi8 = csg7.pos();
                this.minecraft.player.absMoveTo(csi8.x, csi8.y, csi8.z, csg7.yRot(), csg7.xRot());
            }
        }
        while (this.unAckedActions.size() >= 50) {
            final Pair<BlockPos, ServerboundPlayerActionPacket.Action> pair8 = (Pair<BlockPos, ServerboundPlayerActionPacket.Action>)this.unAckedActions.firstKey();
            this.unAckedActions.removeFirst();
            MultiPlayerGameMode.LOGGER.error(new StringBuilder().append("Too many unacked block actions, dropping ").append(pair8).toString());
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.Collections;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.protocol.game.DebugPackets;
import java.util.Random;
import net.minecraft.world.level.LevelReader;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.tags.Tag;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.EntityType;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockItem;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.world.item.Item;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.IdMapper;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.ItemLike;

public class Block implements ItemLike {
    protected static final Logger LOGGER;
    public static final IdMapper<BlockState> BLOCK_STATE_REGISTRY;
    private static final Direction[] UPDATE_SHAPE_ORDER;
    private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE;
    private static final VoxelShape RIGID_SUPPORT_SHAPE;
    private static final VoxelShape CENTER_SUPPORT_SHAPE;
    protected final int lightEmission;
    protected final float destroySpeed;
    protected final float explosionResistance;
    protected final boolean isTicking;
    protected final SoundType soundType;
    protected final Material material;
    protected final MaterialColor materialColor;
    private final float friction;
    protected final StateDefinition<Block, BlockState> stateDefinition;
    private BlockState defaultBlockState;
    protected final boolean hasCollision;
    private final boolean dynamicShape;
    @Nullable
    private ResourceLocation drops;
    @Nullable
    private String descriptionId;
    @Nullable
    private Item item;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<BlockStatePairKey>> OCCLUSION_CACHE;
    
    public static int getId(@Nullable final BlockState bvt) {
        if (bvt == null) {
            return 0;
        }
        final int integer2 = Block.BLOCK_STATE_REGISTRY.getId(bvt);
        return (integer2 == -1) ? 0 : integer2;
    }
    
    public static BlockState stateById(final int integer) {
        final BlockState bvt2 = Block.BLOCK_STATE_REGISTRY.byId(integer);
        return (bvt2 == null) ? Blocks.AIR.defaultBlockState() : bvt2;
    }
    
    public static Block byItem(@Nullable final Item bce) {
        if (bce instanceof BlockItem) {
            return ((BlockItem)bce).getBlock();
        }
        return Blocks.AIR;
    }
    
    public static BlockState pushEntitiesUp(final BlockState bvt1, final BlockState bvt2, final Level bhr, final BlockPos ew) {
        final VoxelShape ctc5 = Shapes.joinUnoptimized(bvt1.getCollisionShape(bhr, ew), bvt2.getCollisionShape(bhr, ew), BooleanOp.ONLY_SECOND).move(ew.getX(), ew.getY(), ew.getZ());
        final List<Entity> list6 = bhr.getEntities(null, ctc5.bounds());
        for (final Entity aio8 : list6) {
            final double double9 = Shapes.collide(Direction.Axis.Y, aio8.getBoundingBox().move(0.0, 1.0, 0.0), (Stream<VoxelShape>)Stream.of(ctc5), -1.0);
            aio8.teleportTo(aio8.x, aio8.y + 1.0 + double9, aio8.z);
        }
        return bvt2;
    }
    
    public static VoxelShape box(final double double1, final double double2, final double double3, final double double4, final double double5, final double double6) {
        return Shapes.box(double1 / 16.0, double2 / 16.0, double3 / 16.0, double4 / 16.0, double5 / 16.0, double6 / 16.0);
    }
    
    @Deprecated
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return bvt.isFaceSturdy(bhb, ew, Direction.UP) && this.lightEmission < 14;
    }
    
    @Deprecated
    public boolean isAir(final BlockState bvt) {
        return false;
    }
    
    @Deprecated
    public int getLightEmission(final BlockState bvt) {
        return this.lightEmission;
    }
    
    @Deprecated
    public Material getMaterial(final BlockState bvt) {
        return this.material;
    }
    
    @Deprecated
    public MaterialColor getMapColor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return this.materialColor;
    }
    
    @Deprecated
    public void updateNeighbourShapes(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final int integer) {
        try (final BlockPos.PooledMutableBlockPos b6 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (final Direction fb11 : Block.UPDATE_SHAPE_ORDER) {
                b6.set(ew).move(fb11);
                final BlockState bvt2 = bhs.getBlockState(b6);
                final BlockState bvt3 = bvt2.updateShape(fb11.getOpposite(), bvt, bhs, b6, ew);
                updateOrDestroy(bvt2, bvt3, bhs, b6, integer);
            }
        }
    }
    
    public boolean is(final Tag<Block> zg) {
        return zg.contains(this);
    }
    
    public static BlockState updateFromNeighbourShapes(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew) {
        BlockState bvt2 = bvt;
        final BlockPos.MutableBlockPos a5 = new BlockPos.MutableBlockPos();
        for (final Direction fb9 : Block.UPDATE_SHAPE_ORDER) {
            a5.set(ew).move(fb9);
            bvt2 = bvt2.updateShape(fb9, bhs.getBlockState(a5), bhs, ew, a5);
        }
        return bvt2;
    }
    
    public static void updateOrDestroy(final BlockState bvt1, final BlockState bvt2, final LevelAccessor bhs, final BlockPos ew, final int integer) {
        if (bvt2 != bvt1) {
            if (bvt2.isAir()) {
                if (!bhs.isClientSide()) {
                    bhs.destroyBlock(ew, (integer & 0x20) == 0x0);
                }
            }
            else {
                bhs.setBlock(ew, bvt2, integer & 0xFFFFFFDF);
            }
        }
    }
    
    @Deprecated
    public void updateIndirectNeighbourShapes(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew, final int integer) {
    }
    
    @Deprecated
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        return bvt1;
    }
    
    @Deprecated
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return bvt;
    }
    
    @Deprecated
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt;
    }
    
    public Block(final Properties c) {
        final StateDefinition.Builder<Block, BlockState> a3 = new StateDefinition.Builder<Block, BlockState>(this);
        this.createBlockStateDefinition(a3);
        this.material = c.material;
        this.materialColor = c.materialColor;
        this.hasCollision = c.hasCollision;
        this.soundType = c.soundType;
        this.lightEmission = c.lightEmission;
        this.explosionResistance = c.explosionResistance;
        this.destroySpeed = c.destroyTime;
        this.isTicking = c.isTicking;
        this.friction = c.friction;
        this.dynamicShape = c.dynamicShape;
        this.drops = c.drops;
        this.stateDefinition = a3.<BlockState>create(BlockState::new);
        this.registerDefaultState(this.stateDefinition.any());
    }
    
    public static boolean isExceptionForConnection(final Block bmv) {
        return bmv instanceof LeavesBlock || bmv == Blocks.BARRIER || bmv == Blocks.CARVED_PUMPKIN || bmv == Blocks.JACK_O_LANTERN || bmv == Blocks.MELON || bmv == Blocks.PUMPKIN;
    }
    
    @Deprecated
    public boolean isRedstoneConductor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.getMaterial().isSolidBlocking() && bvt.isCollisionShapeFullBlock(bhb, ew) && !bvt.isSignalSource();
    }
    
    @Deprecated
    public boolean isViewBlocking(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return this.material.blocksMotion() && bvt.isCollisionShapeFullBlock(bhb, ew);
    }
    
    @Deprecated
    public boolean hasCustomBreakingProgress(final BlockState bvt) {
        return false;
    }
    
    @Deprecated
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        switch (cns) {
            case LAND: {
                return !bvt.isCollisionShapeFullBlock(bhb, ew);
            }
            case WATER: {
                return bhb.getFluidState(ew).is(FluidTags.WATER);
            }
            case AIR: {
                return !bvt.isCollisionShapeFullBlock(bhb, ew);
            }
            default: {
                return false;
            }
        }
    }
    
    @Deprecated
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Deprecated
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        return this.material.isReplaceable() && (ban.getItemInHand().isEmpty() || ban.getItemInHand().getItem() != this.asItem());
    }
    
    @Deprecated
    public float getDestroySpeed(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return this.destroySpeed;
    }
    
    public boolean isRandomlyTicking(final BlockState bvt) {
        return this.isTicking;
    }
    
    public boolean isEntityBlock() {
        return this instanceof EntityBlock;
    }
    
    @Deprecated
    public boolean hasPostProcess(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Deprecated
    public int getLightColor(final BlockState bvt, final BlockAndBiomeGetter bgz, final BlockPos ew) {
        return bgz.getLightColor(ew, bvt.getLightEmission());
    }
    
    public static boolean shouldRenderFace(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        final BlockPos ew2 = ew.relative(fb);
        final BlockState bvt2 = bhb.getBlockState(ew2);
        if (bvt.skipRendering(bvt2, fb)) {
            return false;
        }
        if (!bvt2.canOcclude()) {
            return true;
        }
        final BlockStatePairKey a7 = new BlockStatePairKey(bvt, bvt2, fb);
        final Object2ByteLinkedOpenHashMap<BlockStatePairKey> object2ByteLinkedOpenHashMap8 = (Object2ByteLinkedOpenHashMap<BlockStatePairKey>)Block.OCCLUSION_CACHE.get();
        final byte byte9 = object2ByteLinkedOpenHashMap8.getAndMoveToFirst(a7);
        if (byte9 != 127) {
            return byte9 != 0;
        }
        final VoxelShape ctc10 = bvt.getFaceOcclusionShape(bhb, ew, fb);
        final VoxelShape ctc11 = bvt2.getFaceOcclusionShape(bhb, ew2, fb.getOpposite());
        final boolean boolean12 = Shapes.joinIsNotEmpty(ctc10, ctc11, BooleanOp.ONLY_FIRST);
        if (object2ByteLinkedOpenHashMap8.size() == 200) {
            object2ByteLinkedOpenHashMap8.removeLastByte();
        }
        object2ByteLinkedOpenHashMap8.putAndMoveToFirst(a7, (byte)(byte)(boolean12 ? 1 : 0));
        return boolean12;
    }
    
    @Deprecated
    public boolean canOcclude(final BlockState bvt) {
        return this.hasCollision && this.getRenderLayer() == BlockLayer.SOLID;
    }
    
    @Deprecated
    public boolean skipRendering(final BlockState bvt1, final BlockState bvt2, final Direction fb) {
        return false;
    }
    
    @Deprecated
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Shapes.block();
    }
    
    @Deprecated
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return this.hasCollision ? bvt.getShape(bhb, ew) : Shapes.empty();
    }
    
    @Deprecated
    public VoxelShape getOcclusionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.getShape(bhb, ew);
    }
    
    @Deprecated
    public VoxelShape getInteractionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return Shapes.empty();
    }
    
    public static boolean canSupportRigidBlock(final BlockGetter bhb, final BlockPos ew) {
        final BlockState bvt3 = bhb.getBlockState(ew);
        return !bvt3.is(BlockTags.LEAVES) && !Shapes.joinIsNotEmpty(bvt3.getCollisionShape(bhb, ew).getFaceShape(Direction.UP), Block.RIGID_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
    }
    
    public static boolean canSupportCenter(final LevelReader bhu, final BlockPos ew, final Direction fb) {
        final BlockState bvt4 = bhu.getBlockState(ew);
        return !bvt4.is(BlockTags.LEAVES) && !Shapes.joinIsNotEmpty(bvt4.getCollisionShape(bhu, ew).getFaceShape(fb), Block.CENTER_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
    }
    
    public static boolean isFaceSturdy(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return !bvt.is(BlockTags.LEAVES) && isFaceFull(bvt.getCollisionShape(bhb, ew), fb);
    }
    
    public static boolean isFaceFull(final VoxelShape ctc, final Direction fb) {
        final VoxelShape ctc2 = ctc.getFaceShape(fb);
        return isShapeFullBlock(ctc2);
    }
    
    public static boolean isShapeFullBlock(final VoxelShape ctc) {
        return (boolean)Block.SHAPE_FULL_BLOCK_CACHE.getUnchecked(ctc);
    }
    
    @Deprecated
    public final boolean isSolidRender(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.canOcclude() && isShapeFullBlock(bvt.getOcclusionShape(bhb, ew));
    }
    
    public boolean propagatesSkylightDown(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return !isShapeFullBlock(bvt.getShape(bhb, ew)) && bvt.getFluidState().isEmpty();
    }
    
    @Deprecated
    public int getLightBlock(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        if (bvt.isSolidRender(bhb, ew)) {
            return bhb.getMaxLightLevel();
        }
        return bvt.propagatesSkylightDown(bhb, ew) ? 0 : 1;
    }
    
    @Deprecated
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return false;
    }
    
    @Deprecated
    public void randomTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        this.tick(bvt, bhr, ew, random);
    }
    
    @Deprecated
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
    }
    
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
    }
    
    public void destroy(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
    }
    
    @Deprecated
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        DebugPackets.sendNeighborsUpdatePacket(bhr, ew3);
    }
    
    public int getTickDelay(final LevelReader bhu) {
        return 10;
    }
    
    @Nullable
    @Deprecated
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return null;
    }
    
    @Deprecated
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
    }
    
    @Deprecated
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (this.isEntityBlock() && bvt1.getBlock() != bvt4.getBlock()) {
            bhr.removeBlockEntity(ew);
        }
    }
    
    @Deprecated
    public float getDestroyProgress(final BlockState bvt, final Player awg, final BlockGetter bhb, final BlockPos ew) {
        final float float6 = bvt.getDestroySpeed(bhb, ew);
        if (float6 == -1.0f) {
            return 0.0f;
        }
        final int integer7 = awg.canDestroy(bvt) ? 30 : 100;
        return awg.getDestroySpeed(bvt) / float6 / integer7;
    }
    
    @Deprecated
    public void spawnAfterBreak(final BlockState bvt, final Level bhr, final BlockPos ew, final ItemStack bcj) {
    }
    
    public ResourceLocation getLootTable() {
        if (this.drops == null) {
            final ResourceLocation qv2 = Registry.BLOCK.getKey(this);
            this.drops = new ResourceLocation(qv2.getNamespace(), "blocks/" + qv2.getPath());
        }
        return this.drops;
    }
    
    @Deprecated
    public List<ItemStack> getDrops(final BlockState bvt, final LootContext.Builder a) {
        final ResourceLocation qv4 = this.getLootTable();
        if (qv4 == BuiltInLootTables.EMPTY) {
            return (List<ItemStack>)Collections.emptyList();
        }
        final LootContext coy5 = a.<BlockState>withParameter(LootContextParams.BLOCK_STATE, bvt).create(LootContextParamSets.BLOCK);
        final ServerLevel vk6 = coy5.getLevel();
        final LootTable cpb7 = vk6.getServer().getLootTables().get(qv4);
        return cpb7.getRandomItems(coy5);
    }
    
    public static List<ItemStack> getDrops(final BlockState bvt, final ServerLevel vk, final BlockPos ew, @Nullable final BlockEntity btw) {
        final LootContext.Builder a5 = new LootContext.Builder(vk).withRandom(vk.random).<BlockPos>withParameter(LootContextParams.BLOCK_POS, ew).<ItemStack>withParameter(LootContextParams.TOOL, ItemStack.EMPTY).<BlockEntity>withOptionalParameter(LootContextParams.BLOCK_ENTITY, btw);
        return bvt.getDrops(a5);
    }
    
    public static List<ItemStack> getDrops(final BlockState bvt, final ServerLevel vk, final BlockPos ew, @Nullable final BlockEntity btw, final Entity aio, final ItemStack bcj) {
        final LootContext.Builder a7 = new LootContext.Builder(vk).withRandom(vk.random).<BlockPos>withParameter(LootContextParams.BLOCK_POS, ew).<ItemStack>withParameter(LootContextParams.TOOL, bcj).<Entity>withParameter(LootContextParams.THIS_ENTITY, aio).<BlockEntity>withOptionalParameter(LootContextParams.BLOCK_ENTITY, btw);
        return bvt.getDrops(a7);
    }
    
    public static void dropResources(final BlockState bvt, final LootContext.Builder a) {
        final ServerLevel vk3 = a.getLevel();
        final BlockPos ew4 = a.<BlockPos>getParameter(LootContextParams.BLOCK_POS);
        bvt.getDrops(a).forEach(bcj -> popResource(vk3, ew4, bcj));
        bvt.spawnAfterBreak(vk3, ew4, ItemStack.EMPTY);
    }
    
    public static void dropResources(final BlockState bvt, final Level bhr, final BlockPos ew) {
        if (bhr instanceof ServerLevel) {
            getDrops(bvt, (ServerLevel)bhr, ew, null).forEach(bcj -> popResource(bhr, ew, bcj));
        }
        bvt.spawnAfterBreak(bhr, ew, ItemStack.EMPTY);
    }
    
    public static void dropResources(final BlockState bvt, final Level bhr, final BlockPos ew, @Nullable final BlockEntity btw) {
        if (bhr instanceof ServerLevel) {
            getDrops(bvt, (ServerLevel)bhr, ew, btw).forEach(bcj -> popResource(bhr, ew, bcj));
        }
        bvt.spawnAfterBreak(bhr, ew, ItemStack.EMPTY);
    }
    
    public static void dropResources(final BlockState bvt, final Level bhr, final BlockPos ew, @Nullable final BlockEntity btw, final Entity aio, final ItemStack bcj) {
        if (bhr instanceof ServerLevel) {
            getDrops(bvt, (ServerLevel)bhr, ew, btw, aio, bcj).forEach(bcj -> popResource(bhr, ew, bcj));
        }
        bvt.spawnAfterBreak(bhr, ew, bcj);
    }
    
    public static void popResource(final Level bhr, final BlockPos ew, final ItemStack bcj) {
        if (bhr.isClientSide || bcj.isEmpty() || !bhr.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            return;
        }
        final float float4 = 0.5f;
        final double double5 = bhr.random.nextFloat() * 0.5f + 0.25;
        final double double6 = bhr.random.nextFloat() * 0.5f + 0.25;
        final double double7 = bhr.random.nextFloat() * 0.5f + 0.25;
        final ItemEntity atx11 = new ItemEntity(bhr, ew.getX() + double5, ew.getY() + double6, ew.getZ() + double7, bcj);
        atx11.setDefaultPickUpDelay();
        bhr.addFreshEntity(atx11);
    }
    
    protected void popExperience(final Level bhr, final BlockPos ew, int integer) {
        if (!bhr.isClientSide && bhr.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            while (integer > 0) {
                final int integer2 = ExperienceOrb.getExperienceValue(integer);
                integer -= integer2;
                bhr.addFreshEntity(new ExperienceOrb(bhr, ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5, integer2));
            }
        }
    }
    
    public float getExplosionResistance() {
        return this.explosionResistance;
    }
    
    public void wasExploded(final Level bhr, final BlockPos ew, final Explosion bhk) {
    }
    
    public BlockLayer getRenderLayer() {
        return BlockLayer.SOLID;
    }
    
    @Deprecated
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return true;
    }
    
    @Deprecated
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        return false;
    }
    
    public void stepOn(final Level bhr, final BlockPos ew, final Entity aio) {
    }
    
    @Nullable
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return this.defaultBlockState();
    }
    
    @Deprecated
    public void attack(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg) {
    }
    
    @Deprecated
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return 0;
    }
    
    @Deprecated
    public boolean isSignalSource(final BlockState bvt) {
        return false;
    }
    
    @Deprecated
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
    }
    
    @Deprecated
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return 0;
    }
    
    public void playerDestroy(final Level bhr, final Player awg, final BlockPos ew, final BlockState bvt, @Nullable final BlockEntity btw, final ItemStack bcj) {
        awg.awardStat(Stats.BLOCK_MINED.get(this));
        awg.causeFoodExhaustion(0.005f);
        dropResources(bvt, bhr, ew, btw, awg, bcj);
    }
    
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
    }
    
    public boolean isPossibleToRespawnInThis() {
        return !this.material.isSolid() && !this.material.isLiquid();
    }
    
    public Component getName() {
        return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
    }
    
    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("block", Registry.BLOCK.getKey(this));
        }
        return this.descriptionId;
    }
    
    @Deprecated
    public boolean triggerEvent(final BlockState bvt, final Level bhr, final BlockPos ew, final int integer4, final int integer5) {
        return false;
    }
    
    @Deprecated
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return this.material.getPushReaction();
    }
    
    @Deprecated
    public float getShadeBrightness(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.isCollisionShapeFullBlock(bhb, ew) ? 0.2f : 1.0f;
    }
    
    public void fallOn(final Level bhr, final BlockPos ew, final Entity aio, final float float4) {
        aio.causeFallDamage(float4, 1.0f);
    }
    
    public void updateEntityAfterFallOn(final BlockGetter bhb, final Entity aio) {
        aio.setDeltaMovement(aio.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }
    
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(this);
    }
    
    public void fillItemCategory(final CreativeModeTab bba, final NonNullList<ItemStack> fk) {
        fk.add(new ItemStack(this));
    }
    
    @Deprecated
    public FluidState getFluidState(final BlockState bvt) {
        return Fluids.EMPTY.defaultFluidState();
    }
    
    public float getFriction() {
        return this.friction;
    }
    
    @Deprecated
    public long getSeed(final BlockState bvt, final BlockPos ew) {
        return Mth.getSeed(ew);
    }
    
    public void onProjectileHit(final Level bhr, final BlockState bvt, final BlockHitResult csd, final Entity aio) {
    }
    
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        bhr.levelEvent(awg, 2001, ew, getId(bvt));
    }
    
    public void handleRain(final Level bhr, final BlockPos ew) {
    }
    
    public boolean dropFromExplosion(final Explosion bhk) {
        return true;
    }
    
    @Deprecated
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return false;
    }
    
    @Deprecated
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return 0;
    }
    
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
    }
    
    public StateDefinition<Block, BlockState> getStateDefinition() {
        return this.stateDefinition;
    }
    
    protected final void registerDefaultState(final BlockState bvt) {
        this.defaultBlockState = bvt;
    }
    
    public final BlockState defaultBlockState() {
        return this.defaultBlockState;
    }
    
    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }
    
    @Deprecated
    public Vec3 getOffset(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        final OffsetType b5 = this.getOffsetType();
        if (b5 == OffsetType.NONE) {
            return Vec3.ZERO;
        }
        final long long6 = Mth.getSeed(ew.getX(), 0, ew.getZ());
        return new Vec3(((long6 & 0xFL) / 15.0f - 0.5) * 0.5, (b5 == OffsetType.XYZ) ? (((long6 >> 4 & 0xFL) / 15.0f - 1.0) * 0.2) : 0.0, ((long6 >> 8 & 0xFL) / 15.0f - 0.5) * 0.5);
    }
    
    public SoundType getSoundType(final BlockState bvt) {
        return this.soundType;
    }
    
    public Item asItem() {
        if (this.item == null) {
            this.item = Item.byBlock(this);
        }
        return this.item;
    }
    
    public boolean hasDynamicShape() {
        return this.dynamicShape;
    }
    
    public String toString() {
        return new StringBuilder().append("Block{").append(Registry.BLOCK.getKey(this)).append("}").toString();
    }
    
    public void appendHoverText(final ItemStack bcj, @Nullable final BlockGetter bhb, final List<Component> list, final TooltipFlag bdr) {
    }
    
    public static boolean equalsStone(final Block bmv) {
        return bmv == Blocks.STONE || bmv == Blocks.GRANITE || bmv == Blocks.DIORITE || bmv == Blocks.ANDESITE;
    }
    
    public static boolean equalsDirt(final Block bmv) {
        return bmv == Blocks.DIRT || bmv == Blocks.COARSE_DIRT || bmv == Blocks.PODZOL;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BLOCK_STATE_REGISTRY = new IdMapper<BlockState>();
        UPDATE_SHAPE_ORDER = new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP };
        SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build((CacheLoader)new CacheLoader<VoxelShape, Boolean>() {
            public Boolean load(final VoxelShape ctc) {
                return !Shapes.joinIsNotEmpty(Shapes.block(), ctc, BooleanOp.NOT_SAME);
            }
        });
        RIGID_SUPPORT_SHAPE = Shapes.join(Shapes.block(), box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
        CENTER_SUPPORT_SHAPE = box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
        OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
            final Object2ByteLinkedOpenHashMap<BlockStatePairKey> object2ByteLinkedOpenHashMap1 = new Object2ByteLinkedOpenHashMap<BlockStatePairKey>(200) {
                protected void rehash(final int integer) {
                }
            };
            object2ByteLinkedOpenHashMap1.defaultReturnValue((byte)127);
            return object2ByteLinkedOpenHashMap1;
        });
    }
    
    public static final class BlockStatePairKey {
        private final BlockState first;
        private final BlockState second;
        private final Direction direction;
        
        public BlockStatePairKey(final BlockState bvt1, final BlockState bvt2, final Direction fb) {
            this.first = bvt1;
            this.second = bvt2;
            this.direction = fb;
        }
        
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof BlockStatePairKey)) {
                return false;
            }
            final BlockStatePairKey a3 = (BlockStatePairKey)object;
            return this.first == a3.first && this.second == a3.second && this.direction == a3.direction;
        }
        
        public int hashCode() {
            int integer2 = this.first.hashCode();
            integer2 = 31 * integer2 + this.second.hashCode();
            integer2 = 31 * integer2 + this.direction.hashCode();
            return integer2;
        }
    }
    
    public static class Properties {
        private Material material;
        private MaterialColor materialColor;
        private boolean hasCollision;
        private SoundType soundType;
        private int lightEmission;
        private float explosionResistance;
        private float destroyTime;
        private boolean isTicking;
        private float friction;
        private ResourceLocation drops;
        private boolean dynamicShape;
        
        private Properties(final Material clo, final MaterialColor clp) {
            this.hasCollision = true;
            this.soundType = SoundType.STONE;
            this.friction = 0.6f;
            this.material = clo;
            this.materialColor = clp;
        }
        
        public static Properties of(final Material clo) {
            return of(clo, clo.getColor());
        }
        
        public static Properties of(final Material clo, final DyeColor bbg) {
            return of(clo, bbg.getMaterialColor());
        }
        
        public static Properties of(final Material clo, final MaterialColor clp) {
            return new Properties(clo, clp);
        }
        
        public static Properties copy(final Block bmv) {
            final Properties c2 = new Properties(bmv.material, bmv.materialColor);
            c2.material = bmv.material;
            c2.destroyTime = bmv.destroySpeed;
            c2.explosionResistance = bmv.explosionResistance;
            c2.hasCollision = bmv.hasCollision;
            c2.isTicking = bmv.isTicking;
            c2.lightEmission = bmv.lightEmission;
            c2.materialColor = bmv.materialColor;
            c2.soundType = bmv.soundType;
            c2.friction = bmv.getFriction();
            c2.dynamicShape = bmv.dynamicShape;
            return c2;
        }
        
        public Properties noCollission() {
            this.hasCollision = false;
            return this;
        }
        
        public Properties friction(final float float1) {
            this.friction = float1;
            return this;
        }
        
        protected Properties sound(final SoundType bry) {
            this.soundType = bry;
            return this;
        }
        
        protected Properties lightLevel(final int integer) {
            this.lightEmission = integer;
            return this;
        }
        
        public Properties strength(final float float1, final float float2) {
            this.destroyTime = float1;
            this.explosionResistance = Math.max(0.0f, float2);
            return this;
        }
        
        protected Properties instabreak() {
            return this.strength(0.0f);
        }
        
        protected Properties strength(final float float1) {
            this.strength(float1, float1);
            return this;
        }
        
        protected Properties randomTicks() {
            this.isTicking = true;
            return this;
        }
        
        protected Properties dynamicShape() {
            this.dynamicShape = true;
            return this;
        }
        
        protected Properties noDrops() {
            this.drops = BuiltInLootTables.EMPTY;
            return this;
        }
        
        public Properties dropsLike(final Block bmv) {
            this.drops = bmv.getLootTable();
            return this;
        }
    }
    
    public enum OffsetType {
        NONE, 
        XZ, 
        XYZ;
    }
}

package net.minecraft.world.level.block.state;

import java.util.Arrays;
import net.minecraft.world.level.EmptyBlockGetter;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.core.Registry;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.Property;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;

public class BlockState extends AbstractStateHolder<Block, BlockState> implements StateHolder<BlockState> {
    @Nullable
    private Cache cache;
    private final int lightEmission;
    private final boolean useShapeForLightOcclusion;
    
    public BlockState(final Block bmv, final ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        super(bmv, immutableMap);
        this.lightEmission = bmv.getLightEmission(this);
        this.useShapeForLightOcclusion = bmv.useShapeForLightOcclusion(this);
    }
    
    public void initCache() {
        if (!this.getBlock().hasDynamicShape()) {
            this.cache = new Cache(this);
        }
    }
    
    public Block getBlock() {
        return (Block)this.owner;
    }
    
    public Material getMaterial() {
        return this.getBlock().getMaterial(this);
    }
    
    public boolean isValidSpawn(final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return this.getBlock().isValidSpawn(this, bhb, ew, ais);
    }
    
    public boolean propagatesSkylightDown(final BlockGetter bhb, final BlockPos ew) {
        if (this.cache != null) {
            return this.cache.propagatesSkylightDown;
        }
        return this.getBlock().propagatesSkylightDown(this, bhb, ew);
    }
    
    public int getLightBlock(final BlockGetter bhb, final BlockPos ew) {
        if (this.cache != null) {
            return this.cache.lightBlock;
        }
        return this.getBlock().getLightBlock(this, bhb, ew);
    }
    
    public VoxelShape getFaceOcclusionShape(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (this.cache != null && this.cache.occlusionShapes != null) {
            return this.cache.occlusionShapes[fb.ordinal()];
        }
        return Shapes.getFaceShape(this.getOcclusionShape(bhb, ew), fb);
    }
    
    public boolean hasLargeCollisionShape() {
        return this.cache == null || this.cache.largeCollisionShape;
    }
    
    public boolean useShapeForLightOcclusion() {
        return this.useShapeForLightOcclusion;
    }
    
    public int getLightEmission() {
        return this.lightEmission;
    }
    
    public boolean isAir() {
        return this.getBlock().isAir(this);
    }
    
    public MaterialColor getMapColor(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().getMapColor(this, bhb, ew);
    }
    
    public BlockState rotate(final Rotation brg) {
        return this.getBlock().rotate(this, brg);
    }
    
    public BlockState mirror(final Mirror bqg) {
        return this.getBlock().mirror(this, bqg);
    }
    
    public boolean hasCustomBreakingProgress() {
        return this.getBlock().hasCustomBreakingProgress(this);
    }
    
    public RenderShape getRenderShape() {
        return this.getBlock().getRenderShape(this);
    }
    
    public int getLightColor(final BlockAndBiomeGetter bgz, final BlockPos ew) {
        return this.getBlock().getLightColor(this, bgz, ew);
    }
    
    public float getShadeBrightness(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().getShadeBrightness(this, bhb, ew);
    }
    
    public boolean isRedstoneConductor(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().isRedstoneConductor(this, bhb, ew);
    }
    
    public boolean isSignalSource() {
        return this.getBlock().isSignalSource(this);
    }
    
    public int getSignal(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return this.getBlock().getSignal(this, bhb, ew, fb);
    }
    
    public boolean hasAnalogOutputSignal() {
        return this.getBlock().hasAnalogOutputSignal(this);
    }
    
    public int getAnalogOutputSignal(final Level bhr, final BlockPos ew) {
        return this.getBlock().getAnalogOutputSignal(this, bhr, ew);
    }
    
    public float getDestroySpeed(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().getDestroySpeed(this, bhb, ew);
    }
    
    public float getDestroyProgress(final Player awg, final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().getDestroyProgress(this, awg, bhb, ew);
    }
    
    public int getDirectSignal(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return this.getBlock().getDirectSignal(this, bhb, ew, fb);
    }
    
    public PushReaction getPistonPushReaction() {
        return this.getBlock().getPistonPushReaction(this);
    }
    
    public boolean isSolidRender(final BlockGetter bhb, final BlockPos ew) {
        if (this.cache != null) {
            return this.cache.solidRender;
        }
        return this.getBlock().isSolidRender(this, bhb, ew);
    }
    
    public boolean canOcclude() {
        if (this.cache != null) {
            return this.cache.canOcclude;
        }
        return this.getBlock().canOcclude(this);
    }
    
    public boolean skipRendering(final BlockState bvt, final Direction fb) {
        return this.getBlock().skipRendering(this, bvt, fb);
    }
    
    public VoxelShape getShape(final BlockGetter bhb, final BlockPos ew) {
        return this.getShape(bhb, ew, CollisionContext.empty());
    }
    
    public VoxelShape getShape(final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return this.getBlock().getShape(this, bhb, ew, csn);
    }
    
    public VoxelShape getCollisionShape(final BlockGetter bhb, final BlockPos ew) {
        if (this.cache != null) {
            return this.cache.collisionShape;
        }
        return this.getCollisionShape(bhb, ew, CollisionContext.empty());
    }
    
    public VoxelShape getCollisionShape(final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return this.getBlock().getCollisionShape(this, bhb, ew, csn);
    }
    
    public VoxelShape getOcclusionShape(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().getOcclusionShape(this, bhb, ew);
    }
    
    public VoxelShape getInteractionShape(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().getInteractionShape(this, bhb, ew);
    }
    
    public final boolean entityCanStandOn(final BlockGetter bhb, final BlockPos ew, final Entity aio) {
        return Block.isFaceFull(this.getCollisionShape(bhb, ew, CollisionContext.of(aio)), Direction.UP);
    }
    
    public Vec3 getOffset(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().getOffset(this, bhb, ew);
    }
    
    public boolean triggerEvent(final Level bhr, final BlockPos ew, final int integer3, final int integer4) {
        return this.getBlock().triggerEvent(this, bhr, ew, integer3, integer4);
    }
    
    public void neighborChanged(final Level bhr, final BlockPos ew2, final Block bmv, final BlockPos ew4, final boolean boolean5) {
        this.getBlock().neighborChanged(this, bhr, ew2, bmv, ew4, boolean5);
    }
    
    public void updateNeighbourShapes(final LevelAccessor bhs, final BlockPos ew, final int integer) {
        this.getBlock().updateNeighbourShapes(this, bhs, ew, integer);
    }
    
    public void updateIndirectNeighbourShapes(final LevelAccessor bhs, final BlockPos ew, final int integer) {
        this.getBlock().updateIndirectNeighbourShapes(this, bhs, ew, integer);
    }
    
    public void onPlace(final Level bhr, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        this.getBlock().onPlace(this, bhr, ew, bvt, boolean4);
    }
    
    public void onRemove(final Level bhr, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        this.getBlock().onRemove(this, bhr, ew, bvt, boolean4);
    }
    
    public void tick(final Level bhr, final BlockPos ew, final Random random) {
        this.getBlock().tick(this, bhr, ew, random);
    }
    
    public void randomTick(final Level bhr, final BlockPos ew, final Random random) {
        this.getBlock().randomTick(this, bhr, ew, random);
    }
    
    public void entityInside(final Level bhr, final BlockPos ew, final Entity aio) {
        this.getBlock().entityInside(this, bhr, ew, aio);
    }
    
    public void spawnAfterBreak(final Level bhr, final BlockPos ew, final ItemStack bcj) {
        this.getBlock().spawnAfterBreak(this, bhr, ew, bcj);
    }
    
    public List<ItemStack> getDrops(final LootContext.Builder a) {
        return this.getBlock().getDrops(this, a);
    }
    
    public boolean use(final Level bhr, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        return this.getBlock().use(this, bhr, csd.getBlockPos(), awg, ahi, csd);
    }
    
    public void attack(final Level bhr, final BlockPos ew, final Player awg) {
        this.getBlock().attack(this, bhr, ew, awg);
    }
    
    public boolean isViewBlocking(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().isViewBlocking(this, bhb, ew);
    }
    
    public BlockState updateShape(final Direction fb, final BlockState bvt, final LevelAccessor bhs, final BlockPos ew4, final BlockPos ew5) {
        return this.getBlock().updateShape(this, fb, bvt, bhs, ew4, ew5);
    }
    
    public boolean isPathfindable(final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return this.getBlock().isPathfindable(this, bhb, ew, cns);
    }
    
    public boolean canBeReplaced(final BlockPlaceContext ban) {
        return this.getBlock().canBeReplaced(this, ban);
    }
    
    public boolean canSurvive(final LevelReader bhu, final BlockPos ew) {
        return this.getBlock().canSurvive(this, bhu, ew);
    }
    
    public boolean hasPostProcess(final BlockGetter bhb, final BlockPos ew) {
        return this.getBlock().hasPostProcess(this, bhb, ew);
    }
    
    @Nullable
    public MenuProvider getMenuProvider(final Level bhr, final BlockPos ew) {
        return this.getBlock().getMenuProvider(this, bhr, ew);
    }
    
    public boolean is(final Tag<Block> zg) {
        return this.getBlock().is(zg);
    }
    
    public FluidState getFluidState() {
        return this.getBlock().getFluidState(this);
    }
    
    public boolean isRandomlyTicking() {
        return this.getBlock().isRandomlyTicking(this);
    }
    
    public long getSeed(final BlockPos ew) {
        return this.getBlock().getSeed(this, ew);
    }
    
    public SoundType getSoundType() {
        return this.getBlock().getSoundType(this);
    }
    
    public void onProjectileHit(final Level bhr, final BlockState bvt, final BlockHitResult csd, final Entity aio) {
        this.getBlock().onProjectileHit(bhr, bvt, csd, aio);
    }
    
    public boolean isFaceSturdy(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (this.cache != null) {
            return this.cache.isFaceSturdy[fb.ordinal()];
        }
        return Block.isFaceSturdy(this, bhb, ew, fb);
    }
    
    public boolean isCollisionShapeFullBlock(final BlockGetter bhb, final BlockPos ew) {
        if (this.cache != null) {
            return this.cache.isCollisionShapeFullBlock;
        }
        return Block.isShapeFullBlock(this.getCollisionShape(bhb, ew));
    }
    
    public static <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps, final BlockState bvt) {
        final ImmutableMap<Property<?>, Comparable<?>> immutableMap3 = bvt.getValues();
        T object4;
        if (immutableMap3.isEmpty()) {
            object4 = (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.BLOCK.getKey(bvt.getBlock()).toString())));
        }
        else {
            object4 = (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.BLOCK.getKey(bvt.getBlock()).toString()), dynamicOps.createString("Properties"), dynamicOps.createMap((Map)immutableMap3.entrySet().stream().map(entry -> Pair.of(dynamicOps.createString(((Property)entry.getKey()).getName()), dynamicOps.createString(StateHolder.<Comparable>getName((Property<Comparable>)entry.getKey(), entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object4);
    }
    
    public static <T> BlockState deserialize(final Dynamic<T> dynamic) {
        final Block bmv2 = Registry.BLOCK.get(new ResourceLocation((String)dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
        final Map<String, String> map3 = (Map<String, String>)dynamic.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        BlockState bvt4 = bmv2.defaultBlockState();
        final StateDefinition<Block, BlockState> bvu5 = bmv2.getStateDefinition();
        for (final Map.Entry<String, String> entry7 : map3.entrySet()) {
            final String string8 = (String)entry7.getKey();
            final Property<?> bww9 = bvu5.getProperty(string8);
            if (bww9 != null) {
                bvt4 = StateHolder.setValueHelper(bvt4, bww9, string8, dynamic.toString(), (String)entry7.getValue());
            }
        }
        return bvt4;
    }
    
    static final class Cache {
        private static final Direction[] DIRECTIONS;
        private final boolean canOcclude;
        private final boolean solidRender;
        private final boolean propagatesSkylightDown;
        private final int lightBlock;
        private final VoxelShape[] occlusionShapes;
        private final VoxelShape collisionShape;
        private final boolean largeCollisionShape;
        private final boolean[] isFaceSturdy;
        private final boolean isCollisionShapeFullBlock;
        
        private Cache(final BlockState bvt) {
            final Block bmv3 = bvt.getBlock();
            this.canOcclude = bmv3.canOcclude(bvt);
            this.solidRender = bmv3.isSolidRender(bvt, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            this.propagatesSkylightDown = bmv3.propagatesSkylightDown(bvt, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            this.lightBlock = bmv3.getLightBlock(bvt, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            if (!bvt.canOcclude()) {
                this.occlusionShapes = null;
            }
            else {
                this.occlusionShapes = new VoxelShape[Cache.DIRECTIONS.length];
                final VoxelShape ctc4 = bmv3.getOcclusionShape(bvt, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                for (final Direction fb8 : Cache.DIRECTIONS) {
                    this.occlusionShapes[fb8.ordinal()] = Shapes.getFaceShape(ctc4, fb8);
                }
            }
            this.collisionShape = bmv3.getCollisionShape(bvt, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
            this.largeCollisionShape = Arrays.stream((Object[])Direction.Axis.values()).anyMatch(a -> this.collisionShape.min(a) < 0.0 || this.collisionShape.max(a) > 1.0);
            this.isFaceSturdy = new boolean[6];
            for (final Direction fb9 : Cache.DIRECTIONS) {
                this.isFaceSturdy[fb9.ordinal()] = Block.isFaceSturdy(bvt, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, fb9);
            }
            this.isCollisionShapeFullBlock = Block.isShapeFullBlock(bvt.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
        }
        
        static {
            DIRECTIONS = Direction.values();
        }
    }
}

package net.minecraft.world.level.levelgen.feature;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import org.apache.logging.log4j.Logger;

public class MonsterRoomFeature extends Feature<NoneFeatureConfiguration> {
    private static final Logger LOGGER;
    private static final EntityType<?>[] MOBS;
    private static final BlockState AIR;
    
    public MonsterRoomFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        final int integer7 = 3;
        final int integer8 = random.nextInt(2) + 2;
        final int integer9 = -integer8 - 1;
        final int integer10 = integer8 + 1;
        final int integer11 = -1;
        final int integer12 = 4;
        final int integer13 = random.nextInt(2) + 2;
        final int integer14 = -integer13 - 1;
        final int integer15 = integer13 + 1;
        int integer16 = 0;
        for (int integer17 = integer9; integer17 <= integer10; ++integer17) {
            for (int integer18 = -1; integer18 <= 4; ++integer18) {
                for (int integer19 = integer14; integer19 <= integer15; ++integer19) {
                    final BlockPos ew2 = ew.offset(integer17, integer18, integer19);
                    final Material clo21 = bhs.getBlockState(ew2).getMaterial();
                    final boolean boolean22 = clo21.isSolid();
                    if (integer18 == -1 && !boolean22) {
                        return false;
                    }
                    if (integer18 == 4 && !boolean22) {
                        return false;
                    }
                    if ((integer17 == integer9 || integer17 == integer10 || integer19 == integer14 || integer19 == integer15) && integer18 == 0 && bhs.isEmptyBlock(ew2) && bhs.isEmptyBlock(ew2.above())) {
                        ++integer16;
                    }
                }
            }
        }
        if (integer16 < 1 || integer16 > 5) {
            return false;
        }
        for (int integer17 = integer9; integer17 <= integer10; ++integer17) {
            for (int integer18 = 3; integer18 >= -1; --integer18) {
                for (int integer19 = integer14; integer19 <= integer15; ++integer19) {
                    final BlockPos ew2 = ew.offset(integer17, integer18, integer19);
                    if (integer17 == integer9 || integer18 == -1 || integer19 == integer14 || integer17 == integer10 || integer18 == 4 || integer19 == integer15) {
                        if (ew2.getY() >= 0 && !bhs.getBlockState(ew2.below()).getMaterial().isSolid()) {
                            bhs.setBlock(ew2, MonsterRoomFeature.AIR, 2);
                        }
                        else if (bhs.getBlockState(ew2).getMaterial().isSolid() && bhs.getBlockState(ew2).getBlock() != Blocks.CHEST) {
                            if (integer18 == -1 && random.nextInt(4) != 0) {
                                bhs.setBlock(ew2, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                            }
                            else {
                                bhs.setBlock(ew2, Blocks.COBBLESTONE.defaultBlockState(), 2);
                            }
                        }
                    }
                    else if (bhs.getBlockState(ew2).getBlock() != Blocks.CHEST) {
                        bhs.setBlock(ew2, MonsterRoomFeature.AIR, 2);
                    }
                }
            }
        }
        for (int integer17 = 0; integer17 < 2; ++integer17) {
            for (int integer18 = 0; integer18 < 3; ++integer18) {
                final int integer19 = ew.getX() + random.nextInt(integer8 * 2 + 1) - integer8;
                final int integer20 = ew.getY();
                final int integer21 = ew.getZ() + random.nextInt(integer13 * 2 + 1) - integer13;
                final BlockPos ew3 = new BlockPos(integer19, integer20, integer21);
                if (bhs.isEmptyBlock(ew3)) {
                    int integer22 = 0;
                    for (final Direction fb25 : Direction.Plane.HORIZONTAL) {
                        if (bhs.getBlockState(ew3.relative(fb25)).getMaterial().isSolid()) {
                            ++integer22;
                        }
                    }
                    if (integer22 == 1) {
                        bhs.setBlock(ew3, StructurePiece.reorient(bhs, ew3, Blocks.CHEST.defaultBlockState()), 2);
                        RandomizableContainerBlockEntity.setLootTable(bhs, random, ew3, BuiltInLootTables.SIMPLE_DUNGEON);
                        break;
                    }
                }
            }
        }
        bhs.setBlock(ew, Blocks.SPAWNER.defaultBlockState(), 2);
        final BlockEntity btw17 = bhs.getBlockEntity(ew);
        if (btw17 instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)btw17).getSpawner().setEntityId(this.randomEntityId(random));
        }
        else {
            MonsterRoomFeature.LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", ew.getX(), ew.getY(), ew.getZ());
        }
        return true;
    }
    
    private EntityType<?> randomEntityId(final Random random) {
        return MonsterRoomFeature.MOBS[random.nextInt(MonsterRoomFeature.MOBS.length)];
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MOBS = new EntityType[] { EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER };
        AIR = Blocks.CAVE_AIR.defaultBlockState();
    }
}

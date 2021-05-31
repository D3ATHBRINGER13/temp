package net.minecraft.world.level.dimension.end;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeConfiguration;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import java.util.List;
import net.minecraft.server.level.ServerLevel;

public enum DragonRespawnAnimation {
    START {
        @Override
        public void tick(final ServerLevel vk, final EndDragonFight byr, final List<EndCrystal> list, final int integer, final BlockPos ew) {
            final BlockPos ew2 = new BlockPos(0, 128, 0);
            for (final EndCrystal aso9 : list) {
                aso9.setBeamTarget(ew2);
            }
            byr.setRespawnStage(DragonRespawnAnimation$1.PREPARING_TO_SUMMON_PILLARS);
        }
    }, 
    PREPARING_TO_SUMMON_PILLARS {
        @Override
        public void tick(final ServerLevel vk, final EndDragonFight byr, final List<EndCrystal> list, final int integer, final BlockPos ew) {
            if (integer < 100) {
                if (integer == 0 || integer == 50 || integer == 51 || integer == 52 || integer >= 95) {
                    vk.levelEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            }
            else {
                byr.setRespawnStage(DragonRespawnAnimation$2.SUMMONING_PILLARS);
            }
        }
    }, 
    SUMMONING_PILLARS {
        @Override
        public void tick(final ServerLevel vk, final EndDragonFight byr, final List<EndCrystal> list, final int integer, final BlockPos ew) {
            final int integer2 = 40;
            final boolean boolean8 = integer % 40 == 0;
            final boolean boolean9 = integer % 40 == 39;
            if (boolean8 || boolean9) {
                final List<SpikeFeature.EndSpike> list2 = SpikeFeature.getSpikesForLevel(vk);
                final int integer3 = integer / 40;
                if (integer3 < list2.size()) {
                    final SpikeFeature.EndSpike a12 = (SpikeFeature.EndSpike)list2.get(integer3);
                    if (boolean8) {
                        for (final EndCrystal aso14 : list) {
                            aso14.setBeamTarget(new BlockPos(a12.getCenterX(), a12.getHeight() + 1, a12.getCenterZ()));
                        }
                    }
                    else {
                        final int integer4 = 10;
                        for (final BlockPos ew2 : BlockPos.betweenClosed(new BlockPos(a12.getCenterX() - 10, a12.getHeight() - 10, a12.getCenterZ() - 10), new BlockPos(a12.getCenterX() + 10, a12.getHeight() + 10, a12.getCenterZ() + 10))) {
                            vk.removeBlock(ew2, false);
                        }
                        vk.explode(null, a12.getCenterX() + 0.5f, a12.getHeight(), a12.getCenterZ() + 0.5f, 5.0f, Explosion.BlockInteraction.DESTROY);
                        final SpikeConfiguration ceo14 = new SpikeConfiguration(true, (List<SpikeFeature.EndSpike>)ImmutableList.of(a12), new BlockPos(0, 128, 0));
                        Feature.END_SPIKE.place(vk, vk.getChunkSource().getGenerator(), new Random(), new BlockPos(a12.getCenterX(), 45, a12.getCenterZ()), ceo14);
                    }
                }
                else if (boolean8) {
                    byr.setRespawnStage(DragonRespawnAnimation$3.SUMMONING_DRAGON);
                }
            }
        }
    }, 
    SUMMONING_DRAGON {
        @Override
        public void tick(final ServerLevel vk, final EndDragonFight byr, final List<EndCrystal> list, final int integer, final BlockPos ew) {
            if (integer >= 100) {
                byr.setRespawnStage(DragonRespawnAnimation$4.END);
                byr.resetSpikeCrystals();
                for (final EndCrystal aso8 : list) {
                    aso8.setBeamTarget(null);
                    vk.explode(aso8, aso8.x, aso8.y, aso8.z, 6.0f, Explosion.BlockInteraction.NONE);
                    aso8.remove();
                }
            }
            else if (integer >= 80) {
                vk.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
            else if (integer == 0) {
                for (final EndCrystal aso8 : list) {
                    aso8.setBeamTarget(new BlockPos(0, 128, 0));
                }
            }
            else if (integer < 5) {
                vk.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        }
    }, 
    END {
        @Override
        public void tick(final ServerLevel vk, final EndDragonFight byr, final List<EndCrystal> list, final int integer, final BlockPos ew) {
        }
    };
    
    public abstract void tick(final ServerLevel vk, final EndDragonFight byr, final List<EndCrystal> list, final int integer, final BlockPos ew);
}

package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.GameRules;
import net.minecraft.server.level.ServerLevel;
import com.google.common.collect.Lists;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;

public class HarvestFarmland extends Behavior<Villager> {
    @Nullable
    private BlockPos aboveFarmlandPos;
    private boolean canPlantStuff;
    private boolean wantsToReapStuff;
    private long nextOkStartTime;
    private int timeWorkedSoFar;
    private final List<BlockPos> validFarmlandAroundVillager;
    
    public HarvestFarmland() {
        super((Map)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT));
        this.validFarmlandAroundVillager = (List<BlockPos>)Lists.newArrayList();
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        if (!vk.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        if (avt.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }
        this.canPlantStuff = avt.hasFarmSeeds();
        this.wantsToReapStuff = false;
        final SimpleContainer aho4 = avt.getInventory();
        for (int integer5 = aho4.getContainerSize(), integer6 = 0; integer6 < integer5; ++integer6) {
            final ItemStack bcj7 = aho4.getItem(integer6);
            if (bcj7.isEmpty()) {
                this.wantsToReapStuff = true;
                break;
            }
            if (bcj7.getItem() == Items.WHEAT_SEEDS || bcj7.getItem() == Items.BEETROOT_SEEDS) {
                this.wantsToReapStuff = true;
                break;
            }
        }
        final BlockPos.MutableBlockPos a6 = new BlockPos.MutableBlockPos(avt.x, avt.y, avt.z);
        this.validFarmlandAroundVillager.clear();
        for (int integer7 = -1; integer7 <= 1; ++integer7) {
            for (int integer8 = -1; integer8 <= 1; ++integer8) {
                for (int integer9 = -1; integer9 <= 1; ++integer9) {
                    a6.set(avt.x + integer7, avt.y + integer8, avt.z + integer9);
                    if (this.validPos(a6, vk)) {
                        this.validFarmlandAroundVillager.add(new BlockPos(a6));
                    }
                }
            }
        }
        this.aboveFarmlandPos = this.getValidFarmland(vk);
        return (this.canPlantStuff || this.wantsToReapStuff) && this.aboveFarmlandPos != null;
    }
    
    @Nullable
    private BlockPos getValidFarmland(final ServerLevel vk) {
        return this.validFarmlandAroundVillager.isEmpty() ? null : ((BlockPos)this.validFarmlandAroundVillager.get(vk.getRandom().nextInt(this.validFarmlandAroundVillager.size())));
    }
    
    private boolean validPos(final BlockPos ew, final ServerLevel vk) {
        final BlockState bvt4 = vk.getBlockState(ew);
        final Block bmv5 = bvt4.getBlock();
        final Block bmv6 = vk.getBlockState(ew.below()).getBlock();
        return (bmv5 instanceof CropBlock && ((CropBlock)bmv5).isMaxAge(bvt4) && this.wantsToReapStuff) || (bvt4.isAir() && bmv6 instanceof FarmBlock && this.canPlantStuff);
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        if (long3 > this.nextOkStartTime && this.aboveFarmlandPos != null) {
            avt.getBrain().<BlockPosWrapper>setMemory((MemoryModuleType<BlockPosWrapper>)MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(this.aboveFarmlandPos));
            avt.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosWrapper(this.aboveFarmlandPos), 0.5f, 1));
        }
    }
    
    @Override
    protected void stop(final ServerLevel vk, final Villager avt, final long long3) {
        avt.getBrain().<PositionWrapper>eraseMemory(MemoryModuleType.LOOK_TARGET);
        avt.getBrain().<WalkTarget>eraseMemory(MemoryModuleType.WALK_TARGET);
        this.timeWorkedSoFar = 0;
        this.nextOkStartTime = long3 + 40L;
    }
    
    @Override
    protected void tick(final ServerLevel vk, final Villager avt, final long long3) {
        if (this.aboveFarmlandPos != null && long3 > this.nextOkStartTime) {
            final BlockState bvt6 = vk.getBlockState(this.aboveFarmlandPos);
            final Block bmv7 = bvt6.getBlock();
            final Block bmv8 = vk.getBlockState(this.aboveFarmlandPos.below()).getBlock();
            if (bmv7 instanceof CropBlock && ((CropBlock)bmv7).isMaxAge(bvt6) && this.wantsToReapStuff) {
                vk.destroyBlock(this.aboveFarmlandPos, true);
            }
            if (bvt6.isAir() && bmv8 instanceof FarmBlock && this.canPlantStuff) {
                final SimpleContainer aho9 = avt.getInventory();
                int integer10 = 0;
                while (integer10 < aho9.getContainerSize()) {
                    final ItemStack bcj11 = aho9.getItem(integer10);
                    boolean boolean12 = false;
                    if (!bcj11.isEmpty()) {
                        if (bcj11.getItem() == Items.WHEAT_SEEDS) {
                            vk.setBlock(this.aboveFarmlandPos, Blocks.WHEAT.defaultBlockState(), 3);
                            boolean12 = true;
                        }
                        else if (bcj11.getItem() == Items.POTATO) {
                            vk.setBlock(this.aboveFarmlandPos, Blocks.POTATOES.defaultBlockState(), 3);
                            boolean12 = true;
                        }
                        else if (bcj11.getItem() == Items.CARROT) {
                            vk.setBlock(this.aboveFarmlandPos, Blocks.CARROTS.defaultBlockState(), 3);
                            boolean12 = true;
                        }
                        else if (bcj11.getItem() == Items.BEETROOT_SEEDS) {
                            vk.setBlock(this.aboveFarmlandPos, Blocks.BEETROOTS.defaultBlockState(), 3);
                            boolean12 = true;
                        }
                    }
                    if (boolean12) {
                        vk.playSound(null, this.aboveFarmlandPos.getX(), this.aboveFarmlandPos.getY(), this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0f, 1.0f);
                        bcj11.shrink(1);
                        if (bcj11.isEmpty()) {
                            aho9.setItem(integer10, ItemStack.EMPTY);
                            break;
                        }
                        break;
                    }
                    else {
                        ++integer10;
                    }
                }
            }
            if (bmv7 instanceof CropBlock && !((CropBlock)bmv7).isMaxAge(bvt6)) {
                this.validFarmlandAroundVillager.remove(this.aboveFarmlandPos);
                this.aboveFarmlandPos = this.getValidFarmland(vk);
                if (this.aboveFarmlandPos != null) {
                    this.nextOkStartTime = long3 + 20L;
                    avt.getBrain().<WalkTarget>setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosWrapper(this.aboveFarmlandPos), 0.5f, 1));
                    avt.getBrain().<BlockPosWrapper>setMemory((MemoryModuleType<BlockPosWrapper>)MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(this.aboveFarmlandPos));
                }
            }
        }
        ++this.timeWorkedSoFar;
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final Villager avt, final long long3) {
        return this.timeWorkedSoFar < 200;
    }
}

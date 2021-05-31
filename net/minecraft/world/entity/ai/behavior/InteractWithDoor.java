package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.level.pathfinder.Node;
import java.util.HashSet;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.world.level.Level;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.tags.BlockTags;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.Brain;
import java.util.stream.Collectors;
import net.minecraft.core.GlobalPos;
import java.util.List;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.LivingEntity;

public class InteractWithDoor extends Behavior<LivingEntity> {
    public InteractWithDoor() {
        super((Map)ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTABLE_DOORS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.OPENED_DOORS, MemoryStatus.REGISTERED));
    }
    
    @Override
    protected void start(final ServerLevel vk, final LivingEntity aix, final long long3) {
        final Brain<?> ajm6 = aix.getBrain();
        final Path cnr7 = (Path)ajm6.<Path>getMemory(MemoryModuleType.PATH).get();
        final List<GlobalPos> list8 = (List<GlobalPos>)ajm6.<List<GlobalPos>>getMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
        final List<BlockPos> list9 = (List<BlockPos>)cnr7.getNodes().stream().map(cnp -> new BlockPos(cnp.x, cnp.y, cnp.z)).collect(Collectors.toList());
        final Set<BlockPos> set10 = this.getDoorsThatAreOnMyPath(vk, list8, list9);
        final int integer11 = cnr7.getIndex() - 1;
        this.openOrCloseDoors(vk, list9, set10, integer11, aix, ajm6);
    }
    
    private Set<BlockPos> getDoorsThatAreOnMyPath(final ServerLevel vk, final List<GlobalPos> list2, final List<BlockPos> list3) {
        return (Set<BlockPos>)list2.stream().filter(fd -> fd.dimension() == vk.getDimension().getType()).map(GlobalPos::pos).filter(list3::contains).collect(Collectors.toSet());
    }
    
    private void openOrCloseDoors(final ServerLevel vk, final List<BlockPos> list, final Set<BlockPos> set, final int integer, final LivingEntity aix, final Brain<?> ajm) {
        set.forEach(ew -> {
            final int integer2 = list.indexOf(ew);
            final BlockState bvt7 = vk.getBlockState(ew);
            final Block bmv8 = bvt7.getBlock();
            if (BlockTags.WOODEN_DOORS.contains(bmv8) && bmv8 instanceof DoorBlock) {
                final boolean boolean9 = integer2 >= integer;
                ((DoorBlock)bmv8).setOpen(vk, ew, boolean9);
                final GlobalPos fd10 = GlobalPos.of(vk.getDimension().getType(), ew);
                if (!ajm.<Set<GlobalPos>>getMemory(MemoryModuleType.OPENED_DOORS).isPresent() && boolean9) {
                    ajm.<HashSet>setMemory(MemoryModuleType.OPENED_DOORS, Sets.newHashSet((Object[])new GlobalPos[] { fd10 }));
                }
                else {
                    ajm.<Set<GlobalPos>>getMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
                        if (boolean9) {
                            set.add(fd10);
                        }
                        else {
                            set.remove(fd10);
                        }
                    });
                }
            }
        });
        closeAllOpenedDoors(vk, list, integer, aix, ajm);
    }
    
    public static void closeAllOpenedDoors(final ServerLevel vk, final List<BlockPos> list, final int integer, final LivingEntity aix, final Brain<?> ajm) {
        ajm.<Set<GlobalPos>>getMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
            final Iterator<GlobalPos> iterator6 = (Iterator<GlobalPos>)set.iterator();
            while (iterator6.hasNext()) {
                final GlobalPos fd7 = (GlobalPos)iterator6.next();
                final BlockPos ew8 = fd7.pos();
                final int integer2 = list.indexOf(ew8);
                if (vk.getDimension().getType() != fd7.dimension()) {
                    iterator6.remove();
                }
                else {
                    final BlockState bvt10 = vk.getBlockState(ew8);
                    final Block bmv11 = bvt10.getBlock();
                    if (!BlockTags.WOODEN_DOORS.contains(bmv11) || !(bmv11 instanceof DoorBlock) || integer2 >= integer || !ew8.closerThan(aix.position(), 4.0)) {
                        continue;
                    }
                    ((DoorBlock)bmv11).setOpen(vk, ew8, false);
                    iterator6.remove();
                }
            }
        });
    }
}

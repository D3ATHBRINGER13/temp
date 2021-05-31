package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class AssignProfessionFromJobSite extends Behavior<Villager> {
    public AssignProfessionFromJobSite() {
        super((Map)ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT));
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        return avt.getVillagerData().getProfession() == VillagerProfession.NONE;
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        final GlobalPos fd6 = (GlobalPos)avt.getBrain().<GlobalPos>getMemory(MemoryModuleType.JOB_SITE).get();
        final MinecraftServer minecraftServer7 = vk.getServer();
        minecraftServer7.getLevel(fd6.dimension()).getPoiManager().getType(fd6.pos()).ifPresent(aqs -> Registry.VILLAGER_PROFESSION.stream().filter(avw -> avw.getJobPoiType() == aqs).findFirst().ifPresent(avw -> {
            avt.setVillagerData(avt.getVillagerData().setProfession(avw));
            avt.refreshBrain(vk);
        }));
    }
}

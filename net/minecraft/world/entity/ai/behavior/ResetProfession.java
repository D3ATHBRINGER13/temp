package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class ResetProfession extends Behavior<Villager> {
    public ResetProfession() {
        super((Map)ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT));
    }
    
    @Override
    protected boolean checkExtraStartConditions(final ServerLevel vk, final Villager avt) {
        final VillagerData avu4 = avt.getVillagerData();
        return avu4.getProfession() != VillagerProfession.NONE && avu4.getProfession() != VillagerProfession.NITWIT && avt.getVillagerXp() == 0 && avu4.getLevel() <= 1;
    }
    
    @Override
    protected void start(final ServerLevel vk, final Villager avt, final long long3) {
        avt.setVillagerData(avt.getVillagerData().setProfession(VillagerProfession.NONE));
        avt.refreshBrain(vk);
    }
}

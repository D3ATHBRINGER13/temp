package net.minecraft.world.entity.ai.behavior;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import java.util.Map;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Set;
import net.minecraft.world.entity.LivingEntity;

public class GateBehavior<E extends LivingEntity> extends Behavior<E> {
    private final Set<MemoryModuleType<?>> exitErasedMemories;
    private final OrderPolicy orderPolicy;
    private final RunningPolicy runningPolicy;
    private final WeightedList<Behavior<? super E>> behaviors;
    
    public GateBehavior(final Map<MemoryModuleType<?>, MemoryStatus> map, final Set<MemoryModuleType<?>> set, final OrderPolicy a, final RunningPolicy b, final List<Pair<Behavior<? super E>, Integer>> list) {
        super(map);
        this.behaviors = new WeightedList<Behavior<? super E>>();
        this.exitErasedMemories = set;
        this.orderPolicy = a;
        this.runningPolicy = b;
        list.forEach(pair -> this.behaviors.add(pair.getFirst(), (int)pair.getSecond()));
    }
    
    @Override
    protected boolean canStillUse(final ServerLevel vk, final E aix, final long long3) {
        return this.behaviors.stream().filter(ajy -> ajy.getStatus() == Status.RUNNING).anyMatch(ajy -> ajy.canStillUse(vk, aix, long3));
    }
    
    @Override
    protected boolean timedOut(final long long1) {
        return false;
    }
    
    @Override
    protected void start(final ServerLevel vk, final E aix, final long long3) {
        this.orderPolicy.apply(this.behaviors);
        this.runningPolicy.<E>apply(this.behaviors, vk, aix, long3);
    }
    
    @Override
    protected void tick(final ServerLevel vk, final E aix, final long long3) {
        this.behaviors.stream().filter(ajy -> ajy.getStatus() == Status.RUNNING).forEach(ajy -> ajy.tickOrStop(vk, aix, long3));
    }
    
    @Override
    protected void stop(final ServerLevel vk, final E aix, final long long3) {
        this.behaviors.stream().filter(ajy -> ajy.getStatus() == Status.RUNNING).forEach(ajy -> ajy.doStop(vk, aix, long3));
        this.exitErasedMemories.forEach(aix.getBrain()::eraseMemory);
    }
    
    @Override
    public String toString() {
        final Set<? extends Behavior<? super E>> set2 = this.behaviors.stream().filter(ajy -> ajy.getStatus() == Status.RUNNING).collect(Collectors.toSet());
        return "(" + this.getClass().getSimpleName() + "): " + set2;
    }
    
    enum OrderPolicy {
        ORDERED((Consumer<WeightedList<?>>)(ama -> {})), 
        SHUFFLED((Consumer<WeightedList<?>>)WeightedList::shuffle);
        
        private final Consumer<WeightedList<?>> consumer;
        
        private OrderPolicy(final Consumer<WeightedList<?>> consumer) {
            this.consumer = consumer;
        }
        
        public void apply(final WeightedList<?> ama) {
            this.consumer.accept(ama);
        }
    }
    
    enum RunningPolicy {
        RUN_ONE {
            @Override
            public <E extends LivingEntity> void apply(final WeightedList<Behavior<? super E>> ama, final ServerLevel vk, final E aix, final long long4) {
                ama.stream().filter(ajy -> ajy.getStatus() == Status.STOPPED).filter(ajy -> ajy.tryStart(vk, aix, long4)).findFirst();
            }
        }, 
        TRY_ALL {
            @Override
            public <E extends LivingEntity> void apply(final WeightedList<Behavior<? super E>> ama, final ServerLevel vk, final E aix, final long long4) {
                ama.stream().filter(ajy -> ajy.getStatus() == Status.STOPPED).forEach(ajy -> ajy.tryStart(vk, aix, long4));
            }
        };
        
        public abstract <E extends LivingEntity> void apply(final WeightedList<Behavior<? super E>> ama, final ServerLevel vk, final E aix, final long long4);
    }
}

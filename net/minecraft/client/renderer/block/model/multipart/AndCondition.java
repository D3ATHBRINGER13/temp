package net.minecraft.client.renderer.block.model.multipart;

import java.util.stream.Collectors;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;

public class AndCondition implements Condition {
    private final Iterable<? extends Condition> conditions;
    
    public AndCondition(final Iterable<? extends Condition> iterable) {
        this.conditions = iterable;
    }
    
    public Predicate<BlockState> getPredicate(final StateDefinition<Block, BlockState> bvu) {
        final List<Predicate<BlockState>> list3 = (List<Predicate<BlockState>>)Streams.stream((Iterable)this.conditions).map(doq -> doq.getPredicate(bvu)).collect(Collectors.toList());
        return (Predicate<BlockState>)(bvt -> list3.stream().allMatch(predicate -> predicate.test(bvt)));
    }
}

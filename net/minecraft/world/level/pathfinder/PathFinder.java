package net.minecraft.world.level.pathfinder;

import java.util.List;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.Comparator;
import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;
import com.google.common.collect.Sets;
import java.util.Set;

public class PathFinder {
    private final BinaryHeap openSet;
    private final Set<Node> closedSet;
    private final Node[] neighbors;
    private final int maxVisitedNodes;
    private NodeEvaluator nodeEvaluator;
    
    public PathFinder(final NodeEvaluator cnq, final int integer) {
        this.openSet = new BinaryHeap();
        this.closedSet = (Set<Node>)Sets.newHashSet();
        this.neighbors = new Node[32];
        this.nodeEvaluator = cnq;
        this.maxVisitedNodes = integer;
    }
    
    @Nullable
    public Path findPath(final LevelReader bhu, final Mob aiy, final Set<BlockPos> set, final float float4, final int integer) {
        this.openSet.clear();
        this.nodeEvaluator.prepare(bhu, aiy);
        final Node cnp7 = this.nodeEvaluator.getStart();
        final Map<Target, BlockPos> map8 = (Map<Target, BlockPos>)set.stream().collect(Collectors.toMap(ew -> this.nodeEvaluator.getGoal(ew.getX(), ew.getY(), ew.getZ()), Function.identity()));
        final Path cnr9 = this.findPath(cnp7, map8, float4, integer);
        this.nodeEvaluator.done();
        return cnr9;
    }
    
    @Nullable
    private Path findPath(final Node cnp, final Map<Target, BlockPos> map, final float float3, final int integer) {
        final Set<Target> set6 = (Set<Target>)map.keySet();
        cnp.g = 0.0f;
        cnp.h = this.getBestH(cnp, set6);
        cnp.f = cnp.h;
        this.openSet.clear();
        this.closedSet.clear();
        this.openSet.insert(cnp);
        int integer2 = 0;
        while (!this.openSet.isEmpty() && ++integer2 < this.maxVisitedNodes) {
            final Node cnp2 = this.openSet.pop();
            cnp2.closed = true;
            set6.stream().filter(cnv -> cnp2.distanceManhattan(cnv) <= integer).forEach(Target::setReached);
            if (set6.stream().anyMatch(Target::isReached)) {
                break;
            }
            if (cnp2.distanceTo(cnp) >= float3) {
                continue;
            }
            for (int integer3 = this.nodeEvaluator.getNeighbors(this.neighbors, cnp2), integer4 = 0; integer4 < integer3; ++integer4) {
                final Node cnp3 = this.neighbors[integer4];
                final float float4 = cnp2.distanceTo(cnp3);
                cnp3.walkedDistance = cnp2.walkedDistance + float4;
                final float float5 = cnp2.g + float4 + cnp3.costMalus;
                if (cnp3.walkedDistance < float3 && (!cnp3.inOpenSet() || float5 < cnp3.g)) {
                    cnp3.cameFrom = cnp2;
                    cnp3.g = float5;
                    cnp3.h = this.getBestH(cnp3, set6) * 1.5f;
                    if (cnp3.inOpenSet()) {
                        this.openSet.changeCost(cnp3, cnp3.g + cnp3.h);
                    }
                    else {
                        cnp3.f = cnp3.g + cnp3.h;
                        this.openSet.insert(cnp3);
                    }
                }
            }
        }
        Stream<Path> stream8;
        if (set6.stream().anyMatch(Target::isReached)) {
            stream8 = (Stream<Path>)set6.stream().filter(Target::isReached).map(cnv -> this.reconstructPath(cnv.getBestNode(), (BlockPos)map.get(cnv), true)).sorted(Comparator.comparingInt(Path::getSize));
        }
        else {
            stream8 = (Stream<Path>)set6.stream().map(cnv -> this.reconstructPath(cnv.getBestNode(), (BlockPos)map.get(cnv), false)).sorted(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getSize));
        }
        final Optional<Path> optional9 = (Optional<Path>)stream8.findFirst();
        if (!optional9.isPresent()) {
            return null;
        }
        final Path cnr10 = (Path)optional9.get();
        return cnr10;
    }
    
    private float getBestH(final Node cnp, final Set<Target> set) {
        float float4 = Float.MAX_VALUE;
        for (final Target cnv6 : set) {
            final float float5 = cnp.distanceTo(cnv6);
            cnv6.updateBest(float5, cnp);
            float4 = Math.min(float5, float4);
        }
        return float4;
    }
    
    private Path reconstructPath(final Node cnp, final BlockPos ew, final boolean boolean3) {
        final List<Node> list5 = (List<Node>)Lists.newArrayList();
        Node cnp2 = cnp;
        list5.add(0, cnp2);
        while (cnp2.cameFrom != null) {
            cnp2 = cnp2.cameFrom;
            list5.add(0, cnp2);
        }
        return new Path(list5, ew, boolean3);
    }
}

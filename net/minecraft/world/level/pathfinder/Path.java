package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import java.util.Set;
import java.util.List;

public class Path {
    private final List<Node> nodes;
    private Node[] openSet;
    private Node[] closedSet;
    private Set<Target> targetNodes;
    private int index;
    private final BlockPos target;
    private final float distToTarget;
    private final boolean reached;
    
    public Path(final List<Node> list, final BlockPos ew, final boolean boolean3) {
        this.openSet = new Node[0];
        this.closedSet = new Node[0];
        this.nodes = list;
        this.target = ew;
        this.distToTarget = (list.isEmpty() ? Float.MAX_VALUE : ((Node)this.nodes.get(this.nodes.size() - 1)).distanceManhattan(this.target));
        this.reached = boolean3;
    }
    
    public void next() {
        ++this.index;
    }
    
    public boolean isDone() {
        return this.index >= this.nodes.size();
    }
    
    @Nullable
    public Node last() {
        if (!this.nodes.isEmpty()) {
            return (Node)this.nodes.get(this.nodes.size() - 1);
        }
        return null;
    }
    
    public Node get(final int integer) {
        return (Node)this.nodes.get(integer);
    }
    
    public List<Node> getNodes() {
        return this.nodes;
    }
    
    public void truncate(final int integer) {
        if (this.nodes.size() > integer) {
            this.nodes.subList(integer, this.nodes.size()).clear();
        }
    }
    
    public void set(final int integer, final Node cnp) {
        this.nodes.set(integer, cnp);
    }
    
    public int getSize() {
        return this.nodes.size();
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int integer) {
        this.index = integer;
    }
    
    public Vec3 getPos(final Entity aio, final int integer) {
        final Node cnp4 = (Node)this.nodes.get(integer);
        final double double5 = cnp4.x + (int)(aio.getBbWidth() + 1.0f) * 0.5;
        final double double6 = cnp4.y;
        final double double7 = cnp4.z + (int)(aio.getBbWidth() + 1.0f) * 0.5;
        return new Vec3(double5, double6, double7);
    }
    
    public Vec3 currentPos(final Entity aio) {
        return this.getPos(aio, this.index);
    }
    
    public Vec3 currentPos() {
        final Node cnp2 = (Node)this.nodes.get(this.index);
        return new Vec3(cnp2.x, cnp2.y, cnp2.z);
    }
    
    public boolean sameAs(@Nullable final Path cnr) {
        if (cnr == null) {
            return false;
        }
        if (cnr.nodes.size() != this.nodes.size()) {
            return false;
        }
        for (int integer3 = 0; integer3 < this.nodes.size(); ++integer3) {
            final Node cnp4 = (Node)this.nodes.get(integer3);
            final Node cnp5 = (Node)cnr.nodes.get(integer3);
            if (cnp4.x != cnp5.x || cnp4.y != cnp5.y || cnp4.z != cnp5.z) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canReach() {
        return this.reached;
    }
    
    public Node[] getOpenSet() {
        return this.openSet;
    }
    
    public Node[] getClosedSet() {
        return this.closedSet;
    }
    
    public static Path createFromStream(final FriendlyByteBuf je) {
        final boolean boolean2 = je.readBoolean();
        final int integer3 = je.readInt();
        final int integer4 = je.readInt();
        final Set<Target> set5 = (Set<Target>)Sets.newHashSet();
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            set5.add(Target.createFromStream(je));
        }
        final BlockPos ew6 = new BlockPos(je.readInt(), je.readInt(), je.readInt());
        final List<Node> list7 = (List<Node>)Lists.newArrayList();
        for (int integer6 = je.readInt(), integer7 = 0; integer7 < integer6; ++integer7) {
            list7.add(Node.createFromStream(je));
        }
        final Node[] arr9 = new Node[je.readInt()];
        for (int integer8 = 0; integer8 < arr9.length; ++integer8) {
            arr9[integer8] = Node.createFromStream(je);
        }
        final Node[] arr10 = new Node[je.readInt()];
        for (int integer9 = 0; integer9 < arr10.length; ++integer9) {
            arr10[integer9] = Node.createFromStream(je);
        }
        final Path cnr11 = new Path(list7, ew6, boolean2);
        cnr11.openSet = arr9;
        cnr11.closedSet = arr10;
        cnr11.targetNodes = set5;
        cnr11.index = integer3;
        return cnr11;
    }
    
    public String toString() {
        return new StringBuilder().append("Path(length=").append(this.nodes.size()).append(")").toString();
    }
    
    public BlockPos getTarget() {
        return this.target;
    }
    
    public float getDistToTarget() {
        return this.distToTarget;
    }
}

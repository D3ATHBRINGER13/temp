package net.minecraft.world.level.pathfinder;

import net.minecraft.network.FriendlyByteBuf;

public class Target extends Node {
    private float bestHeuristic;
    private Node bestNode;
    private boolean reached;
    
    public Target(final Node cnp) {
        super(cnp.x, cnp.y, cnp.z);
        this.bestHeuristic = Float.MAX_VALUE;
    }
    
    public Target(final int integer1, final int integer2, final int integer3) {
        super(integer1, integer2, integer3);
        this.bestHeuristic = Float.MAX_VALUE;
    }
    
    public void updateBest(final float float1, final Node cnp) {
        if (float1 < this.bestHeuristic) {
            this.bestHeuristic = float1;
            this.bestNode = cnp;
        }
    }
    
    public Node getBestNode() {
        return this.bestNode;
    }
    
    public void setReached() {
        this.reached = true;
    }
    
    public boolean isReached() {
        return this.reached;
    }
    
    public static Target createFromStream(final FriendlyByteBuf je) {
        final Target cnv2 = new Target(je.readInt(), je.readInt(), je.readInt());
        cnv2.walkedDistance = je.readFloat();
        cnv2.costMalus = je.readFloat();
        cnv2.closed = je.readBoolean();
        cnv2.type = BlockPathTypes.values()[je.readInt()];
        cnv2.f = je.readFloat();
        return cnv2;
    }
}

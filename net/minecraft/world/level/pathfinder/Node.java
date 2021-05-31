package net.minecraft.world.level.pathfinder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class Node {
    public final int x;
    public final int y;
    public final int z;
    private final int hash;
    public int heapIdx;
    public float g;
    public float h;
    public float f;
    public Node cameFrom;
    public boolean closed;
    public float walkedDistance;
    public float costMalus;
    public BlockPathTypes type;
    
    public Node(final int integer1, final int integer2, final int integer3) {
        this.heapIdx = -1;
        this.type = BlockPathTypes.BLOCKED;
        this.x = integer1;
        this.y = integer2;
        this.z = integer3;
        this.hash = createHash(integer1, integer2, integer3);
    }
    
    public Node cloneMove(final int integer1, final int integer2, final int integer3) {
        final Node cnp5 = new Node(integer1, integer2, integer3);
        cnp5.heapIdx = this.heapIdx;
        cnp5.g = this.g;
        cnp5.h = this.h;
        cnp5.f = this.f;
        cnp5.cameFrom = this.cameFrom;
        cnp5.closed = this.closed;
        cnp5.walkedDistance = this.walkedDistance;
        cnp5.costMalus = this.costMalus;
        cnp5.type = this.type;
        return cnp5;
    }
    
    public static int createHash(final int integer1, final int integer2, final int integer3) {
        return (integer2 & 0xFF) | (integer1 & 0x7FFF) << 8 | (integer3 & 0x7FFF) << 24 | ((integer1 < 0) ? Integer.MIN_VALUE : 0) | ((integer3 < 0) ? 32768 : 0);
    }
    
    public float distanceTo(final Node cnp) {
        final float float3 = (float)(cnp.x - this.x);
        final float float4 = (float)(cnp.y - this.y);
        final float float5 = (float)(cnp.z - this.z);
        return Mth.sqrt(float3 * float3 + float4 * float4 + float5 * float5);
    }
    
    public float distanceToSqr(final Node cnp) {
        final float float3 = (float)(cnp.x - this.x);
        final float float4 = (float)(cnp.y - this.y);
        final float float5 = (float)(cnp.z - this.z);
        return float3 * float3 + float4 * float4 + float5 * float5;
    }
    
    public float distanceManhattan(final Node cnp) {
        final float float3 = (float)Math.abs(cnp.x - this.x);
        final float float4 = (float)Math.abs(cnp.y - this.y);
        final float float5 = (float)Math.abs(cnp.z - this.z);
        return float3 + float4 + float5;
    }
    
    public float distanceManhattan(final BlockPos ew) {
        final float float3 = (float)Math.abs(ew.getX() - this.x);
        final float float4 = (float)Math.abs(ew.getY() - this.y);
        final float float5 = (float)Math.abs(ew.getZ() - this.z);
        return float3 + float4 + float5;
    }
    
    public BlockPos asBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }
    
    public boolean equals(final Object object) {
        if (object instanceof Node) {
            final Node cnp3 = (Node)object;
            return this.hash == cnp3.hash && this.x == cnp3.x && this.y == cnp3.y && this.z == cnp3.z;
        }
        return false;
    }
    
    public int hashCode() {
        return this.hash;
    }
    
    public boolean inOpenSet() {
        return this.heapIdx >= 0;
    }
    
    public String toString() {
        return new StringBuilder().append("Node{x=").append(this.x).append(", y=").append(this.y).append(", z=").append(this.z).append('}').toString();
    }
    
    public static Node createFromStream(final FriendlyByteBuf je) {
        final Node cnp2 = new Node(je.readInt(), je.readInt(), je.readInt());
        cnp2.walkedDistance = je.readFloat();
        cnp2.costMalus = je.readFloat();
        cnp2.closed = je.readBoolean();
        cnp2.type = BlockPathTypes.values()[je.readInt()];
        cnp2.f = je.readFloat();
        return cnp2;
    }
}

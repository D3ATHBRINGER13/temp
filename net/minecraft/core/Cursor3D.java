package net.minecraft.core;

public class Cursor3D {
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private int x;
    private int y;
    private int z;
    private boolean started;
    
    public Cursor3D(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.minX = integer1;
        this.minY = integer2;
        this.minZ = integer3;
        this.maxX = integer4;
        this.maxY = integer5;
        this.maxZ = integer6;
    }
    
    public boolean advance() {
        if (!this.started) {
            this.x = this.minX;
            this.y = this.minY;
            this.z = this.minZ;
            return this.started = true;
        }
        if (this.x == this.maxX && this.y == this.maxY && this.z == this.maxZ) {
            return false;
        }
        if (this.x < this.maxX) {
            ++this.x;
        }
        else if (this.y < this.maxY) {
            this.x = this.minX;
            ++this.y;
        }
        else if (this.z < this.maxZ) {
            this.x = this.minX;
            this.y = this.minY;
            ++this.z;
        }
        return true;
    }
    
    public int nextX() {
        return this.x;
    }
    
    public int nextY() {
        return this.y;
    }
    
    public int nextZ() {
        return this.z;
    }
    
    public int getNextType() {
        int integer2 = 0;
        if (this.x == this.minX || this.x == this.maxX) {
            ++integer2;
        }
        if (this.y == this.minY || this.y == this.maxY) {
            ++integer2;
        }
        if (this.z == this.minZ || this.z == this.maxZ) {
            ++integer2;
        }
        return integer2;
    }
}

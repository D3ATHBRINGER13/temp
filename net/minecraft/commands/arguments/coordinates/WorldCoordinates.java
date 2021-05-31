package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.CommandSourceStack;

public class WorldCoordinates implements Coordinates {
    private final WorldCoordinate x;
    private final WorldCoordinate y;
    private final WorldCoordinate z;
    
    public WorldCoordinates(final WorldCoordinate ds1, final WorldCoordinate ds2, final WorldCoordinate ds3) {
        this.x = ds1;
        this.y = ds2;
        this.z = ds3;
    }
    
    public Vec3 getPosition(final CommandSourceStack cd) {
        final Vec3 csi3 = cd.getPosition();
        return new Vec3(this.x.get(csi3.x), this.y.get(csi3.y), this.z.get(csi3.z));
    }
    
    public Vec2 getRotation(final CommandSourceStack cd) {
        final Vec2 csh3 = cd.getRotation();
        return new Vec2((float)this.x.get(csh3.x), (float)this.y.get(csh3.y));
    }
    
    public boolean isXRelative() {
        return this.x.isRelative();
    }
    
    public boolean isYRelative() {
        return this.y.isRelative();
    }
    
    public boolean isZRelative() {
        return this.z.isRelative();
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof WorldCoordinates)) {
            return false;
        }
        final WorldCoordinates dt3 = (WorldCoordinates)object;
        return this.x.equals(dt3.x) && this.y.equals(dt3.y) && this.z.equals(dt3.z);
    }
    
    public static WorldCoordinates parseInt(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        final WorldCoordinate ds3 = WorldCoordinate.parseInt(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(integer2);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        final WorldCoordinate ds4 = WorldCoordinate.parseInt(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(integer2);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        final WorldCoordinate ds5 = WorldCoordinate.parseInt(stringReader);
        return new WorldCoordinates(ds3, ds4, ds5);
    }
    
    public static WorldCoordinates parseDouble(final StringReader stringReader, final boolean boolean2) throws CommandSyntaxException {
        final int integer3 = stringReader.getCursor();
        final WorldCoordinate ds4 = WorldCoordinate.parseDouble(stringReader, boolean2);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(integer3);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        final WorldCoordinate ds5 = WorldCoordinate.parseDouble(stringReader, false);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(integer3);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        final WorldCoordinate ds6 = WorldCoordinate.parseDouble(stringReader, boolean2);
        return new WorldCoordinates(ds4, ds5, ds6);
    }
    
    public static WorldCoordinates current() {
        return new WorldCoordinates(new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0));
    }
    
    public int hashCode() {
        int integer2 = this.x.hashCode();
        integer2 = 31 * integer2 + this.y.hashCode();
        integer2 = 31 * integer2 + this.z.hashCode();
        return integer2;
    }
}

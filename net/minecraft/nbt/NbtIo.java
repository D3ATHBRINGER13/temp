package net.minecraft.nbt;

import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;

public class NbtIo {
    public static CompoundTag readCompressed(final InputStream inputStream) throws IOException {
        try (final DataInputStream dataInputStream2 = new DataInputStream((InputStream)new BufferedInputStream((InputStream)new GZIPInputStream(inputStream)))) {
            return read((DataInput)dataInputStream2, NbtAccounter.UNLIMITED);
        }
    }
    
    public static void writeCompressed(final CompoundTag id, final OutputStream outputStream) throws IOException {
        try (final DataOutputStream dataOutputStream3 = new DataOutputStream((OutputStream)new BufferedOutputStream((OutputStream)new GZIPOutputStream(outputStream)))) {
            write(id, (DataOutput)dataOutputStream3);
        }
    }
    
    public static void safeWrite(final CompoundTag id, final File file) throws IOException {
        final File file2 = new File(file.getAbsolutePath() + "_tmp");
        if (file2.exists()) {
            file2.delete();
        }
        write(id, file2);
        if (file.exists()) {
            file.delete();
        }
        if (file.exists()) {
            throw new IOException(new StringBuilder().append("Failed to delete ").append(file).toString());
        }
        file2.renameTo(file);
    }
    
    public static void write(final CompoundTag id, final File file) throws IOException {
        final DataOutputStream dataOutputStream3 = new DataOutputStream((OutputStream)new FileOutputStream(file));
        try {
            write(id, (DataOutput)dataOutputStream3);
        }
        finally {
            dataOutputStream3.close();
        }
    }
    
    @Nullable
    public static CompoundTag read(final File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        final DataInputStream dataInputStream2 = new DataInputStream((InputStream)new FileInputStream(file));
        try {
            return read((DataInput)dataInputStream2, NbtAccounter.UNLIMITED);
        }
        finally {
            dataInputStream2.close();
        }
    }
    
    public static CompoundTag read(final DataInputStream dataInputStream) throws IOException {
        return read((DataInput)dataInputStream, NbtAccounter.UNLIMITED);
    }
    
    public static CompoundTag read(final DataInput dataInput, final NbtAccounter in) throws IOException {
        final Tag iu3 = readUnnamedTag(dataInput, 0, in);
        if (iu3 instanceof CompoundTag) {
            return (CompoundTag)iu3;
        }
        throw new IOException("Root tag must be a named compound tag");
    }
    
    public static void write(final CompoundTag id, final DataOutput dataOutput) throws IOException {
        writeUnnamedTag(id, dataOutput);
    }
    
    private static void writeUnnamedTag(final Tag iu, final DataOutput dataOutput) throws IOException {
        dataOutput.writeByte((int)iu.getId());
        if (iu.getId() == 0) {
            return;
        }
        dataOutput.writeUTF("");
        iu.write(dataOutput);
    }
    
    private static Tag readUnnamedTag(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        final byte byte4 = dataInput.readByte();
        if (byte4 == 0) {
            return new EndTag();
        }
        dataInput.readUTF();
        final Tag iu5 = Tag.newTag(byte4);
        try {
            iu5.load(dataInput, integer, in);
        }
        catch (IOException iOException6) {
            final CrashReport d7 = CrashReport.forThrowable((Throwable)iOException6, "Loading NBT data");
            final CrashReportCategory e8 = d7.addCategory("NBT Tag");
            e8.setDetail("Tag type", byte4);
            throw new ReportedException(d7);
        }
        return iu5;
    }
}

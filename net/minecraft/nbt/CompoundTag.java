package net.minecraft.nbt;

import org.apache.logging.log4j.LogManager;
import com.google.common.base.Strings;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.util.Objects;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import java.util.Collection;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.ReportedException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import java.util.Set;
import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;
import java.io.DataOutput;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;

public class CompoundTag implements Tag {
    private static final Logger LOGGER;
    private static final Pattern SIMPLE_VALUE;
    private final Map<String, Tag> tags;
    
    public CompoundTag() {
        this.tags = (Map<String, Tag>)Maps.newHashMap();
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        for (final String string4 : this.tags.keySet()) {
            final Tag iu5 = (Tag)this.tags.get(string4);
            writeNamedTag(string4, iu5, dataOutput);
        }
        dataOutput.writeByte(0);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(384L);
        if (integer > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        this.tags.clear();
        byte byte5;
        while ((byte5 = readNamedTagType(dataInput, in)) != 0) {
            final String string6 = readNamedTagName(dataInput, in);
            in.accountBits(224 + 16 * string6.length());
            final Tag iu7 = readNamedTagData(byte5, string6, dataInput, integer + 1, in);
            if (this.tags.put(string6, iu7) != null) {
                in.accountBits(288L);
            }
        }
    }
    
    public Set<String> getAllKeys() {
        return (Set<String>)this.tags.keySet();
    }
    
    public byte getId() {
        return 10;
    }
    
    public int size() {
        return this.tags.size();
    }
    
    @Nullable
    public Tag put(final String string, final Tag iu) {
        return (Tag)this.tags.put(string, iu);
    }
    
    public void putByte(final String string, final byte byte2) {
        this.tags.put(string, new ByteTag(byte2));
    }
    
    public void putShort(final String string, final short short2) {
        this.tags.put(string, new ShortTag(short2));
    }
    
    public void putInt(final String string, final int integer) {
        this.tags.put(string, new IntTag(integer));
    }
    
    public void putLong(final String string, final long long2) {
        this.tags.put(string, new LongTag(long2));
    }
    
    public void putUUID(final String string, final UUID uUID) {
        this.putLong(string + "Most", uUID.getMostSignificantBits());
        this.putLong(string + "Least", uUID.getLeastSignificantBits());
    }
    
    public UUID getUUID(final String string) {
        return new UUID(this.getLong(string + "Most"), this.getLong(string + "Least"));
    }
    
    public boolean hasUUID(final String string) {
        return this.contains(string + "Most", 99) && this.contains(string + "Least", 99);
    }
    
    public void putFloat(final String string, final float float2) {
        this.tags.put(string, new FloatTag(float2));
    }
    
    public void putDouble(final String string, final double double2) {
        this.tags.put(string, new DoubleTag(double2));
    }
    
    public void putString(final String string1, final String string2) {
        this.tags.put(string1, new StringTag(string2));
    }
    
    public void putByteArray(final String string, final byte[] arr) {
        this.tags.put(string, new ByteArrayTag(arr));
    }
    
    public void putIntArray(final String string, final int[] arr) {
        this.tags.put(string, new IntArrayTag(arr));
    }
    
    public void putIntArray(final String string, final List<Integer> list) {
        this.tags.put(string, new IntArrayTag(list));
    }
    
    public void putLongArray(final String string, final long[] arr) {
        this.tags.put(string, new LongArrayTag(arr));
    }
    
    public void putLongArray(final String string, final List<Long> list) {
        this.tags.put(string, new LongArrayTag(list));
    }
    
    public void putBoolean(final String string, final boolean boolean2) {
        this.putByte(string, (byte)(boolean2 ? 1 : 0));
    }
    
    @Nullable
    public Tag get(final String string) {
        return (Tag)this.tags.get(string);
    }
    
    public byte getTagType(final String string) {
        final Tag iu3 = (Tag)this.tags.get(string);
        if (iu3 == null) {
            return 0;
        }
        return iu3.getId();
    }
    
    public boolean contains(final String string) {
        return this.tags.containsKey(string);
    }
    
    public boolean contains(final String string, final int integer) {
        final int integer2 = this.getTagType(string);
        return integer2 == integer || (integer == 99 && (integer2 == 1 || integer2 == 2 || integer2 == 3 || integer2 == 4 || integer2 == 5 || integer2 == 6));
    }
    
    public byte getByte(final String string) {
        try {
            if (this.contains(string, 99)) {
                return ((NumericTag)this.tags.get(string)).getAsByte();
            }
        }
        catch (ClassCastException ex) {}
        return 0;
    }
    
    public short getShort(final String string) {
        try {
            if (this.contains(string, 99)) {
                return ((NumericTag)this.tags.get(string)).getAsShort();
            }
        }
        catch (ClassCastException ex) {}
        return 0;
    }
    
    public int getInt(final String string) {
        try {
            if (this.contains(string, 99)) {
                return ((NumericTag)this.tags.get(string)).getAsInt();
            }
        }
        catch (ClassCastException ex) {}
        return 0;
    }
    
    public long getLong(final String string) {
        try {
            if (this.contains(string, 99)) {
                return ((NumericTag)this.tags.get(string)).getAsLong();
            }
        }
        catch (ClassCastException ex) {}
        return 0L;
    }
    
    public float getFloat(final String string) {
        try {
            if (this.contains(string, 99)) {
                return ((NumericTag)this.tags.get(string)).getAsFloat();
            }
        }
        catch (ClassCastException ex) {}
        return 0.0f;
    }
    
    public double getDouble(final String string) {
        try {
            if (this.contains(string, 99)) {
                return ((NumericTag)this.tags.get(string)).getAsDouble();
            }
        }
        catch (ClassCastException ex) {}
        return 0.0;
    }
    
    public String getString(final String string) {
        try {
            if (this.contains(string, 8)) {
                return ((Tag)this.tags.get(string)).getAsString();
            }
        }
        catch (ClassCastException ex) {}
        return "";
    }
    
    public byte[] getByteArray(final String string) {
        try {
            if (this.contains(string, 7)) {
                return ((ByteArrayTag)this.tags.get(string)).getAsByteArray();
            }
        }
        catch (ClassCastException classCastException3) {
            throw new ReportedException(this.createReport(string, 7, classCastException3));
        }
        return new byte[0];
    }
    
    public int[] getIntArray(final String string) {
        try {
            if (this.contains(string, 11)) {
                return ((IntArrayTag)this.tags.get(string)).getAsIntArray();
            }
        }
        catch (ClassCastException classCastException3) {
            throw new ReportedException(this.createReport(string, 11, classCastException3));
        }
        return new int[0];
    }
    
    public long[] getLongArray(final String string) {
        try {
            if (this.contains(string, 12)) {
                return ((LongArrayTag)this.tags.get(string)).getAsLongArray();
            }
        }
        catch (ClassCastException classCastException3) {
            throw new ReportedException(this.createReport(string, 12, classCastException3));
        }
        return new long[0];
    }
    
    public CompoundTag getCompound(final String string) {
        try {
            if (this.contains(string, 10)) {
                return (CompoundTag)this.tags.get(string);
            }
        }
        catch (ClassCastException classCastException3) {
            throw new ReportedException(this.createReport(string, 10, classCastException3));
        }
        return new CompoundTag();
    }
    
    public ListTag getList(final String string, final int integer) {
        try {
            if (this.getTagType(string) == 9) {
                final ListTag ik4 = (ListTag)this.tags.get(string);
                if (ik4.isEmpty() || ik4.getElementType() == integer) {
                    return ik4;
                }
                return new ListTag();
            }
        }
        catch (ClassCastException classCastException4) {
            throw new ReportedException(this.createReport(string, 9, classCastException4));
        }
        return new ListTag();
    }
    
    public boolean getBoolean(final String string) {
        return this.getByte(string) != 0;
    }
    
    public void remove(final String string) {
        this.tags.remove(string);
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder("{");
        Collection<String> collection3 = (Collection<String>)this.tags.keySet();
        if (CompoundTag.LOGGER.isDebugEnabled()) {
            final List<String> list4 = (List<String>)Lists.newArrayList((Iterable)this.tags.keySet());
            Collections.sort((List)list4);
            collection3 = (Collection<String>)list4;
        }
        for (final String string5 : collection3) {
            if (stringBuilder2.length() != 1) {
                stringBuilder2.append(',');
            }
            stringBuilder2.append(handleEscape(string5)).append(':').append(this.tags.get(string5));
        }
        return stringBuilder2.append('}').toString();
    }
    
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }
    
    private CrashReport createReport(final String string, final int integer, final ClassCastException classCastException) {
        final CrashReport d5 = CrashReport.forThrowable((Throwable)classCastException, "Reading NBT data");
        final CrashReportCategory e6 = d5.addCategory("Corrupt NBT tag", 1);
        e6.setDetail("Tag type found", (CrashReportDetail<String>)(() -> CompoundTag.TAG_NAMES[((Tag)this.tags.get(string)).getId()]));
        e6.setDetail("Tag type expected", (CrashReportDetail<String>)(() -> CompoundTag.TAG_NAMES[integer]));
        e6.setDetail("Tag name", string);
        return d5;
    }
    
    public CompoundTag copy() {
        final CompoundTag id2 = new CompoundTag();
        for (final String string4 : this.tags.keySet()) {
            id2.put(string4, ((Tag)this.tags.get(string4)).copy());
        }
        return id2;
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)object).tags));
    }
    
    public int hashCode() {
        return this.tags.hashCode();
    }
    
    private static void writeNamedTag(final String string, final Tag iu, final DataOutput dataOutput) throws IOException {
        dataOutput.writeByte((int)iu.getId());
        if (iu.getId() == 0) {
            return;
        }
        dataOutput.writeUTF(string);
        iu.write(dataOutput);
    }
    
    private static byte readNamedTagType(final DataInput dataInput, final NbtAccounter in) throws IOException {
        return dataInput.readByte();
    }
    
    private static String readNamedTagName(final DataInput dataInput, final NbtAccounter in) throws IOException {
        return dataInput.readUTF();
    }
    
    static Tag readNamedTagData(final byte byte1, final String string, final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        final Tag iu6 = Tag.newTag(byte1);
        try {
            iu6.load(dataInput, integer, in);
        }
        catch (IOException iOException7) {
            final CrashReport d8 = CrashReport.forThrowable((Throwable)iOException7, "Loading NBT data");
            final CrashReportCategory e9 = d8.addCategory("NBT Tag");
            e9.setDetail("Tag name", string);
            e9.setDetail("Tag type", byte1);
            throw new ReportedException(d8);
        }
        return iu6;
    }
    
    public CompoundTag merge(final CompoundTag id) {
        for (final String string4 : id.tags.keySet()) {
            final Tag iu5 = (Tag)id.tags.get(string4);
            if (iu5.getId() == 10) {
                if (this.contains(string4, 10)) {
                    final CompoundTag id2 = this.getCompound(string4);
                    id2.merge((CompoundTag)iu5);
                }
                else {
                    this.put(string4, iu5.copy());
                }
            }
            else {
                this.put(string4, iu5.copy());
            }
        }
        return this;
    }
    
    protected static String handleEscape(final String string) {
        if (CompoundTag.SIMPLE_VALUE.matcher((CharSequence)string).matches()) {
            return string;
        }
        return StringTag.quoteAndEscape(string);
    }
    
    protected static Component handleEscapePretty(final String string) {
        if (CompoundTag.SIMPLE_VALUE.matcher((CharSequence)string).matches()) {
            return new TextComponent(string).withStyle(CompoundTag.SYNTAX_HIGHLIGHTING_KEY);
        }
        final String string2 = StringTag.quoteAndEscape(string);
        final String string3 = string2.substring(0, 1);
        final Component jo4 = new TextComponent(string2.substring(1, string2.length() - 1)).withStyle(CompoundTag.SYNTAX_HIGHLIGHTING_KEY);
        return new TextComponent(string3).append(jo4).append(string3);
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        if (this.tags.isEmpty()) {
            return new TextComponent("{}");
        }
        final Component jo4 = new TextComponent("{");
        Collection<String> collection5 = (Collection<String>)this.tags.keySet();
        if (CompoundTag.LOGGER.isDebugEnabled()) {
            final List<String> list6 = (List<String>)Lists.newArrayList((Iterable)this.tags.keySet());
            Collections.sort((List)list6);
            collection5 = (Collection<String>)list6;
        }
        if (!string.isEmpty()) {
            jo4.append("\n");
        }
        final Iterator<String> iterator6 = (Iterator<String>)collection5.iterator();
        while (iterator6.hasNext()) {
            final String string2 = (String)iterator6.next();
            final Component jo5 = new TextComponent(Strings.repeat(string, integer + 1)).append(handleEscapePretty(string2)).append(String.valueOf(':')).append(" ").append(((Tag)this.tags.get(string2)).getPrettyDisplay(string, integer + 1));
            if (iterator6.hasNext()) {
                jo5.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
            }
            jo4.append(jo5);
        }
        if (!string.isEmpty()) {
            jo4.append("\n").append(Strings.repeat(string, integer));
        }
        jo4.append("}");
        return jo4;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    }
}

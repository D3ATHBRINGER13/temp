package net.minecraft.client.resources.metadata.animation;

import java.util.Iterator;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.List;

public class AnimationMetadataSection {
    public static final AnimationMetadataSectionSerializer SERIALIZER;
    private final List<AnimationFrame> frames;
    private final int frameWidth;
    private final int frameHeight;
    private final int defaultFrameTime;
    private final boolean interpolatedFrames;
    
    public AnimationMetadataSection(final List<AnimationFrame> list, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        this.frames = list;
        this.frameWidth = integer2;
        this.frameHeight = integer3;
        this.defaultFrameTime = integer4;
        this.interpolatedFrames = boolean5;
    }
    
    public int getFrameHeight() {
        return this.frameHeight;
    }
    
    public int getFrameWidth() {
        return this.frameWidth;
    }
    
    public int getFrameCount() {
        return this.frames.size();
    }
    
    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }
    
    public boolean isInterpolatedFrames() {
        return this.interpolatedFrames;
    }
    
    private AnimationFrame getFrame(final int integer) {
        return (AnimationFrame)this.frames.get(integer);
    }
    
    public int getFrameTime(final int integer) {
        final AnimationFrame dyc3 = this.getFrame(integer);
        if (dyc3.isTimeUnknown()) {
            return this.defaultFrameTime;
        }
        return dyc3.getTime();
    }
    
    public int getFrameIndex(final int integer) {
        return ((AnimationFrame)this.frames.get(integer)).getIndex();
    }
    
    public Set<Integer> getUniqueFrameIndices() {
        final Set<Integer> set2 = (Set<Integer>)Sets.newHashSet();
        for (final AnimationFrame dyc4 : this.frames) {
            set2.add(dyc4.getIndex());
        }
        return set2;
    }
    
    static {
        SERIALIZER = new AnimationMetadataSectionSerializer();
    }
}

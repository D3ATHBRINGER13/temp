package net.minecraft.world.entity.ai.behavior;

import java.util.stream.Stream;
import java.util.Comparator;
import com.google.common.collect.Lists;
import java.util.Random;
import java.util.List;

public class WeightedList<U> {
    private final List<WeightedEntry<? extends U>> entries;
    private final Random random;
    
    public WeightedList() {
        this(new Random());
    }
    
    public WeightedList(final Random random) {
        this.entries = (List<WeightedEntry<? extends U>>)Lists.newArrayList();
        this.random = random;
    }
    
    public void add(final U object, final int integer) {
        this.entries.add(new WeightedEntry((Object)object, integer));
    }
    
    public void shuffle() {
        this.entries.forEach(a -> a.setRandom(this.random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(WeightedEntry::getRandWeight));
    }
    
    public Stream<? extends U> stream() {
        return this.entries.stream().map(WeightedEntry::getData);
    }
    
    public String toString() {
        return new StringBuilder().append("WeightedList[").append(this.entries).append("]").toString();
    }
    
    class WeightedEntry<T> {
        private final T data;
        private final int weight;
        private double randWeight;
        
        private WeightedEntry(final T object, final int integer) {
            this.weight = integer;
            this.data = object;
        }
        
        public double getRandWeight() {
            return this.randWeight;
        }
        
        public void setRandom(final float float1) {
            this.randWeight = -Math.pow((double)float1, (double)(1.0f / this.weight));
        }
        
        public T getData() {
            return this.data;
        }
        
        public String toString() {
            return new StringBuilder().append("").append(this.weight).append(":").append(this.data).toString();
        }
    }
}

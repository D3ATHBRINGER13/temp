package net.minecraft.world.level.levelgen;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public class GenerationStep {
    public enum Decoration {
        RAW_GENERATION("raw_generation"), 
        LOCAL_MODIFICATIONS("local_modifications"), 
        UNDERGROUND_STRUCTURES("underground_structures"), 
        SURFACE_STRUCTURES("surface_structures"), 
        UNDERGROUND_ORES("underground_ores"), 
        UNDERGROUND_DECORATION("underground_decoration"), 
        VEGETAL_DECORATION("vegetal_decoration"), 
        TOP_LAYER_MODIFICATION("top_layer_modification");
        
        private static final Map<String, Decoration> BY_NAME;
        private final String name;
        
        private Decoration(final String string3) {
            this.name = string3;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Decoration::getName, b -> b));
        }
    }
    
    public enum Carving {
        AIR("air"), 
        LIQUID("liquid");
        
        private static final Map<String, Carving> BY_NAME;
        private final String name;
        
        private Carving(final String string3) {
            this.name = string3;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(Carving::getName, a -> a));
        }
    }
}

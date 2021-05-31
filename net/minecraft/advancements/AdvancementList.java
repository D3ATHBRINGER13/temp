package net.minecraft.advancements;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;
import com.google.common.base.Functions;
import java.util.Iterator;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class AdvancementList {
    private static final Logger LOGGER;
    private final Map<ResourceLocation, Advancement> advancements;
    private final Set<Advancement> roots;
    private final Set<Advancement> tasks;
    private Listener listener;
    
    public AdvancementList() {
        this.advancements = (Map<ResourceLocation, Advancement>)Maps.newHashMap();
        this.roots = (Set<Advancement>)Sets.newLinkedHashSet();
        this.tasks = (Set<Advancement>)Sets.newLinkedHashSet();
    }
    
    private void remove(final Advancement q) {
        for (final Advancement q2 : q.getChildren()) {
            this.remove(q2);
        }
        AdvancementList.LOGGER.info("Forgot about advancement {}", q.getId());
        this.advancements.remove(q.getId());
        if (q.getParent() == null) {
            this.roots.remove(q);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementRoot(q);
            }
        }
        else {
            this.tasks.remove(q);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementTask(q);
            }
        }
    }
    
    public void remove(final Set<ResourceLocation> set) {
        for (final ResourceLocation qv4 : set) {
            final Advancement q5 = (Advancement)this.advancements.get(qv4);
            if (q5 == null) {
                AdvancementList.LOGGER.warn("Told to remove advancement {} but I don't know what that is", qv4);
            }
            else {
                this.remove(q5);
            }
        }
    }
    
    public void add(final Map<ResourceLocation, Advancement.Builder> map) {
        final Function<ResourceLocation, Advancement> function3 = (Function<ResourceLocation, Advancement>)Functions.forMap((Map)this.advancements, null);
        while (!map.isEmpty()) {
            boolean boolean4 = false;
            final Iterator<Map.Entry<ResourceLocation, Advancement.Builder>> iterator5 = (Iterator<Map.Entry<ResourceLocation, Advancement.Builder>>)map.entrySet().iterator();
            while (iterator5.hasNext()) {
                final Map.Entry<ResourceLocation, Advancement.Builder> entry6 = (Map.Entry<ResourceLocation, Advancement.Builder>)iterator5.next();
                final ResourceLocation qv7 = (ResourceLocation)entry6.getKey();
                final Advancement.Builder a8 = (Advancement.Builder)entry6.getValue();
                if (a8.canBuild(function3)) {
                    final Advancement q9 = a8.build(qv7);
                    this.advancements.put(qv7, q9);
                    boolean4 = true;
                    iterator5.remove();
                    if (q9.getParent() == null) {
                        this.roots.add(q9);
                        if (this.listener == null) {
                            continue;
                        }
                        this.listener.onAddAdvancementRoot(q9);
                    }
                    else {
                        this.tasks.add(q9);
                        if (this.listener == null) {
                            continue;
                        }
                        this.listener.onAddAdvancementTask(q9);
                    }
                }
            }
            if (!boolean4) {
                for (final Map.Entry<ResourceLocation, Advancement.Builder> entry6 : map.entrySet()) {
                    AdvancementList.LOGGER.error("Couldn't load advancement {}: {}", entry6.getKey(), entry6.getValue());
                }
                break;
            }
        }
        AdvancementList.LOGGER.info("Loaded {} advancements", this.advancements.size());
    }
    
    public void clear() {
        this.advancements.clear();
        this.roots.clear();
        this.tasks.clear();
        if (this.listener != null) {
            this.listener.onAdvancementsCleared();
        }
    }
    
    public Iterable<Advancement> getRoots() {
        return (Iterable<Advancement>)this.roots;
    }
    
    public Collection<Advancement> getAllAdvancements() {
        return (Collection<Advancement>)this.advancements.values();
    }
    
    @Nullable
    public Advancement get(final ResourceLocation qv) {
        return (Advancement)this.advancements.get(qv);
    }
    
    public void setListener(@Nullable final Listener a) {
        this.listener = a;
        if (a != null) {
            for (final Advancement q4 : this.roots) {
                a.onAddAdvancementRoot(q4);
            }
            for (final Advancement q4 : this.tasks) {
                a.onAddAdvancementTask(q4);
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public interface Listener {
        void onAddAdvancementRoot(final Advancement q);
        
        void onRemoveAdvancementRoot(final Advancement q);
        
        void onAddAdvancementTask(final Advancement q);
        
        void onRemoveAdvancementTask(final Advancement q);
        
        void onAdvancementsCleared();
    }
}

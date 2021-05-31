package net.minecraft.world.entity.ai.attributes;

import java.util.Iterator;
import java.util.Collection;
import javax.annotation.Nullable;
import com.google.common.collect.HashMultimap;
import net.minecraft.util.InsensitiveStringMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;

public abstract class BaseAttributeMap {
    protected final Map<Attribute, AttributeInstance> attributesByObject;
    protected final Map<String, AttributeInstance> attributesByName;
    protected final Multimap<Attribute, Attribute> descendantsByParent;
    
    public BaseAttributeMap() {
        this.attributesByObject = (Map<Attribute, AttributeInstance>)Maps.newHashMap();
        this.attributesByName = (Map<String, AttributeInstance>)new InsensitiveStringMap();
        this.descendantsByParent = (Multimap<Attribute, Attribute>)HashMultimap.create();
    }
    
    @Nullable
    public AttributeInstance getInstance(final Attribute ajn) {
        return (AttributeInstance)this.attributesByObject.get(ajn);
    }
    
    @Nullable
    public AttributeInstance getInstance(final String string) {
        return (AttributeInstance)this.attributesByName.get(string);
    }
    
    public AttributeInstance registerAttribute(final Attribute ajn) {
        if (this.attributesByName.containsKey(ajn.getName())) {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        final AttributeInstance ajo3 = this.createAttributeInstance(ajn);
        this.attributesByName.put(ajn.getName(), ajo3);
        this.attributesByObject.put(ajn, ajo3);
        for (Attribute ajn2 = ajn.getParentAttribute(); ajn2 != null; ajn2 = ajn2.getParentAttribute()) {
            this.descendantsByParent.put(ajn2, ajn);
        }
        return ajo3;
    }
    
    protected abstract AttributeInstance createAttributeInstance(final Attribute ajn);
    
    public Collection<AttributeInstance> getAttributes() {
        return (Collection<AttributeInstance>)this.attributesByName.values();
    }
    
    public void onAttributeModified(final AttributeInstance ajo) {
    }
    
    public void removeAttributeModifiers(final Multimap<String, AttributeModifier> multimap) {
        for (final Map.Entry<String, AttributeModifier> entry4 : multimap.entries()) {
            final AttributeInstance ajo5 = this.getInstance((String)entry4.getKey());
            if (ajo5 != null) {
                ajo5.removeModifier((AttributeModifier)entry4.getValue());
            }
        }
    }
    
    public void addAttributeModifiers(final Multimap<String, AttributeModifier> multimap) {
        for (final Map.Entry<String, AttributeModifier> entry4 : multimap.entries()) {
            final AttributeInstance ajo5 = this.getInstance((String)entry4.getKey());
            if (ajo5 != null) {
                ajo5.removeModifier((AttributeModifier)entry4.getValue());
                ajo5.addModifier((AttributeModifier)entry4.getValue());
            }
        }
    }
}

package net.minecraft.world.entity.ai.attributes;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.util.InsensitiveStringMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;

public class ModifiableAttributeMap extends BaseAttributeMap {
    private final Set<AttributeInstance> dirtyAttributes;
    protected final Map<String, AttributeInstance> attributesByLegacy;
    
    public ModifiableAttributeMap() {
        this.dirtyAttributes = (Set<AttributeInstance>)Sets.newHashSet();
        this.attributesByLegacy = (Map<String, AttributeInstance>)new InsensitiveStringMap();
    }
    
    @Override
    public ModifiableAttributeInstance getInstance(final Attribute ajn) {
        return (ModifiableAttributeInstance)super.getInstance(ajn);
    }
    
    @Override
    public ModifiableAttributeInstance getInstance(final String string) {
        AttributeInstance ajo3 = super.getInstance(string);
        if (ajo3 == null) {
            ajo3 = (AttributeInstance)this.attributesByLegacy.get(string);
        }
        return (ModifiableAttributeInstance)ajo3;
    }
    
    @Override
    public AttributeInstance registerAttribute(final Attribute ajn) {
        final AttributeInstance ajo3 = super.registerAttribute(ajn);
        if (ajn instanceof RangedAttribute && ((RangedAttribute)ajn).getImportLegacyName() != null) {
            this.attributesByLegacy.put(((RangedAttribute)ajn).getImportLegacyName(), ajo3);
        }
        return ajo3;
    }
    
    @Override
    protected AttributeInstance createAttributeInstance(final Attribute ajn) {
        return new ModifiableAttributeInstance(this, ajn);
    }
    
    @Override
    public void onAttributeModified(final AttributeInstance ajo) {
        if (ajo.getAttribute().isClientSyncable()) {
            this.dirtyAttributes.add(ajo);
        }
        for (final Attribute ajn4 : this.descendantsByParent.get(ajo.getAttribute())) {
            final ModifiableAttributeInstance ajs5 = this.getInstance(ajn4);
            if (ajs5 != null) {
                ajs5.setDirty();
            }
        }
    }
    
    public Set<AttributeInstance> getDirtyAttributes() {
        return this.dirtyAttributes;
    }
    
    public Collection<AttributeInstance> getSyncableAttributes() {
        final Set<AttributeInstance> set2 = (Set<AttributeInstance>)Sets.newHashSet();
        for (final AttributeInstance ajo4 : this.getAttributes()) {
            if (ajo4.getAttribute().isClientSyncable()) {
                set2.add(ajo4);
            }
        }
        return (Collection<AttributeInstance>)set2;
    }
}

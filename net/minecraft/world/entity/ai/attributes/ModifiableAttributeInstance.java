package net.minecraft.world.entity.ai.attributes;

import java.util.Iterator;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.Collection;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.UUID;
import java.util.Set;
import java.util.Map;

public class ModifiableAttributeInstance implements AttributeInstance {
    private final BaseAttributeMap attributeMap;
    private final Attribute attribute;
    private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiers;
    private final Map<String, Set<AttributeModifier>> modifiersByName;
    private final Map<UUID, AttributeModifier> modifierById;
    private double baseValue;
    private boolean dirty;
    private double cachedValue;
    
    public ModifiableAttributeInstance(final BaseAttributeMap ajr, final Attribute ajn) {
        this.modifiers = (Map<AttributeModifier.Operation, Set<AttributeModifier>>)Maps.newEnumMap((Class)AttributeModifier.Operation.class);
        this.modifiersByName = (Map<String, Set<AttributeModifier>>)Maps.newHashMap();
        this.modifierById = (Map<UUID, AttributeModifier>)Maps.newHashMap();
        this.dirty = true;
        this.attributeMap = ajr;
        this.attribute = ajn;
        this.baseValue = ajn.getDefaultValue();
        for (final AttributeModifier.Operation a7 : AttributeModifier.Operation.values()) {
            this.modifiers.put(a7, Sets.newHashSet());
        }
    }
    
    public Attribute getAttribute() {
        return this.attribute;
    }
    
    public double getBaseValue() {
        return this.baseValue;
    }
    
    public void setBaseValue(final double double1) {
        if (double1 == this.getBaseValue()) {
            return;
        }
        this.baseValue = double1;
        this.setDirty();
    }
    
    public Collection<AttributeModifier> getModifiers(final AttributeModifier.Operation a) {
        return (Collection<AttributeModifier>)this.modifiers.get(a);
    }
    
    public Collection<AttributeModifier> getModifiers() {
        final Set<AttributeModifier> set2 = (Set<AttributeModifier>)Sets.newHashSet();
        for (final AttributeModifier.Operation a6 : AttributeModifier.Operation.values()) {
            set2.addAll((Collection)this.getModifiers(a6));
        }
        return (Collection<AttributeModifier>)set2;
    }
    
    @Nullable
    public AttributeModifier getModifier(final UUID uUID) {
        return (AttributeModifier)this.modifierById.get(uUID);
    }
    
    public boolean hasModifier(final AttributeModifier ajp) {
        return this.modifierById.get(ajp.getId()) != null;
    }
    
    public void addModifier(final AttributeModifier ajp) {
        if (this.getModifier(ajp.getId()) != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        final Set<AttributeModifier> set3 = (Set<AttributeModifier>)this.modifiersByName.computeIfAbsent(ajp.getName(), string -> Sets.newHashSet());
        ((Set)this.modifiers.get(ajp.getOperation())).add(ajp);
        set3.add(ajp);
        this.modifierById.put(ajp.getId(), ajp);
        this.setDirty();
    }
    
    protected void setDirty() {
        this.dirty = true;
        this.attributeMap.onAttributeModified(this);
    }
    
    public void removeModifier(final AttributeModifier ajp) {
        for (final AttributeModifier.Operation a6 : AttributeModifier.Operation.values()) {
            ((Set)this.modifiers.get(a6)).remove(ajp);
        }
        final Set<AttributeModifier> set3 = (Set<AttributeModifier>)this.modifiersByName.get(ajp.getName());
        if (set3 != null) {
            set3.remove(ajp);
            if (set3.isEmpty()) {
                this.modifiersByName.remove(ajp.getName());
            }
        }
        this.modifierById.remove(ajp.getId());
        this.setDirty();
    }
    
    public void removeModifier(final UUID uUID) {
        final AttributeModifier ajp3 = this.getModifier(uUID);
        if (ajp3 != null) {
            this.removeModifier(ajp3);
        }
    }
    
    public void removeModifiers() {
        Collection<AttributeModifier> collection2 = this.getModifiers();
        if (collection2 == null) {
            return;
        }
        collection2 = (Collection<AttributeModifier>)Lists.newArrayList((Iterable)collection2);
        for (final AttributeModifier ajp4 : collection2) {
            this.removeModifier(ajp4);
        }
    }
    
    public double getValue() {
        if (this.dirty) {
            this.cachedValue = this.calculateValue();
            this.dirty = false;
        }
        return this.cachedValue;
    }
    
    private double calculateValue() {
        double double2 = this.getBaseValue();
        for (final AttributeModifier ajp5 : this.getAppliedModifiers(AttributeModifier.Operation.ADDITION)) {
            double2 += ajp5.getAmount();
        }
        double double3 = double2;
        for (final AttributeModifier ajp6 : this.getAppliedModifiers(AttributeModifier.Operation.MULTIPLY_BASE)) {
            double3 += double2 * ajp6.getAmount();
        }
        for (final AttributeModifier ajp6 : this.getAppliedModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            double3 *= 1.0 + ajp6.getAmount();
        }
        return this.attribute.sanitizeValue(double3);
    }
    
    private Collection<AttributeModifier> getAppliedModifiers(final AttributeModifier.Operation a) {
        final Set<AttributeModifier> set3 = (Set<AttributeModifier>)Sets.newHashSet((Iterable)this.getModifiers(a));
        for (Attribute ajn4 = this.attribute.getParentAttribute(); ajn4 != null; ajn4 = ajn4.getParentAttribute()) {
            final AttributeInstance ajo5 = this.attributeMap.getInstance(ajn4);
            if (ajo5 != null) {
                set3.addAll((Collection)ajo5.getModifiers(a));
            }
        }
        return (Collection<AttributeModifier>)set3;
    }
}

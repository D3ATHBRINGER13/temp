package net.minecraft.world.damagesource;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class IndirectEntityDamageSource extends EntityDamageSource {
    private final Entity owner;
    
    public IndirectEntityDamageSource(final String string, final Entity aio2, @Nullable final Entity aio3) {
        super(string, aio2);
        this.owner = aio3;
    }
    
    @Nullable
    @Override
    public Entity getDirectEntity() {
        return this.entity;
    }
    
    @Nullable
    @Override
    public Entity getEntity() {
        return this.owner;
    }
    
    @Override
    public Component getLocalizedDeathMessage(final LivingEntity aix) {
        final Component jo3 = (this.owner == null) ? this.entity.getDisplayName() : this.owner.getDisplayName();
        final ItemStack bcj4 = (this.owner instanceof LivingEntity) ? ((LivingEntity)this.owner).getMainHandItem() : ItemStack.EMPTY;
        final String string5 = "death.attack." + this.msgId;
        final String string6 = string5 + ".item";
        if (!bcj4.isEmpty() && bcj4.hasCustomHoverName()) {
            return new TranslatableComponent(string6, new Object[] { aix.getDisplayName(), jo3, bcj4.getDisplayName() });
        }
        return new TranslatableComponent(string5, new Object[] { aix.getDisplayName(), jo3 });
    }
}

package net.minecraft.world.damagesource;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class EntityDamageSource extends DamageSource {
    @Nullable
    protected final Entity entity;
    private boolean isThorns;
    
    public EntityDamageSource(final String string, @Nullable final Entity aio) {
        super(string);
        this.entity = aio;
    }
    
    public EntityDamageSource setThorns() {
        this.isThorns = true;
        return this;
    }
    
    public boolean isThorns() {
        return this.isThorns;
    }
    
    @Nullable
    @Override
    public Entity getEntity() {
        return this.entity;
    }
    
    @Override
    public Component getLocalizedDeathMessage(final LivingEntity aix) {
        final ItemStack bcj3 = (this.entity instanceof LivingEntity) ? ((LivingEntity)this.entity).getMainHandItem() : ItemStack.EMPTY;
        final String string4 = "death.attack." + this.msgId;
        if (!bcj3.isEmpty() && bcj3.hasCustomHoverName()) {
            return new TranslatableComponent(string4 + ".item", new Object[] { aix.getDisplayName(), this.entity.getDisplayName(), bcj3.getDisplayName() });
        }
        return new TranslatableComponent(string4, new Object[] { aix.getDisplayName(), this.entity.getDisplayName() });
    }
    
    @Override
    public boolean scalesWithDifficulty() {
        return this.entity != null && this.entity instanceof LivingEntity && !(this.entity instanceof Player);
    }
    
    @Nullable
    @Override
    public Vec3 getSourcePosition() {
        return new Vec3(this.entity.x, this.entity.y, this.entity.z);
    }
}

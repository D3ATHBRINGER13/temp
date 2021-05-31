package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends AbstractArmorLayer<T, M, A> {
    public HumanoidArmorLayer(final RenderLayerParent<T, M> dtr, final A dhp2, final A dhp3) {
        super(dtr, dhp2, dhp3);
    }
    
    @Override
    protected void setPartVisibility(final A dhp, final EquipmentSlot ait) {
        this.hideAllArmor(dhp);
        switch (ait) {
            case HEAD: {
                dhp.head.visible = true;
                dhp.hat.visible = true;
                break;
            }
            case CHEST: {
                dhp.body.visible = true;
                dhp.rightArm.visible = true;
                dhp.leftArm.visible = true;
                break;
            }
            case LEGS: {
                dhp.body.visible = true;
                dhp.rightLeg.visible = true;
                dhp.leftLeg.visible = true;
                break;
            }
            case FEET: {
                dhp.rightLeg.visible = true;
                dhp.leftLeg.visible = true;
                break;
            }
        }
    }
    
    @Override
    protected void hideAllArmor(final A dhp) {
        dhp.setAllVisible(false);
    }
}

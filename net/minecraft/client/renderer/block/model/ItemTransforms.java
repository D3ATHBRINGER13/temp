package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.blaze3d.platform.GlStateManager;

public class ItemTransforms {
    public static final ItemTransforms NO_TRANSFORMS;
    public static float transX;
    public static float transY;
    public static float transZ;
    public static float rotX;
    public static float rotY;
    public static float rotZ;
    public static float scaleX;
    public static float scaleY;
    public static float scaleZ;
    public final ItemTransform thirdPersonLeftHand;
    public final ItemTransform thirdPersonRightHand;
    public final ItemTransform firstPersonLeftHand;
    public final ItemTransform firstPersonRightHand;
    public final ItemTransform head;
    public final ItemTransform gui;
    public final ItemTransform ground;
    public final ItemTransform fixed;
    
    private ItemTransforms() {
        this(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
    }
    
    public ItemTransforms(final ItemTransforms dom) {
        this.thirdPersonLeftHand = dom.thirdPersonLeftHand;
        this.thirdPersonRightHand = dom.thirdPersonRightHand;
        this.firstPersonLeftHand = dom.firstPersonLeftHand;
        this.firstPersonRightHand = dom.firstPersonRightHand;
        this.head = dom.head;
        this.gui = dom.gui;
        this.ground = dom.ground;
        this.fixed = dom.fixed;
    }
    
    public ItemTransforms(final ItemTransform dol1, final ItemTransform dol2, final ItemTransform dol3, final ItemTransform dol4, final ItemTransform dol5, final ItemTransform dol6, final ItemTransform dol7, final ItemTransform dol8) {
        this.thirdPersonLeftHand = dol1;
        this.thirdPersonRightHand = dol2;
        this.firstPersonLeftHand = dol3;
        this.firstPersonRightHand = dol4;
        this.head = dol5;
        this.gui = dol6;
        this.ground = dol7;
        this.fixed = dol8;
    }
    
    public void apply(final TransformType b) {
        apply(this.getTransform(b), false);
    }
    
    public static void apply(final ItemTransform dol, final boolean boolean2) {
        if (dol == ItemTransform.NO_TRANSFORM) {
            return;
        }
        final int integer3 = boolean2 ? -1 : 1;
        GlStateManager.translatef(integer3 * (ItemTransforms.transX + dol.translation.x()), ItemTransforms.transY + dol.translation.y(), ItemTransforms.transZ + dol.translation.z());
        final float float4 = ItemTransforms.rotX + dol.rotation.x();
        float float5 = ItemTransforms.rotY + dol.rotation.y();
        float float6 = ItemTransforms.rotZ + dol.rotation.z();
        if (boolean2) {
            float5 = -float5;
            float6 = -float6;
        }
        GlStateManager.multMatrix(new Matrix4f(new Quaternion(float4, float5, float6, true)));
        GlStateManager.scalef(ItemTransforms.scaleX + dol.scale.x(), ItemTransforms.scaleY + dol.scale.y(), ItemTransforms.scaleZ + dol.scale.z());
    }
    
    public ItemTransform getTransform(final TransformType b) {
        switch (b) {
            case THIRD_PERSON_LEFT_HAND: {
                return this.thirdPersonLeftHand;
            }
            case THIRD_PERSON_RIGHT_HAND: {
                return this.thirdPersonRightHand;
            }
            case FIRST_PERSON_LEFT_HAND: {
                return this.firstPersonLeftHand;
            }
            case FIRST_PERSON_RIGHT_HAND: {
                return this.firstPersonRightHand;
            }
            case HEAD: {
                return this.head;
            }
            case GUI: {
                return this.gui;
            }
            case GROUND: {
                return this.ground;
            }
            case FIXED: {
                return this.fixed;
            }
            default: {
                return ItemTransform.NO_TRANSFORM;
            }
        }
    }
    
    public boolean hasTransform(final TransformType b) {
        return this.getTransform(b) != ItemTransform.NO_TRANSFORM;
    }
    
    static {
        NO_TRANSFORMS = new ItemTransforms();
    }
    
    public enum TransformType {
        NONE, 
        THIRD_PERSON_LEFT_HAND, 
        THIRD_PERSON_RIGHT_HAND, 
        FIRST_PERSON_LEFT_HAND, 
        FIRST_PERSON_RIGHT_HAND, 
        HEAD, 
        GUI, 
        GROUND, 
        FIXED;
    }
    
    public static class Deserializer implements JsonDeserializer<ItemTransforms> {
        protected Deserializer() {
        }
        
        public ItemTransforms deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = jsonElement.getAsJsonObject();
            final ItemTransform dol6 = this.getTransform(jsonDeserializationContext, jsonObject5, "thirdperson_righthand");
            ItemTransform dol7 = this.getTransform(jsonDeserializationContext, jsonObject5, "thirdperson_lefthand");
            if (dol7 == ItemTransform.NO_TRANSFORM) {
                dol7 = dol6;
            }
            final ItemTransform dol8 = this.getTransform(jsonDeserializationContext, jsonObject5, "firstperson_righthand");
            ItemTransform dol9 = this.getTransform(jsonDeserializationContext, jsonObject5, "firstperson_lefthand");
            if (dol9 == ItemTransform.NO_TRANSFORM) {
                dol9 = dol8;
            }
            final ItemTransform dol10 = this.getTransform(jsonDeserializationContext, jsonObject5, "head");
            final ItemTransform dol11 = this.getTransform(jsonDeserializationContext, jsonObject5, "gui");
            final ItemTransform dol12 = this.getTransform(jsonDeserializationContext, jsonObject5, "ground");
            final ItemTransform dol13 = this.getTransform(jsonDeserializationContext, jsonObject5, "fixed");
            return new ItemTransforms(dol7, dol6, dol9, dol8, dol10, dol11, dol12, dol13);
        }
        
        private ItemTransform getTransform(final JsonDeserializationContext jsonDeserializationContext, final JsonObject jsonObject, final String string) {
            if (jsonObject.has(string)) {
                return (ItemTransform)jsonDeserializationContext.deserialize(jsonObject.get(string), (Type)ItemTransform.class);
            }
            return ItemTransform.NO_TRANSFORM;
        }
    }
}

package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BookModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

public class EnchantTableRenderer extends BlockEntityRenderer<EnchantmentTableBlockEntity> {
    private static final ResourceLocation BOOK_LOCATION;
    private final BookModel bookModel;
    
    public EnchantTableRenderer() {
        this.bookModel = new BookModel();
    }
    
    @Override
    public void render(final EnchantmentTableBlockEntity buh, final double double2, final double double3, final double double4, final float float5, final int integer) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 0.75f, (float)double4 + 0.5f);
        final float float6 = buh.time + float5;
        GlStateManager.translatef(0.0f, 0.1f + Mth.sin(float6 * 0.1f) * 0.01f, 0.0f);
        float float7;
        for (float7 = buh.rot - buh.oRot; float7 >= 3.1415927f; float7 -= 6.2831855f) {}
        while (float7 < -3.1415927f) {
            float7 += 6.2831855f;
        }
        final float float8 = buh.oRot + float7 * float5;
        GlStateManager.rotatef(-float8 * 57.295776f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(80.0f, 0.0f, 0.0f, 1.0f);
        this.bindTexture(EnchantTableRenderer.BOOK_LOCATION);
        float float9 = Mth.lerp(float5, buh.oFlip, buh.flip) + 0.25f;
        float float10 = Mth.lerp(float5, buh.oFlip, buh.flip) + 0.75f;
        float9 = (float9 - Mth.fastFloor(float9)) * 1.6f - 0.3f;
        float10 = (float10 - Mth.fastFloor(float10)) * 1.6f - 0.3f;
        if (float9 < 0.0f) {
            float9 = 0.0f;
        }
        if (float10 < 0.0f) {
            float10 = 0.0f;
        }
        if (float9 > 1.0f) {
            float9 = 1.0f;
        }
        if (float10 > 1.0f) {
            float10 = 1.0f;
        }
        final float float11 = Mth.lerp(float5, buh.oOpen, buh.open);
        GlStateManager.enableCull();
        this.bookModel.render(float6, float9, float10, float11, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
    
    static {
        BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
    }
}

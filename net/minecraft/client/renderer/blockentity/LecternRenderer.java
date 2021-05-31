package net.minecraft.client.renderer.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.client.model.BookModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class LecternRenderer extends BlockEntityRenderer<LecternBlockEntity> {
    private static final ResourceLocation BOOK_LOCATION;
    private final BookModel bookModel;
    
    public LecternRenderer() {
        this.bookModel = new BookModel();
    }
    
    @Override
    public void render(final LecternBlockEntity buo, final double double2, final double double3, final double double4, final float float5, final int integer) {
        final BlockState bvt11 = buo.getBlockState();
        if (!bvt11.<Boolean>getValue((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2 + 0.5f, (float)double3 + 1.0f + 0.0625f, (float)double4 + 0.5f);
        final float float6 = bvt11.<Direction>getValue((Property<Direction>)LecternBlock.FACING).getClockWise().toYRot();
        GlStateManager.rotatef(-float6, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(67.5f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(0.0f, -0.125f, 0.0f);
        this.bindTexture(LecternRenderer.BOOK_LOCATION);
        GlStateManager.enableCull();
        this.bookModel.render(0.0f, 0.1f, 0.9f, 1.2f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
    
    static {
        BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
    }
}

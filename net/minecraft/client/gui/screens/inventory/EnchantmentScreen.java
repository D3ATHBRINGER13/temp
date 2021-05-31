package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.ChatFormatting;
import com.google.common.collect.Lists;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Matrix4f;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import java.util.Random;
import net.minecraft.client.model.BookModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.EnchantmentMenu;

public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION;
    private static final ResourceLocation ENCHANTING_BOOK_LOCATION;
    private static final BookModel BOOK_MODEL;
    private final Random random;
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last;
    
    public EnchantmentScreen(final EnchantmentMenu aza, final Inventory awf, final Component jo) {
        super(aza, awf, jo);
        this.random = new Random();
        this.last = ItemStack.EMPTY;
    }
    
    @Override
    protected void renderLabels(final int integer1, final int integer2) {
        this.font.draw(this.title.getColoredString(), 12.0f, 5.0f, 4210752);
        this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0f, (float)(this.imageHeight - 96 + 2), 4210752);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.tickBook();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        final int integer2 = (this.width - this.imageWidth) / 2;
        final int integer3 = (this.height - this.imageHeight) / 2;
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            final double double3 = double1 - (integer2 + 60);
            final double double4 = double2 - (integer3 + 14 + 19 * integer4);
            if (double3 >= 0.0 && double4 >= 0.0 && double3 < 108.0 && double4 < 19.0 && ((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, integer4)) {
                this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, integer4);
                return true;
            }
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    protected void renderBg(final float float1, final int integer2, final int integer3) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(EnchantmentScreen.ENCHANTING_TABLE_LOCATION);
        final int integer4 = (this.width - this.imageWidth) / 2;
        final int integer5 = (this.height - this.imageHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.imageWidth, this.imageHeight);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        final int integer6 = (int)this.minecraft.window.getGuiScale();
        GlStateManager.viewport((this.width - 320) / 2 * integer6, (this.height - 240) / 2 * integer6, 320 * integer6, 240 * integer6);
        GlStateManager.translatef(-0.34f, 0.23f, 0.0f);
        GlStateManager.multMatrix(Matrix4f.perspective(90.0, 1.3333334f, 9.0f, 80.0f));
        final float float2 = 1.0f;
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        Lighting.turnOn();
        GlStateManager.translatef(0.0f, 3.3f, -16.0f);
        GlStateManager.scalef(1.0f, 1.0f, 1.0f);
        final float float3 = 5.0f;
        GlStateManager.scalef(5.0f, 5.0f, 5.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        this.minecraft.getTextureManager().bind(EnchantmentScreen.ENCHANTING_BOOK_LOCATION);
        GlStateManager.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
        final float float4 = Mth.lerp(float1, this.oOpen, this.open);
        GlStateManager.translatef((1.0f - float4) * 0.2f, (1.0f - float4) * 0.1f, (1.0f - float4) * 0.25f);
        GlStateManager.rotatef(-(1.0f - float4) * 90.0f - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        float float5 = Mth.lerp(float1, this.oFlip, this.flip) + 0.25f;
        float float6 = Mth.lerp(float1, this.oFlip, this.flip) + 0.75f;
        float5 = (float5 - Mth.fastFloor(float5)) * 1.6f - 0.3f;
        float6 = (float6 - Mth.fastFloor(float6)) * 1.6f - 0.3f;
        if (float5 < 0.0f) {
            float5 = 0.0f;
        }
        if (float6 < 0.0f) {
            float6 = 0.0f;
        }
        if (float5 > 1.0f) {
            float5 = 1.0f;
        }
        if (float6 > 1.0f) {
            float6 = 1.0f;
        }
        GlStateManager.enableRescaleNormal();
        EnchantmentScreen.BOOK_MODEL.render(0.0f, float5, float6, float4, 0.0f, 0.0625f);
        GlStateManager.disableRescaleNormal();
        Lighting.turnOff();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        Lighting.turnOff();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantmentNames.getInstance().initSeed(((EnchantmentMenu)this.menu).getEnchantmentSeed());
        final int integer7 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int integer8 = 0; integer8 < 3; ++integer8) {
            final int integer9 = integer4 + 60;
            final int integer10 = integer9 + 20;
            this.blitOffset = 0;
            this.minecraft.getTextureManager().bind(EnchantmentScreen.ENCHANTING_TABLE_LOCATION);
            final int integer11 = ((EnchantmentMenu)this.menu).costs[integer8];
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (integer11 == 0) {
                this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 185, 108, 19);
            }
            else {
                final String string18 = new StringBuilder().append("").append(integer11).toString();
                final int integer12 = 86 - this.font.width(string18);
                final String string19 = EnchantmentNames.getInstance().getRandomName(this.font, integer12);
                Font cyu21 = this.minecraft.getFontManager().get(Minecraft.ALT_FONT);
                int integer13 = 6839882;
                if ((integer7 < integer8 + 1 || this.minecraft.player.experienceLevel < integer11) && !this.minecraft.player.abilities.instabuild) {
                    this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 185, 108, 19);
                    this.blit(integer9 + 1, integer5 + 15 + 19 * integer8, 16 * integer8, 239, 16, 16);
                    cyu21.drawWordWrap(string19, integer10, integer5 + 16 + 19 * integer8, integer12, (integer13 & 0xFEFEFE) >> 1);
                    integer13 = 4226832;
                }
                else {
                    final int integer14 = integer2 - (integer4 + 60);
                    final int integer15 = integer3 - (integer5 + 14 + 19 * integer8);
                    if (integer14 >= 0 && integer15 >= 0 && integer14 < 108 && integer15 < 19) {
                        this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 204, 108, 19);
                        integer13 = 16777088;
                    }
                    else {
                        this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 166, 108, 19);
                    }
                    this.blit(integer9 + 1, integer5 + 15 + 19 * integer8, 16 * integer8, 223, 16, 16);
                    cyu21.drawWordWrap(string19, integer10, integer5 + 16 + 19 * integer8, integer12, integer13);
                    integer13 = 8453920;
                }
                cyu21 = this.minecraft.font;
                cyu21.drawShadow(string18, (float)(integer10 + 86 - cyu21.width(string18)), (float)(integer5 + 16 + 19 * integer8 + 7), integer13);
            }
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, float float3) {
        float3 = this.minecraft.getFrameTime();
        this.renderBackground();
        super.render(integer1, integer2, float3);
        this.renderTooltip(integer1, integer2);
        final boolean boolean5 = this.minecraft.player.abilities.instabuild;
        final int integer3 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            final int integer5 = ((EnchantmentMenu)this.menu).costs[integer4];
            final Enchantment bfs9 = Enchantment.byId(((EnchantmentMenu)this.menu).enchantClue[integer4]);
            final int integer6 = ((EnchantmentMenu)this.menu).levelClue[integer4];
            final int integer7 = integer4 + 1;
            if (this.isHovering(60, 14 + 19 * integer4, 108, 17, integer1, integer2) && integer5 > 0 && integer6 >= 0 && bfs9 != null) {
                final List<String> list12 = (List<String>)Lists.newArrayList();
                list12.add(new StringBuilder().append("").append((Object)ChatFormatting.WHITE).append((Object)ChatFormatting.ITALIC).append(I18n.get("container.enchant.clue", bfs9.getFullname(integer6).getColoredString())).toString());
                if (!boolean5) {
                    list12.add("");
                    if (this.minecraft.player.experienceLevel < integer5) {
                        list12.add(((Object)ChatFormatting.RED + I18n.get("container.enchant.level.requirement", ((EnchantmentMenu)this.menu).costs[integer4])));
                    }
                    else {
                        String string13;
                        if (integer7 == 1) {
                            string13 = I18n.get("container.enchant.lapis.one");
                        }
                        else {
                            string13 = I18n.get("container.enchant.lapis.many", integer7);
                        }
                        final ChatFormatting c14 = (integer3 >= integer7) ? ChatFormatting.GRAY : ChatFormatting.RED;
                        list12.add(new StringBuilder().append((Object)c14).append("").append(string13).toString());
                        if (integer7 == 1) {
                            string13 = I18n.get("container.enchant.level.one");
                        }
                        else {
                            string13 = I18n.get("container.enchant.level.many", integer7);
                        }
                        list12.add(new StringBuilder().append((Object)ChatFormatting.GRAY).append("").append(string13).toString());
                    }
                }
                this.renderTooltip(list12, integer1, integer2);
                break;
            }
        }
    }
    
    public void tickBook() {
        final ItemStack bcj2 = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
        if (!ItemStack.matches(bcj2, this.last)) {
            this.last = bcj2;
            do {
                this.flipT += this.random.nextInt(4) - this.random.nextInt(4);
            } while (this.flip <= this.flipT + 1.0f && this.flip >= this.flipT - 1.0f);
        }
        ++this.time;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean boolean3 = false;
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            if (((EnchantmentMenu)this.menu).costs[integer4] != 0) {
                boolean3 = true;
            }
        }
        if (boolean3) {
            this.open += 0.2f;
        }
        else {
            this.open -= 0.2f;
        }
        this.open = Mth.clamp(this.open, 0.0f, 1.0f);
        float float4 = (this.flipT - this.flip) * 0.4f;
        final float float5 = 0.2f;
        float4 = Mth.clamp(float4, -0.2f, 0.2f);
        this.flipA += (float4 - this.flipA) * 0.9f;
        this.flip += this.flipA;
    }
    
    static {
        ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
        ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
        BOOK_MODEL = new BookModel();
    }
}

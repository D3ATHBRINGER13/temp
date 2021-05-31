package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Supplier;
import com.google.common.collect.Lists;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.nbt.Tag;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public class BannerBlockEntity extends BlockEntity implements Nameable {
    private Component name;
    private DyeColor baseColor;
    private ListTag itemPatterns;
    private boolean receivedData;
    private List<BannerPattern> patterns;
    private List<DyeColor> colors;
    private String textureHashName;
    
    public BannerBlockEntity() {
        super(BlockEntityType.BANNER);
        this.baseColor = DyeColor.WHITE;
    }
    
    public BannerBlockEntity(final DyeColor bbg) {
        this();
        this.baseColor = bbg;
    }
    
    public void fromItem(final ItemStack bcj, final DyeColor bbg) {
        this.itemPatterns = null;
        final CompoundTag id4 = bcj.getTagElement("BlockEntityTag");
        if (id4 != null && id4.contains("Patterns", 9)) {
            this.itemPatterns = id4.getList("Patterns", 10).copy();
        }
        this.baseColor = bbg;
        this.patterns = null;
        this.colors = null;
        this.textureHashName = "";
        this.receivedData = true;
        this.name = (bcj.hasCustomHoverName() ? bcj.getHoverName() : null);
    }
    
    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return new TranslatableComponent("block.minecraft.banner", new Object[0]);
    }
    
    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }
    
    public void setCustomName(final Component jo) {
        this.name = jo;
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        if (this.itemPatterns != null) {
            id.put("Patterns", (Tag)this.itemPatterns);
        }
        if (this.name != null) {
            id.putString("CustomName", Component.Serializer.toJson(this.name));
        }
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        if (id.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(id.getString("CustomName"));
        }
        if (this.hasLevel()) {
            this.baseColor = ((AbstractBannerBlock)this.getBlockState().getBlock()).getColor();
        }
        else {
            this.baseColor = null;
        }
        this.itemPatterns = id.getList("Patterns", 10);
        this.patterns = null;
        this.colors = null;
        this.textureHashName = null;
        this.receivedData = true;
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 6, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
    
    public static int getPatternCount(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTagElement("BlockEntityTag");
        if (id2 != null && id2.contains("Patterns")) {
            return id2.getList("Patterns", 10).size();
        }
        return 0;
    }
    
    public List<BannerPattern> getPatterns() {
        this.createPatternList();
        return this.patterns;
    }
    
    public List<DyeColor> getColors() {
        this.createPatternList();
        return this.colors;
    }
    
    public String getTextureHashName() {
        this.createPatternList();
        return this.textureHashName;
    }
    
    private void createPatternList() {
        if (this.patterns != null && this.colors != null && this.textureHashName != null) {
            return;
        }
        if (!this.receivedData) {
            this.textureHashName = "";
            return;
        }
        this.patterns = (List<BannerPattern>)Lists.newArrayList();
        this.colors = (List<DyeColor>)Lists.newArrayList();
        final DyeColor bbg2 = this.getBaseColor((Supplier<BlockState>)this::getBlockState);
        if (bbg2 == null) {
            this.textureHashName = "banner_missing";
        }
        else {
            this.patterns.add(BannerPattern.BASE);
            this.colors.add(bbg2);
            this.textureHashName = new StringBuilder().append("b").append(bbg2.getId()).toString();
            if (this.itemPatterns != null) {
                for (int integer3 = 0; integer3 < this.itemPatterns.size(); ++integer3) {
                    final CompoundTag id4 = this.itemPatterns.getCompound(integer3);
                    final BannerPattern btp5 = BannerPattern.byHash(id4.getString("Pattern"));
                    if (btp5 != null) {
                        this.patterns.add(btp5);
                        final int integer4 = id4.getInt("Color");
                        this.colors.add(DyeColor.byId(integer4));
                        this.textureHashName = this.textureHashName + btp5.getHashname() + integer4;
                    }
                }
            }
        }
    }
    
    public static void removeLastPattern(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTagElement("BlockEntityTag");
        if (id2 == null || !id2.contains("Patterns", 9)) {
            return;
        }
        final ListTag ik3 = id2.getList("Patterns", 10);
        if (ik3.isEmpty()) {
            return;
        }
        ik3.remove(ik3.size() - 1);
        if (ik3.isEmpty()) {
            bcj.removeTagKey("BlockEntityTag");
        }
    }
    
    public ItemStack getItem(final BlockState bvt) {
        final ItemStack bcj3 = new ItemStack(BannerBlock.byColor(this.getBaseColor((Supplier<BlockState>)(() -> bvt))));
        if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
            bcj3.getOrCreateTagElement("BlockEntityTag").put("Patterns", (Tag)this.itemPatterns.copy());
        }
        if (this.name != null) {
            bcj3.setHoverName(this.name);
        }
        return bcj3;
    }
    
    public DyeColor getBaseColor(final Supplier<BlockState> supplier) {
        if (this.baseColor == null) {
            this.baseColor = ((AbstractBannerBlock)((BlockState)supplier.get()).getBlock()).getColor();
        }
        return this.baseColor;
    }
}

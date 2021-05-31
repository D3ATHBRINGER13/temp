package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.WrittenBookItem;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Clearable;

public class LecternBlockEntity extends BlockEntity implements Clearable, MenuProvider {
    private final Container bookAccess;
    private final ContainerData dataAccess;
    private ItemStack book;
    private int page;
    private int pageCount;
    
    public LecternBlockEntity() {
        super(BlockEntityType.LECTERN);
        this.bookAccess = new Container() {
            public int getContainerSize() {
                return 1;
            }
            
            public boolean isEmpty() {
                return LecternBlockEntity.this.book.isEmpty();
            }
            
            public ItemStack getItem(final int integer) {
                return (integer == 0) ? LecternBlockEntity.this.book : ItemStack.EMPTY;
            }
            
            public ItemStack removeItem(final int integer1, final int integer2) {
                if (integer1 == 0) {
                    final ItemStack bcj4 = LecternBlockEntity.this.book.split(integer2);
                    if (LecternBlockEntity.this.book.isEmpty()) {
                        LecternBlockEntity.this.onBookItemRemove();
                    }
                    return bcj4;
                }
                return ItemStack.EMPTY;
            }
            
            public ItemStack removeItemNoUpdate(final int integer) {
                if (integer == 0) {
                    final ItemStack bcj3 = LecternBlockEntity.this.book;
                    LecternBlockEntity.this.book = ItemStack.EMPTY;
                    LecternBlockEntity.this.onBookItemRemove();
                    return bcj3;
                }
                return ItemStack.EMPTY;
            }
            
            public void setItem(final int integer, final ItemStack bcj) {
            }
            
            public int getMaxStackSize() {
                return 1;
            }
            
            public void setChanged() {
                LecternBlockEntity.this.setChanged();
            }
            
            public boolean stillValid(final Player awg) {
                return LecternBlockEntity.this.level.getBlockEntity(LecternBlockEntity.this.worldPosition) == LecternBlockEntity.this && awg.distanceToSqr(LecternBlockEntity.this.worldPosition.getX() + 0.5, LecternBlockEntity.this.worldPosition.getY() + 0.5, LecternBlockEntity.this.worldPosition.getZ() + 0.5) <= 64.0 && LecternBlockEntity.this.hasBook();
            }
            
            public boolean canPlaceItem(final int integer, final ItemStack bcj) {
                return false;
            }
            
            public void clearContent() {
            }
        };
        this.dataAccess = new ContainerData() {
            public int get(final int integer) {
                return (integer == 0) ? LecternBlockEntity.this.page : 0;
            }
            
            public void set(final int integer1, final int integer2) {
                if (integer1 == 0) {
                    LecternBlockEntity.this.setPage(integer2);
                }
            }
            
            public int getCount() {
                return 1;
            }
        };
        this.book = ItemStack.EMPTY;
    }
    
    public ItemStack getBook() {
        return this.book;
    }
    
    public boolean hasBook() {
        final Item bce2 = this.book.getItem();
        return bce2 == Items.WRITABLE_BOOK || bce2 == Items.WRITTEN_BOOK;
    }
    
    public void setBook(final ItemStack bcj) {
        this.setBook(bcj, null);
    }
    
    private void onBookItemRemove() {
        this.page = 0;
        this.pageCount = 0;
        LecternBlock.resetBookState(this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
    }
    
    public void setBook(final ItemStack bcj, @Nullable final Player awg) {
        this.book = this.resolveBook(bcj, awg);
        this.page = 0;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.setChanged();
    }
    
    private void setPage(final int integer) {
        final int integer2 = Mth.clamp(integer, 0, this.pageCount - 1);
        if (integer2 != this.page) {
            this.page = integer2;
            this.setChanged();
            LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    public int getPage() {
        return this.page;
    }
    
    public int getRedstoneSignal() {
        final float float2 = (this.pageCount > 1) ? (this.getPage() / (this.pageCount - 1.0f)) : 1.0f;
        return Mth.floor(float2 * 14.0f) + (this.hasBook() ? 1 : 0);
    }
    
    private ItemStack resolveBook(final ItemStack bcj, @Nullable final Player awg) {
        if (this.level instanceof ServerLevel && bcj.getItem() == Items.WRITTEN_BOOK) {
            WrittenBookItem.resolveBookComponents(bcj, this.createCommandSourceStack(awg), awg);
        }
        return bcj;
    }
    
    private CommandSourceStack createCommandSourceStack(@Nullable final Player awg) {
        String string3;
        Component jo4;
        if (awg == null) {
            string3 = "Lectern";
            jo4 = new TextComponent("Lectern");
        }
        else {
            string3 = awg.getName().getString();
            jo4 = awg.getDisplayName();
        }
        final Vec3 csi5 = new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5);
        return new CommandSourceStack(CommandSource.NULL, csi5, Vec2.ZERO, (ServerLevel)this.level, 2, string3, jo4, this.level.getServer(), awg);
    }
    
    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        if (id.contains("Book", 10)) {
            this.book = this.resolveBook(ItemStack.of(id.getCompound("Book")), null);
        }
        else {
            this.book = ItemStack.EMPTY;
        }
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.page = Mth.clamp(id.getInt("Page"), 0, this.pageCount - 1);
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        if (!this.getBook().isEmpty()) {
            id.put("Book", (Tag)this.getBook().save(new CompoundTag()));
            id.putInt("Page", this.page);
        }
        return id;
    }
    
    @Override
    public void clearContent() {
        this.setBook(ItemStack.EMPTY);
    }
    
    public AbstractContainerMenu createMenu(final int integer, final Inventory awf, final Player awg) {
        return new LecternMenu(integer, this.bookAccess, this.dataAccess);
    }
    
    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.lectern", new Object[0]);
    }
}

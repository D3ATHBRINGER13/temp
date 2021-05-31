package net.minecraft.world.level.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PlayerHeadBlock extends SkullBlock {
    protected PlayerHeadBlock(final Properties c) {
        super(Types.PLAYER, c);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        super.setPlacedBy(bhr, ew, bvt, aix, bcj);
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof SkullBlockEntity) {
            final SkullBlockEntity but8 = (SkullBlockEntity)btw7;
            GameProfile gameProfile9 = null;
            if (bcj.hasTag()) {
                final CompoundTag id10 = bcj.getTag();
                if (id10.contains("SkullOwner", 10)) {
                    gameProfile9 = NbtUtils.readGameProfile(id10.getCompound("SkullOwner"));
                }
                else if (id10.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)id10.getString("SkullOwner"))) {
                    gameProfile9 = new GameProfile((UUID)null, id10.getString("SkullOwner"));
                }
            }
            but8.setOwner(gameProfile9);
        }
    }
}

package net.minecraft.world.item;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class PlayerHeadItem extends StandingAndWallBlockItem {
    public PlayerHeadItem(final Block bmv1, final Block bmv2, final Properties a) {
        super(bmv1, bmv2, a);
    }
    
    @Override
    public Component getName(final ItemStack bcj) {
        if (bcj.getItem() == Items.PLAYER_HEAD && bcj.hasTag()) {
            String string3 = null;
            final CompoundTag id4 = bcj.getTag();
            if (id4.contains("SkullOwner", 8)) {
                string3 = id4.getString("SkullOwner");
            }
            else if (id4.contains("SkullOwner", 10)) {
                final CompoundTag id5 = id4.getCompound("SkullOwner");
                if (id5.contains("Name", 8)) {
                    string3 = id5.getString("Name");
                }
            }
            if (string3 != null) {
                return new TranslatableComponent(this.getDescriptionId() + ".named", new Object[] { string3 });
            }
        }
        return super.getName(bcj);
    }
    
    @Override
    public boolean verifyTagAfterLoad(final CompoundTag id) {
        super.verifyTagAfterLoad(id);
        if (id.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)id.getString("SkullOwner"))) {
            GameProfile gameProfile3 = new GameProfile((UUID)null, id.getString("SkullOwner"));
            gameProfile3 = SkullBlockEntity.updateGameprofile(gameProfile3);
            id.put("SkullOwner", (Tag)NbtUtils.writeGameProfile(new CompoundTag(), gameProfile3));
            return true;
        }
        return false;
    }
}

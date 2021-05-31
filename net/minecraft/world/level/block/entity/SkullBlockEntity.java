package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.UUID;
import net.minecraft.util.StringUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.server.players.GameProfileCache;
import com.mojang.authlib.GameProfile;

public class SkullBlockEntity extends BlockEntity implements TickableBlockEntity {
    private GameProfile owner;
    private int mouthTickCount;
    private boolean isMovingMouth;
    private static GameProfileCache profileCache;
    private static MinecraftSessionService sessionService;
    
    public SkullBlockEntity() {
        super(BlockEntityType.SKULL);
    }
    
    public static void setProfileCache(final GameProfileCache xr) {
        SkullBlockEntity.profileCache = xr;
    }
    
    public static void setSessionService(final MinecraftSessionService minecraftSessionService) {
        SkullBlockEntity.sessionService = minecraftSessionService;
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        if (this.owner != null) {
            final CompoundTag id2 = new CompoundTag();
            NbtUtils.writeGameProfile(id2, this.owner);
            id.put("Owner", (Tag)id2);
        }
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        if (id.contains("Owner", 10)) {
            this.setOwner(NbtUtils.readGameProfile(id.getCompound("Owner")));
        }
        else if (id.contains("ExtraType", 8)) {
            final String string3 = id.getString("ExtraType");
            if (!StringUtil.isNullOrEmpty(string3)) {
                this.setOwner(new GameProfile((UUID)null, string3));
            }
        }
    }
    
    @Override
    public void tick() {
        final Block bmv2 = this.getBlockState().getBlock();
        if (bmv2 == Blocks.DRAGON_HEAD || bmv2 == Blocks.DRAGON_WALL_HEAD) {
            if (this.level.hasNeighborSignal(this.worldPosition)) {
                this.isMovingMouth = true;
                ++this.mouthTickCount;
            }
            else {
                this.isMovingMouth = false;
            }
        }
    }
    
    public float getMouthAnimation(final float float1) {
        if (this.isMovingMouth) {
            return this.mouthTickCount + float1;
        }
        return (float)this.mouthTickCount;
    }
    
    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 4, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
    
    public void setOwner(@Nullable final GameProfile gameProfile) {
        this.owner = gameProfile;
        this.updateOwnerProfile();
    }
    
    private void updateOwnerProfile() {
        this.owner = updateGameprofile(this.owner);
        this.setChanged();
    }
    
    public static GameProfile updateGameprofile(final GameProfile gameProfile) {
        if (gameProfile == null || StringUtil.isNullOrEmpty(gameProfile.getName())) {
            return gameProfile;
        }
        if (gameProfile.isComplete() && gameProfile.getProperties().containsKey("textures")) {
            return gameProfile;
        }
        if (SkullBlockEntity.profileCache == null || SkullBlockEntity.sessionService == null) {
            return gameProfile;
        }
        GameProfile gameProfile2 = SkullBlockEntity.profileCache.get(gameProfile.getName());
        if (gameProfile2 == null) {
            return gameProfile;
        }
        final Property property3 = (Property)Iterables.getFirst((Iterable)gameProfile2.getProperties().get("textures"), null);
        if (property3 == null) {
            gameProfile2 = SkullBlockEntity.sessionService.fillProfileProperties(gameProfile2, true);
        }
        return gameProfile2;
    }
}

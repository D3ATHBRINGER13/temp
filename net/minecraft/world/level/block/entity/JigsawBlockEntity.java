package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class JigsawBlockEntity extends BlockEntity {
    private ResourceLocation attachementType;
    private ResourceLocation targetPool;
    private String finalState;
    
    public JigsawBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
        this.attachementType = new ResourceLocation("empty");
        this.targetPool = new ResourceLocation("empty");
        this.finalState = "minecraft:air";
    }
    
    public JigsawBlockEntity() {
        this(BlockEntityType.JIGSAW);
    }
    
    public ResourceLocation getAttachementType() {
        return this.attachementType;
    }
    
    public ResourceLocation getTargetPool() {
        return this.targetPool;
    }
    
    public String getFinalState() {
        return this.finalState;
    }
    
    public void setAttachementType(final ResourceLocation qv) {
        this.attachementType = qv;
    }
    
    public void setTargetPool(final ResourceLocation qv) {
        this.targetPool = qv;
    }
    
    public void setFinalState(final String string) {
        this.finalState = string;
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        id.putString("attachement_type", this.attachementType.toString());
        id.putString("target_pool", this.targetPool.toString());
        id.putString("final_state", this.finalState);
        return id;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.attachementType = new ResourceLocation(id.getString("attachement_type"));
        this.targetPool = new ResourceLocation(id.getString("target_pool"));
        this.finalState = id.getString("final_state");
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 12, this.getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
}

package net.minecraft.world.level.block;

public abstract class StemGrownBlock extends Block {
    public StemGrownBlock(final Properties c) {
        super(c);
    }
    
    public abstract StemBlock getStem();
    
    public abstract AttachedStemBlock getAttachedStem();
}

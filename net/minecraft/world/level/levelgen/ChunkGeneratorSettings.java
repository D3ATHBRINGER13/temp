package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkGeneratorSettings {
    protected int villagesSpacing;
    protected final int villagesSeparation = 8;
    protected int monumentsSpacing;
    protected int monumentsSeparation;
    protected int strongholdsDistance;
    protected int strongholdsCount;
    protected int strongholdsSpread;
    protected int templesSpacing;
    protected final int templesSeparation = 8;
    protected final int oceanRuinSpacing = 16;
    protected final int oceanRuinSeparation = 8;
    protected int endCitySpacing;
    protected final int endCitySeparation = 11;
    protected final int shipwreckSpacing = 16;
    protected final int shipwreckSeparation = 8;
    protected int woodlandMansionSpacing;
    protected final int woodlandMangionSeparation = 20;
    protected BlockState defaultBlock;
    protected BlockState defaultFluid;
    
    public ChunkGeneratorSettings() {
        this.villagesSpacing = 32;
        this.monumentsSpacing = 32;
        this.monumentsSeparation = 5;
        this.strongholdsDistance = 32;
        this.strongholdsCount = 128;
        this.strongholdsSpread = 3;
        this.templesSpacing = 32;
        this.endCitySpacing = 20;
        this.woodlandMansionSpacing = 80;
        this.defaultBlock = Blocks.STONE.defaultBlockState();
        this.defaultFluid = Blocks.WATER.defaultBlockState();
    }
    
    public int getVillagesSpacing() {
        return this.villagesSpacing;
    }
    
    public int getVillagesSeparation() {
        return 8;
    }
    
    public int getMonumentsSpacing() {
        return this.monumentsSpacing;
    }
    
    public int getMonumentsSeparation() {
        return this.monumentsSeparation;
    }
    
    public int getStrongholdsDistance() {
        return this.strongholdsDistance;
    }
    
    public int getStrongholdsCount() {
        return this.strongholdsCount;
    }
    
    public int getStrongholdsSpread() {
        return this.strongholdsSpread;
    }
    
    public int getTemplesSpacing() {
        return this.templesSpacing;
    }
    
    public int getTemplesSeparation() {
        return 8;
    }
    
    public int getShipwreckSpacing() {
        return 16;
    }
    
    public int getShipwreckSeparation() {
        return 8;
    }
    
    public int getOceanRuinSpacing() {
        return 16;
    }
    
    public int getOceanRuinSeparation() {
        return 8;
    }
    
    public int getEndCitySpacing() {
        return this.endCitySpacing;
    }
    
    public int getEndCitySeparation() {
        return 11;
    }
    
    public int getWoodlandMansionSpacing() {
        return this.woodlandMansionSpacing;
    }
    
    public int getWoodlandMangionSeparation() {
        return 20;
    }
    
    public BlockState getDefaultBlock() {
        return this.defaultBlock;
    }
    
    public BlockState getDefaultFluid() {
        return this.defaultFluid;
    }
    
    public void setDefaultBlock(final BlockState bvt) {
        this.defaultBlock = bvt;
    }
    
    public void setDefaultFluid(final BlockState bvt) {
        this.defaultFluid = bvt;
    }
    
    public int getBedrockRoofPosition() {
        return 0;
    }
    
    public int getBedrockFloorPosition() {
        return 256;
    }
}

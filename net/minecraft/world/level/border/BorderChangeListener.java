package net.minecraft.world.level.border;

public interface BorderChangeListener {
    void onBorderSizeSet(final WorldBorder bxf, final double double2);
    
    void onBorderSizeLerping(final WorldBorder bxf, final double double2, final double double3, final long long4);
    
    void onBorderCenterSet(final WorldBorder bxf, final double double2, final double double3);
    
    void onBorderSetWarningTime(final WorldBorder bxf, final int integer);
    
    void onBorderSetWarningBlocks(final WorldBorder bxf, final int integer);
    
    void onBorderSetDamagePerBlock(final WorldBorder bxf, final double double2);
    
    void onBorderSetDamageSafeZOne(final WorldBorder bxf, final double double2);
    
    public static class DelegateBorderChangeListener implements BorderChangeListener {
        private final WorldBorder worldBorder;
        
        public DelegateBorderChangeListener(final WorldBorder bxf) {
            this.worldBorder = bxf;
        }
        
        public void onBorderSizeSet(final WorldBorder bxf, final double double2) {
            this.worldBorder.setSize(double2);
        }
        
        public void onBorderSizeLerping(final WorldBorder bxf, final double double2, final double double3, final long long4) {
            this.worldBorder.lerpSizeBetween(double2, double3, long4);
        }
        
        public void onBorderCenterSet(final WorldBorder bxf, final double double2, final double double3) {
            this.worldBorder.setCenter(double2, double3);
        }
        
        public void onBorderSetWarningTime(final WorldBorder bxf, final int integer) {
            this.worldBorder.setWarningTime(integer);
        }
        
        public void onBorderSetWarningBlocks(final WorldBorder bxf, final int integer) {
            this.worldBorder.setWarningBlocks(integer);
        }
        
        public void onBorderSetDamagePerBlock(final WorldBorder bxf, final double double2) {
            this.worldBorder.setDamagePerBlock(double2);
        }
        
        public void onBorderSetDamageSafeZOne(final WorldBorder bxf, final double double2) {
            this.worldBorder.setDamageSafeZone(double2);
        }
    }
}

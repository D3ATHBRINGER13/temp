package net.minecraft.world.level.block;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.Nameable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantmentTableBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE;
    
    protected EnchantmentTableBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return EnchantmentTableBlock.SHAPE;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        super.animateTick(bvt, bhr, ew, random);
        for (int integer6 = -2; integer6 <= 2; ++integer6) {
            for (int integer7 = -2; integer7 <= 2; ++integer7) {
                if (integer6 > -2 && integer6 < 2 && integer7 == -1) {
                    integer7 = 2;
                }
                if (random.nextInt(16) == 0) {
                    for (int integer8 = 0; integer8 <= 1; ++integer8) {
                        final BlockPos ew2 = ew.offset(integer6, integer8, integer7);
                        if (bhr.getBlockState(ew2).getBlock() == Blocks.BOOKSHELF) {
                            if (!bhr.isEmptyBlock(ew.offset(integer6 / 2, 0, integer7 / 2))) {
                                break;
                            }
                            bhr.addParticle(ParticleTypes.ENCHANT, ew.getX() + 0.5, ew.getY() + 2.0, ew.getZ() + 0.5, integer6 + random.nextFloat() - 0.5, integer8 - random.nextFloat() - 1.0f, integer7 + random.nextFloat() - 0.5);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new EnchantmentTableBlockEntity();
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        awg.openMenu(bvt.getMenuProvider(bhr, ew));
        return true;
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final BlockEntity btw5 = bhr.getBlockEntity(ew);
        if (btw5 instanceof EnchantmentTableBlockEntity) {
            final Component jo6 = ((Nameable)btw5).getDisplayName();
            return new SimpleMenuProvider((integer, awf, awg) -> new EnchantmentMenu(integer, awf, ContainerLevelAccess.create(bhr, ew)), jo6);
        }
        return null;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof EnchantmentTableBlockEntity) {
                ((EnchantmentTableBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    }
}

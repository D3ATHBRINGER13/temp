package net.minecraft.client.tutorial;

import net.minecraft.network.chat.TranslatableComponent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import java.util.Iterator;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import java.util.Set;

public class FindTreeTutorialStepInstance implements TutorialStepInstance {
    private static final Set<Block> TREE_BLOCKS;
    private static final Component TITLE;
    private static final Component DESCRIPTION;
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;
    
    public FindTreeTutorialStepInstance(final Tutorial eaz) {
        this.tutorial = eaz;
    }
    
    public void tick() {
        ++this.timeWaiting;
        if (this.tutorial.getGameMode() != GameType.SURVIVAL) {
            this.tutorial.setStep(TutorialSteps.NONE);
            return;
        }
        if (this.timeWaiting == 1) {
            final LocalPlayer dmp2 = this.tutorial.getMinecraft().player;
            if (dmp2 != null) {
                for (final Block bmv4 : FindTreeTutorialStepInstance.TREE_BLOCKS) {
                    if (dmp2.inventory.contains(new ItemStack(bmv4))) {
                        this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                        return;
                    }
                }
                if (hasPunchedTreesPreviously(dmp2)) {
                    this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                    return;
                }
            }
        }
        if (this.timeWaiting >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, FindTreeTutorialStepInstance.TITLE, FindTreeTutorialStepInstance.DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
        }
    }
    
    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }
    
    public void onLookAt(final MultiPlayerLevel dkf, final HitResult csf) {
        if (csf.getType() == HitResult.Type.BLOCK) {
            final BlockState bvt4 = dkf.getBlockState(((BlockHitResult)csf).getBlockPos());
            if (FindTreeTutorialStepInstance.TREE_BLOCKS.contains(bvt4.getBlock())) {
                this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
            }
        }
    }
    
    public void onGetItem(final ItemStack bcj) {
        for (final Block bmv4 : FindTreeTutorialStepInstance.TREE_BLOCKS) {
            if (bcj.getItem() == bmv4.asItem()) {
                this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
            }
        }
    }
    
    public static boolean hasPunchedTreesPreviously(final LocalPlayer dmp) {
        for (final Block bmv3 : FindTreeTutorialStepInstance.TREE_BLOCKS) {
            if (dmp.getStats().getValue(Stats.BLOCK_MINED.get(bmv3)) > 0) {
                return true;
            }
        }
        return false;
    }
    
    static {
        TREE_BLOCKS = (Set)Sets.newHashSet((Object[])new Block[] { Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES });
        TITLE = new TranslatableComponent("tutorial.find_tree.title", new Object[0]);
        DESCRIPTION = new TranslatableComponent("tutorial.find_tree.description", new Object[0]);
    }
}

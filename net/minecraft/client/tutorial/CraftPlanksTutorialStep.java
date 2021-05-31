package net.minecraft.client.tutorial;

import net.minecraft.network.chat.TranslatableComponent;
import java.util.Iterator;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.GameType;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;

public class CraftPlanksTutorialStep implements TutorialStepInstance {
    private static final Component CRAFT_TITLE;
    private static final Component CRAFT_DESCRIPTION;
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;
    
    public CraftPlanksTutorialStep(final Tutorial eaz) {
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
                if (dmp2.inventory.contains(ItemTags.PLANKS)) {
                    this.tutorial.setStep(TutorialSteps.NONE);
                    return;
                }
                if (hasCraftedPlanksPreviously(dmp2, ItemTags.PLANKS)) {
                    this.tutorial.setStep(TutorialSteps.NONE);
                    return;
                }
            }
        }
        if (this.timeWaiting >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.WOODEN_PLANKS, CraftPlanksTutorialStep.CRAFT_TITLE, CraftPlanksTutorialStep.CRAFT_DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
        }
    }
    
    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }
    
    public void onGetItem(final ItemStack bcj) {
        final Item bce3 = bcj.getItem();
        if (ItemTags.PLANKS.contains(bce3)) {
            this.tutorial.setStep(TutorialSteps.NONE);
        }
    }
    
    public static boolean hasCraftedPlanksPreviously(final LocalPlayer dmp, final Tag<Item> zg) {
        for (final Item bce4 : zg.getValues()) {
            if (dmp.getStats().getValue(Stats.ITEM_CRAFTED.get(bce4)) > 0) {
                return true;
            }
        }
        return false;
    }
    
    static {
        CRAFT_TITLE = new TranslatableComponent("tutorial.craft_planks.title", new Object[0]);
        CRAFT_DESCRIPTION = new TranslatableComponent("tutorial.craft_planks.description", new Object[0]);
    }
}

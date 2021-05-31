package net.minecraft.client.gui.chat;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.NarratorStatus;
import net.minecraft.network.chat.ChatType;
import com.mojang.text2speech.Narrator;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.chat.Component;

public class NarratorChatListener implements ChatListener {
    public static final Component NO_TITLE;
    private static final Logger LOGGER;
    public static final NarratorChatListener INSTANCE;
    private final Narrator narrator;
    
    public NarratorChatListener() {
        this.narrator = Narrator.getNarrator();
    }
    
    public void handle(final ChatType jm, final Component jo) {
        final NarratorStatus cye4 = getStatus();
        if (cye4 == NarratorStatus.OFF || !this.narrator.active()) {
            return;
        }
        if (cye4 == NarratorStatus.ALL || (cye4 == NarratorStatus.CHAT && jm == ChatType.CHAT) || (cye4 == NarratorStatus.SYSTEM && jm == ChatType.SYSTEM)) {
            Component jo2;
            if (jo instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent)jo).getKey())) {
                jo2 = new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent)jo).getArgs());
            }
            else {
                jo2 = jo;
            }
            this.doSay(jm.shouldInterrupt(), jo2.getString());
        }
    }
    
    public void sayNow(final String string) {
        final NarratorStatus cye3 = getStatus();
        if (this.narrator.active() && cye3 != NarratorStatus.OFF && cye3 != NarratorStatus.CHAT && !string.isEmpty()) {
            this.narrator.clear();
            this.doSay(true, string);
        }
    }
    
    private static NarratorStatus getStatus() {
        return Minecraft.getInstance().options.narratorStatus;
    }
    
    private void doSay(final boolean boolean1, final String string) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            NarratorChatListener.LOGGER.debug("Narrating: {}", string);
        }
        this.narrator.say(string, boolean1);
    }
    
    public void updateNarratorStatus(final NarratorStatus cye) {
        this.clear();
        this.narrator.say(new TranslatableComponent("options.narrator", new Object[0]).getString() + " : " + new TranslatableComponent(cye.getKey(), new Object[0]).getString(), true);
        final ToastComponent dan3 = Minecraft.getInstance().getToasts();
        if (this.narrator.active()) {
            if (cye == NarratorStatus.OFF) {
                SystemToast.addOrUpdate(dan3, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), null);
            }
            else {
                SystemToast.addOrUpdate(dan3, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.enabled", new Object[0]), new TranslatableComponent(cye.getKey(), new Object[0]));
            }
        }
        else {
            SystemToast.addOrUpdate(dan3, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), new TranslatableComponent("options.narrator.notavailable", new Object[0]));
        }
    }
    
    public boolean isActive() {
        return this.narrator.active();
    }
    
    public void clear() {
        if (getStatus() == NarratorStatus.OFF || !this.narrator.active()) {
            return;
        }
        this.narrator.clear();
    }
    
    public void destroy() {
        this.narrator.destroy();
    }
    
    static {
        NO_TITLE = new TextComponent("");
        LOGGER = LogManager.getLogger();
        INSTANCE = new NarratorChatListener();
    }
}

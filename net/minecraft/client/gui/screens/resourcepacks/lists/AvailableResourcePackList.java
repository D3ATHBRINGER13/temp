package net.minecraft.client.gui.screens.resourcepacks.lists;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.Minecraft;

public class AvailableResourcePackList extends ResourcePackList {
    public AvailableResourcePackList(final Minecraft cyc, final int integer2, final int integer3) {
        super(cyc, integer2, integer3, new TranslatableComponent("resourcePack.available.title", new Object[0]));
    }
}

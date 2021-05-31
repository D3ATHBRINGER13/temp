package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;

public interface ContextAwareComponent {
    Component resolve(@Nullable final CommandSourceStack cd, @Nullable final Entity aio, final int integer) throws CommandSyntaxException;
}

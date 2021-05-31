package net.minecraft.world.level;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import java.util.Date;
import net.minecraft.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.ResultConsumer;
import net.minecraft.util.StringUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.text.SimpleDateFormat;
import net.minecraft.commands.CommandSource;

public abstract class BaseCommandBlock implements CommandSource {
    private static final SimpleDateFormat TIME_FORMAT;
    private long lastExecution;
    private boolean updateLastExecution;
    private int successCount;
    private boolean trackOutput;
    private Component lastOutput;
    private String command;
    private Component name;
    
    public BaseCommandBlock() {
        this.lastExecution = -1L;
        this.updateLastExecution = true;
        this.trackOutput = true;
        this.command = "";
        this.name = new TextComponent("@");
    }
    
    public int getSuccessCount() {
        return this.successCount;
    }
    
    public void setSuccessCount(final int integer) {
        this.successCount = integer;
    }
    
    public Component getLastOutput() {
        return (this.lastOutput == null) ? new TextComponent("") : this.lastOutput;
    }
    
    public CompoundTag save(final CompoundTag id) {
        id.putString("Command", this.command);
        id.putInt("SuccessCount", this.successCount);
        id.putString("CustomName", Component.Serializer.toJson(this.name));
        id.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            id.putString("LastOutput", Component.Serializer.toJson(this.lastOutput));
        }
        id.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            id.putLong("LastExecution", this.lastExecution);
        }
        return id;
    }
    
    public void load(final CompoundTag id) {
        this.command = id.getString("Command");
        this.successCount = id.getInt("SuccessCount");
        if (id.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(id.getString("CustomName"));
        }
        if (id.contains("TrackOutput", 1)) {
            this.trackOutput = id.getBoolean("TrackOutput");
        }
        if (id.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = Component.Serializer.fromJson(id.getString("LastOutput"));
            }
            catch (Throwable throwable3) {
                this.lastOutput = new TextComponent(throwable3.getMessage());
            }
        }
        else {
            this.lastOutput = null;
        }
        if (id.contains("UpdateLastExecution")) {
            this.updateLastExecution = id.getBoolean("UpdateLastExecution");
        }
        if (this.updateLastExecution && id.contains("LastExecution")) {
            this.lastExecution = id.getLong("LastExecution");
        }
        else {
            this.lastExecution = -1L;
        }
    }
    
    public void setCommand(final String string) {
        this.command = string;
        this.successCount = 0;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public boolean performCommand(final Level bhr) {
        if (bhr.isClientSide || bhr.getGameTime() == this.lastExecution) {
            return false;
        }
        if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = new TextComponent("#itzlipofutzli");
            this.successCount = 1;
            return true;
        }
        this.successCount = 0;
        final MinecraftServer minecraftServer3 = this.getLevel().getServer();
        if (minecraftServer3 != null && minecraftServer3.isInitialized() && minecraftServer3.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
                this.lastOutput = null;
                final CommandSourceStack cd4 = this.createCommandSourceStack().withCallback((ResultConsumer<CommandSourceStack>)((commandContext, boolean2, integer) -> {
                    if (boolean2) {
                        ++this.successCount;
                    }
                }));
                minecraftServer3.getCommands().performCommand(cd4, this.command);
            }
            catch (Throwable throwable4) {
                final CrashReport d5 = CrashReport.forThrowable(throwable4, "Executing command block");
                final CrashReportCategory e6 = d5.addCategory("Command to be executed");
                e6.setDetail("Command", (CrashReportDetail<String>)this::getCommand);
                e6.setDetail("Name", (CrashReportDetail<String>)(() -> this.getName().getString()));
                throw new ReportedException(d5);
            }
        }
        if (this.updateLastExecution) {
            this.lastExecution = bhr.getGameTime();
        }
        else {
            this.lastExecution = -1L;
        }
        return true;
    }
    
    public Component getName() {
        return this.name;
    }
    
    public void setName(final Component jo) {
        this.name = jo;
    }
    
    public void sendMessage(final Component jo) {
        if (this.trackOutput) {
            this.lastOutput = new TextComponent("[" + BaseCommandBlock.TIME_FORMAT.format(new Date()) + "] ").append(jo);
            this.onUpdated();
        }
    }
    
    public abstract ServerLevel getLevel();
    
    public abstract void onUpdated();
    
    public void setLastOutput(@Nullable final Component jo) {
        this.lastOutput = jo;
    }
    
    public void setTrackOutput(final boolean boolean1) {
        this.trackOutput = boolean1;
    }
    
    public boolean isTrackOutput() {
        return this.trackOutput;
    }
    
    public boolean usedBy(final Player awg) {
        if (!awg.canUseGameMasterBlocks()) {
            return false;
        }
        if (awg.getCommandSenderWorld().isClientSide) {
            awg.openMinecartCommandBlock(this);
        }
        return true;
    }
    
    public abstract Vec3 getPosition();
    
    public abstract CommandSourceStack createCommandSourceStack();
    
    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }
    
    public boolean acceptsFailure() {
        return this.trackOutput;
    }
    
    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }
    
    static {
        TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    }
}

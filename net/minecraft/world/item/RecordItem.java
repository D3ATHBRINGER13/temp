package net.minecraft.world.item;

import com.google.common.collect.Maps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundEvent;
import java.util.Map;

public class RecordItem extends Item {
    private static final Map<SoundEvent, RecordItem> BY_NAME;
    private final int analogOutput;
    private final SoundEvent sound;
    
    protected RecordItem(final int integer, final SoundEvent yo, final Properties a) {
        super(a);
        this.analogOutput = integer;
        this.sound = yo;
        RecordItem.BY_NAME.put(this.sound, this);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        if (bvt5.getBlock() != Blocks.JUKEBOX || bvt5.<Boolean>getValue((Property<Boolean>)JukeboxBlock.HAS_RECORD)) {
            return InteractionResult.PASS;
        }
        final ItemStack bcj6 = bdu.getItemInHand();
        if (!bhr3.isClientSide) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord(bhr3, ew4, bvt5, bcj6);
            bhr3.levelEvent(null, 1010, ew4, Item.getId(this));
            bcj6.shrink(1);
            final Player awg7 = bdu.getPlayer();
            if (awg7 != null) {
                awg7.awardStat(Stats.PLAY_RECORD);
            }
        }
        return InteractionResult.SUCCESS;
    }
    
    public int getAnalogOutput() {
        return this.analogOutput;
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        list.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
    }
    
    public Component getDisplayName() {
        return new TranslatableComponent(this.getDescriptionId() + ".desc", new Object[0]);
    }
    
    @Nullable
    public static RecordItem getBySound(final SoundEvent yo) {
        return (RecordItem)RecordItem.BY_NAME.get(yo);
    }
    
    public SoundEvent getSound() {
        return this.sound;
    }
    
    static {
        BY_NAME = (Map)Maps.newHashMap();
    }
}

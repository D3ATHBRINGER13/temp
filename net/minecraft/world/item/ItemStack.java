package net.minecraft.world.item;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.chat.HoverEvent;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.tags.TagManager;
import java.util.Objects;
import com.google.common.collect.HashMultimap;
import net.minecraft.world.item.enchantment.Enchantment;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import com.mojang.brigadier.StringReader;
import java.util.Iterator;
import com.google.common.collect.Multimap;
import java.util.Collection;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import com.google.common.collect.Lists;
import java.util.List;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import java.util.Random;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ItemLike;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.nbt.CompoundTag;
import java.text.DecimalFormat;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
    private static final Logger LOGGER;
    public static final ItemStack EMPTY;
    public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT;
    private int count;
    private int popTime;
    @Deprecated
    private final Item item;
    private CompoundTag tag;
    private boolean emptyCacheFlag;
    private ItemFrame frame;
    private BlockInWorld cachedBreakBlock;
    private boolean cachedBreakBlockResult;
    private BlockInWorld cachedPlaceBlock;
    private boolean cachedPlaceBlockResult;
    
    private static DecimalFormat getAttributeDecimalFormat() {
        final DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
        decimalFormat1.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        return decimalFormat1;
    }
    
    public ItemStack(final ItemLike bhq) {
        this(bhq, 1);
    }
    
    public ItemStack(final ItemLike bhq, final int integer) {
        this.item = ((bhq == null) ? null : bhq.asItem());
        this.count = integer;
        this.updateEmptyCacheFlag();
    }
    
    private void updateEmptyCacheFlag() {
        this.emptyCacheFlag = false;
        this.emptyCacheFlag = this.isEmpty();
    }
    
    private ItemStack(final CompoundTag id) {
        this.item = Registry.ITEM.get(new ResourceLocation(id.getString("id")));
        this.count = id.getByte("Count");
        if (id.contains("tag", 10)) {
            this.tag = id.getCompound("tag");
            this.getItem().verifyTagAfterLoad(id);
        }
        if (this.getItem().canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
        this.updateEmptyCacheFlag();
    }
    
    public static ItemStack of(final CompoundTag id) {
        try {
            return new ItemStack(id);
        }
        catch (RuntimeException runtimeException2) {
            ItemStack.LOGGER.debug("Tried to load invalid item: {}", id, runtimeException2);
            return ItemStack.EMPTY;
        }
    }
    
    public boolean isEmpty() {
        return this == ItemStack.EMPTY || (this.getItem() == null || this.getItem() == Items.AIR) || this.count <= 0;
    }
    
    public ItemStack split(final int integer) {
        final int integer2 = Math.min(integer, this.count);
        final ItemStack bcj4 = this.copy();
        bcj4.setCount(integer2);
        this.shrink(integer2);
        return bcj4;
    }
    
    public Item getItem() {
        return this.emptyCacheFlag ? Items.AIR : this.item;
    }
    
    public InteractionResult useOn(final UseOnContext bdu) {
        final Player awg3 = bdu.getPlayer();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockInWorld bvx5 = new BlockInWorld(bdu.getLevel(), ew4, false);
        if (awg3 != null && !awg3.abilities.mayBuild && !this.hasAdventureModePlaceTagForBlock(bdu.getLevel().getTagManager(), bvx5)) {
            return InteractionResult.PASS;
        }
        final Item bce6 = this.getItem();
        final InteractionResult ahj7 = bce6.useOn(bdu);
        if (awg3 != null && ahj7 == InteractionResult.SUCCESS) {
            awg3.awardStat(Stats.ITEM_USED.get(bce6));
        }
        return ahj7;
    }
    
    public float getDestroySpeed(final BlockState bvt) {
        return this.getItem().getDestroySpeed(this, bvt);
    }
    
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        return this.getItem().use(bhr, awg, ahi);
    }
    
    public ItemStack finishUsingItem(final Level bhr, final LivingEntity aix) {
        return this.getItem().finishUsingItem(this, bhr, aix);
    }
    
    public CompoundTag save(final CompoundTag id) {
        final ResourceLocation qv3 = Registry.ITEM.getKey(this.getItem());
        id.putString("id", (qv3 == null) ? "minecraft:air" : qv3.toString());
        id.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            id.put("tag", (Tag)this.tag);
        }
        return id;
    }
    
    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }
    
    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
    }
    
    public boolean isDamageableItem() {
        if (this.emptyCacheFlag || this.getItem().getMaxDamage() <= 0) {
            return false;
        }
        final CompoundTag id2 = this.getTag();
        return id2 == null || !id2.getBoolean("Unbreakable");
    }
    
    public boolean isDamaged() {
        return this.isDamageableItem() && this.getDamageValue() > 0;
    }
    
    public int getDamageValue() {
        return (this.tag == null) ? 0 : this.tag.getInt("Damage");
    }
    
    public void setDamageValue(final int integer) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, integer));
    }
    
    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }
    
    public boolean hurt(int integer, final Random random, @Nullable final ServerPlayer vl) {
        if (!this.isDamageableItem()) {
            return false;
        }
        if (integer > 0) {
            final int integer2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int integer3 = 0;
            for (int integer4 = 0; integer2 > 0 && integer4 < integer; ++integer4) {
                if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, integer2, random)) {
                    ++integer3;
                }
            }
            integer -= integer3;
            if (integer <= 0) {
                return false;
            }
        }
        if (vl != null && integer != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(vl, this, this.getDamageValue() + integer);
        }
        final int integer2 = this.getDamageValue() + integer;
        this.setDamageValue(integer2);
        return integer2 >= this.getMaxDamage();
    }
    
    public <T extends LivingEntity> void hurtAndBreak(final int integer, final T aix, final Consumer<T> consumer) {
        if (aix.level.isClientSide || (aix instanceof Player && ((Player)aix).abilities.instabuild)) {
            return;
        }
        if (!this.isDamageableItem()) {
            return;
        }
        if (this.hurt(integer, aix.getRandom(), (aix instanceof ServerPlayer) ? aix : null)) {
            consumer.accept(aix);
            final Item bce5 = this.getItem();
            this.shrink(1);
            if (aix instanceof Player) {
                ((Player)aix).awardStat(Stats.ITEM_BROKEN.get(bce5));
            }
            this.setDamageValue(0);
        }
    }
    
    public void hurtEnemy(final LivingEntity aix, final Player awg) {
        final Item bce4 = this.getItem();
        if (bce4.hurtEnemy(this, aix, awg)) {
            awg.awardStat(Stats.ITEM_USED.get(bce4));
        }
    }
    
    public void mineBlock(final Level bhr, final BlockState bvt, final BlockPos ew, final Player awg) {
        final Item bce6 = this.getItem();
        if (bce6.mineBlock(this, bhr, bvt, ew, awg)) {
            awg.awardStat(Stats.ITEM_USED.get(bce6));
        }
    }
    
    public boolean canDestroySpecial(final BlockState bvt) {
        return this.getItem().canDestroySpecial(bvt);
    }
    
    public boolean interactEnemy(final Player awg, final LivingEntity aix, final InteractionHand ahi) {
        return this.getItem().interactEnemy(this, awg, aix, ahi);
    }
    
    public ItemStack copy() {
        final ItemStack bcj2 = new ItemStack(this.getItem(), this.count);
        bcj2.setPopTime(this.getPopTime());
        if (this.tag != null) {
            bcj2.tag = this.tag.copy();
        }
        return bcj2;
    }
    
    public static boolean tagMatches(final ItemStack bcj1, final ItemStack bcj2) {
        return (bcj1.isEmpty() && bcj2.isEmpty()) || (!bcj1.isEmpty() && !bcj2.isEmpty() && (bcj1.tag != null || bcj2.tag == null) && (bcj1.tag == null || bcj1.tag.equals(bcj2.tag)));
    }
    
    public static boolean matches(final ItemStack bcj1, final ItemStack bcj2) {
        return (bcj1.isEmpty() && bcj2.isEmpty()) || (!bcj1.isEmpty() && !bcj2.isEmpty() && bcj1.matches(bcj2));
    }
    
    private boolean matches(final ItemStack bcj) {
        return this.count == bcj.count && this.getItem() == bcj.getItem() && (this.tag != null || bcj.tag == null) && (this.tag == null || this.tag.equals(bcj.tag));
    }
    
    public static boolean isSame(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj1 == bcj2 || (!bcj1.isEmpty() && !bcj2.isEmpty() && bcj1.sameItem(bcj2));
    }
    
    public static boolean isSameIgnoreDurability(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj1 == bcj2 || (!bcj1.isEmpty() && !bcj2.isEmpty() && bcj1.sameItemStackIgnoreDurability(bcj2));
    }
    
    public boolean sameItem(final ItemStack bcj) {
        return !bcj.isEmpty() && this.getItem() == bcj.getItem();
    }
    
    public boolean sameItemStackIgnoreDurability(final ItemStack bcj) {
        if (this.isDamageableItem()) {
            return !bcj.isEmpty() && this.getItem() == bcj.getItem();
        }
        return this.sameItem(bcj);
    }
    
    public String getDescriptionId() {
        return this.getItem().getDescriptionId(this);
    }
    
    public String toString() {
        return new StringBuilder().append(this.count).append(" ").append(this.getItem()).toString();
    }
    
    public void inventoryTick(final Level bhr, final Entity aio, final int integer, final boolean boolean4) {
        if (this.popTime > 0) {
            --this.popTime;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, bhr, aio, integer, boolean4);
        }
    }
    
    public void onCraftedBy(final Level bhr, final Player awg, final int integer) {
        awg.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), integer);
        this.getItem().onCraftedBy(this, bhr, awg);
    }
    
    public int getUseDuration() {
        return this.getItem().getUseDuration(this);
    }
    
    public UseAnim getUseAnimation() {
        return this.getItem().getUseAnimation(this);
    }
    
    public void releaseUsing(final Level bhr, final LivingEntity aix, final int integer) {
        this.getItem().releaseUsing(this, bhr, aix, integer);
    }
    
    public boolean useOnRelease() {
        return this.getItem().useOnRelease(this);
    }
    
    public boolean hasTag() {
        return !this.emptyCacheFlag && this.tag != null && !this.tag.isEmpty();
    }
    
    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }
    
    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
        }
        return this.tag;
    }
    
    public CompoundTag getOrCreateTagElement(final String string) {
        if (this.tag == null || !this.tag.contains(string, 10)) {
            final CompoundTag id3 = new CompoundTag();
            this.addTagElement(string, id3);
            return id3;
        }
        return this.tag.getCompound(string);
    }
    
    @Nullable
    public CompoundTag getTagElement(final String string) {
        if (this.tag == null || !this.tag.contains(string, 10)) {
            return null;
        }
        return this.tag.getCompound(string);
    }
    
    public void removeTagKey(final String string) {
        if (this.tag != null && this.tag.contains(string)) {
            this.tag.remove(string);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }
    }
    
    public ListTag getEnchantmentTags() {
        if (this.tag != null) {
            return this.tag.getList("Enchantments", 10);
        }
        return new ListTag();
    }
    
    public void setTag(@Nullable final CompoundTag id) {
        this.tag = id;
    }
    
    public Component getHoverName() {
        final CompoundTag id2 = this.getTagElement("display");
        if (id2 != null && id2.contains("Name", 8)) {
            try {
                final Component jo3 = Component.Serializer.fromJson(id2.getString("Name"));
                if (jo3 != null) {
                    return jo3;
                }
                id2.remove("Name");
            }
            catch (JsonParseException jsonParseException3) {
                id2.remove("Name");
            }
        }
        return this.getItem().getName(this);
    }
    
    public ItemStack setHoverName(@Nullable final Component jo) {
        final CompoundTag id3 = this.getOrCreateTagElement("display");
        if (jo != null) {
            id3.putString("Name", Component.Serializer.toJson(jo));
        }
        else {
            id3.remove("Name");
        }
        return this;
    }
    
    public void resetHoverName() {
        final CompoundTag id2 = this.getTagElement("display");
        if (id2 != null) {
            id2.remove("Name");
            if (id2.isEmpty()) {
                this.removeTagKey("display");
            }
        }
        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }
    }
    
    public boolean hasCustomHoverName() {
        final CompoundTag id2 = this.getTagElement("display");
        return id2 != null && id2.contains("Name", 8);
    }
    
    public List<Component> getTooltipLines(@Nullable final Player awg, final TooltipFlag bdr) {
        final List<Component> list4 = (List<Component>)Lists.newArrayList();
        final Component jo5 = new TextComponent("").append(this.getHoverName()).withStyle(this.getRarity().color);
        if (this.hasCustomHoverName()) {
            jo5.withStyle(ChatFormatting.ITALIC);
        }
        list4.add(jo5);
        if (!bdr.isAdvanced() && !this.hasCustomHoverName() && this.getItem() == Items.FILLED_MAP) {
            list4.add(new TextComponent(new StringBuilder().append("#").append(MapItem.getMapId(this)).toString()).withStyle(ChatFormatting.GRAY));
        }
        int integer6 = 0;
        if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
            integer6 = this.tag.getInt("HideFlags");
        }
        if ((integer6 & 0x20) == 0x0) {
            this.getItem().appendHoverText(this, (awg == null) ? null : awg.level, list4, bdr);
        }
        if (this.hasTag()) {
            if ((integer6 & 0x1) == 0x0) {
                appendEnchantmentNames(list4, this.getEnchantmentTags());
            }
            if (this.tag.contains("display", 10)) {
                final CompoundTag id7 = this.tag.getCompound("display");
                if (id7.contains("color", 3)) {
                    if (bdr.isAdvanced()) {
                        list4.add(new TranslatableComponent("item.color", new Object[] { String.format("#%06X", new Object[] { id7.getInt("color") }) }).withStyle(ChatFormatting.GRAY));
                    }
                    else {
                        list4.add(new TranslatableComponent("item.dyed", new Object[0]).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    }
                }
                if (id7.getTagType("Lore") == 9) {
                    final ListTag ik8 = id7.getList("Lore", 8);
                    for (int integer7 = 0; integer7 < ik8.size(); ++integer7) {
                        final String string10 = ik8.getString(integer7);
                        try {
                            final Component jo6 = Component.Serializer.fromJson(string10);
                            if (jo6 != null) {
                                list4.add(ComponentUtils.mergeStyles(jo6, new Style().setColor(ChatFormatting.DARK_PURPLE).setItalic(true)));
                            }
                        }
                        catch (JsonParseException jsonParseException11) {
                            id7.remove("Lore");
                        }
                    }
                }
            }
        }
        for (final EquipmentSlot ait10 : EquipmentSlot.values()) {
            final Multimap<String, AttributeModifier> multimap11 = this.getAttributeModifiers(ait10);
            if (!multimap11.isEmpty() && (integer6 & 0x2) == 0x0) {
                list4.add(new TextComponent(""));
                list4.add(new TranslatableComponent("item.modifiers." + ait10.getName(), new Object[0]).withStyle(ChatFormatting.GRAY));
                for (final Map.Entry<String, AttributeModifier> entry13 : multimap11.entries()) {
                    final AttributeModifier ajp14 = (AttributeModifier)entry13.getValue();
                    double double15 = ajp14.getAmount();
                    boolean boolean19 = false;
                    if (awg != null) {
                        if (ajp14.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                            double15 += awg.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                            double15 += EnchantmentHelper.getDamageBonus(this, MobType.UNDEFINED);
                            boolean19 = true;
                        }
                        else if (ajp14.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                            double15 += awg.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                            boolean19 = true;
                        }
                    }
                    double double16;
                    if (ajp14.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE || ajp14.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        double16 = double15 * 100.0;
                    }
                    else {
                        double16 = double15;
                    }
                    if (boolean19) {
                        list4.add(new TextComponent(" ").append(new TranslatableComponent(new StringBuilder().append("attribute.modifier.equals.").append(ajp14.getOperation().toValue()).toString(), new Object[] { ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(double16), new TranslatableComponent("attribute.name." + (String)entry13.getKey(), new Object[0]) })).withStyle(ChatFormatting.DARK_GREEN));
                    }
                    else if (double15 > 0.0) {
                        list4.add(new TranslatableComponent(new StringBuilder().append("attribute.modifier.plus.").append(ajp14.getOperation().toValue()).toString(), new Object[] { ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(double16), new TranslatableComponent("attribute.name." + (String)entry13.getKey(), new Object[0]) }).withStyle(ChatFormatting.BLUE));
                    }
                    else {
                        if (double15 >= 0.0) {
                            continue;
                        }
                        double16 *= -1.0;
                        list4.add(new TranslatableComponent(new StringBuilder().append("attribute.modifier.take.").append(ajp14.getOperation().toValue()).toString(), new Object[] { ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(double16), new TranslatableComponent("attribute.name." + (String)entry13.getKey(), new Object[0]) }).withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
        if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (integer6 & 0x4) == 0x0) {
            list4.add(new TranslatableComponent("item.unbreakable", new Object[0]).withStyle(ChatFormatting.BLUE));
        }
        if (this.hasTag() && this.tag.contains("CanDestroy", 9) && (integer6 & 0x8) == 0x0) {
            final ListTag ik9 = this.tag.getList("CanDestroy", 8);
            if (!ik9.isEmpty()) {
                list4.add(new TextComponent(""));
                list4.add(new TranslatableComponent("item.canBreak", new Object[0]).withStyle(ChatFormatting.GRAY));
                for (int integer8 = 0; integer8 < ik9.size(); ++integer8) {
                    list4.addAll((Collection)expandBlockState(ik9.getString(integer8)));
                }
            }
        }
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9) && (integer6 & 0x10) == 0x0) {
            final ListTag ik9 = this.tag.getList("CanPlaceOn", 8);
            if (!ik9.isEmpty()) {
                list4.add(new TextComponent(""));
                list4.add(new TranslatableComponent("item.canPlace", new Object[0]).withStyle(ChatFormatting.GRAY));
                for (int integer8 = 0; integer8 < ik9.size(); ++integer8) {
                    list4.addAll((Collection)expandBlockState(ik9.getString(integer8)));
                }
            }
        }
        if (bdr.isAdvanced()) {
            if (this.isDamaged()) {
                list4.add(new TranslatableComponent("item.durability", new Object[] { this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage() }));
            }
            list4.add(new TextComponent(Registry.ITEM.getKey(this.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
            if (this.hasTag()) {
                list4.add(new TranslatableComponent("item.nbt_tags", new Object[] { this.getTag().getAllKeys().size() }).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        return list4;
    }
    
    public static void appendEnchantmentNames(final List<Component> list, final ListTag ik) {
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            final CompoundTag id4 = ik.getCompound(integer3);
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(id4.getString("id"))).ifPresent(bfs -> list.add(bfs.getFullname(id4.getInt("lvl"))));
        }
    }
    
    private static Collection<Component> expandBlockState(final String string) {
        try {
            final BlockStateParser dh2 = new BlockStateParser(new StringReader(string), true).parse(true);
            final BlockState bvt3 = dh2.getState();
            final ResourceLocation qv4 = dh2.getTag();
            final boolean boolean5 = bvt3 != null;
            final boolean boolean6 = qv4 != null;
            if (boolean5 || boolean6) {
                if (boolean5) {
                    return (Collection<Component>)Lists.newArrayList((Iterable)bvt3.getBlock().getName().withStyle(ChatFormatting.DARK_GRAY));
                }
                final net.minecraft.tags.Tag<Block> zg7 = BlockTags.getAllTags().getTag(qv4);
                if (zg7 != null) {
                    final Collection<Block> collection8 = zg7.getValues();
                    if (!collection8.isEmpty()) {
                        return (Collection<Component>)collection8.stream().map(Block::getName).map(jo -> jo.withStyle(ChatFormatting.DARK_GRAY)).collect(Collectors.toList());
                    }
                }
            }
        }
        catch (CommandSyntaxException ex) {}
        return (Collection<Component>)Lists.newArrayList((Iterable)new TextComponent("missingno").withStyle(ChatFormatting.DARK_GRAY));
    }
    
    public boolean hasFoil() {
        return this.getItem().isFoil(this);
    }
    
    public Rarity getRarity() {
        return this.getItem().getRarity(this);
    }
    
    public boolean isEnchantable() {
        return this.getItem().isEnchantable(this) && !this.isEnchanted();
    }
    
    public void enchant(final Enchantment bfs, final int integer) {
        this.getOrCreateTag();
        if (!this.tag.contains("Enchantments", 9)) {
            this.tag.put("Enchantments", (Tag)new ListTag());
        }
        final ListTag ik4 = this.tag.getList("Enchantments", 10);
        final CompoundTag id5 = new CompoundTag();
        id5.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(bfs)));
        id5.putShort("lvl", (short)(byte)integer);
        ik4.add(id5);
    }
    
    public boolean isEnchanted() {
        return this.tag != null && this.tag.contains("Enchantments", 9) && !this.tag.getList("Enchantments", 10).isEmpty();
    }
    
    public void addTagElement(final String string, final Tag iu) {
        this.getOrCreateTag().put(string, iu);
    }
    
    public boolean isFramed() {
        return this.frame != null;
    }
    
    public void setFramed(@Nullable final ItemFrame atn) {
        this.frame = atn;
    }
    
    @Nullable
    public ItemFrame getFrame() {
        return this.emptyCacheFlag ? null : this.frame;
    }
    
    public int getBaseRepairCost() {
        if (this.hasTag() && this.tag.contains("RepairCost", 3)) {
            return this.tag.getInt("RepairCost");
        }
        return 0;
    }
    
    public void setRepairCost(final int integer) {
        this.getOrCreateTag().putInt("RepairCost", integer);
    }
    
    public Multimap<String, AttributeModifier> getAttributeModifiers(final EquipmentSlot ait) {
        Multimap<String, AttributeModifier> multimap3;
        if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
            multimap3 = (Multimap<String, AttributeModifier>)HashMultimap.create();
            final ListTag ik4 = this.tag.getList("AttributeModifiers", 10);
            for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
                final CompoundTag id6 = ik4.getCompound(integer5);
                final AttributeModifier ajp7 = SharedMonsterAttributes.loadAttributeModifier(id6);
                if (ajp7 != null) {
                    if (!id6.contains("Slot", 8) || id6.getString("Slot").equals(ait.getName())) {
                        if (ajp7.getId().getLeastSignificantBits() != 0L && ajp7.getId().getMostSignificantBits() != 0L) {
                            multimap3.put(id6.getString("AttributeName"), ajp7);
                        }
                    }
                }
            }
        }
        else {
            multimap3 = this.getItem().getDefaultAttributeModifiers(ait);
        }
        return multimap3;
    }
    
    public void addAttributeModifier(final String string, final AttributeModifier ajp, @Nullable final EquipmentSlot ait) {
        this.getOrCreateTag();
        if (!this.tag.contains("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", (Tag)new ListTag());
        }
        final ListTag ik5 = this.tag.getList("AttributeModifiers", 10);
        final CompoundTag id6 = SharedMonsterAttributes.saveAttributeModifier(ajp);
        id6.putString("AttributeName", string);
        if (ait != null) {
            id6.putString("Slot", ait.getName());
        }
        ik5.add(id6);
    }
    
    public Component getDisplayName() {
        final Component jo2 = new TextComponent("").append(this.getHoverName());
        if (this.hasCustomHoverName()) {
            jo2.withStyle(ChatFormatting.ITALIC);
        }
        final Component jo3 = ComponentUtils.wrapInSquareBrackets(jo2);
        if (!this.emptyCacheFlag) {
            final CompoundTag id4 = this.save(new CompoundTag());
            jo3.withStyle(this.getRarity().color).withStyle((Consumer<Style>)(jw -> jw.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponent(id4.toString())))));
        }
        return jo3;
    }
    
    private static boolean areSameBlocks(final BlockInWorld bvx1, @Nullable final BlockInWorld bvx2) {
        return bvx2 != null && bvx1.getState() == bvx2.getState() && ((bvx1.getEntity() == null && bvx2.getEntity() == null) || (bvx1.getEntity() != null && bvx2.getEntity() != null && Objects.equals(bvx1.getEntity().save(new CompoundTag()), bvx2.getEntity().save(new CompoundTag()))));
    }
    
    public boolean hasAdventureModeBreakTagForBlock(final TagManager zi, final BlockInWorld bvx) {
        if (areSameBlocks(bvx, this.cachedBreakBlock)) {
            return this.cachedBreakBlockResult;
        }
        this.cachedBreakBlock = bvx;
        if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            final ListTag ik4 = this.tag.getList("CanDestroy", 8);
            for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
                final String string6 = ik4.getString(integer5);
                try {
                    final Predicate<BlockInWorld> predicate7 = BlockPredicateArgument.blockPredicate().parse(new StringReader(string6)).create(zi);
                    if (predicate7.test(bvx)) {
                        return this.cachedBreakBlockResult = true;
                    }
                }
                catch (CommandSyntaxException ex) {}
            }
        }
        return this.cachedBreakBlockResult = false;
    }
    
    public boolean hasAdventureModePlaceTagForBlock(final TagManager zi, final BlockInWorld bvx) {
        if (areSameBlocks(bvx, this.cachedPlaceBlock)) {
            return this.cachedPlaceBlockResult;
        }
        this.cachedPlaceBlock = bvx;
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            final ListTag ik4 = this.tag.getList("CanPlaceOn", 8);
            for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
                final String string6 = ik4.getString(integer5);
                try {
                    final Predicate<BlockInWorld> predicate7 = BlockPredicateArgument.blockPredicate().parse(new StringReader(string6)).create(zi);
                    if (predicate7.test(bvx)) {
                        return this.cachedPlaceBlockResult = true;
                    }
                }
                catch (CommandSyntaxException ex) {}
            }
        }
        return this.cachedPlaceBlockResult = false;
    }
    
    public int getPopTime() {
        return this.popTime;
    }
    
    public void setPopTime(final int integer) {
        this.popTime = integer;
    }
    
    public int getCount() {
        return this.emptyCacheFlag ? 0 : this.count;
    }
    
    public void setCount(final int integer) {
        this.count = integer;
        this.updateEmptyCacheFlag();
    }
    
    public void grow(final int integer) {
        this.setCount(this.count + integer);
    }
    
    public void shrink(final int integer) {
        this.grow(-integer);
    }
    
    public void onUseTick(final Level bhr, final LivingEntity aix, final int integer) {
        this.getItem().onUseTick(bhr, aix, this, integer);
    }
    
    public boolean isEdible() {
        return this.getItem().isEdible();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY = new ItemStack((ItemLike)null);
        ATTRIBUTE_MODIFIER_FORMAT = getAttributeDecimalFormat();
    }
}

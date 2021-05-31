package net.minecraft.network.chat;

import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import com.google.common.collect.Streams;
import java.util.stream.Stream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.IllegalFormatException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.regex.Pattern;
import java.util.List;
import net.minecraft.locale.Language;

public class TranslatableComponent extends BaseComponent implements ContextAwareComponent {
    private static final Language DEFAULT_LANGUAGE;
    private static final Language LANGUAGE;
    private final String key;
    private final Object[] args;
    private final Object decomposeLock;
    private long decomposedLanguageTime;
    protected final List<Component> decomposedParts;
    public static final Pattern FORMAT_PATTERN;
    
    public TranslatableComponent(final String string, final Object... arr) {
        this.decomposeLock = new Object();
        this.decomposedLanguageTime = -1L;
        this.decomposedParts = (List<Component>)Lists.newArrayList();
        this.key = string;
        this.args = arr;
        for (int integer4 = 0; integer4 < arr.length; ++integer4) {
            final Object object5 = arr[integer4];
            if (object5 instanceof Component) {
                final Component jo6 = ((Component)object5).deepCopy();
                this.args[integer4] = jo6;
                jo6.getStyle().inheritFrom(this.getStyle());
            }
            else if (object5 == null) {
                this.args[integer4] = "null";
            }
        }
    }
    
    @VisibleForTesting
    synchronized void decompose() {
        synchronized (this.decomposeLock) {
            final long long3 = TranslatableComponent.LANGUAGE.getLastUpdateTime();
            if (long3 == this.decomposedLanguageTime) {
                return;
            }
            this.decomposedLanguageTime = long3;
            this.decomposedParts.clear();
        }
        try {
            this.decomposeTemplate(TranslatableComponent.LANGUAGE.getElement(this.key));
        }
        catch (TranslatableFormatException jz2) {
            this.decomposedParts.clear();
            try {
                this.decomposeTemplate(TranslatableComponent.DEFAULT_LANGUAGE.getElement(this.key));
            }
            catch (TranslatableFormatException jz3) {
                throw jz2;
            }
        }
    }
    
    protected void decomposeTemplate(final String string) {
        final Matcher matcher3 = TranslatableComponent.FORMAT_PATTERN.matcher((CharSequence)string);
        try {
            int integer4 = 0;
            int integer5;
            int integer7;
            for (integer5 = 0; matcher3.find(integer5); integer5 = integer7) {
                final int integer6 = matcher3.start();
                integer7 = matcher3.end();
                if (integer6 > integer5) {
                    final Component jo8 = new TextComponent(String.format(string.substring(integer5, integer6), new Object[0]));
                    jo8.getStyle().inheritFrom(this.getStyle());
                    this.decomposedParts.add(jo8);
                }
                final String string2 = matcher3.group(2);
                final String string3 = string.substring(integer6, integer7);
                if ("%".equals(string2) && "%%".equals(string3)) {
                    final Component jo9 = new TextComponent("%");
                    jo9.getStyle().inheritFrom(this.getStyle());
                    this.decomposedParts.add(jo9);
                }
                else {
                    if (!"s".equals(string2)) {
                        throw new TranslatableFormatException(this, "Unsupported format: '" + string3 + "'");
                    }
                    final String string4 = matcher3.group(1);
                    final int integer8 = (string4 != null) ? (Integer.parseInt(string4) - 1) : integer4++;
                    if (integer8 < this.args.length) {
                        this.decomposedParts.add(this.getComponent(integer8));
                    }
                }
            }
            if (integer5 < string.length()) {
                final Component jo10 = new TextComponent(String.format(string.substring(integer5), new Object[0]));
                jo10.getStyle().inheritFrom(this.getStyle());
                this.decomposedParts.add(jo10);
            }
        }
        catch (IllegalFormatException illegalFormatException4) {
            throw new TranslatableFormatException(this, (Throwable)illegalFormatException4);
        }
    }
    
    private Component getComponent(final int integer) {
        if (integer >= this.args.length) {
            throw new TranslatableFormatException(this, integer);
        }
        final Object object3 = this.args[integer];
        Component jo4;
        if (object3 instanceof Component) {
            jo4 = (Component)object3;
        }
        else {
            jo4 = new TextComponent((object3 == null) ? "null" : object3.toString());
            jo4.getStyle().inheritFrom(this.getStyle());
        }
        return jo4;
    }
    
    @Override
    public Component setStyle(final Style jw) {
        super.setStyle(jw);
        for (final Object object6 : this.args) {
            if (object6 instanceof Component) {
                ((Component)object6).getStyle().inheritFrom(this.getStyle());
            }
        }
        if (this.decomposedLanguageTime > -1L) {
            for (final Component jo4 : this.decomposedParts) {
                jo4.getStyle().inheritFrom(jw);
            }
        }
        return this;
    }
    
    @Override
    public Stream<Component> stream() {
        this.decompose();
        return (Stream<Component>)Streams.concat(new Stream[] { this.decomposedParts.stream(), this.siblings.stream() }).flatMap(Component::stream);
    }
    
    public String getContents() {
        this.decompose();
        final StringBuilder stringBuilder2 = new StringBuilder();
        for (final Component jo4 : this.decomposedParts) {
            stringBuilder2.append(jo4.getContents());
        }
        return stringBuilder2.toString();
    }
    
    public TranslatableComponent copy() {
        final Object[] arr2 = new Object[this.args.length];
        for (int integer3 = 0; integer3 < this.args.length; ++integer3) {
            if (this.args[integer3] instanceof Component) {
                arr2[integer3] = ((Component)this.args[integer3]).deepCopy();
            }
            else {
                arr2[integer3] = this.args[integer3];
            }
        }
        return new TranslatableComponent(this.key, arr2);
    }
    
    @Override
    public Component resolve(@Nullable final CommandSourceStack cd, @Nullable final Entity aio, final int integer) throws CommandSyntaxException {
        final Object[] arr5 = new Object[this.args.length];
        for (int integer2 = 0; integer2 < arr5.length; ++integer2) {
            final Object object7 = this.args[integer2];
            if (object7 instanceof Component) {
                arr5[integer2] = ComponentUtils.updateForEntity(cd, (Component)object7, aio, integer);
            }
            else {
                arr5[integer2] = object7;
            }
        }
        return new TranslatableComponent(this.key, arr5);
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof TranslatableComponent) {
            final TranslatableComponent jy3 = (TranslatableComponent)object;
            return Arrays.equals(this.args, jy3.args) && this.key.equals(jy3.key) && super.equals(object);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int integer2 = super.hashCode();
        integer2 = 31 * integer2 + this.key.hashCode();
        integer2 = 31 * integer2 + Arrays.hashCode(this.args);
        return integer2;
    }
    
    @Override
    public String toString() {
        return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
    
    public String getKey() {
        return this.key;
    }
    
    public Object[] getArgs() {
        return this.args;
    }
    
    static {
        DEFAULT_LANGUAGE = new Language();
        LANGUAGE = Language.getInstance();
        FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    }
}

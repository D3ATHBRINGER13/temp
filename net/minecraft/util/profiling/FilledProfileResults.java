package net.minecraft.util.profiling;

import org.apache.logging.log4j.LogManager;
import net.minecraft.Util;
import java.util.Locale;
import net.minecraft.SharedConstants;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class FilledProfileResults implements ProfileResults {
    private static final Logger LOGGER;
    private final Map<String, Long> times;
    private final Map<String, Long> counts;
    private final long startTimeNano;
    private final int startTimeTicks;
    private final long endTimeNano;
    private final int endTimeTicks;
    private final int tickDuration;
    
    public FilledProfileResults(final Map<String, Long> map1, final Map<String, Long> map2, final long long3, final int integer4, final long long5, final int integer6) {
        this.times = map1;
        this.counts = map2;
        this.startTimeNano = long3;
        this.startTimeTicks = integer4;
        this.endTimeNano = long5;
        this.endTimeTicks = integer6;
        this.tickDuration = integer6 - integer4;
    }
    
    public List<ResultField> getTimes(String string) {
        final String string2 = string;
        long long4 = (long)(this.times.containsKey("root") ? this.times.get("root") : 0L);
        final long long5 = (long)this.times.getOrDefault(string, (-1L));
        final long long6 = (long)this.counts.getOrDefault(string, 0L);
        final List<ResultField> list10 = (List<ResultField>)Lists.newArrayList();
        if (!string.isEmpty()) {
            string += '\u001e';
        }
        long long7 = 0L;
        for (final String string3 : this.times.keySet()) {
            if (string3.length() > string.length() && string3.startsWith(string) && string3.indexOf(30, string.length() + 1) < 0) {
                long7 += (long)this.times.get(string3);
            }
        }
        final float float13 = (float)long7;
        if (long7 < long5) {
            long7 = long5;
        }
        if (long4 < long7) {
            long4 = long7;
        }
        final Set<String> set14 = (Set<String>)Sets.newHashSet((Iterable)this.times.keySet());
        set14.addAll((Collection)this.counts.keySet());
        for (final String string4 : set14) {
            if (string4.length() > string.length() && string4.startsWith(string) && string4.indexOf(30, string.length() + 1) < 0) {
                final long long8 = (long)this.times.getOrDefault(string4, 0L);
                final double double19 = long8 * 100.0 / long7;
                final double double20 = long8 * 100.0 / long4;
                final String string5 = string4.substring(string.length());
                final long long9 = (long)this.counts.getOrDefault(string4, 0L);
                list10.add(new ResultField(string5, double19, double20, long9));
            }
        }
        for (final String string4 : this.times.keySet()) {
            this.times.put(string4, ((long)this.times.get((Object)string4) * 999L / 1000L));
        }
        if (long7 > float13) {
            list10.add(new ResultField("unspecified", (long7 - float13) * 100.0 / long7, (long7 - float13) * 100.0 / long4, long6));
        }
        Collections.sort((List)list10);
        list10.add(0, new ResultField(string2, 100.0, long7 * 100.0 / long4, long6));
        return list10;
    }
    
    public long getStartTimeNano() {
        return this.startTimeNano;
    }
    
    public int getStartTimeTicks() {
        return this.startTimeTicks;
    }
    
    public long getEndTimeNano() {
        return this.endTimeNano;
    }
    
    public int getEndTimeTicks() {
        return this.endTimeTicks;
    }
    
    public boolean saveResults(final File file) {
        file.getParentFile().mkdirs();
        Writer writer3 = null;
        try {
            writer3 = (Writer)new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);
            writer3.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
            return true;
        }
        catch (Throwable throwable4) {
            FilledProfileResults.LOGGER.error("Could not save profiler results to {}", file, throwable4);
            return false;
        }
        finally {
            IOUtils.closeQuietly(writer3);
        }
    }
    
    protected String getProfilerResults(final long long1, final int integer) {
        final StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append("---- Minecraft Profiler Results ----\n");
        stringBuilder5.append("// ");
        stringBuilder5.append(getComment());
        stringBuilder5.append("\n\n");
        stringBuilder5.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
        stringBuilder5.append("Time span: ").append(long1 / 1000000L).append(" ms\n");
        stringBuilder5.append("Tick span: ").append(integer).append(" ticks\n");
        stringBuilder5.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", new Object[] { integer / (long1 / 1.0E9f) })).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringBuilder5.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendProfilerResults(0, "root", stringBuilder5);
        stringBuilder5.append("--- END PROFILE DUMP ---\n\n");
        return stringBuilder5.toString();
    }
    
    public String getProfilerResults() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        this.appendProfilerResults(0, "root", stringBuilder2);
        return stringBuilder2.toString();
    }
    
    private void appendProfilerResults(final int integer, final String string, final StringBuilder stringBuilder) {
        final List<ResultField> list5 = this.getTimes(string);
        if (list5.size() < 3) {
            return;
        }
        for (int integer2 = 1; integer2 < list5.size(); ++integer2) {
            final ResultField ago7 = (ResultField)list5.get(integer2);
            stringBuilder.append(String.format("[%02d] ", new Object[] { integer }));
            for (int integer3 = 0; integer3 < integer; ++integer3) {
                stringBuilder.append("|   ");
            }
            stringBuilder.append(ago7.name).append('(').append(ago7.count).append('/').append(String.format(Locale.ROOT, "%.0f", new Object[] { ago7.count / (float)this.tickDuration })).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", new Object[] { ago7.percentage })).append("%/").append(String.format(Locale.ROOT, "%.2f", new Object[] { ago7.globalPercentage })).append("%\n");
            if (!"unspecified".equals(ago7.name)) {
                try {
                    this.appendProfilerResults(integer + 1, string + '\u001e' + ago7.name, stringBuilder);
                }
                catch (Exception exception8) {
                    stringBuilder.append("[[ EXCEPTION ").append(exception8).append(" ]]");
                }
            }
        }
    }
    
    private static String getComment() {
        final String[] arr1 = { "Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server." };
        try {
            return arr1[(int)(Util.getNanos() % arr1.length)];
        }
        catch (Throwable throwable2) {
            return "Witty comment unavailable :(";
        }
    }
    
    public int getTickDuration() {
        return this.tickDuration;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

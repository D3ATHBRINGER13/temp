package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.client.gui.Font;
import java.util.Random;

public class EnchantmentNames {
    private static final EnchantmentNames INSTANCE;
    private final Random random;
    private final String[] words;
    
    private EnchantmentNames() {
        this.random = new Random();
        this.words = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale phnglui mglwnafh cthulhu rlyeh wgahnagl fhtagnbaguette".split(" ");
    }
    
    public static EnchantmentNames getInstance() {
        return EnchantmentNames.INSTANCE;
    }
    
    public String getRandomName(final Font cyu, final int integer) {
        final int integer2 = this.random.nextInt(2) + 3;
        String string5 = "";
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            if (integer3 > 0) {
                string5 += " ";
            }
            string5 += this.words[this.random.nextInt(this.words.length)];
        }
        final List<String> list6 = cyu.split(string5, integer);
        return StringUtils.join((Iterable)((list6.size() >= 2) ? list6.subList(0, 2) : list6), " ");
    }
    
    public void initSeed(final long long1) {
        this.random.setSeed(long1);
    }
    
    static {
        INSTANCE = new EnchantmentNames();
    }
}

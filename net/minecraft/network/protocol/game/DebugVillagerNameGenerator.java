package net.minecraft.network.protocol.game;

import java.util.Random;
import java.util.UUID;

public class DebugVillagerNameGenerator {
    private static final String[] NAMES_FIRST_PART;
    private static final String[] NAMES_SECOND_PART;
    
    public static String getVillagerName(final UUID uUID) {
        final Random random2 = getRandom(uUID);
        return getRandomString(random2, DebugVillagerNameGenerator.NAMES_FIRST_PART) + getRandomString(random2, DebugVillagerNameGenerator.NAMES_SECOND_PART);
    }
    
    private static String getRandomString(final Random random, final String[] arr) {
        return arr[random.nextInt(arr.length)];
    }
    
    private static Random getRandom(final UUID uUID) {
        return new Random((long)(uUID.hashCode() >> 2));
    }
    
    static {
        NAMES_FIRST_PART = new String[] { "Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook" };
        NAMES_SECOND_PART = new String[] { "Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue" };
    }
}

package net.minecraft.client.resources.language;

public class I18n {
    private static Locale locale;
    
    static void setLocale(final Locale dya) {
        I18n.locale = dya;
    }
    
    public static String get(final String string, final Object... arr) {
        return I18n.locale.get(string, arr);
    }
    
    public static boolean exists(final String string) {
        return I18n.locale.has(string);
    }
}

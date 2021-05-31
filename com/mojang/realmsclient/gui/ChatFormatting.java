package com.mojang.realmsclient.gui;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.Map;

public enum ChatFormatting {
    BLACK('0'), 
    DARK_BLUE('1'), 
    DARK_GREEN('2'), 
    DARK_AQUA('3'), 
    DARK_RED('4'), 
    DARK_PURPLE('5'), 
    GOLD('6'), 
    GRAY('7'), 
    DARK_GRAY('8'), 
    BLUE('9'), 
    GREEN('a'), 
    AQUA('b'), 
    RED('c'), 
    LIGHT_PURPLE('d'), 
    YELLOW('e'), 
    WHITE('f'), 
    OBFUSCATED('k', true), 
    BOLD('l', true), 
    STRIKETHROUGH('m', true), 
    UNDERLINE('n', true), 
    ITALIC('o', true), 
    RESET('r');
    
    private static final Map<Character, ChatFormatting> FORMATTING_BY_CHAR;
    private static final Map<String, ChatFormatting> FORMATTING_BY_NAME;
    private static final Pattern STRIP_FORMATTING_PATTERN;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    
    private ChatFormatting(final char character) {
        this(character, false);
    }
    
    private ChatFormatting(final char character, final boolean boolean4) {
        this.code = character;
        this.isFormat = boolean4;
        this.toString = new StringBuilder().append("§").append(character).toString();
    }
    
    public char getChar() {
        return this.code;
    }
    
    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
    
    public String toString() {
        return this.toString;
    }
    
    static {
        FORMATTING_BY_CHAR = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(ChatFormatting::getChar, cvw -> cvw));
        FORMATTING_BY_NAME = (Map)Arrays.stream((Object[])values()).collect(Collectors.toMap(ChatFormatting::getName, cvw -> cvw));
        STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    }
}

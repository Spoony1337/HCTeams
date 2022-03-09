package rip.orbit.hcteams.util;

import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;

public class SymbolUtil
{
    public static String UNICODE_MINI_STAR;
    public static String UNICODE_NORMAL_STAR;
    public static String UNICODE_ORIGIN_STAR;
    public static String UNICODE_STAR_CUTE;
    public static String UNICODE_VERTICAL_BAR;
    public static String UNICODE_CAUTION;
    public static String UNICODE_ARROW_LEFT;
    public static String UNICODE_ARROW_RIGHT;
    public static String UNICODE_ARROWS_LEFT;
    public static String UNICODE_ARROWS_RIGHT;
    public static String UNICODE_HEART;
    public static String UNICODE_ICE_STAR;
    public static String UNICODE_STICK;
    
    static {
        UNICODE_MINI_STAR = StringEscapeUtils.unescapeJava("\u22c6");
        UNICODE_NORMAL_STAR = StringEscapeUtils.unescapeJava("\u2605");
        UNICODE_ORIGIN_STAR = StringEscapeUtils.unescapeJava("\u2726");
        UNICODE_STAR_CUTE = StringEscapeUtils.unescapeJava("\u2735");
        UNICODE_VERTICAL_BAR = StringEscapeUtils.unescapeJava("\u2503");
        UNICODE_CAUTION = StringEscapeUtils.unescapeJava("\u26a0");
        UNICODE_ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25c0");
        UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("\u25b6");
        UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("«");
        UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("»");
        UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");
        UNICODE_ICE_STAR = StringEscapeUtils.unescapeJava("\u2746");
        UNICODE_STICK = StringEscapeUtils.unescapeJava("\u2503");
    }
}

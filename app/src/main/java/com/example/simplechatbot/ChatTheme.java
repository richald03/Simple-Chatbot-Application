package com.example.simplechatbot;

import android.graphics.Color;

public class ChatTheme {
    public static final String THEME_BLUE = "blue";
    public static final String THEME_GREEN = "green";
    public static final String THEME_ROSE = "rose";

    public final int primary;
    public final int primaryDark;
    public final int accent;
    public final int background;
    public final int surface;
    public final int surfaceAlt;
    public final int textPrimary;
    public final int textSecondary;
    public final int userBubbleStart;
    public final int userBubbleEnd;
    public final int userBubbleStroke;
    public final int botBubble;
    public final int botBubbleStroke;
    public final int userText;
    public final int botText;
    public final int timeUserBg;
    public final int timeUserText;
    public final int timeBotBg;
    public final int timeBotText;
    public final int userAvatarFill;
    public final int userAvatarText;
    public final int botAvatarFill;
    public final int botAvatarText;
    public final int divider;

    private ChatTheme(
            int primary,
            int primaryDark,
            int accent,
            int background,
            int surface,
            int surfaceAlt,
            int textPrimary,
            int textSecondary,
            int userBubbleStart,
            int userBubbleEnd,
            int userBubbleStroke,
            int botBubble,
            int botBubbleStroke,
            int userText,
            int botText,
            int timeUserBg,
            int timeUserText,
            int timeBotBg,
            int timeBotText,
            int userAvatarFill,
            int userAvatarText,
            int botAvatarFill,
            int botAvatarText,
            int divider
    ) {
        this.primary = primary;
        this.primaryDark = primaryDark;
        this.accent = accent;
        this.background = background;
        this.surface = surface;
        this.surfaceAlt = surfaceAlt;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.userBubbleStart = userBubbleStart;
        this.userBubbleEnd = userBubbleEnd;
        this.userBubbleStroke = userBubbleStroke;
        this.botBubble = botBubble;
        this.botBubbleStroke = botBubbleStroke;
        this.userText = userText;
        this.botText = botText;
        this.timeUserBg = timeUserBg;
        this.timeUserText = timeUserText;
        this.timeBotBg = timeBotBg;
        this.timeBotText = timeBotText;
        this.userAvatarFill = userAvatarFill;
        this.userAvatarText = userAvatarText;
        this.botAvatarFill = botAvatarFill;
        this.botAvatarText = botAvatarText;
        this.divider = divider;
    }

    public static ChatTheme from(String themeKey, boolean darkMode) {
        String key = themeKey == null ? THEME_BLUE : themeKey;
        switch (key) {
            case THEME_GREEN:
                return darkMode ? greenDark() : greenLight();
            case THEME_ROSE:
                return darkMode ? roseDark() : roseLight();
            case THEME_BLUE:
            default:
                return darkMode ? blueDark() : blueLight();
        }
    }

    private static ChatTheme blueLight() {
        return new ChatTheme(
                color("#2563EB"), color("#1D4ED8"), color("#60A5FA"),
                color("#F5F7FB"), color("#FFFFFF"), color("#EEF4FF"),
                color("#111827"), color("#6B7280"),
                color("#2563EB"), color("#3B82F6"), color("#1D4ED8"),
                color("#FFFFFF"), color("#D6DEEE"),
                color("#FFFFFF"), color("#111827"),
                color("#1AFFFFFF"), color("#DBEAFE"),
                color("#EEF4FF"), color("#64748B"),
                color("#DBEAFE"), color("#1D4ED8"),
                color("#111827"), color("#F8FAFC"),
                color("#E5E7EB")
        );
    }

    private static ChatTheme blueDark() {
        return new ChatTheme(
                color("#60A5FA"), color("#1D4ED8"), color("#93C5FD"),
                color("#0F172A"), color("#111827"), color("#172033"),
                color("#F9FAFB"), color("#9CA3AF"),
                color("#2563EB"), color("#3B82F6"), color("#60A5FA"),
                color("#172033"), color("#334155"),
                color("#FFFFFF"), color("#F9FAFB"),
                color("#1AFFFFFF"), color("#DBEAFE"),
                color("#172033"), color("#CBD5E1"),
                color("#1E3A8A"), color("#DBEAFE"),
                color("#E2E8F0"), color("#0F172A"),
                color("#374151")
        );
    }

    private static ChatTheme greenLight() {
        return new ChatTheme(
                color("#059669"), color("#047857"), color("#34D399"),
                color("#F4FBF7"), color("#FFFFFF"), color("#E9F9F0"),
                color("#102A1F"), color("#5B6F66"),
                color("#059669"), color("#10B981"), color("#047857"),
                color("#FFFFFF"), color("#D5E8DE"),
                color("#FFFFFF"), color("#102A1F"),
                color("#14FFFFFF"), color("#D1FAE5"),
                color("#EAFBF2"), color("#4B6357"),
                color("#D1FAE5"), color("#047857"),
                color("#102A1F"), color("#F0FDF4"),
                color("#DDEFE5")
        );
    }

    private static ChatTheme greenDark() {
        return new ChatTheme(
                color("#34D399"), color("#047857"), color("#6EE7B7"),
                color("#0C1713"), color("#102019"), color("#173126"),
                color("#F3FBF7"), color("#94A89D"),
                color("#059669"), color("#10B981"), color("#34D399"),
                color("#173126"), color("#29503F"),
                color("#FFFFFF"), color("#F3FBF7"),
                color("#18FFFFFF"), color("#D1FAE5"),
                color("#173126"), color("#D1E7DA"),
                color("#064E3B"), color("#D1FAE5"),
                color("#DCFCE7"), color("#102019"),
                color("#2A4437")
        );
    }

    private static ChatTheme roseLight() {
        return new ChatTheme(
                color("#E11D48"), color("#BE123C"), color("#FB7185"),
                color("#FFF6F8"), color("#FFFFFF"), color("#FFF0F3"),
                color("#2E1320"), color("#7A6570"),
                color("#E11D48"), color("#F43F5E"), color("#BE123C"),
                color("#FFFFFF"), color("#F0D8DF"),
                color("#FFFFFF"), color("#2E1320"),
                color("#14FFFFFF"), color("#FFE4E9"),
                color("#FFF0F3"), color("#7A5B66"),
                color("#FFE4E9"), color("#BE123C"),
                color("#2E1320"), color("#FFF7F9"),
                color("#F3DFE5")
        );
    }

    private static ChatTheme roseDark() {
        return new ChatTheme(
                color("#FB7185"), color("#BE123C"), color("#FDA4AF"),
                color("#180D13"), color("#24111A"), color("#311723"),
                color("#FFF7F9"), color("#BFA8B0"),
                color("#E11D48"), color("#F43F5E"), color("#FB7185"),
                color("#311723"), color("#5B2A3A"),
                color("#FFFFFF"), color("#FFF7F9"),
                color("#18FFFFFF"), color("#FFE4E9"),
                color("#311723"), color("#F3CAD4"),
                color("#881337"), color("#FFE4E9"),
                color("#FFE4E6"), color("#2A0F18"),
                color("#4A2834")
        );
    }

    private static int color(String hex) {
        return Color.parseColor(hex);
    }
}

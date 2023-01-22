package me.ed333.plugin.advancedannouncement.utils;

import me.ed333.plugin.advancedannouncement.instances.component.TextComponentBlock;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.regex.Matcher;

public class TextHandler {
    public static @NotNull String handleColor(String content, boolean legacy) {
        StringBuilder sb = new StringBuilder();
        Matcher colorMatcher = PlaceholderRegex.getCOLOR_PLACEHOLDER_MATCHER(content);
        int lastIndex = 0;
        String lastCode = null;

        while (colorMatcher.find()) {
            int start = colorMatcher.start();
            int end = colorMatcher.end();

            if (lastCode == null) {
                sb.append(content, lastIndex, start);
            } else {
                String textBetween = content.substring(lastIndex, start);
                StringBuilder magicCodes = new StringBuilder();
                Matcher magicCodeMatcher = PlaceholderRegex.getMAGIC_CODE_MATCHER(lastCode);
                while (magicCodeMatcher.find()) {
                    magicCodes.append(magicCodeMatcher.group());
                }

                if (lastCode.matches(PlaceholderRegex.HEX_COLOR_PLACEHOLDER_REGEX_STRING)) {
                    Matcher matcher = PlaceholderRegex.getHEX_COLOR_CODE_MATCHER(lastCode);
                    String colorCode ="#FFFFFF";
                    if (matcher.find()) {
                        colorCode = matcher.group();
                    }
                    ChatColor chatColor;
                    Color color = new Color(Integer.parseInt(colorCode.substring(1), 16));
                    if (legacy) {
                        chatColor = LegacyColor.getLegacyClosestColor(color);
                        if (chatColor == null) chatColor = ChatColor.WHITE;
                    } else {
                        chatColor = ChatColor.of(color);
                    }

                    textBetween = chatColor + ChatColor.translateAlternateColorCodes('&', magicCodes.toString()) + textBetween;
                } else if (lastCode.matches(PlaceholderRegex.GRADIENT_COLOR_PLACEHOLDER_REGEX_STRING)) {
                    Matcher hexMatcher = PlaceholderRegex.getHEX_COLOR_CODE_MATCHER(lastCode);
                    String fromCode = null;
                    String toCode = null;

                    while (hexMatcher.find()) {
                        if (fromCode == null) {
                            fromCode = hexMatcher.group();
                        } else {
                            toCode = hexMatcher.group();
                        }
                    }

                    if (fromCode != null && toCode != null) {
                        Color fromColor = new Color(Integer.parseInt(fromCode.substring(1), 16));
                        Color toColor = new Color(Integer.parseInt(toCode.substring(1), 16));
                        textBetween = createGradientString(textBetween, fromColor, toColor, legacy, magicCodes.toString());
                    }
                } else if (lastCode.matches(PlaceholderRegex.RAINBOW_PLACEHOLDER_REGEX_STRING)) {
                    textBetween = createRainbowGradient(textBetween, legacy, magicCodes.toString());
                } else if (lastCode.matches(PlaceholderRegex.LEGACY_CODE_REGEX_STRING)) {
                    textBetween = ChatColor.translateAlternateColorCodes('&', lastCode + textBetween);
                }
                sb.append(textBetween);
            }
            lastCode = colorMatcher.group();
            lastIndex = end;
        }

        if (lastIndex < content.length()) {
            if (lastCode == null) {
                sb.append(content, lastIndex, content.length());
            } else {
                String textBetween = content.substring(lastIndex);
                StringBuilder magicCodes = new StringBuilder();
                Matcher magicCodeMatcher = PlaceholderRegex.getMAGIC_CODE_MATCHER(lastCode);
                while (magicCodeMatcher.find()) {
                    magicCodes.append(magicCodeMatcher.group());
                }

                if (lastCode.matches(PlaceholderRegex.HEX_COLOR_PLACEHOLDER_REGEX_STRING)) {
                    Matcher matcher = PlaceholderRegex.getHEX_COLOR_CODE_MATCHER(lastCode);
                    String colorCode ="#FFFFFF";
                    if (matcher.find()) {
                        colorCode = matcher.group();
                    }
                    ChatColor chatColor;
                    Color color = new Color(Integer.parseInt(colorCode.substring(1), 16));
                    if (legacy) {
                        chatColor = LegacyColor.getLegacyClosestColor(color);
                        if (chatColor == null) chatColor = ChatColor.WHITE;
                    } else {
                        chatColor = ChatColor.of(color);
                    }

                    textBetween = chatColor + ChatColor.translateAlternateColorCodes('&', magicCodes.toString()) + textBetween;
                } else if (lastCode.matches(PlaceholderRegex.GRADIENT_COLOR_PLACEHOLDER_REGEX_STRING)) {
                    Matcher hexMatcher = PlaceholderRegex.getHEX_COLOR_CODE_MATCHER(lastCode);
                    String fromCode = null;
                    String toCode = null;
                    while (hexMatcher.find()) {
                        if (fromCode == null) {
                            fromCode = hexMatcher.group();
                        } else {
                            toCode = hexMatcher.group();
                        }
                    }
                    if (fromCode != null && toCode != null) {
                        Color fromColor = new Color(Integer.parseInt(fromCode.substring(1), 16));
                        Color toColor = new Color(Integer.parseInt(toCode.substring(1), 16));

                        textBetween = createGradientString(textBetween, fromColor, toColor, legacy, magicCodes.toString());
                    }
                } else if (lastCode.matches(PlaceholderRegex.RAINBOW_PLACEHOLDER_REGEX_STRING)) {
                    textBetween = createRainbowGradient(textBetween, legacy, magicCodes.toString());
                } else if (lastCode.matches(PlaceholderRegex.LEGACY_CODE_REGEX_STRING)) {
                    textBetween = ChatColor.translateAlternateColorCodes('&', lastCode + textBetween);
                }
                sb.append(textBetween);
            }
        }
        return sb.toString();
    }

    public static @NotNull String createGradientString(String text, Color from, Color to, boolean legacy, @Nullable String magicCode) {
        return createGradientString(text, from.getRed(), from.getGreen(), from.getBlue(), to.getRed(), to.getGreen(), to.getBlue(), legacy, magicCode);
    }

    public static @NotNull String createGradientString(@NotNull String text, int r1, int g1, int b1, int r2, int g2, int b2, boolean legacy, @Nullable String magicCode) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            double rate = (double) i / length;
            int r = (int) (r1 + (r2 - r1) * rate);
            int g = (int) (g1 + (g2 - g1) * rate);
            int b = (int) (b1 + (b2 - b1) * rate);
            ChatColor chatColor;
            Color nowColor = new Color(r, g, b);
            if (legacy) {
                chatColor = LegacyColor.getLegacyClosestColor(nowColor);
                if (chatColor == null) chatColor = ChatColor.WHITE;
            } else {
                chatColor = ChatColor.of(nowColor);
            }
            sb.append(chatColor).append(magicCode != null ? ChatColor.translateAlternateColorCodes('&', magicCode) : "").append(text.charAt(i));
        }
        return sb.toString();
    }

    public static @NotNull String createRainbowGradient(@NotNull String text, boolean legacy, @Nullable String magicCode) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            double hue = (double) i / length;
            int color = Color.HSBtoRGB((float) hue, 1.0f, 1.0f);
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = color & 0xff;
            ChatColor chatColor;
            Color nowColor = new Color(r, g, b);
            if (legacy) {
                chatColor = LegacyColor.getLegacyClosestColor(nowColor);
            } else {
                chatColor = ChatColor.of(new Color(r, g, b));
            }
            sb.append(chatColor == null ? ChatColor.WHITE : chatColor).append(magicCode != null ? ChatColor.translateAlternateColorCodes('&', magicCode) : "").append(text.charAt(i));
        }
        return sb.toString();
    }

    public static @NotNull TextComponent toTextComponent(String content, boolean legacy) {
        TextComponent result = new TextComponent();
        Matcher matcher = PlaceholderRegex.getCOMPONENT_PLACEHOLDER_MATCHER(content);

        int lastIndex = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            TextComponent coloredPlainText = new TextComponent();
            for (BaseComponent baseComponent : TextComponent.fromLegacyText(handleColor(content.substring(lastIndex, start), legacy))) {
                coloredPlainText.addExtra(baseComponent);
            }
            result.addExtra(coloredPlainText);
            String placeHolder = matcher.group();
            String blockName = placeHolder.substring(1, placeHolder.length() - 1);
            TextComponentBlock block = TextComponentBlock.forName(blockName);
            if (block != null) {
                result.addExtra(block.getComponent());
            }
            lastIndex = end;
        }

        if (lastIndex < content.length()) {
            TextComponent coloredPlainText = new TextComponent();
            for (BaseComponent baseComponent : TextComponent.fromLegacyText(handleColor(content.substring(lastIndex), legacy))) {
                coloredPlainText.addExtra(baseComponent);
            }
            result.addExtra(coloredPlainText);
        }
        return result;
    }
}

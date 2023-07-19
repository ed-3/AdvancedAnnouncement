package top.ed333.mcplugin.advancedann.common.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ed333.mcplugin.advancedann.common.components.ComponentBlock;
import top.ed333.mcplugin.advancedann.common.components.ComponentManager;

import java.awt.*;
import java.util.regex.Matcher;

public class TextHandler {
    /**
     * @param isLegacy whether this server's version is lower than 1.16
     */
    public static @NotNull JsonArray constructToJsonArr(@NotNull String content, boolean isLegacy) {
        JsonArray array = new JsonArray();

        Matcher componentMatcher = PlaceholderRegex.TextRegex.getCOMPONENT_PLACEHOLDER_MATCHER(content);

        int lastIndex = 0;
        while (componentMatcher.find()) {
            int start = componentMatcher.start();
            int end = componentMatcher.end();
            JsonArray a1 = constructArr(content.substring(lastIndex, start), isLegacy);
            array.addAll(a1);
            String placeholder = componentMatcher.group();
            String placeholderName = placeholder.substring(1, placeholder.length() - 1);
            ComponentBlock block = ComponentManager.forName(placeholderName);
            if (block != null) {
                array.addAll(block.constructToJsonArr(isLegacy));
            } else {
                JsonObject result = new JsonObject();
                result.addProperty("text", placeholder);
                array.add(result);
            }
            lastIndex = end;
        }

        if (lastIndex < content.length()) {
            array.addAll(constructArr(content.substring(lastIndex), isLegacy));
        }
        return array;
    }

    public static @NotNull String handleColor(String content, boolean isLegacy) {
        StringBuilder sb = new StringBuilder();

        int lastIndex = 0;
        String lastCode = null;
        Matcher colorMatcher = PlaceholderRegex.TextRegex.getCOLOR_PLACEHOLDER_MATCHER(content);

        while (colorMatcher.find()) {
            int start = colorMatcher.start();
            int end = colorMatcher.end();

            if (lastCode == null) {
                sb.append(content, lastIndex, start);
            } else {
                String textBetween = content.substring(lastIndex, start);
                sb.append(dealColor(textBetween, lastCode, isLegacy));
            }
            lastCode = colorMatcher.group();
            lastIndex = end;
        }

        if (lastIndex < content.length()) {
            if (lastCode == null) {
                sb.append(content, lastIndex, content.length());
            } else {
                String textBetween = content.substring(lastIndex);
                sb.append(dealColor(textBetween, lastCode, isLegacy));
            }
        }
        return sb.toString();
    }

    /**
     * create gradient string by {@link Color}
     * @param text the gradient text
     * @param from the start color
     * @param to the end color
     * @param legacy use legacy color
     * @param magicCode magic codes
     * @return string that created
     */
    public static @NotNull String createGradientString(
            String text,
            Color from,
            Color to,
            boolean legacy,
            @Nullable String magicCode
    ) {
        return createGradientString(
                text,
                from.getRed(), from.getGreen(), from.getBlue(),
                to.getRed(), to.getGreen(), to.getBlue(),
                legacy,
                magicCode
        );
    }

    public static @NotNull String createGradientString(
            @NotNull String text,
            int r1, int g1, int b1,
            int r2, int g2, int b2,
            boolean legacy,
            @Nullable String magicCode
    ) {
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

    private static String dealColor(String input, String lastCode, boolean legacy) {
        // deal magic code
        StringBuilder magicCodes = new StringBuilder();
        Matcher magicCodeMatcher = PlaceholderRegex.TextRegex.getMAGIC_CODE_MATCHER(lastCode);
        while (magicCodeMatcher.find()) {
            magicCodes.append(magicCodeMatcher.group());
        }

        // deal hex color
        if (lastCode.matches(PlaceholderRegex.TextRegex.HEX_COLOR_PLACEHOLDER_REGEX_STRING)) {
            Matcher matcher = PlaceholderRegex.TextRegex.getHEX_COLOR_CODE_MATCHER(lastCode);
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

            input = chatColor + ChatColor.translateAlternateColorCodes('&', magicCodes.toString()) + input;

        } else if (lastCode.matches(PlaceholderRegex.TextRegex.GRADIENT_COLOR_PLACEHOLDER_REGEX_STRING)) {
            // deal gradient color
            Matcher hexMatcher = PlaceholderRegex.TextRegex.getHEX_COLOR_CODE_MATCHER(lastCode);
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

                input = createGradientString(input, fromColor, toColor, legacy, magicCodes.toString());
            }
        } else if (lastCode.matches(PlaceholderRegex.TextRegex.RAINBOW_PLACEHOLDER_REGEX_STRING)) {
            // deal rainbow color
            input = createRainbowGradient(input, legacy, magicCodes.toString());
        } else if (lastCode.matches(PlaceholderRegex.TextRegex.LEGACY_CODE_REGEX_STRING)) {
            // deal legacy color
            input = ChatColor.translateAlternateColorCodes('&', lastCode + input);
        }
        return input;
    }

    private static JsonArray constructArr(String content, boolean isLegacy) {
        JsonArray array = new JsonArray();
        if (content.isEmpty()) {
            return array;
        } else {
            BaseComponent[] component = TextComponent.fromLegacyText(handleColor(content, isLegacy));
            for (BaseComponent baseComponent : component) {
                array.add(constructObj(baseComponent));
            }
        }
        return array;
    }

    public static JsonObject constructObj(BaseComponent component) {
        JsonObject componentJsonObj = new JsonObject();
        String componentText = component.toPlainText();

        ChatColor componentColor = component.getColor();

        componentJsonObj.addProperty("text", componentText);
        if (componentColor != null) componentJsonObj.addProperty("color", componentColor.getName());
        if (component.isBold()) componentJsonObj.addProperty("bold", component.isBold());
        if (component.isItalic()) componentJsonObj.addProperty("italic", component.isItalic());
        if (component.isObfuscated()) componentJsonObj.addProperty("obfuscated", component.isObfuscated());
        if (component.isUnderlined()) componentJsonObj.addProperty("underlined", component.isUnderlined());
        if (component.isStrikethrough()) componentJsonObj.addProperty("strikethrough", component.isStrikethrough());
        return componentJsonObj;
    }


}

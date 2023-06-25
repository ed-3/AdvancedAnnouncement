package top.ed333.mcplugin.advancedann.bungee.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.ArrayList;
import java.util.List;

public class Serializer {
    public static BaseComponent[] serializeToComponent(JsonArray array) {
        List<BaseComponent> result = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject singleObj = element.getAsJsonObject();
            TextComponent aComponent = constructComponent(singleObj);
            if (singleObj.has("hoverEvent")) {
                JsonObject hoverObj = singleObj.get("hoverEvent").getAsJsonObject();
                BaseComponent[] hoverContents = serializeToComponent(hoverObj.get("contents").getAsJsonArray());
                HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverContents));
                aComponent.setHoverEvent(event);
            }

            if (singleObj.has("clickEvent")) {
                JsonObject clickObj = singleObj.get("clickEvent").getAsJsonObject();
                ClickEvent.Action clickAction;
                String actionName = clickObj.get("action").getAsString();
                String actionValue = clickObj.get("value").getAsString();
                try {
                    clickAction = ClickEvent.Action.valueOf(actionName);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                ClickEvent event = new ClickEvent(clickAction, actionValue);
                aComponent.setClickEvent(event);
            }
            result.add(aComponent);
        }

        BaseComponent[] resultArr = new BaseComponent[result.size()];
        for (int i = 0; i < result.size(); i++) {
            resultArr[i] = result.get(i);
        }
        return resultArr;
    }

    private static TextComponent constructComponent(JsonObject inputJson) {
        TextComponent result = new TextComponent();
        result.setText(inputJson.get("text").getAsString());
        if (inputJson.has("insertion"))         result.setInsertion(inputJson.get("insertion").getAsString());
        if (inputJson.has("color"))             result.setColor(ChatColor.of(inputJson.get("color").getAsString()));
        if (inputJson.has("bold"))              result.setBold(inputJson.get("bold").getAsBoolean());
        if (inputJson.has("italic"))            result.setItalic(inputJson.get("italic").getAsBoolean());
        if (inputJson.has("obfuscated"))        result.setObfuscated(inputJson.get("obfuscated").getAsBoolean());
        if (inputJson.has("underlined"))        result.setUnderlined(inputJson.get("underlined").getAsBoolean());
        if (inputJson.has("strikethrough"))     result.setStrikethrough(inputJson.get("strikethrough").getAsBoolean());
        return result;
    }
}

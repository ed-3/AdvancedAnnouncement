package me.ed333.plugin.advancedannouncement.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class NormalBlock extends ComponentBlock {

    private final String text;
    private final String hoverVal;
    private final ClickEvent.Action clickAction;
    private final String clickVal;

    public NormalBlock(String name) {
        this(name, "", "", null, null);
    }

    public NormalBlock(String name, String text) {
        this(name, text, "", null, null);
    }

    public NormalBlock(String name, String text, String hoverVal) {
        this(name, text, hoverVal, null, null);
    }

    public NormalBlock(String name, String text, String hoverVal, ClickEvent.Action action, String clickVal) {
        super(name, ComponentType.NORMAL);
        this.text = text;
        this.clickAction = action;
        this.hoverVal = hoverVal;
        this.clickVal = clickVal;
    }

    public String getText() {
        return text;
    }

    public ClickEvent.Action getClickAction() {
        return clickAction;
    }

    public String getClickVal() {
        return clickVal;
    }

    public String getHoverVal() {
        return hoverVal;
    }

    @Override
    public String constructToJsonStr(CommandSender sender) {
        return constructToJsonArr(sender).toString();
    }

    @Override
    public JsonArray constructToJsonArr(CommandSender sender) {
        JsonArray array = new JsonArray();

        if (this.text.isEmpty()) {
            JsonObject emptyObj = new JsonObject();
            emptyObj.addProperty("", "");
            array.add(emptyObj);
        } else {
            BaseComponent[] component = TextComponent.fromLegacyText(TextHandler.handleColor(this.text, sender));

            for (BaseComponent baseComponent : component) {
                JsonObject componentJsonObj = new JsonObject();
                String componentText = baseComponent.toPlainText();

                ChatColor componentColor = baseComponent.getColor();

                componentJsonObj.addProperty("text", componentText);
                if (componentColor != null) componentJsonObj.addProperty("color", componentColor.getName());
                if (baseComponent.isBold()) componentJsonObj.addProperty("bold", baseComponent.isBold());
                if (baseComponent.isItalic()) componentJsonObj.addProperty("italic", baseComponent.isItalic());
                if (baseComponent.isObfuscated()) componentJsonObj.addProperty("obfuscated", baseComponent.isObfuscated());
                if (baseComponent.isUnderlined()) componentJsonObj.addProperty("underlined", baseComponent.isUnderlined());
                if (baseComponent.isStrikethrough()) componentJsonObj.addProperty("strikethrough", baseComponent.isStrikethrough());

                if (this.hoverVal != null) {
                    JsonObject hoverObj = new JsonObject();
                    hoverObj.addProperty("action", "show_text");

                    JsonArray hoverContentArr = new JsonArray();
                    for (BaseComponent hoverVal : TextComponent.fromLegacyText(TextHandler.handleColor(this.hoverVal, sender))) {
                        JsonObject hoverContentObj = new JsonObject();
                        ChatColor hoverColor = hoverVal.getColor();
                        hoverContentObj.addProperty("text", hoverVal.toPlainText());
                        if (hoverColor != null) hoverContentObj.addProperty("color", hoverColor.getName());
                        if (hoverVal.isBold()) hoverContentObj.addProperty("bold", hoverVal.isBold());
                        if (hoverVal.isItalic()) hoverContentObj.addProperty("italic", hoverVal.isItalic());
                        if (hoverVal.isObfuscated()) hoverContentObj.addProperty("obfuscated", hoverVal.isObfuscated());
                        if (hoverVal.isUnderlined()) hoverContentObj.addProperty("underlined", hoverVal.isUnderlined());
                        if (hoverVal.isStrikethrough()) hoverContentObj.addProperty("strikethrough", hoverVal.isStrikethrough());
                        hoverContentArr.add(hoverContentObj);
                    }


                    hoverObj.add("contents",hoverContentArr);
                    componentJsonObj.add("hoverEvent", hoverObj);
                }

                if (clickAction != null) {
                    JsonObject clickObj = new JsonObject();
                    clickObj.addProperty("action", this.clickAction.name().toLowerCase());
                    clickObj.addProperty("value", this.clickVal);
                    componentJsonObj.add("clickEvent", clickObj);
                }
                array.add(componentJsonObj);
            }
        }
        return array;
    }
}

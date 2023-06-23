package top.ed333.mcplugin.advancedann.common.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

public class NormalBlock extends ComponentBlock {

    private final String text;
    private final String hoverVal;
    private final ClickEvent.Action clickAction;
    private final String clickVal;

    public NormalBlock(String name) {
        this(name, "", "", null, null);
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

    @Override
    public JsonArray constructToJsonArr(boolean isLegacy) {
        JsonArray array = new JsonArray();

        if (this.text.isEmpty()) {
            JsonObject emptyObj = new JsonObject();
            emptyObj.addProperty("", "");
            array.add(emptyObj);
        } else {
            BaseComponent[] component = TextComponent.fromLegacyText(TextHandler.handleColor(this.text, isLegacy));

            for (BaseComponent baseComponent : component) {
                JsonObject componentJsonObj = TextHandler.constructObj(baseComponent);

                if (this.hoverVal != null) {
                    JsonObject hoverObj = new JsonObject();
                    hoverObj.addProperty("action", "show_text");

                    JsonArray hoverContentArr = new JsonArray();
                    for (BaseComponent hoverVal : TextComponent.fromLegacyText(TextHandler.handleColor(this.hoverVal, isLegacy))) {
                        JsonObject hoverContentObj = TextHandler.constructObj(hoverVal);
                        hoverContentArr.add(hoverContentObj);
                    }
                    hoverObj.add("contents", hoverContentArr);
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

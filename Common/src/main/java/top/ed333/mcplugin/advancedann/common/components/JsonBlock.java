package top.ed333.mcplugin.advancedann.common.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class JsonBlock extends ComponentBlock {

    private final JsonArray jsonArr;
    public JsonBlock(String name, String jsonStr) {
        super(name, ComponentType.JSON);

        this.jsonArr = new JsonParser().parse(jsonStr).getAsJsonArray();
    }

    @Override
    public JsonArray constructToJsonArr(boolean isLegacy) {
        return jsonArr;
    }
}

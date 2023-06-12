package me.ed333.plugin.advancedannouncement.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.bukkit.command.CommandSender;

public class JsonBlock extends ComponentBlock {

    private final JsonArray jsonArr;
    public JsonBlock(String name, String jsonStr) {
        super(name, ComponentType.JSON);

        this.jsonArr = new JsonParser().parse(jsonStr).getAsJsonArray();
    }

    @Override
    public String constructToJsonStr(CommandSender sender) {
        return jsonArr.toString();
    }

    @Override
    public JsonArray constructToJsonArr(CommandSender sender) {
        return jsonArr;
    }
}

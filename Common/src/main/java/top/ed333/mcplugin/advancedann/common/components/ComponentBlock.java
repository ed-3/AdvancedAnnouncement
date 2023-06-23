package top.ed333.mcplugin.advancedann.common.components;

import com.google.gson.JsonArray;

// represents a text component in Component.yml
public abstract class ComponentBlock {

    private final String name;
    private final ComponentType type;

    public ComponentBlock(String name, ComponentType type) {
        this.name = name;
        this.type = type;
        ComponentManager.blocks.put(name, this);
    }

    public abstract JsonArray constructToJsonArr(boolean isLegacy);
}

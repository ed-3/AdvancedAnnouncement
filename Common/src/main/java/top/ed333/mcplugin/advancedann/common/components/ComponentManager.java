package top.ed333.mcplugin.advancedann.common.components;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ComponentManager {
    public static final HashMap<String, ComponentBlock> blocks = new HashMap<>();

    public static @Nullable ComponentBlock forName(String name) {
        for (Map.Entry<String, ComponentBlock> entry : blocks.entrySet()) {
            if (entry.getKey().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }
}

package me.ed333.plugin.advancedannouncement.components;

import com.google.gson.JsonArray;
import org.bukkit.command.CommandSender;
// represents a text component in Component.yml
public abstract class ComponentBlock {

    private final String name;
    private final ComponentType type;

    public ComponentBlock(String name, ComponentType type) {
        this.name = name;
        this.type = type;
        ComponentManager.blocks.put(name, this);
    }

    public abstract JsonArray constructToJsonArr(CommandSender sender);
}
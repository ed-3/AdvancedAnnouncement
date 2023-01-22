package me.ed333.plugin.advancedannouncement.instances.component;

import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import net.md_5.bungee.api.chat.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// represents a text component in Component.yml
public class TextComponentBlock {
    public static final HashMap<String, TextComponentBlock> blocks = new HashMap<>();
    private final TextComponent textComponent;
    private final String name;

    public TextComponentBlock(String name) {
        textComponent = new TextComponent();
        this.name = name;
        blocks.put(name, this);
    }

    public TextComponentBlock(String name, String text) {
        textComponent = new TextComponent(TextHandler.handleColor(text, ProtocolUtils.isLegacyServer()));
        this.name = name;
        blocks.put(name, this);
    }

    public void setClick(ClickEvent.Action action, String val) {
        textComponent.setClickEvent(new ClickEvent(action, val));
    }

    public void setHover(HoverEvent.Action action, String content) {
        HoverEvent hover = new HoverEvent(action, TextComponent.fromLegacyText(TextHandler.handleColor(content, ProtocolUtils.isLegacyServer())));
        textComponent.setHoverEvent(hover);
    }

    public TextComponent getComponent() {
        return textComponent;
    }

    @Override
    public String toString() {
        return "TextComponentBlock{" + "textComponent=" + textComponent +
                ", name='" + name + '\'' +
                '}';
    }

    public static @Nullable TextComponentBlock forName(String name) {
        for (Map.Entry<String, TextComponentBlock> entry : blocks.entrySet()) {
            if (entry.getKey().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
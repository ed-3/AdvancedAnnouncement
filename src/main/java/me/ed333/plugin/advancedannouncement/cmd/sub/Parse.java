package me.ed333.plugin.advancedannouncement.cmd.sub;

import me.ed333.plugin.advancedannouncement.cmd.PermissionRequirement;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Parse {
    @PermissionRequirement("aa.command.parse")
    public static void callCmd(@NotNull CommandSender sender, String @NotNull [] args) {

        sender.sendMessage("handle color: " + TextHandler.handleColor(args[1], sender));
        TextComponent textComponent = new TextComponent();
        textComponent.addExtra("handle component: ");
        textComponent.addExtra(TextHandler.toTextComponent(args[1], sender));
        sender.spigot().sendMessage(textComponent);
    }
}

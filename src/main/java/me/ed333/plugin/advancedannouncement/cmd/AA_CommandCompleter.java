package me.ed333.plugin.advancedannouncement.cmd;

import me.ed333.plugin.advancedannouncement.announcement.AnnouncementManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AA_CommandCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (command.getName().equals("advancedannouncement")) {
            if (args.length == 1) {
                return TabEnum.FIRST.list;
            } else if (args.length == 2) {
                String subCmd = args[0];
                switch (subCmd) {
                    case "display":
                    case "broadcast":
                        return TabEnum.LOADED_ANN.list;
                    case "parse":
                        return TabEnum.PARSE.list;
                }
            }
        }
        return null;
    }

    private enum TabEnum {
        FIRST(Arrays.asList("help", "start", "stop", "display", "list", "on", "off", "parse", "reload", "broadcast")),
        LOADED_ANN(AnnouncementManager.getAnnNames()),
        PARSE(Collections.singletonList("请输入要解析的字符串"));
        private final List<String> list;

        TabEnum(List<String> list) {
            this.list = list;
        }
    }
}

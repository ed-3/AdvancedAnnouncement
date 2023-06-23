package top.ed333.mcplugin.advancedann.bungee.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.bungee.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bungee.utils.ConsoleSender;
import top.ed333.mcplugin.advancedann.bungee.utils.LangUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AA_Cmd extends Command implements TabExecutor {
    public AA_Cmd() {
        super("advancedannouncement", "aa.command.root", "aa");
        ConsoleSender.info("§2Registered command.");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            help(sender, args);
            return;
        }

        Method[] subCmd = this.getClass().getDeclaredMethods();
        for (Method method : subCmd) {
            SubCmd cmdAnn = method.getAnnotation(SubCmd.class);
            PermissionRequirement pr = method.getAnnotation(PermissionRequirement.class);

            if (cmdAnn == null) continue;

            for (String subStr : cmdAnn.value()) {
                if (subStr.equalsIgnoreCase(args[0])) {
                    // check perm
                    if (pr != null && !sender.hasPermission(pr.value()[0])) {
                        LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("command.permissionDeny"));
                    }

                    // invoke subcmd handler
                    try {
                        method.invoke(this, sender, args);
                        return;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("internalError") + "\n" + Arrays.toString(e.getStackTrace()));
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SubCmd("help")
    @PermissionRequirement("aa.command.help")
    @SuppressWarnings("unused")
    void help(@NotNull CommandSender sender, String @NotNull [] args) {
        LangUtils.getLangList("command.help").forEach(str -> LangUtils.sendMessage(sender, str));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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
        return null;
    }

    private enum TabEnum {
        FIRST(Arrays.asList("help", "start", "stop", "display", "list", "parse", "reload", "broadcast")),
        LOADED_ANN(AnnouncementManager.getAnnNames()),
        PARSE(Collections.singletonList("请输入要解析的字符串"));
        private final List<String> list;

        TabEnum(List<String> list) {
            this.list = list;
        }
    }
}

package top.ed333.mcplugin.advancedann.bungee.cmd;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bungee.BootStrap;
import top.ed333.mcplugin.advancedann.bungee.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bungee.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bungee.config.Config;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigKeys;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigManager;
import top.ed333.mcplugin.advancedann.bungee.runnable.AnnRunnable;
import top.ed333.mcplugin.advancedann.bungee.utils.*;
import top.ed333.mcplugin.advancedann.common.components.ComponentManager;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            ArgLen argLen = method.getAnnotation(ArgLen.class);
            PlayerOnly playerOnly = method.getAnnotation(PlayerOnly.class);
            PermissionRequirement pr = method.getAnnotation(PermissionRequirement.class);

            if (cmdAnn == null) continue;

            if (playerOnly != null && !(sender instanceof ProxiedPlayer)) {
                LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("command.playerOnly"));
                continue;
            }

            for (String subStr : cmdAnn.value()) {
                if (subStr.equalsIgnoreCase(args[0])) {
                    if (argLen != null) {
                        if (args.length != argLen.value()) {
                            LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("command.invalidArgs"));
                            return;
                        }
                    }

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

    @SubCmd("start")
    @PermissionRequirement("aa.command.start")
    void start(CommandSender sender, String @NotNull [] args) {
        if (AdvancedAnnouncement.announceTask == null) {
            AdvancedAnnouncement.announceTask = SchedulerUtils.scheduleNew(new AnnRunnable(), 60*1000, TimeUnit.MILLISECONDS);
            LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("ann-task-start"));
        } else {
            LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("ann-task-already-start"));
        }
    }

    @SubCmd("stop")
    @PermissionRequirement("aa.command.start")
    @SuppressWarnings("unused")
    void stop(CommandSender sender, String @NotNull [] args) {
        ScheduledTask task = AdvancedAnnouncement.announceTask;
        if (task != null) {
            task.cancel();
            AdvancedAnnouncement.announceTask = null;
            LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("ann-task-stop"));
        } else {
            LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("ann-task-already-stop"));
        }
    }

    @SubCmd({"broadcast", "bc"})
    @PermissionRequirement("aa.command.broadcast")
    @ArgLen(2)
    @SuppressWarnings("unused")
    void broadCast(@NotNull CommandSender sender, String @NotNull [] args) {
        String annName = args[1];
        Announcement ann = AnnouncementManager.forName(annName);
        if (ann == null) {
            LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("command.command-broadcast-not-found"));
            return;
        }

        ann.broadcast();

        LangUtils.sendMessage(sender, LangUtils.parseLang_withPrefix("command.command-broadcast-sent", ann.getName()));
    }

    @SubCmd("display")
    @PermissionRequirement("aa.command.display")
    @ArgLen(2)
    @PlayerOnly
    @SuppressWarnings("unused")
    void display(@NotNull CommandSender sender, String @NotNull [] args) {
        String annName = args[1];
        Announcement ann = AnnouncementManager.forName(annName);
        if (ann == null) {
            LangUtils.sendMessage(sender, LangUtils.getLangText_withPrefix("command.command-display-not-found"));
            return;
        }

        LangUtils.sendMessage(sender, LangUtils.parseLang_withPrefix("command.command-display-message", ann.getName()));
        ann.send((ProxiedPlayer) sender, !ProtocolUtils.canHandleRGB((ProxiedPlayer) sender));
    }

    @SubCmd("list")
    @PermissionRequirement("aa.command.list")
    @SuppressWarnings("unused")
    void list(@NotNull CommandSender sender, String @NotNull [] args) {
        LangUtils.sendMessage(sender, LangUtils.prefix + ChatColor.translateAlternateColorCodes('&', "&3所有已加载的公告: "));
        for (Announcement announcement : AnnouncementManager.loadedAnnouncements) {
            LangUtils.sendMessage(sender, ChatColor.translateAlternateColorCodes('&', " &7&l- &2Name: &a" + announcement.getName() + "&2, Delay: &a" + announcement.getDelay() + "s&2, Type: &a" + announcement.type().name()));
        }
    }

    @ArgLen(2)
    @SubCmd("parse")
    @PermissionRequirement("aa.command.parse")
    @SuppressWarnings("unused")
    void parse(@NotNull CommandSender sender, String @NotNull [] args) {
        boolean useLegacy = false;
        if (sender instanceof ConsoleCommandSender) {
            useLegacy = ProtocolUtils.isLegacyServer();
        } else  if (sender instanceof ProxiedPlayer) {
            useLegacy = ProtocolUtils.isPlayerLegacyVer((ProxiedPlayer) sender);
        }

        LangUtils.sendMessage(sender, "handle color: " + TextHandler.handleColor(args[1], useLegacy));
        JsonArray jsonArray = new JsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("text", "to component: ");
        jsonArray.add(object);
        jsonArray.addAll(TextHandler.constructToJsonArr(args[1], useLegacy));
        sender.sendMessage(Serializer.serializeToComponent(jsonArray));
    }

    @SubCmd("reload")
    @PermissionRequirement("aa.command.reload")
    @SuppressWarnings("unused")
    void reload(@NotNull CommandSender sender, String @NotNull [] args) {
        LangUtils.sendMessage(sender, LangUtils.getLangText("reload.start"));

        if (AdvancedAnnouncement.announceTask != null) {
            AdvancedAnnouncement.announceTask.cancel();
            AdvancedAnnouncement.announceTask = null;
            ConsoleSender.info(LangUtils.getLangText("ann-task-stop"));
        }

        ConfigManager.checkAllFile();
        ConfigManager.loadAll();
        ConfigKeys.initKey();

        // load translation
        Config config = ConfigManager.getConfig("config");
        String translationName = config.getConfig().getString("translation");
        Config translationCfg = new Config(
                "lang",
                AdvancedAnnouncement.INSTANCE.getResourceAsStream("translations/" + translationName + ".yml"),
                new File(AdvancedAnnouncement.DATA_FOLDER, "translations/" + translationName + ".yml")
        );
        ConfigManager.checkFile(translationCfg);
        translationCfg.load();

        // refresh translation
        LangUtils.refreshLang();

        ComponentManager.blocks.clear();
        AnnouncementManager.loadedAnnouncements.clear();
        BootStrap.loadComponentBlock();
        BootStrap.loadAnnouncements();

        AdvancedAnnouncement.announceTask = SchedulerUtils.scheduleNew(new AnnRunnable(), 60*1000, TimeUnit.MILLISECONDS);
        ConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        LangUtils.sendMessage(sender, LangUtils.getLangText("reload.done"));
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

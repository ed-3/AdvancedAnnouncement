package top.ed333.mcplugin.advancedann.bukkit.cmd;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.command.ConsoleCommandSender;
import top.ed333.mcplugin.advancedann.bukkit.Bootstrap;
import top.ed333.mcplugin.advancedann.bukkit.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bukkit.config.Config;
import top.ed333.mcplugin.advancedann.bukkit.config.ConfigManager;
import top.ed333.mcplugin.advancedann.bukkit.runnables.AnnounceRunnable;
import top.ed333.mcplugin.advancedann.bukkit.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bukkit.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bukkit.config.ConfigKeys;
import top.ed333.mcplugin.advancedann.bukkit.utils.GlobalConsoleSender;
import top.ed333.mcplugin.advancedann.bukkit.utils.LangUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.common.components.ComponentManager;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AA_CommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("advancedannouncement")) {
            if (args.length == 0) {
                help(sender, args);
                return true;
            }

            Method[] subCmd = this.getClass().getDeclaredMethods();
            for (Method method : subCmd) {
                SubCmd cmdAnn = method.getAnnotation(SubCmd.class);
                PermissionRequirement permissionRequirement = method.getAnnotation(PermissionRequirement.class);
                if (cmdAnn == null) continue;

                for (String subStr : cmdAnn.value()) {
                    if (subStr.equalsIgnoreCase(args[0])) {

                        // check perm
                        if (permissionRequirement != null && !sender.hasPermission(permissionRequirement.value()[0])) {
                            sender.sendMessage(LangUtils.getLangText_withPrefix("command.permissionDeny"));
                        }

                        // invoke subcmd handler
                        try {
                            method.invoke(this, sender, args);
                            return true;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            sender.sendMessage(LangUtils.getLangText_withPrefix("internalError") + "\n" + Arrays.toString(e.getStackTrace()));
                            e.printStackTrace();
                        }
                    }
                }
            }

            // subcmd not find
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidCommand"));
        }
        return true;
    }

    // sub command handler
    @SubCmd("start")
    @PermissionRequirement("aa.command.start")
    @SuppressWarnings("unused")
    void start(CommandSender sender, String @NotNull [] args) {
        if (AdvancedAnnouncement.announceTask == null) {
            AdvancedAnnouncement.announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, 600L);
            sender.sendMessage(LangUtils.getLangText_withPrefix("ann-task-start"));
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("ann-task-already-start"));
        }
    }

    @SubCmd("stop")
    @PermissionRequirement("aa.command.start")
    @SuppressWarnings("unused")
    void stop(CommandSender sender, String @NotNull [] args) {
        BukkitTask task = AdvancedAnnouncement.announceTask;
        if (task != null) {
            task.cancel();
            AdvancedAnnouncement.announceTask = null;
            sender.sendMessage(LangUtils.getLangText_withPrefix("ann-task-stop"));
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("ann-task-already-stop"));
        }
    }

    @SubCmd({"broadcast", "bc"})
    @PermissionRequirement("aa.command.broadcast")
    @SuppressWarnings("unused")
    void broadCast(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length != 2) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidArgs"));
            return;
        }

        String annName = args[1];
        Announcement ann = AnnouncementManager.forName(annName);
        if (ann == null) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-broadcast-not-found"));
            return;
        }

        ann.broadcast();

        sender.sendMessage(LangUtils.parseLang_withPrefix("command.command-broadcast-sent", ann.getName()));
    }

    @SubCmd("display")
    @PermissionRequirement("aa.command.display")
    @SuppressWarnings("unused")
    void display(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length != 2) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidArgs"));
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return;
        }

        String annName = args[1];
        Announcement ann = AnnouncementManager.forName(annName);
        if (ann == null) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-found"));
            return;
        }

        sender.sendMessage(LangUtils.parseLang_withPrefix("command.command-display-message", ann.getName()));
        ann.send(sender, ProtocolUtils.canHandleRGB((Player) sender));
    }

    @SubCmd("help")
    @PermissionRequirement("aa.command.help")
    @SuppressWarnings("unused")
    void help(@NotNull CommandSender sender, String @NotNull [] args) {
        LangUtils.getLangList("command.help").forEach(sender::sendMessage);
    }

    @SubCmd("list")
    @PermissionRequirement("aa.command.list")
    @SuppressWarnings("unused")
    void list(@NotNull CommandSender sender, String @NotNull [] args) {
        sender.sendMessage(LangUtils.prefix + ChatColor.translateAlternateColorCodes('&', "&3所有已加载的公告: "));
        for (Announcement announcement : AnnouncementManager.loadedAnnouncements) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7&l- &2Name: &a" + announcement.getName() + "&2, Delay: &a" + announcement.delay() + "s&2, Type: &a" + announcement.type().name()));
        }
    }

    @SubCmd("parse")
    @PermissionRequirement("aa.command.parse")
    @SuppressWarnings("unused")
    void parse(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length != 2) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidArgs"));
            return;
        }

        if (sender instanceof Player) {
            boolean isLegacy = ProtocolUtils.isPlayerLegacyVer((Player) sender);
            if (ProtocolUtils.isLegacyServer()) {
                isLegacy = true;
            }

            sender.sendMessage("handle color: " + TextHandler.handleColor(args[1], isLegacy));
            JsonArray jsonArray = new JsonArray();
            JsonObject object = new JsonObject();
            object.addProperty("text", "to component: ");
            jsonArray.add(object);
            jsonArray.addAll(TextHandler.constructToJsonArr(args[1], ProtocolUtils.canHandleRGB((Player) sender)));
            ProtocolUtils.sendChat((Player) sender, jsonArray.toString());
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
        }
    }

    @SubCmd("reload")
    @PermissionRequirement("aa.command.reload")
    @SuppressWarnings("unused")
    void reload(@NotNull CommandSender sender, String @NotNull [] args) {
        sender.sendMessage(LangUtils.getLangText("reload.start"));

        if (AdvancedAnnouncement.announceTask != null) {
            AdvancedAnnouncement.announceTask.cancel();
            AdvancedAnnouncement.announceTask = null;
            GlobalConsoleSender.info(LangUtils.getLangText("ann-task-stop"));
        }

        ConfigManager.checkAllFile();
        ConfigManager.loadAll();
        ConfigKeys.initKey(ConfigManager.getConfigFile("config"));

        // load translation
        Config config = ConfigManager.getConfig("config");
        String translationName = config.getConfiguration().getString("translation");
        Config translationCfg = new Config(
                "lang",
                AdvancedAnnouncement.INSTANCE.getResource("translations/" + translationName + ".yml"),
                new File(AdvancedAnnouncement.DATA_FOLDER, "translations/" + translationName + ".yml")
        );
        ConfigManager.checkFile(translationCfg);
        translationCfg.load();

        // refresh translation
        LangUtils.refreshLang();

        ComponentManager.blocks.clear();
        AnnouncementManager.loadedAnnouncements.clear();
        Bootstrap.loadComponentBlock();
        Bootstrap.loadAnnouncements();

        AdvancedAnnouncement.announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, 600L);
        GlobalConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        sender.sendMessage(LangUtils.getLangText("reload.done"));
    }
}

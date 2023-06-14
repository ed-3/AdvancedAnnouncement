package me.ed333.plugin.advancedannouncement.cmd;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.Bootstrap;
import me.ed333.plugin.advancedannouncement.announcement.Announcement;
import me.ed333.plugin.advancedannouncement.announcement.AnnouncementManager;
import me.ed333.plugin.advancedannouncement.components.ComponentManager;
import me.ed333.plugin.advancedannouncement.config.Config;
import me.ed333.plugin.advancedannouncement.config.ConfigKeys;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.runnables.AnnounceRunnable;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

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

        String annName = args[1];
        Announcement ann = AnnouncementManager.forName(annName);
        if (ann == null) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-found"));
            return;
        }
        boolean result = ann.send(sender);
        if (result) {
            sender.sendMessage(LangUtils.parseLang_withPrefix("command.command-display-message", ann.getName()));
        }
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
        if (sender instanceof Player) {
            sender.sendMessage("handle color: " + TextHandler.handleColor(args[1], sender));
            JsonArray jsonArray = new JsonArray();
            JsonObject object = new JsonObject();
            object.addProperty("text", "to component: ");
            jsonArray.add(object);
            jsonArray.addAll(TextHandler.constructToJsonArr(args[1], sender));
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

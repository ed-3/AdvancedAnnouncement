package me.ed333.plugin.advancedannouncement.cmd;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.Bootstrap;
import me.ed333.plugin.advancedannouncement.ConfigKeys;
import me.ed333.plugin.advancedannouncement.config.Config;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.config.PluginConfig;
import me.ed333.plugin.advancedannouncement.instances.announcement.Announcement;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementManager;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementType;
import me.ed333.plugin.advancedannouncement.runnables.AnnounceRunnable;
import me.ed333.plugin.advancedannouncement.runnables.PreAnnRunnable;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AA_CommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("autoannouncement")) {
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
                    if (subStr.equals(args[0])) {

                        if (!sender.hasPermission(permissionRequirement.value()[0])) {
                            sender.sendMessage(LangUtils.getLangText_withPrefix("command.permissionDeny"));
                            return false;
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

            // not find
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidCommand"));
        }
        return true;
    }

    @SubCmd("start")
    @PermissionRequirement("aa.command.start")
    @SuppressWarnings("unused")
    void start(CommandSender sender, String @NotNull [] args) {
        AnnounceRunnable runnable = AdvancedAnnouncement.getAnnounceRunnable();
        if (!runnable.isRunning()) {
            runnable.start();
        }
    }

    @SubCmd("stop")
    @PermissionRequirement("aa.command.start")
    @SuppressWarnings("unused")
    void stop(CommandSender sender, String @NotNull [] args) {
        AnnounceRunnable runnable = AdvancedAnnouncement.getAnnounceRunnable();
        if (runnable.isRunning()) {
            runnable.stop();
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

        if (sender instanceof ConsoleCommandSender
                && ann.type().equals(AnnouncementType.CHAT)
                && ConfigKeys.CONSOLE_BROAD_CAST
        ) {
            ann.send(sender);
        }

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
        sender.sendMessage("handle color: " + TextHandler.handleColor(args[1], sender));
        TextComponent textComponent = new TextComponent();
        textComponent.addExtra("handle component: ");
        textComponent.addExtra(TextHandler.toTextComponent(args[1], sender));
        sender.spigot().sendMessage(textComponent);
    }

    @SubCmd("reload")
    @PermissionRequirement("aa.command.reload")
    @SuppressWarnings("unused")
    void reload(@NotNull CommandSender sender, String @NotNull [] args) {
        sender.sendMessage(LangUtils.getLangText("reload.start"));
        PreAnnRunnable.preAnnRunnableList.forEach(PreAnnRunnable::cancel);
        if (AdvancedAnnouncement.getAnnounceRunnable() != null) {
            AdvancedAnnouncement.getAnnounceRunnable().cancel();
            GlobalConsoleSender.info(LangUtils.getLangText("ann-task-cancel"));
        }
        ConfigManager.checkAllFile();
        ConfigManager.loadAll();
        ConfigKeys.initKey(ConfigManager.getConfigFile("config"));

        // load translation
        Config config = ConfigManager.getConfig("config");
        String translationName = config.getConfiguration().getString("translation");
        Config translationCfg = new PluginConfig(
                "lang",
                AdvancedAnnouncement.INSTANCE.getResource("translations/" + translationName + ".yml"),
                new File(AdvancedAnnouncement.DATA_FOLDER, "translations/" + translationName + ".yml")
        );
        ConfigManager.checkFile(translationCfg);
        translationCfg.load();

        // refresh translation
        LangUtils.refreshLang();
        System.out.println(ConfigManager.getIdentifies());
        System.out.println(ConfigManager.getConfig("lang").getConfigFile());

        Bootstrap.loadComponentBlock();
        Bootstrap.loadAnnouncements();

        AdvancedAnnouncement.getAnnounceRunnable().start();
        GlobalConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        sender.sendMessage(LangUtils.getLangText("reload.done"));
    }

    private @NotNull String captureFirstChar(@NotNull String str){
        char[] cs=str.toCharArray();
        cs[0]-=32;
        if (str.equals("broadcast")) {
            cs[5]-=32;
        }
        return String.valueOf(cs);
    }

    private boolean hasPermissions(CommandSender sender, String... permissionStr) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        if (!(sender instanceof Player)) return false;

        for (String str : permissionStr) {
            if (!sender.hasPermission(str)) {
                return false;
            }
        }
        return true;
    }
}

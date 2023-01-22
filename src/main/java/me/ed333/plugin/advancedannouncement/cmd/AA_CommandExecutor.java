package me.ed333.plugin.advancedannouncement.cmd;

import me.ed333.plugin.advancedannouncement.cmd.sub.Help;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AA_CommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("autoannouncement")) {
            if (args.length == 0) {
                Help.callCmd(sender, args);
                return true;
            }
            Class<?> clazz;
            try {
                clazz = Class.forName("me.ed333.plugin.advancedannouncement.cmd.sub." + captureFirstChar(args[0].toLowerCase()));
                Method method = clazz.getDeclaredMethod("callCmd", CommandSender.class, String[].class);

                PlayerOnly ann1 = method.getAnnotation(PlayerOnly.class);
                if (ann1 != null && !(sender instanceof Player)) {
                    sender.sendMessage(LangUtils.getLangText("notPlayer"));
                    return false;
                }

                PermissionRequirement requirement = method.getDeclaredAnnotation(PermissionRequirement.class);
                if (requirement != null && !hasPermissions(sender, requirement.value())) {
                    sender.sendMessage(LangUtils.getLangText_withPrefix("command.permissionDeny"));
                    return false;
                }

                method.invoke(clazz.getConstructor(), sender, args);
            } catch (ClassNotFoundException e) {
                sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidCommand"));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                sender.sendMessage(LangUtils.getLangText_withPrefix("internalError") + "\n" + Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }
        return true;
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

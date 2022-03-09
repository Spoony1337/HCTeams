package rip.orbit.hcteams.util.object;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.util.CC;

public class Messager {

    public static void player(Player player, String msg){
        player.sendMessage(CC.translate(msg));
        return;
    }


    public static void console(String msg){
        Bukkit.getConsoleSender().sendMessage(CC.translate(msg));
        return;
    }

    public static void broadcast(String msg){
        Bukkit.broadcastMessage(CC.translate(msg));
        return;
    }

    public static void warn(String msg) {
        Bukkit.getLogger().warning(msg);
        return;
    }

    public static void commandSender(CommandSender sender, String string) {
        sender.sendMessage(CC.translate(string));
        return;
    }

    public static void notFound(Player player, String string) {
        Messager.player(player, "&cPlayer '&f" + string + "&c' not found!");

    }


    public static void noPerms(Player player) {
        player(player, "&cNo permission.");

    }

}

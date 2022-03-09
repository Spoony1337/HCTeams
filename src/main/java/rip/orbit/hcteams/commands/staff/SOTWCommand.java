package rip.orbit.hcteams.commands.staff;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SOTWCommand {

    @Getter private static Map<String, Long> customTimers = new HashMap<>();
    private static Set<UUID> sotwEnabled = Sets.newHashSet();

    @Command(names = {"sotw enable"}, permission = "")
    public static void sotwEnable(Player sender) {
        if (!isSOTWTimer()) {
            sender.sendMessage(ChatColor.RED + "You can't /sotw enable when there is no SOTW timer...");
            return;
        }

        if (sotwEnabled.add(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + "Successfully disabled your SOTW timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "Your SOTW timer was already disabled...");
        }
    }

    @Command(names = { "sotw cancel" }, permission = "foxtrot.sotw")
    public static void sotwCancel(CommandSender sender) {
        Long removed = customTimers.remove("&f&lSOTW ends in");
        if (removed != null && System.currentTimeMillis() < removed) {
            sender.sendMessage(ChatColor.GREEN + "Deactivated the SOTW timer.");
            return;
        }

        sender.sendMessage(ChatColor.RED + "SOTW timer is not active.");
    }

    @Command(names = "sotw spawn", permission = "")
    public static void spawn(Player sender) {

        if (!isSOTWTimer())
            return;
        if (hasSOTWEnabled(sender.getUniqueId()))
            return;

        sender.teleport(Bukkit.getWorld("world").getSpawnLocation());
        sender.sendMessage(ChatColor.GREEN + "Sent to spawn!");
    }

    @Command(names = "sotw start", permission = "foxtrot.sotw")
    public static void sotwStart(CommandSender sender, @Param(name = "time") String time) {
        int seconds = TimeUtils.parseTime(time);
        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        customTimers.put("&f&lSOTW ends in", System.currentTimeMillis() + (seconds * 1000));
        sender.sendMessage(ChatColor.GREEN + "Started the SOTW timer for " + time);
    }

    @Command(names = "sotw extend", permission = "foxtrot.sotw")
    public static void sotwExtend(CommandSender sender, @Param(name = "time") String time) {
        int seconds;
        try {
            seconds = TimeUtils.parseTime(time);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (!customTimers.containsKey("&f&lSOTW ends in")) {
            sender.sendMessage(ChatColor.RED + "There is currently no active SOTW timer.");
            return;
        }

        customTimers.put("&f&lSOTW ends in", customTimers.get("&f&lSOTW ends in") + (seconds * 1000));
        sender.sendMessage(ChatColor.GREEN + "Extended the SOTW timer by " + time);
    }

    public static boolean isSOTWTimer() {
        return customTimers.containsKey("&f&lSOTW ends in");
    }

    public static boolean hasSOTWEnabled(UUID uuid) {
        return sotwEnabled.contains(uuid);
    }
}
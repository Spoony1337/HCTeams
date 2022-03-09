package rip.orbit.hcteams.commands.lff;

import net.frozenorb.qlib.command.Command;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.lff.menu.LFFMenu;
import rip.orbit.hcteams.team.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LFFCommand {

    private static Map<UUID, Long> cooldownMap = new HashMap<>();

    @Command(names = {"lff"}, permission = "")
    public static void lff(Player player) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        if (team != null) {
            player.sendMessage(ChatColor.RED + "You cannot issue this command while you're in a faction");
            return;
        }
        if(isOnCooldown(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are currently on cooldown for another " + DurationFormatUtils.formatDurationWords(getCooldownLeft(player.getUniqueId()), true, true) + ".");
            return;
        }
        new LFFMenu().openMenu(player);
    }


    private static boolean isOnCooldown(UUID uuid) {
        return getCooldownLeft(uuid) > 0L;
    }

    private static long getCooldownLeft(UUID uuid) {
        if (!cooldownMap.containsKey(uuid))
            return 0L;

        long timeLeft = cooldownMap.getOrDefault(uuid, 0L) - System.currentTimeMillis();

        if (timeLeft <= 0L)
            clearCooldown(uuid);

        return timeLeft;
    }

    public static void applyCooldown(UUID uuid) {
        cooldownMap.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
    }

    private static void clearCooldown(UUID uuid) {
        cooldownMap.remove(uuid);
    }

}

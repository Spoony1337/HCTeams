package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.persist.maps.PlaytimeMap;

import java.util.UUID;

public class PlaytimeCommand {

    @Command(names={ "Playtime", "PTime" }, permission="")
    public static void playtime(Player sender, @Param(name="player", defaultValue="self") UUID player) {
        PlaytimeMap playtime = HCF.getInstance().getPlaytimeMap();
        int playtimeTime = (int) playtime.getPlaytime(player);
        Player bukkitPlayer = HCF.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
            playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + "'s total playtime is " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(playtimeTime) + ChatColor.YELLOW + ".");
    }

}
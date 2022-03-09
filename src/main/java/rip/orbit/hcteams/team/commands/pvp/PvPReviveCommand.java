package rip.orbit.hcteams.team.commands.pvp;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

import java.util.UUID;

public class PvPReviveCommand {

    @Command(names = {"pvptimer revive", "pvp revive", "pvptimer revive", "timer revive", "pvp revive" }, permission = "")
    public static void pvpRevive(Player sender, @Param(name = "player") UUID player) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        int lives = HCF.getInstance().getLivesMap().getLives(sender.getUniqueId());

        if (HCF.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (lives <= 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!HCF.getInstance().getDeathbanMap().isDeathbanned(player)) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        if (HCF.getInstance().getServerHandler().getBetrayer(player) != null) {
            sender.sendMessage(ChatColor.RED + "Betrayers may not be revived!");
            return;
        }

        // Use a friend life.
        HCF.getInstance().getLivesMap().setLives(sender.getUniqueId(), lives - 1);
        sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " with a life!");


        HCF.getInstance().getDeathbanMap().revive(player);
    }

}
package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.teamupgrades.menu.UpgradeMenu;

public class TeamShopCommand {

    @Command(names={ "team shop", "t shop", "f shop", "faction shop", "fac shop",
            "team upgrade", "t upgrade", "f upgrade", "faction upgrade", "fac upgrade",
            "team upgrades", "t upgrades", "f upgrades", "faction upgrades", "fac upgrades"
    }, permission="")
    public static void teamDisband(Player player) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(player);

        if (team == null){
            player.sendMessage(ChatColor.RED + "You need to be in a team to do this.");
            return;
        }

        new UpgradeMenu().openMenu(player);

    }

}
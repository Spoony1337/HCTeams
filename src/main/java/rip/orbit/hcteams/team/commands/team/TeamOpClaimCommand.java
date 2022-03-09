package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.VisualClaim;
import rip.orbit.hcteams.team.claims.VisualClaimType;

public class TeamOpClaimCommand {

    @Command(names={ "team opclaim", "t opclaim", "f opclaim", "faction opclaim", "fac opclaim" }, permission="worldedit.*")
    public static void teamOpClaim(Player sender) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }

        sender.getInventory().remove(TeamClaimCommand.SELECTION_WAND);

        new BukkitRunnable() {

            @Override
			public void run() {
                sender.getInventory().addItem(TeamClaimCommand.SELECTION_WAND.clone());
            }

        }.runTaskLater(HCF.getInstance(), 1L);

        new VisualClaim(sender, VisualClaimType.CREATE, true).draw(false);

        if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.MAP, true).draw(true);
        }
    }

}
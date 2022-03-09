package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.claims.VisualClaim;
import rip.orbit.hcteams.team.claims.VisualClaimType;

public class TeamMapCommand {

    @Command(names={ "team map", "t map", "f map", "faction map", "fac map", "map" }, permission="")
    public static void teamMap(Player sender) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        (new VisualClaim(sender, VisualClaimType.MAP, false)).draw(false);
    }

//    @Command(names={ "team map surface", "t map surface", "f map surface", "faction map surface", "fac map surface", "map surface" }, permission="")
//    public static void teamMapSurface(Player sender) {
//        (new VisualClaim(sender, VisualClaimType.SURFACE_MAP, false)).draw(false);
//    }

}
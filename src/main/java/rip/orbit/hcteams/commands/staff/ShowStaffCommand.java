package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.CC;

public class ShowStaffCommand {

    @Command(names = "togglestaff", permission = "foxtrot.showstaff")
    public static void onCommand(Player player) {
        if (player.hasMetadata("nostaff")) {
            player.removeMetadata("nostaff", HCF.getInstance());
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasMetadata("modmode")) {
                    p.hidePlayer(player);
                }
            }
        } else {
            player.setMetadata("nostaff", new FixedMetadataValue(HCF.getInstance(), true));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasMetadata("modmode")) {
                    p.showPlayer(player);
                }
            }
        }
        player.sendMessage(CC.translate(player.hasMetadata("nostaff") ? "&aYou can see staff" : "&cYou cannot see staff"));
    }
}

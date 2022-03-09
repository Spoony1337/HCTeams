package rip.orbit.hcteams.map.killstreaks.orbittypes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.map.killstreaks.Killstreak;

public class PartnerPackages extends Killstreak {


    @Override
	public String getName() {
        return "3 Partner Packages";
    }


    @Override
	public int[] getKills() {
        return new int[] {
                25
        };
    }


    @Override
	public void apply(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "package give " + player.getName() + "3");
    }

}

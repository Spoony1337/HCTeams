package rip.orbit.hcteams.server.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/07/2021 / 12:33 AM
 * HCTeams / rip.orbit.hcteams.server.object
 */

@AllArgsConstructor
@Data
public class SellItem {

	private double price;
	private Material material;

}

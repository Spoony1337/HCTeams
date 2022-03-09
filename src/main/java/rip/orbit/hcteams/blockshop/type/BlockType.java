package rip.orbit.hcteams.blockshop.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/08/2021 / 10:31 PM
 * HCTeams / rip.orbit.hcteams.blockshop.type
 */

@AllArgsConstructor
@Getter
public enum BlockType {

	CLAY(Material.STAINED_CLAY, ShopType.CLAY, 500),
	GLASS(Material.STAINED_GLASS, ShopType.GLASS, 500),
	WOOL(Material.WOOL, ShopType.WOOL, 500);

	private final Material material;
	private final ShopType type;
	private final int price;

}

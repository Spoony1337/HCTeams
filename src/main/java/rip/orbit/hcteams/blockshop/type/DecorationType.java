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
public enum DecorationType {

	NETHER_BRICK_BLOCK(Material.NETHER_BRICK_ITEM, 0, 1000),
	NETHER_BRICK_STAIRS(Material.NETHER_BRICK_STAIRS, 0, 1000),
	WOOD(Material.WOOD, 0, 1000),
	WOOD1(Material.WOOD, 1, 1000),
	WOOD2(Material.WOOD, 2, 1000),
	WOOD3(Material.WOOD, 3, 1000),
	LEAVES(Material.LEAVES, 0, 1000),
	LEAVES1(Material.LEAVES, 1, 1000),
	LEAVES2(Material.LEAVES, 2, 1000),
	QUARTZ_BLOCK(Material.QUARTZ_BLOCK, 0, 1000),
	QUARTZ_STAIRS(Material.QUARTZ_STAIRS, 0, 1000);

	private final Material material;
	private final int data;
	private final int price;

}

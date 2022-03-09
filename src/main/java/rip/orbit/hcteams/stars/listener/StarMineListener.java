package rip.orbit.hcteams.stars.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.gravity.util.ItemBuilder;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.customtimer.CustomTimer;
import rip.orbit.hcteams.persist.maps.StarsMap;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 6:29 AM
 * HCTeams / rip.orbit.hcteams.stars.listener
 */
public class StarMineListener implements Listener {

	private final ItemStack enchantCrystal = new ItemBuilder(Material.NETHER_STAR)
			.name(CC.translate("&6&lEnchantment Crystal"))
			.lore(CC.translate(Arrays.asList(
					"",
					"&fClick to receive a &6random enchantment&f gem that you can",
					"&6drag&f and &6drop&f on a piece of armor to add the custom",
					"&fenchantment on the rune to that piece of &6gear&f.",
					""
			))).build();


	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		beginStarMine(event);
	}

	public void beginStarMine(BlockBreakEvent event) {
		StarsMap starsMap = HCF.getInstance().getStarsMaps();
		double random = ThreadLocalRandom.current().nextDouble(100 + 1);
		double random2 = ThreadLocalRandom.current().nextDouble(100 + 1);
		int rand = new Random().nextInt(5) + 1;
		if (random <= 1.65) {

			event.getPlayer().sendMessage(CC.translate("&fYou have just found &6" + (rand) + "✧ &fStars whilst mining."));

			if (CustomTimer.byName("x2-Stars") != null) {
				starsMap.add(event.getPlayer().getUniqueId(), rand * 2);
				return;
			}
			starsMap.add(event.getPlayer().getUniqueId(), rand);
		}

		if (random2 <= 0.45) {
			ItemUtils.tryFit(event.getPlayer(), this.enchantCrystal);
			event.getPlayer().sendMessage(CC.chat("&eYou just found an enchantment crystal from mining!"));
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		UUID UID = event.getEntity().getUniqueId();
		int toTake = 5;
		StarsMap starsMap = HCF.getInstance().getStarsMaps();

		if (!(starsMap.get(UID) <= 0)) {
			starsMap.subtract(UID, toTake);
		}

		if (event.getEntity().getKiller() != null) {
			UUID kUID = event.getEntity().getKiller().getUniqueId();
			int toAdd = 10;
			if (CustomTimer.byName("x2-Stars") != null) {
				starsMap.add(kUID, toAdd * 2);
				return;
			}
			starsMap.add(kUID, toAdd);
			Team team = HCF.getInstance().getTeamHandler().getTeam(kUID);
			team.setMeteors(team.getMeteors() + 5);
		}
	}

}

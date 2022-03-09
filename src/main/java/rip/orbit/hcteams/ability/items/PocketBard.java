package rip.orbit.hcteams.ability.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;
import rip.orbit.hcteams.util.item.ItemUtils;
import rip.orbit.hcteams.util.menu.Button;
import rip.orbit.hcteams.util.menu.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/07/2021 / 5:57 PM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class PocketBard extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				" ",
				"&7Right click to reveal a menu to choose",
				"&7a portable bard effect of your choice.",
				" "
		));
	}

	@Override
	public List<String> foundInfo() {
		return CC.translate(Arrays.asList(
				"Ability Packages",
				"Partner Crates",
				"Star Shop (/starshop)"
		));
	}

	@Override
	public String displayName() {
		return CC.chat("&6&lPocketBard");
	}

	@Override
	public String name() {
		return "pocketbard";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.DOUBLE_PLANT;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@EventHandler
	public void onInteractNinjaStar(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (isSimilar(event.getItem())) {
			if (!isClick(event, "RIGHT")) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			if (!canUse(player)) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}

			event.setCancelled(true);
			takeItem(player);

			new Menu() {

				@Override
				public String getTitle(Player player) {
					return "PocketBard Selector";
				}

				@Override
				public int size() {
					return 9;
				}

				@Override
				public Map<Integer, Button> getButtons(Player player) {
					Map<Integer, Button> buttons = new HashMap<>();

					int i = 0;
					for (Ability pb : HCF.getInstance().getAbilityHandler().getPocketbards()) {
						buttons.put(i, new Button() {
							@Override
							public ItemStack getItem(Player player) {
								ItemStack clone = pb.getStack().clone();
								clone.setAmount(3);
								return clone;
							}

							@Override
							public void clicked(Player player, ClickType clickType) {
								ItemUtils.tryFit(player, getItem(player));
								player.closeInventory();
							}
						});
						++i;
					}

					return buttons;
				}
			}.openMenu(player);

		}
	}

}

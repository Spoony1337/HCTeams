package rip.orbit.hcteams.blockshop.menu.category;

import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.menu.buttons.BackButton;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.blockshop.menu.BlockShopMenu;
import rip.orbit.hcteams.blockshop.type.DecorationType;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/08/2021 / 10:36 PM
 * HCTeams / rip.orbit.hcteams.blockshop.menu.category
 */

public class DecorationMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&6Decorations");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (DecorationType type : DecorationType.values()) {
			buttons.put(i, new Button() {
				@Override
				public String getName(Player player) {
					return CC.translate(CC.GOLD + WordUtils.capitalize(type.getMaterial().name().toLowerCase().replace("_", " ")));
				}

				@Override
				public int getAmount(Player player) {
					return 16;
				}

				@Override
				public List<String> getDescription(Player player) {
					return Collections.singletonList(CC.translate("&7┃ &fPrice&7: &6$" + type.getPrice()));
				}

				@Override
				public byte getDamageValue(Player player) {
					return (byte) type.getData();
				}

				@Override
				public void clicked(Player player, int slot, ClickType clickType) {
					double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());
					if (balance < type.getPrice()) {
						player.sendMessage(CC.translate("&cInsufficient Funds."));
						return;
					}
					FrozenEconomyHandler.withdraw(player.getUniqueId(), type.getPrice());
					ItemUtils.tryFit(player, new ItemStack(type.getMaterial(), 16, getDamageValue(player)));
				}

				@Override
				public Material getMaterial(Player player) {
					return type.getMaterial();
				}
			});
			++i;
		}

		buttons.put(26, new BackButton(new BlockShopMenu()));

		return buttons;
	}

}

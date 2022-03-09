package rip.orbit.hcteams.blockshop.menu.category;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.menu.buttons.BackButton;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.blockshop.menu.BlockShopMenu;
import rip.orbit.hcteams.blockshop.type.BlockType;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;
import rip.orbit.hcteams.util.object.ItemBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/08/2021 / 10:36 PM
 * HCTeams / rip.orbit.hcteams.blockshop.menu.category
 */

@AllArgsConstructor
public class CategoryMenu extends Menu {

	private final BlockType blockType;
	private final String title;

	@Override
	public String getTitle(Player player) {
		return CC.translate(this.title);
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int i = 0; i < 15; i++) {
			ItemBuilder builder = new ItemBuilder(blockType.getMaterial());
			builder.data(i);
			buttons.put(i, new CategoryButton(this.blockType, i));
		}

		buttons.put(26, new BackButton(new BlockShopMenu()));

		return buttons;
	}

	@AllArgsConstructor
	public static class CategoryButton extends Button {

		private final BlockType blockType;
		private final int damage;

		@Override
		public String getName(Player player) {
			return CC.translate(colorByData(this.damage) + WordUtils.capitalize(this.blockType.getMaterial().name().toLowerCase().replace("_", " ")));
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.singletonList(CC.translate("&7┃ &fPrice&7: &6$" + blockType.getPrice()));
		}

		@Override
		public int getAmount(Player player) {
			return 16;
		}

		@Override
		public byte getDamageValue(Player player) {
			return (byte) this.damage;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());
			if (balance < blockType.getPrice()) {
				player.sendMessage(CC.translate("&cInsufficient Funds."));
				return;
			}
			FrozenEconomyHandler.withdraw(player.getUniqueId(), blockType.getPrice());
			ItemUtils.tryFit(player, new ItemStack(this.blockType.getMaterial(), 16, getDamageValue(player)));
		}

		@Override
		public Material getMaterial(Player player) {
			return blockType.getMaterial();
		}
	}

	public static ChatColor colorByData(int data) {
		if (data == 0)
			return ChatColor.WHITE;
		else if (data == 1)
			return ChatColor.GOLD;
		else if (data == 2)
			return ChatColor.LIGHT_PURPLE;
		else if (data == 3)
			return ChatColor.AQUA;
		else if (data == 4)
			return ChatColor.YELLOW;
		else if (data == 5)
			return ChatColor.GREEN;
		else if (data == 6)
			return ChatColor.LIGHT_PURPLE;
		else if (data == 7)
			return ChatColor.DARK_GRAY;
		else if (data == 8)
			return ChatColor.GRAY;
		else if (data == 9)
			return ChatColor.DARK_AQUA;
		else if (data == 10)
			return ChatColor.DARK_PURPLE;
		else if (data == 11)
			return ChatColor.DARK_BLUE;
		else if (data == 12)
			return ChatColor.YELLOW;
		else if (data == 13)
			return ChatColor.DARK_GREEN;
		else if (data == 14)
			return ChatColor.RED;
		else if (data == 15)
			return ChatColor.BLACK;

		return ChatColor.GOLD;
	}

}

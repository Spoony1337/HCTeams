package rip.orbit.hcteams.blockshop.menu;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.blockshop.menu.category.CategoryMenu;
import rip.orbit.hcteams.blockshop.menu.category.DecorationMenu;
import rip.orbit.hcteams.blockshop.type.BlockType;
import rip.orbit.hcteams.util.CC;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/08/2021 / 10:33 PM
 * HCTeams / rip.orbit.hcteams.blockshop.menu
 */
public class BlockShopMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&6Block Shop");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(12, new GlassCategoryButton());
		buttons.put(13, new WoolCategoryButton());
		buttons.put(14, new ClayCategoryButton());
		buttons.put(22, new DecorationCategoryButton());

		for (int i = 0; i < 9; i++) {
			try {
				buttons.put((27 + i), Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
			} catch (Exception ignored) {

			}
		}

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	public static class GlassCategoryButton extends Button {

		@Override
		public String getName(Player player) {
			return CC.translate("&6Stained Glass Shop");
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.singletonList(CC.translate("&7Click to view the glass shop blocks."));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.STAINED_GLASS;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			new CategoryMenu(BlockType.GLASS, CC.translate("&6Glass Shop")).openMenu(player);
		}
	}

	public static class DecorationCategoryButton extends Button {

		@Override
		public String getName(Player player) {
			return CC.translate("&6Decoration Shop");
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.singletonList(CC.translate("&7Click to view the decoratory blocks."));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.LEAVES;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			new DecorationMenu().openMenu(player);
		}
	}

	public static class WoolCategoryButton extends Button {

		@Override
		public String getName(Player player) {
			return CC.translate("&6Dyed Wool Shop");
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.singletonList(CC.translate("&7Click to view the wool shop blocks."));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.WOOL;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			new CategoryMenu(BlockType.WOOL, CC.translate("&6Wool Shop")).openMenu(player);
		}
	}

	public static class ClayCategoryButton extends Button {

		@Override
		public String getName(Player player) {
			return CC.translate("&6Stained Clay Shop");
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.singletonList(CC.translate("&7Click to view the clay shop blocks."));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.HARD_CLAY;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			new CategoryMenu(BlockType.CLAY, CC.translate("&6Clay Shop")).openMenu(player);
		}
	}

}

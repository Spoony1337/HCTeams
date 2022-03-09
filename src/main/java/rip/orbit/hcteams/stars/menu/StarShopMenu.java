package rip.orbit.hcteams.stars.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.stars.menu.type.StarShopItem;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;
import rip.orbit.hcteams.util.object.ItemBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 3:17 PM
 * HCTeams / rip.orbit.hcteams.stars.menu
 */
public class StarShopMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&6StarShop (Stars: " + HCF.getInstance().getStarsMaps().get(player.getUniqueId()) + ")");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 9;
		for (StarShopItem value : StarShopItem.values()) {
			buttons.put(i, new ItemButton(value));
			++i;
		}

		buttons.put(51, new ArmorUpgradeButton(Material.IRON_BLOCK,Enchantment.DURABILITY, 3, 70, "Unbreaking 3"));
		buttons.put(52, new FeatherArmorUpgradeButton(Material.CHAINMAIL_BOOTS,85, "Feather Falling IV"));
		buttons.put(53, new ArmorUpgradeButton(Material.CHAINMAIL_HELMET,Enchantment.PROTECTION_ENVIRONMENTAL, 2, 100, "Protection II"));

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@AllArgsConstructor
	public static class ArmorUpgradeButton extends Button {

		private Material material;
		private Enchantment enchantment;
		private int level;
		private int price;
		private String display;

		@Override
		public String getName(Player player) {
			return CC.translate("&6&lUpgrade Armor &7(" + display + ")");
		}

		@Override
		public List<String> getDescription(Player player) {
			return CC.translate(Arrays.asList(
					"&7┃ &fClick to upgrade the item in your hand",
					"&7┃ &fto &6" + display,
					" ",
					"&7┃ &fPrice&7: &6" + price + "✧"
			));
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			int stars = HCF.getInstance().getStarsMaps().get(player.getUniqueId());
			if (stars < price) {
				player.sendMessage(CC.translate("&cInsufficient Funds."));
				return;
			}
			if (upgrade(player, enchantment, level)) {
				HCF.getInstance().getStarsMaps().subtract(player.getUniqueId(), price);
				player.sendMessage(CC.translate("&fYou have just purchased " + getName(player) + " &ffor &6" + price + "✧"));
			}
		}

		@Override
		public Material getMaterial(Player player) {
			return this.material;
		}
	}

	@AllArgsConstructor
	public static class FeatherArmorUpgradeButton extends Button {

		private Material material;
		private int price;
		private String display;

		@Override
		public String getName(Player player) {
			return CC.translate("&6&lUpgrade Armor &7(" + display + ")");
		}

		@Override
		public List<String> getDescription(Player player) {
			return CC.translate(Arrays.asList(
					"&7┃ &fClick to upgrade the item in your hand",
					"&7┃ &fto &6" + display,
					" ",
					"&7┃ &fYou need to have protection 2 on your",
					"&7┃ &fboots for this to work.",
					"&7┃ &fPrice&7: &6" + price + "✧"
			));
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			int stars = HCF.getInstance().getStarsMaps().get(player.getUniqueId());
			if (stars < price) {
				player.sendMessage(CC.translate("&cInsufficient Funds."));
				return;
			}
			if (player.getItemInHand() != null && player.getItemInHand().getType().name().contains("BOOTS")) {
				if (player.getItemInHand().containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL) && player.getItemInHand().getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) >= 2) {
					player.getItemInHand().addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);
					player.updateInventory();
					HCF.getInstance().getStarsMaps().subtract(player.getUniqueId(), price);
					player.sendMessage(CC.translate("&fYou have just purchased " + getName(player) + " &ffor &6" + price + "✧"));
				} else {
					player.sendMessage(CC.translate("&cYour boots must have protection 2 on them to get this upgrade."));
				}
			}
		}

		@Override
		public Material getMaterial(Player player) {
			return this.material;
		}
	}

	@AllArgsConstructor
	public static class ItemButton extends Button {

		private final StarShopItem item;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(item.getMaterial())
					.amount(item.getAmount())
					.name(CC.translate(item.getDisplayName()))
					.data(item.getData())
					.lore(CC.translate(Arrays.asList(
							"&7┃ &fPrice&7: &6" + item.getPrice() + "✧",
							"&7┃ &fClick to purchase this item"
					)))
					.build();
		}

		@Override
		public String getName(Player player) {
			return null;
		}

		@Override
		public List<String> getDescription(Player player) {
			return null;
		}

		@Override
		public Material getMaterial(Player player) {
			return null;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			int stars = HCF.getInstance().getStarsMaps().get(player.getUniqueId());
			if (stars < item.getPrice()) {
				player.sendMessage(CC.translate("&cInsufficient Funds."));
				return;
			}
			HCF.getInstance().getStarsMaps().subtract(player.getUniqueId(), item.getPrice());
			if (item.isUseCommand()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), item.getCommand().replaceAll("%player%", player.getName()));
			} else {
				ItemUtils.tryFit(player, new ItemBuilder(item.getMaterial()).name(CC.translate(item.getDisplayName())).build());
			}
			player.sendMessage(CC.translate("&fYou have just purchased " + item.getDisplayName() + " &ffor &6" + item.getPrice() + "✧"));
		}
	}

	public static boolean upgrade(Player player, Enchantment enchantment, int toLevel) {
		if (player.getItemInHand() != null) {
			if (!isArmor(player.getItemInHand().getType())) {
				player.sendMessage(CC.translate("&cYou are not holding any armor."));
				return false;
			}
			int level = player.getItemInHand().getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
			if (level < 1 && enchantment == Enchantment.PROTECTION_ENVIRONMENTAL) {
				player.sendMessage(CC.translate("&cYou do not have a protection 1 piece of gear in your hand."));
				return false;
			}
			if (level <= 1 && enchantment != Enchantment.PROTECTION_ENVIRONMENTAL) {
				player.sendMessage(CC.translate("&cYou need a protection 2 or higher piece of gear to do this."));
				return false;
			}
			player.getItemInHand().removeEnchantment(enchantment);
			player.getItemInHand().addUnsafeEnchantment(enchantment, toLevel);
			return true;
		}
		return false;
	}

	public boolean isHelmet(Material material) {
		return material.name().contains("HELMET");
	}
	public boolean isChestplate(Material material) {
		return material.name().contains("CHESTPLATE");
	}
	public boolean isLeggings(Material material) {
		return material.name().contains("LEGGINGS");
	}
	public boolean isBoots(Material material) {
		return material.name().contains("BOOTS");
	}
	public static boolean isArmor(Material material) {
		return material.name().contains("BOOTS")
				|| material.name().contains("SWORD")
				|| material.name().contains("CHESTPLATE")
				|| material.name().contains("HELMET")
				|| material.name().contains("LEGGINGS");
	}

}

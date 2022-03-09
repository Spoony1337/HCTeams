package rip.orbit.hcteams.server.spawnershop;

import me.lbuddyboy.crates.util.CC;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.gravity.util.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 11/06/2021 / 10:51 PM
 * ihcf-xenlan / me.lbuddyboy.hcf.extras.spawnershop
 */
public class SpawnerShopMenu extends Menu {
	@Override
	public String getTitle(Player player) {
		return CC.chat("&6Spawner Shop");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(3, new Button() {
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
			public ItemStack getButtonItem(Player player) {
				ItemStack itemStack = new ItemBuilder(Material.MOB_SPAWNER).durability(EntityType.SKELETON.getTypeId()).name(ChatColor.GREEN + "Skeleton Spawner").lore(CC.chat("&7Cost: &a$20,000")).build();
				return itemStack;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());
				if (balance < 20000) {
					player.sendMessage(CC.chat("&cInsufficient funds."));
					return;
				}
				FrozenEconomyHandler.withdraw(player.getUniqueId(), 20000);
				player.getInventory().addItem(new ItemBuilder(Material.MOB_SPAWNER).name(CC.translate("&r&aSkeleton Spawner")).build());
			}
		});


		buttons.put(4, new Button() {
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
			public ItemStack getButtonItem(Player player) {
				ItemStack itemStack = new ItemBuilder(Material.MOB_SPAWNER).durability(EntityType.CAVE_SPIDER.getTypeId()).name(ChatColor.GREEN + "Cave Spider Spawner").lore(CC.chat("&7Cost: &a$15,000")).build();
				return itemStack;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());
				if (balance < 15000) {
					player.sendMessage(CC.chat("&cInsufficient funds."));
					return;
				}
				FrozenEconomyHandler.withdraw(player.getUniqueId(), 15000);
				player.getInventory().addItem(new ItemBuilder(Material.MOB_SPAWNER).name(CC.translate("&r&aCave_Spider Spawner")).build());
			}
		});
		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				ItemStack itemStack = new ItemBuilder(Material.MOB_SPAWNER).durability(EntityType.ZOMBIE.getTypeId()).name(ChatColor.GREEN + "Zombie Spawner").lore(CC.chat("&7Cost: &a$12,500")).build();
				return itemStack;
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
				double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());
				if (balance < 12500) {
					player.sendMessage(CC.chat("&cInsufficient funds."));
					return;
				}
				FrozenEconomyHandler.withdraw(player.getUniqueId(), 12500);
				player.getInventory().addItem(new ItemBuilder(Material.MOB_SPAWNER).name(CC.translate("&r&aZombie Spawner")).build());
			}
		});
		return buttons;
	}



}

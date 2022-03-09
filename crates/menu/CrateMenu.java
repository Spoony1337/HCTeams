package rip.orbit.hcteams.crates.menu;

import lombok.SneakyThrows;
import me.lbuddyboy.crates.api.CrateAPI;
import me.lbuddyboy.crates.menu.RewardsMenu;
import me.lbuddyboy.crates.object.Crate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.crates.CrateHandler;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.menu.Button;
import rip.orbit.hcteams.util.menu.Menu;
import rip.orbit.hcteams.util.object.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 24/04/2021 / 10:30 PM
 * SparkHCTeams / rip.orbit.hcteams.extras.crates.menu
 */
public class CrateMenu extends Menu {

	public CrateHandler handler = HCF.getInstance().getCrateHandler();
	public FileConfiguration conf = handler.getCrateYML().getConfiguration();

	@Override
	public String getTitle(Player player) {
		return CC.chat(conf.getString("gui-title"));
	}

	@Override
	public int size() {
		return conf.getInt("gui-size");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (String sec : conf.getConfigurationSection("crates").getKeys(false)) {
			String main = "crates.";

			buttons.put(conf.getInt(main + sec + ".slot") - 1, new Button() {
				@Override
				public ItemStack getItem(Player player) {
					Material material = Material.valueOf(conf.getString(main + sec + ".item"));
					List<String> lore = conf.getStringList(main + sec + ".lore");
					String name = conf.getString(main + sec + ".name");
					int data = 3;
					ItemStack stack = new ItemBuilder(material).name(CC.chat(name)).data((short) data).lore(CC.translate(lore)).build();

					if (conf.getBoolean(main + sec + ".skull.use-skull")) {
						String owner = conf.getString(main + sec + ".skull.owner");
						SkullMeta meta = (SkullMeta) stack.getItemMeta();
						meta.setOwner(owner);
						stack.setItemMeta(meta);
					}
					return stack;
				}
				@SneakyThrows
				@Override
				public void clicked(Player player, ClickType clickType) {
					Crate crate = CrateAPI.byName(sec);
					if (clickType.isLeftClick()) {
						if (player.getItemInHand() != null && player.getItemInHand().isSimilar(handler.getKey())) {
							if (player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}

							player.updateInventory();
							player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5F, 0.5F);
							if (!conf.contains(main + sec + ".times-used")) {
								conf.createSection(main + sec + ".times-used");
								conf.set(main + sec + ".times-used", 1);
							} else {
								conf.set(main + sec + ".times-used", conf.getInt(main + sec + ".times-used") + 1);
							}
							HCF.getInstance().getCrateHandler().getCrateYML().save();
							crate.open(player);

						} else {
							player.sendMessage(CC.chat("&cYou do not have a partner key in your hand."));
						}
					} else if(clickType.isRightClick()) {
						new RewardsMenu(crate.getName()).openMenu(player);
					}
				}
			});

		}

		return buttons;
	}
}

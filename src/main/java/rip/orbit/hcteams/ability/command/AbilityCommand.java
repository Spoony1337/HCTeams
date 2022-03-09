package rip.orbit.hcteams.ability.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;
import rip.orbit.hcteams.util.menu.Button;
import rip.orbit.hcteams.util.menu.Menu;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/07/2021 / 11:35 AM
 * HCTeams / rip.orbit.hcteams.ability.command
 */
public class AbilityCommand {



	@Command(names = "ability totalgiven", permission = "foxtrot.admin")
	public static void totalGiven(CommandSender sender) {

		int total = 0;

		for (UUID uuid : HCF.getInstance().getAbilityHandler().getAbilityGivenMap().getPlayersGiven()) {
			int amount  = HCF.getInstance().getAbilityHandler().getAbilityGivenMap().get(uuid);

			total += amount;
		}

		sender.sendMessage(CC.translate("&fTotal Ability Items Given This Map&7: &6" + total));
	}

	@Command(names = "ability give", permission = "foxtrot.ability")
	public static void give(CommandSender sender, @Param(name = "player") Player target, @Param(name = "ability") Ability ability, @Param(name = "amount") int amount) {
		ItemStack item = ability.getStack().clone();
		item.setAmount(amount);

		ItemUtils.tryFit(target, item);
		HCF.getInstance().getAbilityHandler().getAbilityGivenMap().add(target.getUniqueId(), amount);
	}

	@Command(names = "ability list", permission = "foxtrot.ability")
	public static void list(Player sender) {
		List<String> names = new ArrayList<>();
		HCF.getInstance().getAbilityHandler().getAbilities().forEach(ability -> names.add(ability.name()));
		sender.sendMessage(CC.chat("&6&lAbility List&f: " + StringUtils.join(names, ", ")));
	}

	@Command(names = {"ability preview"}, permission = "")
	public static void showcase(Player sender) {

		Menu menu = new Menu() {
			@Override
			public boolean usePlaceholder() {
				return true;
			}

			@Override
			public String getTitle(Player player) {
				return "Ability Items";
			}

			@Override
			public int size() {
				return 27;
			}

			@Override
			public Map<Integer, Button> getButtons(Player player) {
				Map<Integer, Button> buttons = new HashMap<>();
				int i = 0;
				for (Ability currentAbility : HCF.getInstance().getAbilityHandler().getAbilities()) {
					buttons.put(i, new Button() {
						@Override
						public ItemStack getItem(Player player) {
							ItemStack stack = currentAbility.getStack().clone();
							ItemMeta meta = stack.getItemMeta();

							List<String> toLore = meta.getLore();
							toLore.add(CC.chat("&7&lFound In:"));
							for (String s : currentAbility.foundInfo()) {
								toLore.add(CC.chat("&7┃ &7" + s));
							}
							meta.setLore(toLore);
							stack.setItemMeta(meta);
							stack.setAmount(1);

							return stack;
						}

						@Override
						public void clicked(Player player, ClickType clickType) {
							if (player.isOp()) {
								player.getInventory().addItem(currentAbility.getStack());
							}
						}
					});
					++i;
				}

				return buttons;
			}
		};
		menu.openMenu(sender);

	}

}

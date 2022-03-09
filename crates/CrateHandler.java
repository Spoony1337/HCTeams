package rip.orbit.hcteams.crates;

import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.crates.file.CrateConfigFile;
import rip.orbit.hcteams.crates.menu.CrateMenu;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;
import rip.orbit.hcteams.util.object.ItemBuilder;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 24/04/2021 / 10:27 PM
 * SparkHCTeams / rip.orbit.hcteams.extras.crates
 */
public class CrateHandler {

	@Getter private CrateConfigFile crateYML;
	@Getter private ItemStack key;

	public CrateHandler() {

		crateYML = new CrateConfigFile(HCF.getInstance(), "crateconfig", HCF.getInstance().getDataFolder().getAbsolutePath());

		FileConfiguration con = getCrateYML().getConfiguration();

		key = new ItemBuilder(Material.valueOf(con.getString("key.item")))
				.name(CC.chat(con.getString("key.name")))
				.lore(CC.translate(con.getStringList("key.lore")))
				.data((short)con.getInt("key.data"))
				.build();

	}

	@Command(names = "opencratemenu", permission = "")
	public static void open(Player sender) {
		if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
			return;
		}
		new CrateMenu().openMenu(sender);
	}

//	@Command(names = "partnercrates reload", permission = "op")
//	public static void reload(CommandSender sender) throws IOException {
////		HCF.getInstance().getCrateHandler().getCrateYML().sa;
//	}

	@Command(names = "partnercrates giveall", permission = "foxtrot.crates")
	public static void give(CommandSender sender, @Param(name = "amount") int amount) {
		ItemStack key = HCF.getInstance().getCrateHandler().getKey();
		key.setAmount(amount);
		for (Player on : Bukkit.getOnlinePlayers()) {
			ItemUtils.tryFit(on, key);
		}
	}


	@Command(names = "partnercrates give", permission = "foxtrot.crates")
	public static void give(CommandSender sender, @Param(name = "player") Player target, @Param(name = "amount") int amount) {
		ItemStack key = HCF.getInstance().getCrateHandler().getKey();
		key.setAmount(amount);
		ItemUtils.tryFit(target, key);
	}

}

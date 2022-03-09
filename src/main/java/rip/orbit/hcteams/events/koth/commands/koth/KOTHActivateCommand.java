package rip.orbit.hcteams.events.koth.commands.koth;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.koth.KOTH;
import rip.orbit.hcteams.util.CC;

public class KOTHActivateCommand {

	@Command(names = {"KOTH Activate", "KOTH Active", "events activate"}, permission = "foxtrot.activatekoth")
	public static void kothActivate(CommandSender sender, @Param(name = "event") Event koth) {
		if (sender instanceof Player || !(sender instanceof Player)) {
			// Don't start a KOTH if another one is active.
			for (Event otherKoth : HCF.getInstance().getEventHandler().getEvents()) {
				if (otherKoth.isActive()) {
					sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
					return;
				}
			}

			if ((koth.getName().equalsIgnoreCase("citadel") || koth.getName().toLowerCase().contains("conquest")) && !sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
				return;
			}

			koth.activate();
			new BukkitRunnable() {

				@Override
				public void run() {
					FancyMessage points = new FancyMessage(CC.translate("&7(Click Here to Teleport"));

					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						if (player.hasPermission("foxtrot.command.staff")) {
							points.command("/tppos " + ((KOTH) koth).getCapLocation().getBlockX() + " " + ((KOTH) koth).getCapLocation().getBlockY() + " " + ((KOTH) koth).getCapLocation().getBlockZ()).tooltip("§bClick to teleport");
							points.send(player);
						}
					}
				}

			}.runTaskLater(HCF.getInstance(), 5);
			sender.sendMessage(ChatColor.GRAY + "Activated " + koth.getName() + "");
		}
	}
}

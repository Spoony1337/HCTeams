package rip.orbit.hcteams.customtimer.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.customtimer.CustomTimer;
import rip.orbit.hcteams.customtimer.menu.CustomTimerMenu;
import rip.orbit.hcteams.polls.command.PollCommand;
import rip.orbit.hcteams.scoreboard.FoxtrotScoreGetter;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.JavaUtils;
import rip.orbit.hcteams.util.TimeUtil;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/07/2021 / 11:57 PM
 * HCTeams / rip.orbit.hcteams.customtimer.command
 */
public class CustomTimerCommand {

	@Command(names = "customtimer create", permission = "foxtrot.customtimer")
	public static void create(CommandSender sender, @Param(name = "name") String name, @Param(name = "time") String time, @Param(name = "command", wildcard = true) String command) {
		long now = System.currentTimeMillis();
		long longTime = JavaUtils.parse(time) + System.currentTimeMillis();
		CustomTimer customTimer = new CustomTimer(name.replace("-", " "), command, longTime);
		CustomTimer.customTimers.add(customTimer);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!CustomTimer.customTimers.contains(customTimer)) {
					cancel();
					return;
				}
				if (customTimer.getTime() - System.currentTimeMillis() == TimeUtil.parse("1h")) {
					Bukkit.broadcastMessage(CC.translate("&fThe &6" + customTimer.getName() + " Timer &fwill be commencing in &6&n30 minutes&f."));
				}
				if (customTimer.getTime() - System.currentTimeMillis() == TimeUtil.parse("45m")) {
					Bukkit.broadcastMessage(CC.translate("&fThe &6" + customTimer.getName() + " Timer &fwill be commencing in &6&n30 minutes&f."));
				}
				if (customTimer.getTime() - System.currentTimeMillis() == TimeUtil.parse("30m")) {
					Bukkit.broadcastMessage(CC.translate("&fThe &6" + customTimer.getName() + " Timer &fwill be commencing in &6&n30 minutes&f."));
				}
				if (customTimer.getTime() - System.currentTimeMillis() == TimeUtil.parse("15m")) {
					Bukkit.broadcastMessage(CC.translate("&fThe &6" + customTimer.getName() + " Timer &fwill be commencing in &6&n15 minutes&f."));
				}
				if (customTimer.getTime() - System.currentTimeMillis() == TimeUtil.parse("5m")) {
					Bukkit.broadcastMessage(CC.translate("&fThe &6" + customTimer.getName() + " Timer &fwill be commencing in &6&n5 minutes&f."));
				}
			}
		}.runTaskTimerAsynchronously(HCF.getInstance(), 0, 10);
	}

	@Command(names = "startautosotwtimers", permission = "op")
	public static void startautosotwtimers(CommandSender sender, @Param(name = "time") String time) {

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "alert &7[&4Alert&7] &6&lHCF&f Queue is now &b&nopen&f. &7(/play HCF)");

		create(sender, "SOTW-Start-In", time, "startsotwtimers");
		create(sender, "Whitelist-Off", time, "whitelist off");
		create(sender, "Queue-Toggle", time, "runglobalcommand queuetoggle HCF");
		create(sender, "Alert", time, "alert &7[&4Alert&7] &6&lHCF&f is now released & &a&nonline&f.");

	}

	@Command(names = "startsotwtimers", permission = "op")
	public static void startsotwtimers(CommandSender sender) {
		create(sender, "3x-Package-Crate-All", "1h", "package giveall 3");
		create(sender, "3x-Autumn-Key-All", "2h", "cr giveallkey Seasonal 3");
		create(sender, "2x-Partner-Key-All", "3h", "partnercrates giveall 3");
		create(sender, "50%-SOTW-Sale", "24h", "");
		SOTWCommand.sotwStart(sender, "1h30m");
		new BukkitRunnable() {
			@Override
			public void run() {
				PollCommand.pollsCreate(Bukkit.getConsoleSender(), "sotw-extend-20m", "Extend-SOTW", "Should we extend SOTW Timer by 20 minutes?");
				Bukkit.broadcastMessage(CC.translate("&7[&4Owner&7] &4LBuddyB0y&7: &fMake sure to vote on the poll! &7(/polls)"));
			}
		}.runTaskLater(HCF.getInstance(), 20 * 60 * 15 + (20 * 60 * 60));

	}

	@Command(names = "customtimer delete", permission = "foxtrot.customtimer")
	public static void delete(CommandSender sender, @Param(name = "name") String name) {
		CustomTimer customTimer = CustomTimer.byName(name.replace("-", " "));
		CustomTimer.customTimers.remove(customTimer);
		sender.sendMessage(CC.chat("&cDeleted the " + name + " customtimer."));
	}

	@Command(names = "customtimer list", permission = "foxtrot.customtimer")
	public static void list(CommandSender sender) {
		sender.sendMessage(CC.chat("&6&lActive CustomTimer List"));
		if (CustomTimer.customTimers.isEmpty()) {
			sender.sendMessage(CC.chat("&cNone"));
		} else {
			if (sender instanceof Player) {
				new CustomTimerMenu().openMenu((Player) sender);
			} else {
			CustomTimer.customTimers.forEach(customTimer -> {
				sender.sendMessage(CC.chat("&7 - " + customTimer.getName() + " (" + FoxtrotScoreGetter.getTimerScore(customTimer.getTime()) + ") {" + customTimer.getCommand() + "}"));
			});
			}
		}
	}

}

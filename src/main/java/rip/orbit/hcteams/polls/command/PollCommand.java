package rip.orbit.hcteams.polls.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.polls.Poll;
import rip.orbit.hcteams.polls.menu.PollMenu;
import rip.orbit.hcteams.util.CC;

import java.util.ArrayList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 6:00 PM
 * HCTeams / rip.orbit.hcteams.polls.command
 */
public class PollCommand {

	@Command(names = {"polls", "poll"}, permission = "")
	public static void polls(Player sender) {
		new PollMenu().openMenu(sender);
	}

	@Command(names = {"polls create", "poll create"}, permission = "poll.admin")
	public static void pollsCreate(CommandSender sender, @Param(name = "yesCommand - = space") String yesCommand, @Param(name = "title") String title, @Param(name = "question", wildcard = true) String question) {

		Poll poll = new Poll(title, question, 0, 0, new ArrayList<>());

		HCF.getInstance().getPollHandler().getPolls().add(poll);

		Bukkit.broadcastMessage(CC.translate(" "));
		Bukkit.broadcastMessage(CC.translate("&6&lPoll &7(" + title + ")"));
		Bukkit.broadcastMessage(CC.translate(" "));
		Bukkit.broadcastMessage(CC.translate("&7┃ &fQuestion&7: &6" + poll.getQuestion()));
		Bukkit.broadcastMessage(CC.translate(" "));
		Bukkit.broadcastMessage(CC.translate("&7&oThis poll will end in 5 minutes!"));
		Bukkit.broadcastMessage(CC.translate(" "));

		sender.sendMessage(CC.translate("&aPoll created!"));

		new BukkitRunnable() {
			@Override
			public void run() {
				Poll selected = null;
				for (Poll poll1 : HCF.getInstance().getPollHandler().getPolls()) {
					if (poll1.getTitle().equals(title)) {
						selected = poll1;
					}
				}
				if (selected.getYes() > selected.getNo()) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), yesCommand.replaceAll("-", " "));
				}
				HCF.getInstance().getPollHandler().getPolls().remove(selected);
				Bukkit.broadcastMessage(CC.translate(" "));
				Bukkit.broadcastMessage(CC.translate("&6&lPoll &7(" + title + ")"));
				Bukkit.broadcastMessage(CC.translate(" "));
				Bukkit.broadcastMessage(CC.translate("&7┃ &fQuestion&7: &6" + poll.getQuestion()));
				if (selected.getNo() > selected.getYes()) {
					Bukkit.broadcastMessage(CC.translate("&7┃ &fMost Voted&7: &cNO"));
				} else if (selected.getNo() == selected.getYes()) {
					Bukkit.broadcastMessage(CC.translate("&7┃ &fMost Voted&7: &eTIE"));
				} else {
					Bukkit.broadcastMessage(CC.translate("&7┃ &fMost Voted&7: &aYES"));
				}
				Bukkit.broadcastMessage(CC.translate(" "));
				Bukkit.broadcastMessage(CC.translate("&7&oThe " + title + " poll has now concluded."));
				Bukkit.broadcastMessage(CC.translate(" "));
			}
		}.runTaskLater(HCF.getInstance(), 20 * 60 * 2);

	}

}

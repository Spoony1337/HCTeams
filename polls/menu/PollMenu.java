package rip.orbit.hcteams.polls.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.polls.Poll;
import rip.orbit.hcteams.util.CC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 5:58 PM
 * HCTeams / rip.orbit.hcteams.polls.menu
 */
public class PollMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "All Polls";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Poll poll : HCF.getInstance().getPollHandler().getPolls()) {
			buttons.put(i, new PollButton(poll));
		}
		++i;

		return buttons;
	}

	@AllArgsConstructor
	public static class PollButton extends Button {

		private final Poll poll;

		@Override
		public String getName(Player player) {
			return CC.translate("&6" + poll.getTitle() + " Poll");
		}

		@Override
		public List<String> getDescription(Player player) {
			return CC.translate(Arrays.asList(
					"&7┃ &fQuestion&7: &6" + poll.getQuestion(),
					"",
					"&7┃ &fYes'&7: &a" + poll.getYes(),
					"&7┃ &fNo's&7: &c" + poll.getNo(),
					"",
					"&7&oLeft Click to vote &aYes",
					"&7&oRight Click to vote &cNo"
			));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.BOOK_AND_QUILL;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (poll.getClaimedPlayers().contains(player.getUniqueId())) {
				player.sendMessage(CC.translate("&cYou have already voted!"));
				return;
			}
			if (clickType == ClickType.LEFT) {
				poll.setYes(poll.getYes() + 1);
				player.sendMessage(CC.translate("&aVoted Yes!"));
			} else {
				poll.setNo(poll.getNo() + 1);
				player.sendMessage(CC.translate("&cVoted No!"));
			}
			poll.getClaimedPlayers().add(player.getUniqueId());
		}
	}

}

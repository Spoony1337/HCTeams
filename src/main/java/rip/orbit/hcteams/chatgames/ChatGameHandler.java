package rip.orbit.hcteams.chatgames;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.chatgames.type.ChatMath;
import rip.orbit.hcteams.chatgames.type.ChatQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 4:35 PM
 * HCTeams / rip.orbit.hcteams.chatgames
 */
public class ChatGameHandler {

	@Getter private final List<ChatGame> chatGames;

	public ChatGameHandler() {
		chatGames = new ArrayList<>();
		chatGames.add(new ChatQuestion());
		chatGames.add(new ChatMath());

		new BukkitRunnable() {
			@Override
			public void run() {
				ChatGame game = chatGames.get((new Random().nextInt(chatGames.size()) - 1));
				game.start();
			}
		}.runTaskTimerAsynchronously(HCF.getInstance(), 20 * 60 * 5, 20 * 60 * 5);
	}

}

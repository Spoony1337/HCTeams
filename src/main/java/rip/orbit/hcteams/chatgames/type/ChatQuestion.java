package rip.orbit.hcteams.chatgames.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.chatgames.ChatGame;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 4:37 PM
 * HCTeams / rip.orbit.hcteams.chatgames.type
 */
public class ChatQuestion extends ChatGame {

	private Question pickedQuestion = null;
	private double tickedTime;

	private final List<Question> questions = Arrays.asList(
			new Question("What is the name of the server you are currently playing?", Arrays.asList("Orbit", "Orbit.rip")),
			new Question("What kit gives you tons of ability items & a full Protection 2 Diamond Set with Custom Enchants including Ability Master I?", Collections.singletonList("Autumn")),
			new Question("What time do our SOTW's take place?", Arrays.asList("Saturday 3 PM EST", "Saturday @ 3 PM EST")),
			new Question("What is the top rank?", Collections.singletonList("Orbit")),
			new Question("Who is the developer of the server, but also an owner?", Collections.singletonList("LBuddyBoy")),
			new Question("What is the rank you can get by running /freerank?", Collections.singletonList("Star")),
			new Question("Who is the main owner of the server, mainly the config specialist?", Collections.singletonList("Premieres"))
	);

	@Override
	public String name() {
		return "Chat Question";
	}

	@Override
	public void start() {
		this.started = true;
		this.tickedTime = 0;

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!started) {
					cancel();
					return;
				}
				tickedTime = tickedTime + 0.1;
			}
		}.runTaskTimer(HCF.getInstance(), 5, 5);

		Question picked = questions.get((new Random().nextInt(questions.size())) - 1);

		this.pickedQuestion = picked;

		List<String> format = Arrays.asList(
				" ",
				"&6&lQuestion Game",
				" ",
				"&7┃ &fRespond with the correct answer",
				"&7┃ &fto receive a &6Partner Key&7&o.",
				" ",
				"&7┃ &fQuestion&7: &6" + picked.getQuestion(),
				" "
		);

		format.forEach(s -> {
			Bukkit.broadcastMessage(CC.translate(s));
		});

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!started)
					return;
				end();
			}
		}.runTaskLater(HCF.getInstance(), 20 * 15);

	}

	@Override
	public void end() {

		this.started = false;

		Bukkit.broadcastMessage(CC.translate("&cNobody answered the question in time."));

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.started) {
			if (this.pickedQuestion != null) {
				if (this.pickedQuestion.getAnswers().contains(event.getMessage())) {
					this.started = false;
					event.setCancelled(true);

					List<String> winMessage = Arrays.asList(
							"",
							"&6&lQuestion Game",
							"",
							"&7┃ &fWinner&7: &6" + event.getPlayer().getDisplayName(),
							"&7┃ &fAnswer&7: &6" + event.getMessage(),
							"&7┃ &fTime&7: &6" + Team.DTR_FORMAT2.format(tickedTime) + "s",
							""
					);

					winMessage.forEach(s -> {
						Bukkit.broadcastMessage(CC.translate(s));
					});

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "partnercrates give " + event.getPlayer().getName() + " 1");

				}
			}
		}
	}

	@AllArgsConstructor
	@Data
	public static class Question {
		private final String question;
		private final List<String> answers;
	}

}

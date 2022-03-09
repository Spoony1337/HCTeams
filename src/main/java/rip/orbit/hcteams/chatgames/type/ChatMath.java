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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 5:25 PM
 * HCTeams / rip.orbit.hcteams.chatgames.type
 */
public class ChatMath extends ChatGame {

	private Equation pickedEquation = null;
	private double tickedTime;

	private final List<Equation> equations = new ArrayList<>();

	public ChatMath() {
		for (int i = 0; i < 50; i++) {
			int first = new Random().nextInt(1500);
			int second = new Random().nextInt(2000);
			if (i <= 15) {
				equations.add(new Equation(first, second, "*"));
			} else if (i <= 30) {
				equations.add(new Equation(first, second, "+"));
			} else if (i <= 40) {
				equations.add(new Equation(first, second, "-"));
			} else if (i <= 45) {
				equations.add(new Equation(first, second, "/"));
			}
		}
	}

	@Override
	public String name() {
		return "Math Game";
	}

	@Override
	public void start() {
		this.started = true;
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

		Equation picked = equations.get((new Random().nextInt(equations.size())) - 1);

		this.pickedEquation = picked;

		List<String> format = Arrays.asList(
				" ",
				"&6&lMath Game",
				" ",
				"&7┃ &fRespond with the correct answer",
				"&7┃ &fto receive a &6Partner Key&7&o.",
				" ",
				"&7┃ &fQuestion&7: &6" + picked.getFirstNumber() + " " + picked.getEquationType() + " " + picked.getSecondNumber(),
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

		Bukkit.broadcastMessage(CC.translate("&cNobody answered the equation in time."));

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.started) {
			if (this.pickedEquation != null) {
				try {
					if (this.pickedEquation.getTotal() == Integer.parseInt(event.getMessage())) {
						this.started = false;
						event.setCancelled(true);

						List<String> winMessage = Arrays.asList(
								"",
								"&6&lMath Game",
								"",
								"&7┃ &fWinner&7: &6" + event.getPlayer().getDisplayName(),
								"&7┃ &fAnswer&7: &6" + this.pickedEquation.getTotal(),
								"&7┃ &fTime&7: &6" + Team.DTR_FORMAT2.format(tickedTime) + "s",
								""
						);

						winMessage.forEach(s -> {
							Bukkit.broadcastMessage(CC.translate(s));
						});

						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "partnercrates give " + event.getPlayer().getName() + " 1");

					}
				} catch (NumberFormatException ignored) {

				}
			}
		}
	}

	@AllArgsConstructor
	@Data
	public static class Equation {
		private final int firstNumber;
		private final int secondNumber;
		private final String equationType;

		public int getTotal() {
			if (this.equationType.equalsIgnoreCase("*")) {
				return firstNumber * secondNumber;
			} else if (this.equationType.equalsIgnoreCase("+")) {
				return firstNumber + secondNumber;
			} else if (this.equationType.equalsIgnoreCase("/")) {
				return firstNumber / secondNumber;
			} else if (this.equationType.equalsIgnoreCase("-")) {
				return firstNumber - secondNumber;
			} else {
				return firstNumber * secondNumber;
			}
		}

	}

}

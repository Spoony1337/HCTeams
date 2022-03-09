package rip.orbit.hcteams.scoreboard;

import net.frozenorb.qlib.autoreboot.AutoRebootHandler;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.util.LinkedList;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.commands.staff.EOTWCommand;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.customtimer.CustomTimer;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.events.conquest.game.ConquestGame;
import rip.orbit.hcteams.events.dtc.DTC;
import rip.orbit.hcteams.events.killtheking.KingEvent;
import rip.orbit.hcteams.events.koth.KOTH;
import rip.orbit.hcteams.events.purge.commands.PurgeCommands;
import rip.orbit.hcteams.listener.kits.GoldenAppleListener;
import rip.orbit.hcteams.map.duel.Duel;
import rip.orbit.hcteams.map.game.Game;
import rip.orbit.hcteams.map.game.GameHandler;
import rip.orbit.hcteams.map.stats.StatsEntry;
import rip.orbit.hcteams.pvpclasses.PvPClass;
import rip.orbit.hcteams.pvpclasses.PvPClassHandler;
import rip.orbit.hcteams.pvpclasses.pvpclasses.ArcherClass;
import rip.orbit.hcteams.pvpclasses.pvpclasses.BardClass;
import rip.orbit.hcteams.server.EnderpearlCooldownHandler;
import rip.orbit.hcteams.server.ServerHandler;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.settings.Setting;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.commands.team.TeamStuckCommand;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.Logout;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FoxtrotScoreGetter implements ScoreGetter {

	private String formatBasicTps(double tps) {
		return "" + Math.min(Math.round(tps * 10.0) / 10.0, 20.0);
	}

	@Override
	public void getScores(LinkedList<String> scores, Player player) {
		Team team = HCF.getInstance().getTeamHandler().getTeam(player);

		if (HCF.getInstance().getMapHandler().getGameHandler() != null && HCF.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
			Game ongoingGame = HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame();
			if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
				ongoingGame.getScoreboardLines(player, scores);
				scores.addFirst("&a&7&m--------------------");
				scores.add("&b&7&m--------------------");
				return;
			}
		}

		if (HCF.getInstance().getInDuelPredicate().test(player)) {
			Duel duel = HCF.getInstance().getMapHandler().getDuelHandler().getDuel(player);

			scores.add("&7┃ &fOpponent&7: &6" + UUIDUtils.name(duel.getOpponent(player.getUniqueId())));

			scores.addFirst("&a&7&m--------------------");
			scores.add("&b&7&m--------------------");

			return;
		}


		String spawnTagScore = getSpawnTagScore(player);
		String enderpearlScore = getEnderpearlScore(player);
		String pvpTimerScore = getPvPTimerScore(player);
		String archerMarkScore = getArcherMarkScore(player);
		String bardEffectScore = getBardEffectScore(player);
		String bardEnergyScore = getBardEnergyScore(player);
		String fstuckScore = getFStuckScore(player);
		String logoutScore = getLogoutScore(player);
		String homeScore = getHomeScore(player);
		String appleScore = getAppleScore(player);

		String mainColor = CC.translate(HCF.getInstance().getConfig().getString("scoreboard.mainColor"));
		String otherColor = CC.translate(HCF.getInstance().getConfig().getString("scoreboard.otherColor"));
		String deathbanScore = getDeathbanScore(player);

		if (Setting.SCOREBOARD_STAFF_BOARD.isEnabled(player) && player.hasMetadata("modmode")) {
			double tps = Bukkit.spigot().getTPS()[1];
			scores.add(mainColor + "&6Mod Mode &7(" + (player.hasMetadata("invisible") ? "&aOn" : "&cOff") + "&7)");
			scores.add("" + otherColor + "&7┃ &fOnline&7: &6" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
			if (player.isOp()) {
				scores.add("" + otherColor + "&7┃ &fTPS&7: &6" + formatBasicTps(tps));
			}
		}

		if (HCF.getInstance().getMapHandler().isKitMap()) {
			StatsEntry stats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());
			if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
				scores.add(otherColor + "&7┃ &fKills&7: &6" + stats.getKills());
				scores.add(otherColor + "&7┃ &fDeaths&7: &6" + stats.getDeaths());
				if (stats.getKillstreak() > 0) {
					scores.add(otherColor + "&7┃ &fKillstreak&7: &6" + stats.getKillstreak());
				}
				scores.add(otherColor + "&7┃ &fBalance&7: &6" + "$" + FrozenEconomyHandler.getBalance(player.getUniqueId()));
			}
		}

		if (spawnTagScore != null) {
			scores.add("&7┃ &fSpawn Tag&7: &6" + spawnTagScore);
		}

		if (homeScore != null) {
			scores.add("&7┃ &fHome§7: &6" + homeScore);
		}

		if (appleScore != null) {
			scores.add("&7┃ &fApple&7: &6" + appleScore);
		}
		long cooldown = HCF.getInstance().getOppleMap().getCooldown(player.getUniqueId());
		if (cooldown > System.currentTimeMillis()) {
			long millisLeft = cooldown - System.currentTimeMillis();
			String msg = TimeUtils.formatLongIntoHHMMSS(TimeUnit.MILLISECONDS.toSeconds(millisLeft));
			scores.add("&7┃ &fGopple&7: **&6" + msg);
		}
		if (enderpearlScore != null) {
			scores.add("&7┃ &fEnderpearl&7: &6" + enderpearlScore);
		}


		if (KingEvent.isStarted(false)) {
			Player target = KingEvent.getFocusedPlayer();
			scores.add(mainColor + "&lKill The King");
			scores.add("&7┃" + otherColor + " King: " + target.getName());
			if (target != player) {
				scores.add("&7┃" + otherColor + " Location: &f" + target.getLocation().getBlockX() + ", " + target.getLocation().getBlockX() + ", " + target.getLocation().getBlockZ());
				if (target.getWorld() == player.getWorld()) {
					int distance = (int) target.getLocation().distance(player.getLocation());
					if (distance <= 3000) {
						scores.add("&7┃" + otherColor + " Distance: &f" + distance + 'm');
					}
				}
			}
			scores.add(otherColor + "&7┃ &fReward: &6" + KingEvent.getReward());
			scores.add(otherColor + "&7┃ &fTime Left: &6" + DurationFormatUtils.formatDuration(KingEvent.getTime(), "mm:ss"));
		}

		if (pvpTimerScore != null) {
			if (HCF.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) {
				scores.add("&7┃ &fStarting Timer&7: &6" + pvpTimerScore);
			} else {
				scores.add("&7┃ &fPvP Timer&7: &6" + pvpTimerScore);
			}
		}

		Iterator<Map.Entry<String, Long>> iterator = SOTWCommand.getCustomTimers().entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Long> timer = iterator.next();
			if (timer.getValue() < System.currentTimeMillis()) {
				iterator.remove();
				continue;
			}

			if (timer.getKey().equals("&f&lSOTW ends in")) {
				if (SOTWCommand.hasSOTWEnabled(player.getUniqueId())) {
					scores.add(ChatColor.translateAlternateColorCodes('&', "&7┃ &f&mSOTW ends in&6&m " + getTimerScore(timer)));
				} else {
					scores.add(ChatColor.translateAlternateColorCodes('&', "&7┃ &fSOTW ends in&6 " + getTimerScore(timer)));
				}
			}
		}

		for (CustomTimer timer : CustomTimer.customTimers) {
			long time = timer.getTime();
			if (getTimerScore(time) != null) {
				scores.add("&7┃ &f" + timer.getName() + "&7: &6" + getTimerScore(time));
			} else {
				CustomTimer.customTimers.remove(timer);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), timer.getCommand());
			}
		}

		if (PurgeCommands.isPurgeTimer()) {
			scores.add(ChatColor.translateAlternateColorCodes('&', "&7┃ &fPurge Event&7: &6" + PurgeCommands.getCustomTimers()));
		}

		for (Event event : HCF.getInstance().getEventHandler().getEvents()) {
			if (!event.isActive() || event.isHidden()) {
				continue;
			}
			String displayName;

			switch (event.getName()) {
				case "EOTW":
					displayName = ChatColor.WHITE.toString() + "EOTW";
					break;
				case "Citadel":
					displayName = ChatColor.WHITE.toString() + "Citadel";
					break;
				case "End":
					displayName = ChatColor.WHITE.toString() + "End";
					break;
				case "Nether":
					displayName = ChatColor.WHITE.toString() + "Nether";
					break;
				default:
					displayName = ChatColor.WHITE.toString() + event.getName();
					break;
			}

			if (event.getType() == EventType.DTC) {
				scores.add("&7┃ " + displayName + "&7: &6" + ((DTC) event).getCurrentPoints());
			} else {
				scores.add("&7┃ " + displayName + "&7: &6" + ScoreFunction.TIME_SIMPLE.apply((float) ((KOTH) event).getRemainingCapTime()));
			}
		}

		if (EOTWCommand.isFfaEnabled()) {
			long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
			if (System.currentTimeMillis() < ffaEnabledAt) {
				long difference = ffaEnabledAt - System.currentTimeMillis();
				scores.add("&7┃ &fFFA&7: &6" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000F));
			}
		}
		PvPClass classs = PvPClassHandler.getPvPClass(player);
		if (classs != null) {
			scores.add("&7┃ &fActive Class&7: &6" + classs.getName());
		}

		if (archerMarkScore != null) {
			scores.add("&7┃ &fArcher Mark&7: &6" + archerMarkScore);
		}

		if (bardEffectScore != null && classs != null) {
			scores.add("&7┃ &fBard Effect&7: &6" + bardEffectScore);
		}

		if (bardEnergyScore != null && classs != null) {
			scores.add("&7┃ &fBard Energy&7: &6" + bardEnergyScore);
		}
		if (ArcherClass.getLastSpeedUsage().containsKey(player.getName()) && ArcherClass.getLastSpeedUsage().get(player.getName()) > System.currentTimeMillis() && classs != null) {
			long millisLeft = ArcherClass.getLastSpeedUsage().get(player.getName()) - System.currentTimeMillis();
			String msg = TimeUtils.formatIntoMMSS((int) millisLeft / 1000);
			scores.add("&7┃ &fSpeed&7: &6" + msg);
		}
		if (ArcherClass.getLastJumpUsage().containsKey(player.getName()) && ArcherClass.getLastJumpUsage().get(player.getName()) > System.currentTimeMillis() && classs != null) {
			long millisLeft = ArcherClass.getLastJumpUsage().get(player.getName()) - System.currentTimeMillis();
			String msg = TimeUtils.formatIntoMMSS((int) millisLeft / 1000);
			scores.add("&7┃ &fJump Boost&7: &6" + msg);
		}

		if (fstuckScore != null) {
			scores.add("&7┃ &fStuck&7: &6" + fstuckScore);
		}

		if (logoutScore != null) {
			scores.add("&7┃ &fLogout&7: &6" + logoutScore);
		}

		if (HCF.getInstance().getAbilityHandler().getAbilityCD().onCooldown(player)) {
			scores.add("&7┃ &fAbility Item&7: &6" + HCF.getInstance().getAbilityHandler().getAbilityCD().getRemaining(player));
		}

		GameHandler gameHandler = HCF.getInstance().getMapHandler().getGameHandler();
		if (gameHandler != null) {
			if (gameHandler.isJoinable())
				scores.add("&7┃ &fEvent&7: &6" + HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame().getGameType().getDisplayName() + " (/join)");
			else if (gameHandler.isHostCooldown())
				scores.add("&7┃ &fEvent Cooldown&7: &6" + gameHandler.getCooldownSeconds() + "s");
		}

		if (HCF.getInstance().getToggleAbilityCDsSBMap().isEnabled(player.getUniqueId())) {
			for (Ability ability : HCF.getInstance().getAbilityHandler().getAbilities()) {
				if (ability.cooldown().onCooldown(player)) {
					scores.add("&7┃ &f" + ChatColor.stripColor(ability.displayName()) + "&7: &6" + ability.cooldown().getRemaining(player));
				}
			}
		}


		ConquestGame conquest = HCF.getInstance().getConquestHandler().getGame();

		if (conquest != null) {
			if (scores.size() != 0) {
				scores.add("&c&7&m--------------------");
			}

			scores.add(mainColor + "Conquest");
			int displayed = 0;

			for (Map.Entry<ObjectId, Integer> entry : conquest.getTeamPoints().entrySet()) {
				Team resolved = HCF.getInstance().getTeamHandler().getTeam(entry.getKey());

				if (resolved != null) {
					scores.add("&7┃ " + resolved.getName(player) + "&7: &6" + entry.getValue());
					displayed++;
				}

				if (displayed == 3) {
					break;
				}
			}

			if (displayed == 0) {
				scores.add("&7No scores yet");
			}
		}

		if (AutoRebootHandler.isRebooting()) {
			scores.add("&7┃ &fRebooting&7: &6" + TimeUtils.formatIntoMMSS(AutoRebootHandler.getRebootSecondsRemaining()));
		}
		if (HCF.getInstance().getClaimOnSbMap().isToggled(player.getUniqueId())) {
			if (team != null) {
				if (team.getFactionFocused() != null || conquest != null) {
					scores.add(" ");
				}
			}
			Location loc = player.getLocation();
			Team ownerTeam = LandBoard.getInstance().getTeam(loc);
			String location;
			if (ownerTeam != null) {
				if (ownerTeam.getName().equals("AbilityHill") && ownerTeam.getMembers().size() == 0) {
					location = ChatColor.YELLOW + "Ability Hill";
				} else if (ownerTeam.getName().equals("PumpkinPatch") && ownerTeam.getMembers().size() == 0) {
					location = ChatColor.GOLD + "Pumpkin Patch";
				} else {
					location = ownerTeam.getName(player);
				}
			} else if (!HCF.getInstance().getServerHandler().isWarzone(loc)) {
				location = ChatColor.GRAY + "Wilderness";
			} else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance().getTeam(loc).getName().equalsIgnoreCase("citadel")) {
				location = ChatColor.DARK_PURPLE + "Citadel";
			} else {
				location = ChatColor.DARK_RED + "Warzone";
			}
			if (!HCF.getInstance().getServerHandler().isUnclaimed(loc)) {
				if (player.hasMetadata("modmode")) {
					scores.add(" ");
				}
				scores.add("&7┃ &fClaim&7: " + location);
				if (team != null) {
					if (team.getFactionFocused() != null || conquest != null) {
						scores.add(" ");
					}
				}
			}
		}
		if (team != null) {
			String dtrColored;
			double dtr;
			if (team.getFactionFocused() != null) {
				Team focusedFaction = team.getFactionFocused();
				scores.add(mainColor + "&6Team&7: " + focusedFaction.getName(player) + " &7(&f" + focusedFaction.getOnlineMemberAmount() + "&7/&f" + focusedFaction.getMembers().size() + "&7)");

				dtr = Double.parseDouble((new DecimalFormat("#.##")).format(focusedFaction.getDTR()));
				if (dtr >= 1.01D) {
					dtrColored = ChatColor.GREEN + String.valueOf(dtr);
				} else if (dtr <= 0.0D) {
					dtrColored = ChatColor.RED + String.valueOf(dtr);
				} else {
					dtrColored = ChatColor.YELLOW + String.valueOf(dtr);
				}

				if (focusedFaction.getHq() != null) {
					scores.add(mainColor + "&7┃ &fHQ&7: &6" + focusedFaction.getHq().getBlockX() + ", " + focusedFaction.getHq().getBlockZ());
				} else {
					scores.add(mainColor + "&7┃ &fHQ&7: &cNot set.");
				}
				scores.add(mainColor + "&7┃ &fDTR&7: &r" + dtrColored + focusedFaction.getDTRSuffix());
			}
		}
		if (!scores.isEmpty()) {
			scores.addFirst("&a&7&m--------------------");
			if (HCF.getInstance().getConfig().getBoolean("scoreboard.footerboolean")) {
				scores.add("&c&l");
				scores.add(CC.translate("&6&oorbit.rip"));
			}
			scores.add("&b&7&m--------------------");
		}
	}

	public String getDeathbanScore(Player player) {
		if (HCF.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
			float diff = HCF.getInstance().getDeathbanMap().getDeathban(player.getUniqueId()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return null;
	}

	public String getAppleScore(Player player) {
		if (GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId()) && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) >= System.currentTimeMillis()) {
			float diff = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getHomeScore(Player player) {
		if (ServerHandler.getHomeTimer().containsKey(player.getName()) && ServerHandler.getHomeTimer().get(player.getName()) >= System.currentTimeMillis()) {
			float diff = ServerHandler.getHomeTimer().get(player.getName()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getFStuckScore(Player player) {
		if (TeamStuckCommand.getWarping().containsKey(player.getName())) {
			float diff = TeamStuckCommand.getWarping().get(player.getName()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return null;
	}

	public String getLogoutScore(Player player) {
		Logout logout = ServerHandler.getTasks().get(player.getName());

		if (logout != null) {
			float diff = logout.getLogoutTime() - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return null;
	}

	public String getSpawnTagScore(Player player) {
		if (SpawnTagHandler.isTagged(player)) {
			float diff = SpawnTagHandler.getTag(player);

			if (diff >= 0) {
				return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getEnderpearlScore(Player player) {
		if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
			float diff = EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getPvPTimerScore(Player player) {
		if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
			int secondsRemaining = HCF.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

			if (secondsRemaining >= 0) {
				return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
			}
		}

		return (null);
	}

	public String getTimerScore(Map.Entry<String, Long> timer) {
		long diff = timer.getValue() - System.currentTimeMillis();

		if (diff > 0) {
			return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
		} else {
			return (null);
		}
	}

	public static String getTimerScore(Long timer) {
		long diff = timer - System.currentTimeMillis();

		if (diff > 0) {
			return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
		} else {
			return (null);
		}
	}

	public String getArcherMarkScore(Player player) {
		if (ArcherClass.isMarked(player)) {
			long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

			if (diff > 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getBardEffectScore(Player player) {
		if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
			float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

			if (diff > 0) {
				return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getBardEnergyScore(Player player) {
		if (BardClass.getEnergy().containsKey(player.getName())) {
			float energy = BardClass.getEnergy().get(player.getName());

			if (energy > 0) {
				return (String.valueOf(BardClass.getEnergy().get(player.getName())));
			}
		}

		return (null);
	}

}
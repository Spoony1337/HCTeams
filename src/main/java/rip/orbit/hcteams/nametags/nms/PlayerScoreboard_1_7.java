package rip.orbit.hcteams.nametags.nms;

import com.cheatbreaker.api.CheatBreakerAPI;
import com.lunarclient.bukkitapi.LunarClientAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.nametags.PlayerScoreboard;
import rip.orbit.hcteams.nametags.ScoreboardInput;
import rip.orbit.hcteams.nametags.base.ScoreboardBase_1_7;
import rip.orbit.hcteams.nametags.util.NmsUtils;
import rip.orbit.hcteams.nametags.util.Tasks;
import rip.orbit.hcteams.pvpclasses.pvpclasses.ArcherClass;
import rip.orbit.hcteams.util.CC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerScoreboard_1_7 extends ScoreboardBase_1_7 implements PlayerScoreboard {

	private static final String SB_LINE = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------";
	private static final ScoreboardInput EMPTY_INPUT = new ScoreboardInput("", "", "");

	private final Deque<ScoreboardInput> entries;
	private Set<String> lastEntries;

	private final ScoreboardInput[] entryCache;

	private final AtomicBoolean update;
	private final AtomicBoolean lastLine;

	private Team members;
	private Team archers;
	private Team focused;
	private Team allies;
	private Team enemies;
	private Team sotw;
	private Team invis;

	public PlayerScoreboard_1_7(Player player) {
		super(player, NmsUtils.getInstance().getPlayerScoreboard(player));

		this.entries = new ArrayDeque<>();
		this.lastEntries = new HashSet<>();

		this.entryCache = new ScoreboardInput[15];

		for (int i = 0; i < 15; i++) {
			this.entryCache[i] = EMPTY_INPUT;
		}

		this.setupTeams();

		this.update = new AtomicBoolean(false);
		this.lastLine = new AtomicBoolean(false);

		player.setScoreboard(this.scoreboard);

		new BukkitRunnable() {
			@Override
			public void run() {
				updateAllTabRelations(Bukkit.getOnlinePlayers());
			}
		}.runTaskTimerAsynchronously(HCF.getInstance(), 0, 20);
	}

	@Override
	public void unregister() {
		synchronized (this.scoreboard) {
			for (Objective objective : this.scoreboard.getObjectives()) {
				objective.unregister();
			}

			for (Team team : this.scoreboard.getTeams()) {
				team.unregister();
			}
		}

		for (Object entry : this.nmsScoreboard.getPlayers().toArray()) {
			this.resetScore((String) entry);
		}

		this.player = null;
	}

	private void setupTeams() {
		this.members = this.getTeam(CC.translate("members"));
		this.members.setPrefix(ChatColor.GREEN.toString());
		this.members.setCanSeeFriendlyInvisibles(true);

		this.allies = this.getTeam(CC.translate("allies"));
		this.allies.setPrefix(ChatColor.AQUA.toString());

		this.archers = this.getTeam(CC.translate("archers"));
		this.archers.setPrefix(ChatColor.DARK_RED.toString());

		this.focused = this.getTeam(CC.translate("focused"));
		this.focused.setPrefix(ChatColor.LIGHT_PURPLE.toString());

		this.enemies = this.getTeam(CC.translate("enemies"));
		this.enemies.setPrefix(ChatColor.RED.toString());

		this.sotw = this.getTeam(CC.translate("sotw"));
		this.sotw.setPrefix(ChatColor.GOLD.toString());

		if ((this.invis = this.scoreboard.getTeam(CC.translate("inviss"))) == null) {
			try {
				Method method = this.scoreboard.getClass().getDeclaredMethod("registerNewTeam", String.class, boolean.class);
				method.setAccessible(true);

				this.invis = (Team) method.invoke(this.scoreboard, CC.translate("invis"), false);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
			}
		}
	}

	@Override
	public void update() {
		if (!this.update.get() && this.lastEntries.isEmpty()) return;

		Set<String> addedEntries = new HashSet<>(this.entries.size());

		for (int i = this.entries.size(); i > 0; i--) {
			ScoreboardInput input = this.entries.pollFirst();
			if (input == null) return;

			addedEntries.add(input.getName());

			if (this.entryCache[i - 1].equals(input)) {
				continue;
			}

			Team team = this.getTeam(input.getName());

			if (!team.hasEntry(input.getName())) {
				team.addEntry(input.getName());
			}

			this.updateTeam(team.getName(), input.getPrefix(), input.getSuffix());

			this.entryCache[i - 1] = input;
			this.setScore(input.getName(), i);
		}

		if (addedEntries.size() < this.lastEntries.size()) {
			for (int i = addedEntries.size(); i < this.lastEntries.size(); i++) {
				this.entryCache[i] = EMPTY_INPUT;
			}
		}

		for (String entry : this.lastEntries) {
			if (!addedEntries.contains(entry)) {
				this.resetScore(entry);
			}
		}

		this.lastEntries = addedEntries;
		this.update.set(false);
	}

	@Override
	public boolean add(String value, String time) {
		if (value.isEmpty() || this.entries.size() >= 16) return false;

		if (time.length() > 16) {
			time = time.substring(0, 16);
		}

		if (value.length() <= 16) {
			this.entries.addLast(new ScoreboardInput("", value, time));
		} else if (value.length() <= 32) {
			this.entries.addLast(new ScoreboardInput(value.substring(0,
					value.length() - 16), value.substring(value.length() - 16), time));
		} else {
			this.entries.addLast(new ScoreboardInput(value.substring(0, 16),
					value.substring(16, 32), time));
		}

		this.lastLine.set(false);
		return true;
	}

	private Team getTeam(String name) {
		synchronized (this.scoreboard) {
			Team team = this.scoreboard.getTeam(name);
			return team == null ? this.scoreboard.registerNewTeam(name) : team;
		}
	}

	@Override
	public void setUpdate(boolean value) {
		this.update.set(value);
	}

	@Override
	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	@Override
	public void clear() {
		this.entries.clear();
	}

	@Override
	public void updateTabRelations(Iterable<? extends Player> players, boolean lunarOnly) {
		if (Thread.currentThread() == NmsUtils.getInstance().getMainThread()) {
			Tasks.async(() -> this.updateAllTabRelations(players));
		} else {
			this.updateAllTabRelations(players);
		}
	}

	private void updateAllTabRelations(Iterable<? extends Player> players) {
		if (this.player == null || this.scoreboard == null) return;
		rip.orbit.hcteams.team.Team team = HCF.getInstance().getTeamHandler().getTeam(this.player.getUniqueId());

		for (Player online : players) {
			List<String> nametag;



			nametag = new ArrayList<>();

			rip.orbit.hcteams.team.Team faction = HCF.getInstance().getTeamHandler().getTeam(online.getUniqueId());
			if (faction != null) {
				double dtr = Double.parseDouble((new DecimalFormat("#.##")).format(faction.getDTR()));

				String tag = ChatColor.GOLD + "[" + faction.getName(this.player) + ChatColor.GRAY + " \u2758 " + faction.getDTRColor() + dtr + faction.getDTRSuffix() + ChatColor.GOLD + "]";
				nametag.add(tag);
			}
			if (this.player == online) {
				this.addAndUpdate(online, nametag, this.members);
				continue;
			} else if (team == null) {
				if (this.invis != null && online.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					this.addAndUpdate(online, nametag, this.invis);
				} else if (ArcherClass.isMarked(online)) {
					this.addAndUpdate(online, nametag, this.archers);
				} else if (HCF.getInstance().getPvPTimerMap().hasTimer(online.getUniqueId())) {
					this.addAndUpdate(online, nametag, this.sotw);
				} else if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(online.getUniqueId())) {
					this.addAndUpdate(online, nametag, this.sotw);
				} else {
					this.addAndUpdate(online, nametag, this.enemies);
				}
				continue;
			}

			rip.orbit.hcteams.team.Team targetFaction = HCF.getInstance().getTeamHandler().getTeam(online.getUniqueId());
			boolean isMemberOrAlly = (targetFaction != null && team == targetFaction || targetFaction != null && team.getAllies().contains(targetFaction.getUniqueId()));

			if (this.invis != null && online.hasPotionEffect(PotionEffectType.INVISIBILITY) && !isMemberOrAlly) {
				this.addAndUpdate(online, nametag, this.invis);
			} else if (team.getFactionFocused() != null && team.getFactionFocused().getOnlineMembers().contains(online)) {
				this.addAndUpdate(online, nametag, this.focused);
			} else if (targetFaction != null && team == targetFaction) {
				this.addAndUpdate(online, nametag, this.members);
			} else if (targetFaction != null && team.getAllies().contains(targetFaction.getUniqueId())) {
				this.addAndUpdate(online, nametag, this.allies);
			} else if (ArcherClass.isMarked(online)) {
				this.addAndUpdate(online, nametag, this.archers);
			} else if (HCF.getInstance().getPvPTimerMap().hasTimer(online.getUniqueId())) {
				this.addAndUpdate(online, nametag, this.sotw);
			} else if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(online.getUniqueId())) {
				this.addAndUpdate(online, nametag, this.sotw);
			} else {
				this.addAndUpdate(online, nametag, this.enemies);
			}
		}
	}

	private void addAndUpdate(Player online, List<String> nametag, Team team) {
		team.addEntry(online.getName());

		if (nametag != null) {
			nametag.add(team.getPrefix() + online.getName());

			if (HCF.getInstance().getScoreboardManager().isLunarEnabled()) {
				double tps1 = Bukkit.spigot().getTPS()[1];
				double tps = Math.min(Math.round(tps1 * 10.0) / 10.0, 20.0);
				if (tps > 17) {
					LunarClientAPI.getInstance().overrideNametag(online, nametag, this.player);
					CheatBreakerAPI.getInstance().overrideNametag(online, nametag, this.player);
				}
			}
		}
	}
}
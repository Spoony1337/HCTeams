package rip.orbit.hcteams.team;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
import net.frozenorb.qlib.serialization.LocationSerializer;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.chat.enums.ChatMode;
import rip.orbit.hcteams.customtimer.CustomTimer;
import rip.orbit.hcteams.events.region.cavern.CavernHandler;
import rip.orbit.hcteams.events.region.glowmtn.GlowHandler;
import rip.orbit.hcteams.map.stats.StatsHandler;
import rip.orbit.hcteams.persist.maps.DeathbanMap;
import rip.orbit.hcteams.team.claims.Claim;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.claims.Subclaim;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.team.dtr.DTRHandler;
import rip.orbit.hcteams.team.teamupgrades.enums.BardUpgrades;
import rip.orbit.hcteams.team.teamupgrades.enums.MeteorUpgrades;
import rip.orbit.hcteams.team.teamupgrades.enums.RegenUpgrades;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.CuboidRegion;
import rip.orbit.hcteams.util.JavaUtils;
import rip.orbit.hcteams.util.object.ChatUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

public class Team {

	// Constants //
	public static DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
	public static DecimalFormat DTR_FORMAT2 = new DecimalFormat("0.0");
	public static String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);
	public static ChatColor ALLY_COLOR = ChatColor.BLUE;
	public static int MAX_CLAIMS = 2;
	public static int MAX_FORCE_INVITES = 10;

	// Internal //
	@Getter
	private boolean needsSave = false;
	@Getter
	private boolean loading = false;

	// Persisted //
	@Getter
	@Setter
	private ObjectId uniqueId;
	@Getter
	private String name;
	@Getter private RegenUpgrades regenUpgrades = RegenUpgrades.DEFAULT;
	@Getter private BardUpgrades bardUpgrades = BardUpgrades.DEFAULT;
	@Getter private MeteorUpgrades meteorUpgrades = MeteorUpgrades.DEFAULT;
	@Getter
	private Location hq;
	@Getter @Setter
	private Location rally;
	@Getter @Setter
	private BukkitTask rallyTask;
	protected boolean open;
	@Getter
	private double balance;
	@Getter
	private double DTR;
	@Getter
	private long DTRCooldown;
	@Getter
	private List<Claim> claims = new ArrayList<>();
	@Getter
	private List<Subclaim> subclaims = new ArrayList<>();
	@Getter
	private UUID owner = null;
	@Getter
	private Set<UUID> members = new HashSet<>();
	@Getter
	private Set<UUID> captains = new HashSet<>();
	@Getter
	private Set<UUID> coleaders = new HashSet<>();
	@Getter
	private Set<UUID> invitations = new HashSet<>();
	@Getter
	private Set<ObjectId> allies = new HashSet<>();
	@Getter
	private Set<ObjectId> requestedAllies = new HashSet<>();
	@Getter
	private String announcement;
	@Getter
	private int maxOnline = -1;
	@Getter
	private boolean powerFaction = false;
	@Getter
	private int lives = 0;
	@Getter
	private int meteors = 0;
	@Getter
	private int points = 0;
	@Getter
	private int kills = 0;
	@Getter
	private int kothCaptures = 0;
	@Getter
	private int diamondsMined = 0;
	@Getter
	private int deaths = 0;
	@Getter
	private int citadelsCapped = 0;
	@Getter
	private int killstreakPoints = 0;

	@Getter
	private int spentPoints = 0; // points spent on faction upgrades (kinda aids)

	@Getter
	private Map<String, Integer> upgradeToTier = new HashMap<>();

	@Getter
	private int forceInvites = MAX_FORCE_INVITES;
	@Getter
	private Set<UUID> historicalMembers = new HashSet<>(); // this will store all players that were once members

	// Not persisted //
	@Getter
	@Setter
	private ChatColor teamColor;
	@Getter
	@Setter
	private UUID focused;
	@Getter
	@Setter
	private long lastRequestReport;

	@Getter
	@Setter
	private int bards;
	@Getter
	@Setter
	private int archers;
	@Getter
	@Setter
	private int rogues;

	private Team factionFocused;

	public Team(String name) {
		this.name = name;
	}

	public void setDTR(double newDTR) {
		setDTR(newDTR, null);
	}

	public void setDTR(double newDTR, Player actor) {
		if (DTR == newDTR) {
			return;
		}

		if (DTR <= 0 && newDTR > 0) {
			TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NO_LONGER_RAIDABLE, ImmutableMap.of());
		}

		if (0 < DTR && newDTR <= 0) {
			TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, actor == null ? ImmutableMap.of() : ImmutableMap.of("actor", actor.getName()));
		}

      /*  if (!isLoading()) {
            if (actor != null) {
                HCF.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + " --> " + newDTR + ". Actor: " + actor.getName());
            } else {
                HCF.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + " --> " + newDTR);
            }
        } */

		this.DTR = newDTR;
		flagForSave();
	}

	public void setName(String name) {
		this.name = name;
		flagForSave();
	}
	public void setDTRRegenUpgrades(String name) {
		this.regenUpgrades = RegenUpgrades.valueOf(name);
		flagForSave();
	}
	public void setMeteorUpgrades(String name) {
		this.meteorUpgrades = MeteorUpgrades.valueOf(name);
		flagForSave();
	}
	public void setBardUpgrades(String name) {
		this.bardUpgrades = BardUpgrades.valueOf(name);
		flagForSave();
	}

	public String getName(Player player) {
		if (name.equals(GlowHandler.getGlowTeamName()) && this.getMembers().size() == 0) {
			return ChatColor.GOLD + "Glowstone Mountain"; // override team name
		} else if (name.equals(CavernHandler.getCavernTeamName()) && this.getMembers().size() == 0) {
			return ChatColor.AQUA + "Cavern";
		} else if (name.equals("AbilityHill") && this.getMembers().size() == 0) {
			return ChatColor.YELLOW + "Ability Hill";
		} else if (name.equals("PumpkinPatch") && this.getMembers().size() == 0) {
			return ChatColor.GOLD + "Pumpkin Patch";
		} else if (owner == null) {
			if (hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
				switch (player.getWorld().getEnvironment()) {
					case NETHER:
						return (ChatColor.GREEN + "Nether Spawn");
					case THE_END:
						return (ChatColor.GREEN + "The End Safezone");
				}

				return (ChatColor.GREEN + "Spawn");
			} else if (hasDTRBitmask(DTRBitmask.KOTH)) {
				return (ChatColor.AQUA + getName() + ChatColor.GOLD + " KOTH");
			} else if (hasDTRBitmask(DTRBitmask.CITADEL)) {
				return (ChatColor.DARK_PURPLE + "Citadel");
			} else if (hasDTRBitmask(DTRBitmask.ROAD)) {
				return (ChatColor.GOLD + getName().replace("Road", " Road"));
			} else if (hasDTRBitmask(DTRBitmask.CONQUEST)) {
				return (ChatColor.YELLOW + "Conquest");
			}
		}

		if (isMember(player.getUniqueId())) {
			return (ChatColor.GREEN + getName());
		} else if (isAlly(player.getUniqueId())) {
			return (Team.ALLY_COLOR + getName());
		} else {
			return (ChatColor.RED + getName());
		}
	}

	public void addMember(UUID member) {
		if (members.add(member)) {
			historicalMembers.add(member);

			if (this.loading) return;
			TeamActionTracker.logActionAsync(this, TeamActionType.PLAYER_JOINED, ImmutableMap.of(
					"playerId", member
			));

			if (getHq() != null) {
				Player m = Bukkit.getPlayer(member);
				LCWaypoint waypoint = new LCWaypoint("HQ", getHq(), Color.BLUE.hashCode(), true);
				LunarClientAPI.getInstance().sendWaypoint(m, waypoint);
			}

			flagForSave();
		}
	}

	public void addCaptain(UUID captain) {
		if (captains.add(captain) && !this.isLoading()) {
			TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CAPTAIN, ImmutableMap.of(
					"playerId", captain
			));

			flagForSave();
		}
	}

	public void addCoLeader(UUID co) {
		if (coleaders.add(co) && !this.isLoading()) {
			TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CO_LEADER, ImmutableMap.of(
					"playerId", co
			));

			flagForSave();
		}
	}

	public void setBalance(double balance) {
		this.balance = balance;
		flagForSave();
	}

	public void setDTRCooldown(long dtrCooldown) {
		this.DTRCooldown = dtrCooldown;
		flagForSave();
	}

	public void removeCaptain(UUID captain) {
		if (captains.remove(captain)) {
			TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CAPTAIN, ImmutableMap.of(
					"playerId", captain
			));

			flagForSave();
		}
	}

	public void removeCoLeader(UUID co) {
		if (coleaders.remove(co)) {
			TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CO_LEADER, ImmutableMap.of(
					"playerId", co
			));

			flagForSave();
		}
	}

	public void setOwner(UUID owner) {
		this.owner = owner;

		if (owner != null) {
			members.add(owner);
			coleaders.remove(owner);
			captains.remove(owner);
		}

		if (this.loading) return;
		TeamActionTracker.logActionAsync(this, TeamActionType.LEADER_CHANGED, ImmutableMap.of(
				"playerId", owner
		));

		flagForSave();
	}

	public void setMaxOnline(int maxOnline) {
		this.maxOnline = maxOnline;
		flagForSave();
	}

	public void setAnnouncement(String announcement) {
		this.announcement = announcement;

		if (this.loading) return;
		TeamActionTracker.logActionAsync(this, TeamActionType.ANNOUNCEMENT_CHANGED, ImmutableMap.of(
				"newAnnouncement", announcement
		));

		flagForSave();
	}

	public void setHq(Location hq) {
		if (getHq() != null) {
			LCWaypoint waypoint = new LCWaypoint("HQ", getHq(), Color.BLUE.hashCode(), true);
			getOnlineMembers().forEach(m -> {
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			});
		}
		String oldHQ = this.hq == null ? "None" : (getHq().getBlockX() + ", " + getHq().getBlockY() + ", " + getHq().getBlockZ());
		String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
		this.hq = hq;
		if (this.loading) return;
		TeamActionTracker.logActionAsync(this, TeamActionType.HEADQUARTERS_CHANGED, ImmutableMap.of(
				"oldHq", oldHQ,
				"newHq", newHQ
		));

		if (hq != null) {
			LCWaypoint waypoint = new LCWaypoint("HQ", hq, Color.BLUE.hashCode(), true);
			getOnlineMembers().forEach(m -> {
				LunarClientAPI.getInstance().sendWaypoint(m, waypoint);
			});
		}

		flagForSave();
	}

	public void setPowerFaction(boolean bool) {
		this.powerFaction = bool;
		if (bool) {
			TeamHandler.addPowerFaction(this);
		} else {
			TeamHandler.removePowerFaction(this);
		}

		if (this.loading) return;
		TeamActionTracker.logActionAsync(this, TeamActionType.POWER_FAC_STATUS_CHANGED, ImmutableMap.of(
				"powerFaction", bool
		));

		flagForSave();
	}

	public void setMeteors(int meteors) {
		if (CustomTimer.byName("DoubleMeteors") != null) {
			this.meteors = (meteors + meteorUpgrades.getAddition()) * 2;
		} else {
			this.meteors = meteors + meteorUpgrades.getAddition();
		}
		flagForSave();
	}

	public void setLives(int lives) {
		this.lives = lives;
		flagForSave();
	}

	public void addives(int lives) {
		this.lives = this.lives + lives;
		flagForSave();
	}

	public void removeives(int lives) {
		this.lives = this.lives - lives;
		flagForSave();
	}

	public boolean addLives(int lives) {
		if (lives < 0) {
			return false;
		}
		this.lives += lives;
		flagForSave();
		return true;
	}

	public boolean removeLives(int lives) {
		if (this.lives < lives || lives < 0) {
			return false; //You twat.
		}
		this.lives -= lives;
		flagForSave();
		return true;
	}

	public void disband() {
		getOnlineMembers().forEach(m -> {
			if (getFactionFocused() != null) {
				LCWaypoint waypoint = new LCWaypoint(getFactionFocused().getName() + "'s HQ", getFactionFocused().getHq(), Color.orange.hashCode(), true);
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			}
			if (getHq() != null) {
				LCWaypoint waypoint = new LCWaypoint("HQ", getHq(), Color.BLUE.hashCode(), true);
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			}
			if (getRallyTask() != null) {
				getRallyTask().cancel();
			}
			if (getRally() != null) {
				LCWaypoint waypoint = new LCWaypoint("Rally", getRally(), Color.yellow.hashCode(), true);
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			}
		});
		try {
			if (owner != null) {
				double refund = balance;

				for (Claim claim : claims) {
					refund += Claim.getPrice(claim, this, false);
				}

				FrozenEconomyHandler.deposit(owner, refund);
				HCF.getInstance().getWrappedBalanceMap().setBalance(owner, FrozenEconomyHandler.getBalance(owner));
				HCF.getInstance().getLogger().info("Economy Logger: Depositing " + refund + " into " + UUIDUtils.name(owner) + "'s account: Disbanded team");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (ObjectId allyId : getAllies()) {
			Team ally = HCF.getInstance().getTeamHandler().getTeam(allyId);

			if (ally != null) {
				ally.getAllies().remove(getUniqueId());
			}
		}

		for (UUID uuid : members) {
			HCF.getInstance().getChatModeMap().setChatMode(uuid, ChatMode.PUBLIC);
		}

		HCF.getInstance().getTeamHandler().removeTeam(this);
		LandBoard.getInstance().clear(this);

		new BukkitRunnable() {

			@Override
			public void run() {
				qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {


					@Override
					public Object execute(Jedis redis) {
						redis.del("fox_teams." + name.toLowerCase());
						return (null);
					}

				});

				DBCollection teamsCollection = HCF.getInstance().getMongoPool().getDB(HCF.MONGO_DB_NAME).getCollection("Teams");
				teamsCollection.remove(getJSONIdentifier());
			}

		}.runTaskAsynchronously(HCF.getInstance());

		needsSave = false;
	}

	public void rename(String newName) {
		String oldName = name;

		HCF.getInstance().getTeamHandler().removeTeam(this);

		this.name = newName;

		HCF.getInstance().getTeamHandler().setupTeam(this);

		qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {


			@Override
			public Object execute(Jedis redis) {
				redis.del("fox_teams." + oldName.toLowerCase());
				return (null);
			}

		});

		// We don't need to do anything here as all we're doing is changing the name, not the Unique ID (which is what Mongo uses)
		// therefore, Mongo will be notified of this once the 'flagForSave()' down below gets processed.

		for (Claim claim : getClaims()) {
			claim.setName(claim.getName().replaceAll(oldName, newName));
		}

		flagForSave();
	}

	public void setForceInvites(int forceInvites) {
		this.forceInvites = forceInvites;
		flagForSave();
	}

	public void setPoints(int points) {
		this.points = points;
		flagForSave();
	}

	public void setKills(int kills) {
		this.kills = kills;
		recalculatePoints();
		flagForSave();
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		recalculatePoints();
		flagForSave();
	}

	public void setKothCaptures(int kothCaptures) {
		this.kothCaptures = kothCaptures;
		recalculatePoints();
		flagForSave();
	}

	public void setDiamondsMined(int diamondsMined) {
		this.diamondsMined = diamondsMined;
		// recalculatePoints();
		flagForSave();
	}

	public void setCitadelsCapped(int citadels) {
		this.citadelsCapped = citadels;
		recalculatePoints();
		flagForSave();
	}

	public void setKillstreakPoints(int killstreakPoints) {
		this.killstreakPoints = killstreakPoints;
		recalculatePoints();
		flagForSave();
	}

	public void addKillstreakPoints(int killstreakPoints) {
		this.killstreakPoints += killstreakPoints;
		recalculatePoints();
		flagForSave();
	}

	public void spendPoints(int points) {
		spentPoints += points;
		recalculatePoints();
		flagForSave();
	}

	public void setSpentPoints(int points) {
		spentPoints = points;
		recalculatePoints();
		flagForSave();
	}

	public void recalculatePoints() {
		int basePoints = 0;

		basePoints += kills * 10;
		basePoints -= deaths * 5;
		basePoints += kothCaptures * 50;
		basePoints += citadelsCapped * 150;
//		basePoints += conquestsCapped * 75;
//        basePoints += spawnersInClaim * 5;
		basePoints += killstreakPoints * 2;
//		basePoints += playtimePoints;
//		basePoints += addedPoints;
//		basePoints -= removedPoints;
		basePoints -= spentPoints;

		if (basePoints < 0) {
			basePoints = 0;
		}

		this.points = basePoints;
	}

	public String[] getPointBreakDown() {
		int basePoints = 0;

		basePoints += kills * 10;
		basePoints -= deaths * 5;
		basePoints += kothCaptures * 50;
		basePoints += citadelsCapped * 150;
//		basePoints += conquestsCapped * 75;
//        basePoints += spawnersInClaim * 5;
		basePoints += killstreakPoints * 2;
//		basePoints += playtimePoints;
//		basePoints += addedPoints;
//		basePoints -= removedPoints;
		basePoints -= spentPoints;

		if (basePoints < 0) {
			basePoints = 0;
		}

		return new String[]{
				"Base Points: " + basePoints,
				"Kills Points: (" + kills + " kills) * 10 = " + (kills * 10),
				"Deaths Points: (" + deaths + " deaths) * 5 = " + (deaths * 5),
//				"Double Points: (" + doublePoints + " Doubled Points) * 1 = " + (doublePoints),
				"KOTH Captures Points: (" + kothCaptures + " caps) * 50 = " + (kothCaptures * 50),
				"Citadel Captures Points: (" + citadelsCapped + " caps) * 150 = " + (citadelsCapped * 150),
//				"Conquest Captures Points: (" + conquestsCapped + " caps) * 75 = " + (citadelsCapped * 75),
//                "Spawners Points: (" + spawnersInClaim + " spawners) * 5 = " + (spawnersInClaim * 5),
				"Kill Streak Points: (" + killstreakPoints + " caps) * 2 = " + (killstreakPoints * 2),
//				"Boss Points: " + playtimePoints,
//				"Extra Added Points: " + addedPoints,
//				"Extra Removed Points: " + removedPoints,
				"Spent Points: " + spentPoints
		};
	}


	public void flagForSave() {
		needsSave = true;
	}

	public boolean isOwner(UUID check) {
		return (check.equals(owner));
	}

	public boolean isMember(UUID check) {
		return members.contains(check);
	}

	public boolean isCaptain(UUID check) {
		return captains.contains(check);
	}

	public boolean isCoLeader(UUID check) {
		return coleaders.contains(check);
	}

	public void validateAllies() {
		Iterator<ObjectId> allyIterator = getAllies().iterator();

		while (allyIterator.hasNext()) {
			ObjectId ally = allyIterator.next();
			Team checkTeam = HCF.getInstance().getTeamHandler().getTeam(ally);

			if (checkTeam == null) {
				allyIterator.remove();
			}
		}
	}

	public boolean isAlly(UUID check) {
		Team checkTeam = HCF.getInstance().getTeamHandler().getTeam(check);
		return (checkTeam != null && isAlly(checkTeam));
	}

	public boolean isAlly(Team team) {
		return (getAllies().contains(team.getUniqueId()));
	}

	public boolean ownsLocation(Location location) {
		return (LandBoard.getInstance().getTeam(location) == this);
	}

	public boolean ownsClaim(Claim claim) {
		return (claims.contains(claim));
	}

	public boolean removeMember(UUID member) {
		members.remove(member);
		captains.remove(member);
		coleaders.remove(member);

		// If the owner leaves (somehow)
		if (isOwner(member)) {
			Iterator<UUID> membersIterator = members.iterator();
			this.owner = membersIterator.hasNext() ? membersIterator.next() : null;
		}

		try {
			for (Subclaim subclaim : subclaims) {
				if (subclaim.isMember(member)) {
					subclaim.removeMember(member);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (DTR > getMaxDTR()) {
			DTR = getMaxDTR();
		}

		if (this.loading) return false;
		TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_REMOVED, ImmutableMap.of(
				"playerId", member
		));

		if (getHq() != null) {
			Player m = Bukkit.getPlayer(member);
			if (m != null) {
				LCWaypoint waypoint = new LCWaypoint("HQ", getHq(), Color.BLUE.hashCode(), true);
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			}
		}

		flagForSave();
		return (owner == null || members.size() == 0);
	}

	public boolean hasDTRBitmask(DTRBitmask bitmaskType) {
		if (getOwner() != null) {
			return (false);
		}

		int dtrInt = (int) DTR;
		return (((dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask()));
	}

	public int getOnlineMemberAmount() {
		int amt = 0;

		for (UUID member : getMembers()) {
			Player exactPlayer = HCF.getInstance().getServer().getPlayer(member);

			if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
				amt++;
			}
		}

		return (amt);
	}

	public Collection<Player> getOnlineMembers() {
		List<Player> players = new ArrayList<>();

		for (UUID member : getMembers()) {
			Player exactPlayer = HCF.getInstance().getServer().getPlayer(member);

			if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
				players.add(exactPlayer);
			}
		}

		return (players);
	}

	public Collection<UUID> getOfflineMembers() {
		List<UUID> players = new ArrayList<>();

		for (UUID member : getMembers()) {
			Player exactPlayer = HCF.getInstance().getServer().getPlayer(member);

			if (exactPlayer == null || exactPlayer.hasMetadata("invisible")) {
				players.add(member);
			}
		}

		return (players);
	}

	public Subclaim getSubclaim(String name) {
		for (Subclaim subclaim : subclaims) {
			if (subclaim.getName().equalsIgnoreCase(name)) {
				return (subclaim);
			}
		}

		return (null);
	}

	public Subclaim getSubclaim(Location location) {
		for (Subclaim subclaim : subclaims) {
			if (new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2()).contains(location)) {
				return (subclaim);
			}
		}

		return (null);
	}

	public int getSize() {
		return (getMembers().size());
	}

	public boolean isRaidable() {
		return (DTR <= 0);
	}

	public void playerDeath(String playerName, double dtrLoss) {
		double newDTR = Math.max(DTR - dtrLoss, -.99);

		TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_DEATH, ImmutableMap.of(
				"playerName", playerName,
				"dtrLoss", dtrLoss,
				"oldDtr", DTR,
				"newDtr", newDTR
		));

		for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
			if (isMember(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "Member Death: " + ChatColor.WHITE + playerName);
				player.sendMessage(ChatColor.RED + "DTR: " + ChatColor.WHITE + DTR_FORMAT.format(newDTR));
			}
		}

		HCF.getInstance().getLogger().info("[TeamDeath] " + name + " > " + "Player death: [" + playerName + "]");
		setDTR(newDTR);

		long cooldown = 0;

		if (isRaidable()) {
			TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
			if (getRegenUpgrades() == RegenUpgrades.DEFAULT) {
				cooldown = System.currentTimeMillis() + HCF.getInstance().getMapHandler().getRegenTimeRaidable();
			} else if (getRegenUpgrades() == RegenUpgrades.THIRTYFIVE) {
				cooldown = System.currentTimeMillis() + JavaUtils.parse("35m");
			} else if (getRegenUpgrades() == RegenUpgrades.THIRTY) {
				cooldown = System.currentTimeMillis() + JavaUtils.parse("30m");
			} else if (getRegenUpgrades() == RegenUpgrades.TWENTYFIVE) {
				cooldown = System.currentTimeMillis() + JavaUtils.parse("25m");
			} else {
				cooldown = System.currentTimeMillis() + HCF.getInstance().getMapHandler().getRegenTimeRaidable();
			}
		} else {
			if (getRegenUpgrades() == RegenUpgrades.DEFAULT) {
				cooldown = System.currentTimeMillis() + HCF.getInstance().getMapHandler().getRegenTimeDeath();
			} else if (getRegenUpgrades() == RegenUpgrades.THIRTYFIVE) {
				cooldown = System.currentTimeMillis() + JavaUtils.parse("35m");
			} else if (getRegenUpgrades() == RegenUpgrades.THIRTY) {
				cooldown = System.currentTimeMillis() + JavaUtils.parse("30m");
			} else if (getRegenUpgrades() == RegenUpgrades.TWENTYFIVE) {
				cooldown = System.currentTimeMillis() + JavaUtils.parse("25m");
			} else {
				cooldown = System.currentTimeMillis() + HCF.getInstance().getMapHandler().getRegenTimeDeath();
			}
		}

		DTRCooldown = cooldown;

		DTRHandler.markOnDTRCooldown(this);
	}

	public double getDTRIncrement() {
		return (getDTRIncrement(getOnlineMemberAmount()));
	}

	public double getDTRIncrement(int playersOnline, boolean offline) {
		double dtrPerHour = DTRHandler.getMaxDTR(getSize());
		return (dtrPerHour / 60);
	}

	public double getDTRIncrement(int playersOnline) {
		return getDTRIncrement(playersOnline, false);
	}

	public double getMaxDTR(boolean offline) {
		return (DTRHandler.getMaxDTR(getSize()));
	}


	public double getMaxDTR() {
		return (DTRHandler.getMaxDTR(getSize()));
	}

	public void load(BasicDBObject obj) {
		loading = true;
		setUniqueId(obj.getObjectId("_id"));
		setOwner(obj.getString("Owner") == null ? null : UUID.fromString(obj.getString("Owner")));
		if (obj.containsKey("CoLeaders"))
			for (Object coLeader : (BasicDBList) obj.get("CoLeaders")) addCoLeader(UUID.fromString((String) coLeader));
		if (obj.containsKey("Captains"))
			for (Object captain : (BasicDBList) obj.get("Captains")) addCaptain(UUID.fromString((String) captain));
		if (obj.containsKey("Members"))
			for (Object member : (BasicDBList) obj.get("Members")) addMember(UUID.fromString((String) member));
		if (obj.containsKey("Invitations")) for (Object invite : (BasicDBList) obj.get("Invitations"))
			getInvitations().add(UUID.fromString((String) invite));
		if (obj.containsKey("DTR")) setDTR(obj.getDouble("DTR"));
		if (obj.containsKey("DTRCooldown")) setDTRCooldown(obj.getDate("DTRCooldown").getTime());
		if (obj.containsKey("Balance")) setBalance(obj.getDouble("Balance"));
		if (obj.containsKey("Points")) setPoints((int) obj.getDouble("Points"));
		if (obj.containsKey("MaxOnline")) setMaxOnline(obj.getInt("MaxOnline"));
		if (obj.containsKey("HQ")) setHq(LocationSerializer.deserialize((BasicDBObject) obj.get("HQ")));
		if (obj.containsKey("Announcement")) setAnnouncement(obj.getString("Announcement"));
		if (obj.containsKey("BardUpgrades")) setBardUpgrades(obj.getString("BardUpgrades"));
		if (obj.containsKey("RegenUpgrades")) setDTRRegenUpgrades(obj.getString("RegenUpgrades"));
		if (obj.containsKey("MeteorUpgrades")) setMeteorUpgrades(obj.getString("MeteorUpgrades"));
		if (obj.containsKey("PowerFaction")) setPowerFaction(obj.getBoolean("PowerFaction"));
		if (obj.containsKey("Lives")) setLives(obj.getInt("Lives"));
		if (obj.containsKey("Meteors")) setMeteors(obj.getInt("Meteors"));
		if (obj.containsKey("Claims"))
			for (Object claim : (BasicDBList) obj.get("Claims")) getClaims().add(Claim.fromJson((BasicDBObject) claim));
		if (obj.containsKey("Subclaims")) for (Object subclaim : (BasicDBList) obj.get("Subclaims"))
			getSubclaims().add(Subclaim.fromJson((BasicDBObject) subclaim));
		if (obj.containsKey("SpentPoints")) setSpentPoints(obj.getInt("SpentPoints"));

		loading = false;
	}

	public void load(String str) {
		load(str, false);
	}

	public void load(String str, boolean forceSave) {
		loading = true;
		String[] lines = str.split("\n");

		for (String line : lines) {
			if (line.indexOf(':') == -1) {
				System.out.println("Found an invalid line... `" + line + "`");
				continue;
			}

			String identifier = line.substring(0, line.indexOf(':'));
			String[] lineParts = line.substring(line.indexOf(':') + 1).split(",");

			if (identifier.equalsIgnoreCase("Owner")) {
				if (!lineParts[0].equals("null")) {
					setOwner(UUID.fromString(lineParts[0].trim()));
				}
			} else if (identifier.equalsIgnoreCase("UUID")) {
				uniqueId = new ObjectId(lineParts[0].trim());
			} else if (identifier.equalsIgnoreCase("Members")) {
				for (String name : lineParts) {
					if (name.length() >= 2) {
						addMember(UUID.fromString(name.trim()));
					}
				}
			} else if (identifier.equalsIgnoreCase("CoLeaders")) {
				for (String name : lineParts) {
					if (name.length() >= 2) {
						addCoLeader(UUID.fromString(name.trim()));
					}
				}
			} else if (identifier.equalsIgnoreCase("Captains")) {
				for (String name : lineParts) {
					if (name.length() >= 2) {
						addCaptain(UUID.fromString(name.trim()));
					}
				}
			} else if (identifier.equalsIgnoreCase("Invited")) {
				for (String name : lineParts) {
					if (name.length() >= 2) {
						getInvitations().add(UUID.fromString(name.trim()));
					}
				}
			} else if (identifier.equalsIgnoreCase("HistoricalMembers")) {
				for (String name : lineParts) {
					if (name.length() >= 2) {
						getHistoricalMembers().add(UUID.fromString(name.trim()));
					}
				}
			} else if (identifier.equalsIgnoreCase("HQ")) {
				setHq(parseLocation(lineParts));
			} else if (identifier.equalsIgnoreCase("DTR")) {
				setDTR(Double.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Balance")) {
				setBalance(Double.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("MaxOnline")) {
				setMaxOnline(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("ForceInvites")) {
				setForceInvites(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("DTRCooldown")) {
				setDTRCooldown(Long.parseLong(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("FriendlyName")) {
				setName(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("Claims")) {
				for (String claim : lineParts) {
					claim = claim.replace("[", "").replace("]", "");

					if (claim.contains(":")) {
						String[] split = claim.split(":");

						int x1 = Integer.parseInt(split[0].trim());
						int y1 = Integer.parseInt(split[1].trim());
						int z1 = Integer.parseInt(split[2].trim());
						int x2 = Integer.parseInt(split[3].trim());
						int y2 = Integer.parseInt(split[4].trim());
						int z2 = Integer.parseInt(split[5].trim());
						String name = split[6].trim();
						String world = split[7].trim();

						Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
						claimObj.setName(name);

						getClaims().add(claimObj);
					}
				}
			} else if (identifier.equalsIgnoreCase("Allies")) {
				// Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
				if (HCF.getInstance().getMapHandler().getAllyLimit() == 0) {
					continue;
				}

				for (String ally : lineParts) {
					ally = ally.replace("[", "").replace("]", "");

					if (ally.length() != 0) {
						allies.add(new ObjectId(ally.trim()));
					}
				}
			} else if (identifier.equalsIgnoreCase("RequestedAllies")) {
				// Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
				if (HCF.getInstance().getMapHandler().getAllyLimit() == 0) {
					continue;
				}

				for (String requestedAlly : lineParts) {
					requestedAlly = requestedAlly.replace("[", "").replace("]", "");

					if (requestedAlly.length() != 0) {
						requestedAllies.add(new ObjectId(requestedAlly.trim()));
					}
				}
			} else if (identifier.equalsIgnoreCase("Subclaims")) {
				for (String subclaim : lineParts) {
					subclaim = subclaim.replace("[", "").replace("]", "");

					if (subclaim.contains(":")) {
						String[] split = subclaim.split(":");

						int x1 = Integer.parseInt(split[0].trim());
						int y1 = Integer.parseInt(split[1].trim());
						int z1 = Integer.parseInt(split[2].trim());
						int x2 = Integer.parseInt(split[3].trim());
						int y2 = Integer.parseInt(split[4].trim());
						int z2 = Integer.parseInt(split[5].trim());
						String name = split[6].trim();
						String membersRaw = "";

						if (split.length >= 8) {
							membersRaw = split[7].trim();
						}

						Location location1 = new Location(HCF.getInstance().getServer().getWorld("world"), x1, y1, z1);
						Location location2 = new Location(HCF.getInstance().getServer().getWorld("world"), x2, y2, z2);
						List<UUID> members = new ArrayList<>();

						for (String uuidString : membersRaw.split(", ")) {
							if (uuidString.isEmpty()) {
								continue;
							}

							members.add(UUID.fromString(uuidString.trim()));
						}

						Subclaim subclaimObj = new Subclaim(location1, location2, name);
						subclaimObj.setMembers(members);

						getSubclaims().add(subclaimObj);
					}
				}
			} else if (identifier.equalsIgnoreCase("Announcement")) {
				setAnnouncement(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("RegenUpgrades")) {
				setDTRRegenUpgrades(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("MeteorUpgrades")) {
				setMeteorUpgrades(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("BardUpgrades")) {
				setBardUpgrades(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("PowerFaction")) {
				setPowerFaction(Boolean.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Lives")) {
				setLives(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Meteors")) {
				setMeteors(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Kills")) {
				setKills(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Deaths")) {
				setDeaths(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("KothCaptures")) {
				setKothCaptures(Integer.valueOf(lineParts[0]));
			} /*else if (identifier.equalsIgnoreCase("DiamondsMined")) {
                setDiamondsMined(Integer.valueOf(lineParts[0]));
            } */ else if (identifier.equalsIgnoreCase("CitadelsCapped")) {
				setCitadelsCapped(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("KillstreakPoints")) {
				setKillstreakPoints(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Points")) {
				setPoints(Integer.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("SpentPoints")) {
				setSpentPoints(Integer.valueOf(lineParts[0]));
			}
		}

		for (UUID member : members) {
			FrozenUUIDCache.ensure(member);
		}

		if (uniqueId == null) {
			uniqueId = new ObjectId();
			HCF.getInstance().getLogger().info("Generating UUID for team " + getName() + "...");
		}
		setTeamColor(ChatUtils.randomChatColor());
		loading = false;
		needsSave = forceSave;
	}

	public String saveString(boolean toJedis) {

		if (toJedis) {
			needsSave = false;
		}

		if (loading) {
			return (null);
		}

		StringBuilder teamString = new StringBuilder();

		StringBuilder members = new StringBuilder();
		StringBuilder captains = new StringBuilder();
		StringBuilder coleaders = new StringBuilder();
		StringBuilder invites = new StringBuilder();
		StringBuilder historicalMembers = new StringBuilder();

		for (UUID member : getMembers()) {
			members.append(member.toString()).append(", ");
		}

		for (UUID captain : getCaptains()) {
			captains.append(captain.toString()).append(", ");
		}

		for (UUID co : getColeaders()) {
			coleaders.append(co.toString()).append(", ");
		}

		for (UUID invite : getInvitations()) {
			invites.append(invite.toString()).append(", ");
		}

		for (UUID member : getHistoricalMembers()) {
			historicalMembers.append(member.toString()).append(", ");
		}

		if (members.length() > 2) {
			members.setLength(members.length() - 2);
		}

		if (captains.length() > 2) {
			captains.setLength(captains.length() - 2);
		}

		if (invites.length() > 2) {
			invites.setLength(invites.length() - 2);
		}

		if (historicalMembers.length() > 2) {
			historicalMembers.setLength(historicalMembers.length() - 2);
		}

		teamString.append("UUID:").append(getUniqueId().toString()).append("\n");
		teamString.append("Owner:").append(getOwner()).append('\n');
		teamString.append("CoLeaders:").append(coleaders.toString()).append('\n');
		teamString.append("Captains:").append(captains.toString()).append('\n');
		teamString.append("Members:").append(members.toString()).append('\n');
		teamString.append("Invited:").append(invites.toString().replace("\n", "")).append('\n');
		teamString.append("Subclaims:").append(getSubclaims().toString().replace("\n", "")).append('\n');
		teamString.append("Claims:").append(getClaims().toString().replace("\n", "")).append('\n');
		teamString.append("Allies:").append(getAllies().toString()).append('\n');
		teamString.append("RequestedAllies:").append(getRequestedAllies().toString()).append('\n');
		teamString.append("HistoricalMembers:").append(historicalMembers.toString()).append('\n');
		teamString.append("DTR:").append(getDTR()).append('\n');
		teamString.append("Balance:").append(getBalance()).append('\n');
		teamString.append("MaxOnline:").append(getMaxOnline()).append('\n');
		teamString.append("ForceInvites:").append(getForceInvites()).append('\n');
		teamString.append("DTRCooldown:").append(getDTRCooldown()).append('\n');
		teamString.append("FriendlyName:").append(getName().replace("\n", "")).append('\n');
		teamString.append("Announcement:").append(String.valueOf(getAnnouncement()).replace("\n", "")).append("\n");
		teamString.append("PowerFaction:").append(isPowerFaction()).append("\n");
		teamString.append("Lives:").append(getLives()).append("\n");
		teamString.append("Meteors:").append(getMeteors()).append("\n");
		teamString.append("Kills:").append(getKills()).append("\n");
		teamString.append("Deaths:").append(getDeaths()).append("\n");
		teamString.append("DiamondsMined:").append(getDiamondsMined()).append("\n");
		teamString.append("KothCaptures:").append(getKothCaptures()).append("\n");
		teamString.append("CitadelsCapped:").append(getCitadelsCapped()).append("\n");
		teamString.append("KillstreakPoints:").append(getKillstreakPoints()).append("\n");
		teamString.append("Points:").append(getPoints()).append("\n");
		teamString.append("SpentPoints:").append(getSpentPoints()).append("\n");
		teamString.append("Points:").append(getPoints()).append('\n');


		if (getHq() != null) {
			teamString.append("HQ:").append(getHq().getWorld().getName()).append(",").append(getHq().getX()).append(",").append(getHq().getY()).append(",").append(getHq().getZ()).append(",").append(getHq().getYaw()).append(",").append(getHq().getPitch()).append('\n');
		}

		return (teamString.toString());
	}

	public BasicDBObject toJSON() {
		BasicDBObject dbObject = new BasicDBObject();

		dbObject.put("Owner", getOwner() == null ? null : getOwner().toString());
		dbObject.put("CoLeaders", UUIDUtils.uuidsToStrings(getColeaders()));
		dbObject.put("Captains", UUIDUtils.uuidsToStrings(getCaptains()));
		dbObject.put("Members", UUIDUtils.uuidsToStrings(getMembers()));
		dbObject.put("Invitations", UUIDUtils.uuidsToStrings(getInvitations()));
		dbObject.put("Allies", getAllies());
		dbObject.put("RequestedAllies", getRequestedAllies());
		dbObject.put("DTR", getDTR());
		dbObject.put("DTRCooldown", new Date(getDTRCooldown()));
		dbObject.put("Balance", getBalance());
		dbObject.put("MaxOnline", getMaxOnline());
		dbObject.put("Name", getName());
		dbObject.put("HQ", LocationSerializer.serialize(getHq()));
		dbObject.put("Announcement", getAnnouncement());
		dbObject.put("PowerFaction", isPowerFaction());
		dbObject.put("Lives", getLives());
		dbObject.put("Meteors", getMeteors());
		dbObject.put("Points", getPoints());

		BasicDBList claims = new BasicDBList();
		BasicDBList subclaims = new BasicDBList();

		for (Claim claim : getClaims()) {
			claims.add(claim.json());
		}

		for (Subclaim subclaim : getSubclaims()) {
			subclaims.add(subclaim.json());
		}

		dbObject.put("Claims", claims);
		dbObject.put("Subclaims", subclaims);
		dbObject.put("Kills", this.kills);
		dbObject.put("Deaths", this.deaths);
		dbObject.put("DiamondsMined", this.diamondsMined);
		dbObject.put("CitadelsCaptured", this.citadelsCapped);
		dbObject.put("KillstreakPoints", this.killstreakPoints);
		dbObject.put("KothCaptures", this.kothCaptures);
		dbObject.put("Points", this.points);
		dbObject.put("SpentPoints", this.spentPoints);

		return (dbObject);
	}

	public BasicDBObject getJSONIdentifier() {
		return (new BasicDBObject("_id", getUniqueId().toHexString()));
	}

	private Location parseLocation(String[] args) {
		if (args.length != 6) {
			return (null);
		}

		World world = HCF.getInstance().getServer().getWorld(args[0]);
		double x = Double.parseDouble(args[1]);
		double y = Double.parseDouble(args[2]);
		double z = Double.parseDouble(args[3]);
		float yaw = Float.parseFloat(args[4]);
		float pitch = Float.parseFloat(args[5]);

		return (new Location(world, x, y, z, yaw, pitch));
	}

	public void sendMessage(String message) {
		for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
			if (isMember(player.getUniqueId())) {
				player.sendMessage(message);
			}
		}
	}

	public void sendTeamInfo(Player player) {
		// Don't make our null teams have DTR....
		// @HCFactions
		if (getOwner() == null) {
			player.sendMessage(GRAY_LINE);
			player.sendMessage(getName(player));

			if (hq != null && hq.getWorld().getEnvironment() != World.Environment.NORMAL) {
				String world = hq.getWorld().getEnvironment() == World.Environment.NETHER ? "Nether" : "End"; // if it's not the nether, it's the end
				player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (hq == null ? "None" : hq.getBlockX() + ", " + hq.getBlockZ() + " (" + world + ")"));
			} else {
				player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (hq == null ? "None" : hq.getBlockX() + ", " + hq.getBlockZ()));
			}

			if (getName().equalsIgnoreCase("Citadel")) {
				Set<ObjectId> cappers = HCF.getInstance().getCitadelHandler().getCappers();
				Set<String> capperNames = new HashSet<>();

				for (ObjectId capper : cappers) {
					Team capperTeam = HCF.getInstance().getTeamHandler().getTeam(capper);

					if (capperTeam != null) {
						capperNames.add(capperTeam.getName());
					}
				}

				if (!cappers.isEmpty()) {
					player.sendMessage(ChatColor.YELLOW + "Currently captured by: " + ChatColor.RED + Joiner.on(", ").join(capperNames));
				}
			}

			player.sendMessage(GRAY_LINE);
			return;
		}

		StatsHandler statsHandler = HCF.getInstance().getMapHandler().getStatsHandler();
		DeathbanMap deathbanMap = HCF.getInstance().getDeathbanMap();
		Player owner = HCF.getInstance().getServer().getPlayer(getOwner());
		StringBuilder allies = new StringBuilder();

		FancyMessage coleadersJson = new FancyMessage("Co-Leaders: ").color(ChatColor.YELLOW);

		FancyMessage captainsJson = new FancyMessage("Captains: ").color(ChatColor.YELLOW);

		if (player.hasPermission("foxtrot.manage")) {
			captainsJson.command("/manageteam demote " + getName()).tooltip("§bClick to demote captains");
		}

		FancyMessage membersJson = new FancyMessage("Members: ").color(ChatColor.YELLOW);

		if (player.hasPermission("foxtrot.manage")) {
			membersJson.command("/manageteam promote " + getName()).tooltip("§bClick to promote members");
		}

		int onlineMembers = 0;

		for (ObjectId allyId : getAllies()) {
			Team ally = HCF.getInstance().getTeamHandler().getTeam(allyId);

			if (ally != null) {
				allies.append(ally.getName(player)).append(ChatColor.YELLOW).append("[").append(ChatColor.GREEN).append(ally.getOnlineMemberAmount()).append("/").append(ally.getSize()).append(ChatColor.YELLOW).append("]").append(ChatColor.GRAY).append(", ");
			}
		}


		for (Player onlineMember : getOnlineMembers()) {
			onlineMembers++;

			// There can only be one owner, so we special case it.
			if (isOwner(onlineMember.getUniqueId())) {
				continue;
			}

			FancyMessage appendTo = membersJson;
			if (isCoLeader(onlineMember.getUniqueId())) {
				appendTo = coleadersJson;
			} else if (isCaptain(onlineMember.getUniqueId())) {
				appendTo = captainsJson;
			}

			if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
				appendTo.then(", ").color(ChatColor.GRAY);
			}

			appendTo.then(onlineMember.getName()).color(ChatColor.GREEN).then("[").color(ChatColor.YELLOW);
			appendTo.then(statsHandler.getStats(onlineMember.getUniqueId()).getKills() + "").color(ChatColor.GREEN);
			appendTo.then("]").color(ChatColor.YELLOW);
		}

		for (UUID offlineMember : getOfflineMembers()) {
			if (isOwner(offlineMember)) {
				continue;
			}

			FancyMessage appendTo = membersJson;
			if (isCoLeader(offlineMember)) {
				appendTo = coleadersJson;
			} else if (isCaptain(offlineMember)) {
				appendTo = captainsJson;
			}

			if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
				appendTo.then(", ").color(ChatColor.GRAY);
			}

			appendTo.then(UUIDUtils.name(offlineMember)).color(deathbanMap.isDeathbanned(offlineMember) ? ChatColor.RED : ChatColor.GRAY);
			appendTo.then("[").color(ChatColor.YELLOW).then("" + statsHandler.getStats(offlineMember).getKills()).color(ChatColor.GREEN);
			appendTo.then("]").color(ChatColor.YELLOW);

		}

		// Now we can actually send all that info we just processed.
		player.sendMessage(GRAY_LINE);

		FancyMessage teamLine = new FancyMessage();

		teamLine.text(ChatColor.BLUE + getName());
		teamLine.then().text(ChatColor.GRAY + " [" + onlineMembers + "/" + getSize() + "]" + ChatColor.DARK_AQUA + " - ");
		teamLine.then().text(ChatColor.YELLOW + "HQ: " + ChatColor.WHITE + (hq == null ? "None" : hq.getBlockX() + ", " + hq.getBlockZ()));

		if (hq != null && player.hasPermission("gravity.staff")) {
			teamLine.command("/tppos " + hq.getBlockX() + " " + hq.getBlockY() + " " + hq.getBlockZ());
			teamLine.tooltip("§aClick to warp to HQ");
		}

		teamLine.then().text("§3 - §e[Focus]").color(ChatColor.YELLOW).command("/f focus " + getName()).tooltip("§bClick to focus team");

		if (player.hasPermission("foxtrot.manage")) {
			teamLine.then().text("§3 - §e[Manage]").color(ChatColor.YELLOW).command("/manageteam manage " + getName()).tooltip("§bClick to manage team");
		}

		teamLine.send(player);

		if (allies.length() > 2) {
			allies.setLength(allies.length() - 2);
			player.sendMessage(ChatColor.YELLOW + "Allies: " + allies.toString());
		}

		FancyMessage leader = new FancyMessage(ChatColor.YELLOW + "Leader: " + (owner == null || owner.hasMetadata("invisible") ? (deathbanMap.isDeathbanned(getOwner()) ? ChatColor.RED : ChatColor.GRAY) : ChatColor.GREEN) + UUIDUtils.name(getOwner()) + ChatColor.YELLOW + "[" + ChatColor.GREEN + statsHandler.getStats(getOwner()).getKills() + ChatColor.YELLOW + "]");


		if (player.hasPermission("foxtrot.manage")) {
			leader.command("/manageteam leader " + getName()).tooltip("§bClick to change leader");
		}

		leader.send(player);

		if (!ChatColor.stripColor(coleadersJson.toOldMessageFormat()).endsWith("s: ")) {
			coleadersJson.send(player);
		}

		if (!ChatColor.stripColor(captainsJson.toOldMessageFormat()).endsWith("s: ")) {
			captainsJson.send(player);
		}


		if (!ChatColor.stripColor(membersJson.toOldMessageFormat()).endsWith("s: ")) {
			membersJson.send(player);
		}


		FancyMessage balance = new FancyMessage(ChatColor.YELLOW + "Balance: " + ChatColor.BLUE + "$" + Math.round(getBalance()));

		if (player.hasPermission("foxtrot.manage")) {
			balance.command("/manageteam balance " + getName()).tooltip("§bClick to modify team balance");
		}

		balance.send(player);


		FancyMessage dtrMessage = new FancyMessage(ChatColor.YELLOW + "Deaths until Raidable: " + getDTRColor() + DTR_FORMAT.format(getDTR()) + getDTRSuffix());


		if (player.hasPermission("foxtrot.manage")) {
			dtrMessage.command("/manageteam dtr " + getName()).tooltip("§bClick to modify team DTR");
		}

		dtrMessage.send(player);

		FancyMessage pointsMessage = new FancyMessage(CC.translate("§ePoints: §c" + getPoints()));

		if (player.hasPermission("foxtrot.manage")) {
			pointsMessage.command("/manageteam points " + getName()).tooltip("§bClick to modify team points");
		}
		pointsMessage.send(player);

		FancyMessage meteorsMessage = new FancyMessage(CC.translate("&eMeteors: §c" + getMeteors()));

		if (player.hasPermission("foxtrot.manage")) {
			meteorsMessage.tooltip("§bClick to modify team meteors").suggest("/setteammeteors <amount>");
		}
		meteorsMessage.send(player);

		if (isMember(player.getUniqueId()) || player.hasPermission("foxtrot.manage")) {
			if (HCF.getInstance().getServerHandler().isForceInvitesEnabled()) {
				player.sendMessage(ChatColor.YELLOW + "Force Invites: " + ChatColor.RED + getForceInvites());
			}
			player.sendMessage(ChatColor.YELLOW + "KOTH Captures: " + ChatColor.RED + getKothCaptures());
		}

		if (DTRHandler.isOnCooldown(this)) {
			if (!player.isOp()) {
				player.sendMessage(ChatColor.YELLOW + "Time Until Regen: " + ChatColor.BLUE + TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000).trim());
			} else {
				FancyMessage message = new FancyMessage(ChatColor.YELLOW + "Time Until Regen: ")
						.tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

				message.then(TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000)).color(ChatColor.BLUE)
						.tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

				message.send(player);
			}
		}

//		if (player.hasPermission("foxtrot.powerfactions")) {
//			FancyMessage powerFactionLine = new FancyMessage();
//			powerFactionLine.text(ChatColor.YELLOW + "Power Faction: ");
//			if (isPowerFaction()) {
//				powerFactionLine.then().text(ChatColor.GREEN + "True");
//				powerFactionLine.command("/powerfaction remove " + getName());
//				powerFactionLine.tooltip("§bClick change faction to a non power faction.");
//			} else {
//				powerFactionLine.then().text(ChatColor.RED + "False");
//				powerFactionLine.command("/powerfaction add " + getName());
//				powerFactionLine.tooltip("§bClick change faction to a power faction.");
//			}
//			powerFactionLine.send(player);
//		}

		// Only show this if they're a member.
		if (isMember(player.getUniqueId()) && announcement != null && !announcement.equals("null")) {
			player.sendMessage(ChatColor.YELLOW + "Announcement: " + ChatColor.LIGHT_PURPLE + announcement);
		}

		player.sendMessage(GRAY_LINE);
		// .... and that is how we do a /f who.
	}


	@Override
	public int hashCode() {
		return uniqueId.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Team)) {
			return false;
		}

		Team other = (Team) obj;
		return other.uniqueId.equals(uniqueId);
	}

	public ChatColor getDTRColor() {
		ChatColor dtrColor = ChatColor.GREEN;

		if (DTR / getMaxDTR() <= 0.25) {
			if (isRaidable()) {
				dtrColor = ChatColor.DARK_RED;
			} else {
				dtrColor = ChatColor.YELLOW;
			}
		}

		return (dtrColor);
	}

	public String getDTRSuffix() {
		if (DTRHandler.isRegenerating(this)) {
			if (getOnlineMemberAmount() == 0) {
				return (ChatColor.GRAY + "◀");
			} else {
				return (ChatColor.GREEN + "▲");
			}
		} else if (DTRHandler.isOnCooldown(this)) {
			return (ChatColor.RED + "■");
		} else {
			return (ChatColor.GREEN + "◀");
		}
	}

	public String getDTRWithColor() {
		String dtrColored;
		double dtr = Double.parseDouble((new DecimalFormat("#.##")).format(getDTR()));
		if (dtr >= 1.01D) {
			dtrColored = ChatColor.GREEN + String.valueOf(dtr);
		} else if (dtr <= 0.0D) {
			dtrColored = ChatColor.RED + String.valueOf(dtr);
		} else {
			dtrColored = ChatColor.YELLOW + String.valueOf(dtr);
		}
		return dtrColored;
	}

	public Team getFactionFocused() {
		return this.factionFocused;
	}

	public void setFactionFocus(Team factionFocused) {
		this.factionFocused = factionFocused;
	}

	public boolean isOpen() {
		return this.open;
	}

	public void addPoints(int pointsToAdd) {
		this.setPoints(this.getPoints() + pointsToAdd);
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
}

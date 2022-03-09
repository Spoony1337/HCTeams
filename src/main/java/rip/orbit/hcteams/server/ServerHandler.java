package rip.orbit.hcteams.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.ItemUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.server.idle.IdleCheckRunnable;
import rip.orbit.hcteams.server.object.SellItem;
import rip.orbit.hcteams.server.uhc.UHCListener;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.Betrayer;
import rip.orbit.hcteams.util.Logout;
import rip.orbit.hcteams.util.item.InventoryUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerHandler {

	public static int WARZONE_RADIUS = 1000;
	public static int WARZONE_BORDER = 3000;

	// NEXT MAP //
	// http://minecraft.gamepedia.com/Potion#Data_value_table
	private Map<PotionType, PotionStatus> potionStatus = new HashMap<>();

	@Getter
	private static Map<String, Logout> tasks = new HashMap<>();

	@Getter
	private String networkName;
	@Getter
	private String serverName;
	@Getter
	private String networkWebsite;
	@Getter
	private String statsWebsiteRoot;

	@Getter
	private String tabServerName;
	@Getter
	private String tabSectionColor;
	@Getter
	private String tabInfoColor;

	@Getter
	private String sbSectionColor;
	@Getter
	private String sbTimeColor;

	@Getter
	private String eventMainColor;
	@Getter
	private String eventOtherColor;
	private static double MAX_DISTANCE = 1;

	@Getter
	private double startingBalance;
	private static List<String> damaged = Lists.newArrayList();
	@Getter
	private boolean squads;
	@Getter
	private boolean idleCheckEnabled;
	@Getter
	private boolean startingTimerEnabled;
	@Getter
	private boolean forceInvitesEnabled;
	@Getter
	private boolean uhcHealing;
	@Getter
	private boolean passiveTagEnabled;
	@Getter
	private boolean allowBoosting;
	@Getter
	private boolean waterPlacementInClaimsAllowed;
	@Getter
	private boolean blockRemovalEnabled;

	@Getter
	private boolean rodPrevention;
	@Getter
	private boolean skybridgePrevention;

	@Getter
	private boolean teamHQInEnemyClaims;

	@Getter
	private Set<Betrayer> betrayers = new HashSet<>();
	@Getter
	private Set<SellItem> sellItems = new HashSet<>();

	@Getter
	private static Map<String, Long> homeTimer = new ConcurrentHashMap<>();

	@Getter
	@Setter
	private boolean EOTW = false;
	@Getter
	@Setter
	private boolean PreEOTW = false;

	@Getter
	private boolean reduceArmorDamage;
	@Getter
	private boolean blockEntitiesThroughPortals;


	@Getter
	private ChatColor archerTagColor;
	@Getter
	private ChatColor stunTagColor;
	@Getter
	private ChatColor defaultRelationColor;

	@Getter
	private int maxProtection, maxPower, maxSharpness;

	@Getter
	private boolean velt;
	@Getter
	private boolean veltKitMap;

	@Getter
	private boolean hardcore;
	@Getter
	private boolean placeBlocksInCombat;

	@Getter
	private boolean customHelp;
	@Getter
	private String helpSectionColor, helpEntryColor, helpInfoColor;

	public ServerHandler() {
		try {
			File f = new File(HCF.getInstance().getDataFolder(), "betrayers.json");

			if (!f.exists()) {
				f.createNewFile();
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(f));

			if (dbo != null) {
				for (Map.Entry<String, Object> obj : dbo.entrySet()) {
					BasicDBObject details = (BasicDBObject) obj.getValue();
					betrayers.add(new Betrayer(
							UUID.fromString(obj.getKey()),
							UUID.fromString(details.getString("AddedBy")),
							details.getString("Reason"),
							details.getLong("Time"))
					);
				}
			}

			for (Betrayer betrayer : betrayers) {
				FrozenUUIDCache.ensure(betrayer.getUuid());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		networkName = HCF.getInstance().getConfig().getString("networkName");
		serverName = HCF.getInstance().getConfig().getString("serverName");
		networkWebsite = HCF.getInstance().getConfig().getString("networkWebsite");
		statsWebsiteRoot = HCF.getInstance().getConfig().getString("statsRoot");

		tabServerName = HCF.getInstance().getConfig().getString("tab.serverName");
		tabSectionColor = HCF.getInstance().getConfig().getString("tab.sectionColor");
		tabInfoColor = HCF.getInstance().getConfig().getString("tab.infoColor");

		sbSectionColor = HCF.getInstance().getConfig().getString("scoreboard.sectionColor", "&6");
		sbTimeColor = HCF.getInstance().getConfig().getString("scoreboard.timeColor", "&f");

		eventMainColor = ChatColor.translateAlternateColorCodes('&', HCF.getInstance().getConfig().getString("event.mainColor", "&3"));
		eventOtherColor = ChatColor.translateAlternateColorCodes('&', HCF.getInstance().getConfig().getString("event.otherColor", "&7"));

		startingBalance = HCF.getInstance().getConfig().getDouble("startingBalance", 500D);

		squads = HCF.getInstance().getConfig().getBoolean("squads");
		idleCheckEnabled = HCF.getInstance().getConfig().getBoolean("idleCheck");
		startingTimerEnabled = HCF.getInstance().getConfig().getBoolean("startingTimer");
		forceInvitesEnabled = HCF.getInstance().getConfig().getBoolean("forceInvites");
		uhcHealing = HCF.getInstance().getConfig().getBoolean("uhcHealing");
		passiveTagEnabled = HCF.getInstance().getConfig().getBoolean("passiveTag");
		allowBoosting = HCF.getInstance().getConfig().getBoolean("allowBoosting");
		waterPlacementInClaimsAllowed = HCF.getInstance().getConfig().getBoolean("waterPlacementInClaims");
		blockRemovalEnabled = HCF.getInstance().getConfig().getBoolean("blockRemoval");

		rodPrevention = HCF.getInstance().getConfig().getBoolean("rodPrevention", true);
		skybridgePrevention = HCF.getInstance().getConfig().getBoolean("skybridgePrevention", true);

		teamHQInEnemyClaims = HCF.getInstance().getConfig().getBoolean("teamHQInEnemyClaims", true);

		for (PotionType type : PotionType.values()) {
			if (type == PotionType.WATER) {
				continue;
			}

			PotionStatus status = new PotionStatus(HCF.getInstance().getConfig().getBoolean("potions." + type + ".drinkables"), HCF.getInstance().getConfig().getBoolean("potions." + type + ".splash"), HCF.getInstance().getConfig().getInt("potions." + type + ".maxLevel", -1));
			potionStatus.put(type, status);
		}

		if (idleCheckEnabled) {
			new IdleCheckRunnable().runTaskTimer(HCF.getInstance(), 60 * 20L, 60 * 20L);
		}

		if (uhcHealing) {
			Bukkit.getPluginManager().registerEvents(new UHCListener(), HCF.getInstance());
		}

		this.reduceArmorDamage = HCF.getInstance().getConfig().getBoolean("reduceArmorDamage", true);
		this.blockEntitiesThroughPortals = HCF.getInstance().getConfig().getBoolean("blockEntitiesThroughPortals", true);

		this.archerTagColor = ChatColor.valueOf(HCF.getInstance().getConfig().getString("archerTagColor", "YELLOW"));
		this.stunTagColor = ChatColor.valueOf(HCF.getInstance().getConfig().getString("stunTagColor", "BLUE"));
		this.defaultRelationColor = ChatColor.valueOf(HCF.getInstance().getConfig().getString("defaultRelationColor", "RED"));

		this.velt = HCF.getInstance().getConfig().getBoolean("velt", false);
		if (this.velt) {
			Bukkit.getLogger().info("Velt mode enabled!");
		}
		this.veltKitMap = HCF.getInstance().getConfig().getBoolean("veltKitMap", false);
		if (this.veltKitMap) {
			Bukkit.getLogger().info("Velt KitMap mode enabled!");
		}

		this.hardcore = HCF.getInstance().getConfig().getBoolean("hardcore", false);

		this.customHelp = HCF.getInstance().getConfig().getBoolean("help.custom", false);
		this.helpSectionColor = HCF.getInstance().getConfig().getString("help.section", "&6");
		this.helpEntryColor = HCF.getInstance().getConfig().getString("help.entry", "&e");
		this.helpInfoColor = HCF.getInstance().getConfig().getString("help.info", "&e");

		this.placeBlocksInCombat = HCF.getInstance().getConfig().getBoolean("placeBlocksInCombat", true);

		this.maxProtection = HCF.getInstance().getConfig().getInt("enchants.protection", 1);
		this.maxPower = HCF.getInstance().getConfig().getInt("enchants.power", 3);
		this.maxSharpness = HCF.getInstance().getConfig().getInt("enchants.sharpness", 1);

		registerPlayerDamageRestrictionListener();

		sellItems.add(new SellItem(125, Material.GOLD_BLOCK));
		sellItems.add(new SellItem(156.25, Material.DIAMOND_BLOCK));
		sellItems.add(new SellItem(175, Material.EMERALD_BLOCK));
		sellItems.add(new SellItem(93.75, Material.LAPIS_BLOCK));
		sellItems.add(new SellItem(95.75, Material.REDSTONE_BLOCK));
		sellItems.add(new SellItem(78.125, Material.IRON_BLOCK));
	}

	public void save() {

		try {
			File f = new File(HCF.getInstance().getDataFolder(), "betrayers.json");

			if (!f.exists()) {
				f.createNewFile();
			}

			BasicDBObject dbo = new BasicDBObject();

			for (Betrayer betrayer : betrayers) {
				BasicDBObject details = new BasicDBObject();
				details.put("AddedBy", betrayer.getAddedBy().toString());
				details.put("Reason", betrayer.getReason());
				details.put("Time", betrayer.getTime());
				dbo.put(betrayer.getUuid().toString(), details);
			}

			FileUtils.write(f, qLib.GSON.toJson(new JsonParser().parse(dbo.toString())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getEnchants() {
		if (Enchantment.DAMAGE_ALL.getMaxLevel() == 0 && Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() == 0) {
			return "No Enchants";
		} else {
			return "Prot " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + ", Sharp " + Enchantment.DAMAGE_ALL.getMaxLevel();
		}
	}

	public String getShortEnchants() {
		if (Enchantment.DAMAGE_ALL.getMaxLevel() == 0 && Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() == 0) {
			return "No Enchants";
		} else {
			return "P" + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + " S" + Enchantment.DAMAGE_ALL.getMaxLevel();
		}
	}

	public boolean isWarzone(Location loc) {
		if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
			return (false);
		}

		return (Math.abs(loc.getBlockX()) <= WARZONE_RADIUS && Math.abs(loc.getBlockZ()) <= WARZONE_RADIUS) || ((Math.abs(loc.getBlockX()) > WARZONE_BORDER || Math.abs(loc.getBlockZ()) > WARZONE_BORDER));
	}

	public boolean isSplashPotionAllowed(PotionType type) {
		return (!potionStatus.containsKey(type) || potionStatus.get(type).splash);
	}

	public boolean isDrinkablePotionAllowed(PotionType type) {
		return (!potionStatus.containsKey(type) || potionStatus.get(type).drinkables);
	}

	public boolean isPotionLevelAllowed(PotionType type, int amplifier) {
		return (!potionStatus.containsKey(type) || potionStatus.get(type).maxLevel == -1 || potionStatus.get(type).maxLevel >= amplifier);
	}

	public void startLogoutSequence(Player player) {
		player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Logging out... " + ChatColor.YELLOW + "Please wait" + ChatColor.RED + " 30" + ChatColor.YELLOW + " seconds.");

		BukkitTask taskid = new BukkitRunnable() {

			int seconds = 30;


			@Override
			public void run() {
				if (player.hasMetadata("frozen")) {
					player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
					cancel();
					return;
				}

				seconds--;
				//player.sendMessage(ChatColor.RED + "" + seconds + "§e seconds..."); // logout is now in the scoreboard, don't bother spamming them

				if (seconds == 0) {
					if (tasks.containsKey(player.getName())) {
						tasks.remove(player.getName());
						player.setMetadata("loggedout", new FixedMetadataValue(HCF.getInstance(), true));
						player.kickPlayer("§cYou have been safely logged out of the server!");
						cancel();
					}
				}

			}
		}.runTaskTimer(HCF.getInstance(), 20L, 20L);

		tasks.put(player.getName(), new Logout(taskid.getTaskId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)));
	}

	public void startSpawnteleport(Player player) {

		Location loc = player.getLocation();
		int xStart = (int) loc.getX();
		int yStart = (int) loc.getY();
		int zStart = (int) loc.getZ();
		player.sendMessage(ChatColor.RED.toString() + "You cannot teleport to spawn, you must travel there (0,0)");

		BukkitTask taskid = new BukkitRunnable() {

			int seconds = 100000;


			@Override
			public void run() {
				if (player.hasMetadata("frozen")) {
					player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Teleport " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
					cancel();
					return;
				}

				if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
					player.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE + " blocks, teleport cancelled!");
					cancel();
					return;
				}
				if (damaged.contains(player.getName())) {
					player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Teleport " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
					cancel();
					return;
				}

				seconds--;
				// player.sendMessage(ChatColor.RED + "" + seconds + "§e seconds..."); // logout is now in the scoreboard, don't bother spamming them

				if (seconds == 0) {
					player.teleport(HCF.getInstance().getServerHandler().getSpawnLocation());
				}
			}
		}.runTaskTimer(HCF.getInstance(), 20L, 20L);
	}

	public RegionData getRegion(Team ownerTo, Location location) {
		if (ownerTo != null && ownerTo.getOwner() == null) {
			if (ownerTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
				return (new RegionData(RegionType.SPAWN, ownerTo));
			} else if (ownerTo.hasDTRBitmask(DTRBitmask.KOTH)) {
				return (new RegionData(RegionType.KOTH, ownerTo));
			} else if (ownerTo.hasDTRBitmask(DTRBitmask.CITADEL)) {
				return (new RegionData(RegionType.CITADEL, ownerTo));
			} else if (ownerTo.hasDTRBitmask(DTRBitmask.ROAD)) {
				return (new RegionData(RegionType.ROAD, ownerTo));
			} else if (ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
				return (new RegionData(RegionType.CONQUEST, ownerTo));
			}
		}

		if (ownerTo != null) {
			return (new RegionData(RegionType.CLAIMED_LAND, ownerTo));
		} else if (isWarzone(location)) {
			return (new RegionData(RegionType.WARZONE, null));
		}

		return (new RegionData(RegionType.WILDNERNESS, null));
	}

	public boolean isUnclaimed(Location loc) {
		return (LandBoard.getInstance().getClaim(loc) == null && !isWarzone(loc));
	}

	public boolean isAdminOverride(Player player) {
		return (player.getGameMode() == GameMode.CREATIVE);
	}

	public Location getSpawnLocation() {
		return (HCF.getInstance().getServer().getWorld("world").getSpawnLocation().add(new Vector(0.5, 1, 0.5)));
	}

	public boolean isUnclaimedOrRaidable(Location loc) {
		Team owner = LandBoard.getInstance().getTeam(loc);
		return (owner == null || owner.isRaidable());
	}

	public double getDTRLoss(Player player) {
		return (getDTRLoss(player.getLocation()));
	}

	public double getDTRLoss(Location location) {
		double dtrLoss = 1.00D;

		if (HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap()) {
			dtrLoss = Math.min(dtrLoss, 0.75D);
		}

		Team ownerTo = LandBoard.getInstance().getTeam(location);
		if (HCF.getInstance().getConquestHandler().getGame() != null && location.getWorld().getEnvironment() == Environment.THE_END && ownerTo != null && ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
			dtrLoss = Math.min(dtrLoss, 0.50D);
		}

		if (HCF.getInstance().getConfig().getBoolean("legions")) {
			if (location.getWorld().getEnvironment() == Environment.THE_END) {
				dtrLoss = Math.min(dtrLoss, 0.50D);
			} else if (location.getWorld().getEnvironment() == Environment.NETHER) {
				dtrLoss = Math.min(dtrLoss, 0.75D);
			}
		}

		if (ownerTo != null) {
			if (ownerTo.hasDTRBitmask(DTRBitmask.QUARTER_DTR_LOSS)) {
				dtrLoss = Math.min(dtrLoss, 0.25D);
			} else if (ownerTo.hasDTRBitmask(DTRBitmask.REDUCED_DTR_LOSS)) {
				dtrLoss = Math.min(dtrLoss, 0.75D);
			}
		}

		return (dtrLoss);
	}

	public long getDeathban(Player player) {
		return (getDeathban(player.getUniqueId(), player.getLocation()));
	}

	public Betrayer getBetrayer(UUID uuid) {
		for (Betrayer betrayer : betrayers) {
			if (uuid.equals(betrayer.getUuid())) {
				return betrayer;
			}
		}

		return null;
	}


	public long getDeathban(UUID playerUUID, Location location) {
		// Things we already know and can easily eliminate.
		if (isPreEOTW()) {
			return (TimeUnit.DAYS.toSeconds(1000));
		} else if (HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap()) {
			return (TimeUnit.SECONDS.toSeconds(5));
		} else if (getBetrayer(playerUUID) != null) {
			return (TimeUnit.DAYS.toSeconds(1));
		}

		Team ownerTo = LandBoard.getInstance().getTeam(location);
		Player player = HCF.getInstance().getServer().getPlayer(playerUUID); // Used in various checks down below.

		// Check DTR flags, which will also take priority over playtime.
		if (ownerTo != null && ownerTo.getOwner() == null) {
			Event linkedKOTH = HCF.getInstance().getEventHandler().getEvent(ownerTo.getName());

			// Only respect the reduced deathban if
			// The KOTH is non-existant (in which case we're probably
			// something like a 1v1 arena) or it is active.
			// If it's there but not active,
			// the null check will be false, the .isActive will be false, so we'll ignore
			// the reduced DB check.
			if (linkedKOTH == null || linkedKOTH.isActive()) {
				if (ownerTo.hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN)) {
					return (TimeUnit.MINUTES.toSeconds(5));
				} else if (ownerTo.hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN)) {
					return (TimeUnit.MINUTES.toSeconds(15));
				}
			}
		}

		int max = Deathban.getDeathbanSeconds(player);

		long ban = HCF.getInstance().getPlaytimeMap().getPlaytime(playerUUID);

		if (player != null && HCF.getInstance().getPlaytimeMap().hasPlayed(playerUUID)) {
			ban += HCF.getInstance().getPlaytimeMap().getCurrentSession(playerUUID) / 1000L;
		}

		return (Math.min(max, ban));
	}

	public void beginHQWarp(Player player, Team team, int warmup, boolean charge) {
		Team inClaim = LandBoard.getInstance().getTeam(player.getLocation());

		// quick fix
		if (team.getBalance() < 0) {
			team.setBalance(0);
		}

		if (inClaim != null) {
			if (HCF.getInstance().getServerHandler().isHardcore() && inClaim.getOwner() != null && !inClaim.isMember(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You may not go to your team HQ from an enemy's claim! Use '/team stuck' first.");
				return;
			}

			if (inClaim.getOwner() == null && (inClaim.hasDTRBitmask(DTRBitmask.KOTH) || inClaim.hasDTRBitmask(DTRBitmask.CITADEL))) {
				player.sendMessage(ChatColor.RED + "You may not go to your team HQ from inside of events!");
				return;
			}

			if (inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
				if (player.getWorld().getEnvironment() != Environment.THE_END) {
					player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
					player.teleport(team.getHq());
				} else {
					player.sendMessage(ChatColor.RED + "You cannot teleport to your HQ  while you're in end spawn!");
				}
				return;
			}
		}


		if (SpawnTagHandler.isTagged(player)) {
			player.sendMessage(ChatColor.RED + "You may not go to your team headquarters while spawn tagged!");
			return;
		}

		boolean isSpawn = inClaim != null && inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE);

		if (charge && !isSpawn) {
			team.setBalance(team.getBalance() - (HCF.getInstance().getServerHandler().isHardcore() ? 20 : 50));
		}

		player.sendMessage(ChatColor.YELLOW + "Teleporting to your team's HQ in " + ChatColor.LIGHT_PURPLE + warmup + " seconds" + ChatColor.YELLOW + "... Stay still and do not take damage.");

		/**
		 * Give player heads up now. They should have 10 seconds to move even just an inch to cancel the tp if they want
		 */
		if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "Your PvP Timer will be removed if the teleport is not cancelled.");
		}

		homeTimer.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(warmup));

		int finalWarmup = warmup;

		new BukkitRunnable() {

			int time = finalWarmup;
			Location startLocation = player.getLocation();
			double startHealth = player.getHealth();


			@Override
			public void run() {
				time--;

				if (!player.getLocation().getWorld().equals(startLocation.getWorld()) || player.getLocation().distanceSquared(startLocation) >= 0.1 || player.getHealth() < startHealth) {
					player.sendMessage(ChatColor.YELLOW + "Teleport cancelled.");
					homeTimer.remove(player.getName());
					cancel();
					return;
				}

				// Reset their previous health, so players can't start on 1/2 a heart, splash, and then be able to take damage before warping.
				startHealth = player.getHealth();

				// Prevent server lag from making the home time inaccurate.
				if (homeTimer.containsKey(player.getName()) && homeTimer.get(player.getName()) <= System.currentTimeMillis()) {
					if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
						HCF.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
					}

					for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
						if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
							enderPearl.remove();
						}
					}

					player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
					player.teleport(team.getHq());
					homeTimer.remove(player.getName());
					cancel();
					return;
				}

				if (time == 0) {
					// Remove their PvP timer.
					if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
						HCF.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
					}

					for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
						if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
							enderPearl.remove();
						}
					}

					player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.RED + team.getName() + ChatColor.YELLOW + "'s HQ.");
					player.teleport(team.getHq());
					homeTimer.remove(player.getName());
					cancel();
				}
			}

		}.runTaskTimer(HCF.getInstance(), 20L, 20L);
	}

	private Map<UUID, Long> playerDamageRestrictMap = Maps.newHashMap();

	public void disablePlayerAttacking(Player player, int seconds) {
		if (seconds == 10) {
			player.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
		}

		playerDamageRestrictMap.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000));
	}

	private void registerPlayerDamageRestrictionListener() {
		HCF.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler(ignoreCancelled = true)
			public void onDamage(EntityDamageByEntityEvent event) {
				Long expiry = playerDamageRestrictMap.get(event.getDamager().getUniqueId());
				if (expiry != null && System.currentTimeMillis() < expiry) {
					event.setCancelled(true);
				}
			}

			@EventHandler
			public void onQuit(PlayerQuitEvent event) {
				playerDamageRestrictMap.remove(event.getPlayer().getUniqueId());
			}
		}, HCF.getInstance());
	}

	public boolean isSpawnBufferZone(Location loc) {
		if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
			return (false);
		}

		int radius = HCF.getInstance().getMapHandler().getWorldBuffer();
		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		return ((x < radius && x > -radius) && (z < radius && z > -radius));
	}

	public boolean isNetherBufferZone(Location loc) {
		if (loc.getWorld().getEnvironment() != Environment.NETHER) {
			return (false);
		}

		int radius = HCF.getInstance().getMapHandler().getNetherBuffer();
		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		return ((x < radius && x > -radius) && (z < radius && z > -radius));
	}

	public void handleShopSign(Sign sign, Player player) {
		if (sign.getLine(0).toLowerCase().contains("all")) {

			int itemsSold = 0;
			int total = 0;
			int i = 0;
			for (ItemStack stack : player.getInventory().getContents()) {

				if (stack == null || stack.getType().equals(Material.AIR)) {
					++i;
					continue;
				}
				SellItem item = getByMaterial(stack.getType());
				if (getSellItems().contains(item)) {
					int totalPrice = (int) (stack.getAmount() * item.getPrice());

					player.getInventory().setItem(i, null);

					player.updateInventory();

					FrozenEconomyHandler.deposit(player.getUniqueId(), totalPrice);

					itemsSold = itemsSold + stack.getAmount();
					total = total + totalPrice;
				} else {
					showSignPacket(player, sign,
							"§cYou do not",
							"§chave any",
							"valuables",
							"§con you!"
					);
				}
				++i;
			}
			showSignPacket(player, sign,
					"§aSOLD§r " + itemsSold,
					"for §a$" + NumberFormat.getNumberInstance(Locale.US).format(total),
					"New Balance:",
					"§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) FrozenEconomyHandler.getBalance(player.getUniqueId()))
			);
			return;
		}
		ItemStack itemStack = (sign.getLine(2).contains("Crowbar") ? InventoryUtils.CROWBAR : ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", "")));

		if (itemStack == null && sign.getLine(2).contains("Antidote")) {
			itemStack = InventoryUtils.ANTIDOTE;
		}

		if (itemStack == null) {
			System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
			return;
		}

		if (sign.getLine(0).toLowerCase().contains("buy")) {
			int price;
			int amount;

			try {
				price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
				amount = Integer.parseInt(sign.getLine(1));
			} catch (NumberFormatException e) {
				return;
			}

			if (FrozenEconomyHandler.getBalance(player.getUniqueId()) >= price) {

				if (FrozenEconomyHandler.getBalance(player.getUniqueId()) > 100000) {
					player.sendMessage("§cYour balance is too high. Please contact an admin to do this.");
					Bukkit.getLogger().severe("[ECONOMY] " + player.getName() + " tried to buy shit at spawn with over 100K.");
					return;
				}


				if (Double.isNaN(FrozenEconomyHandler.getBalance(player.getUniqueId()))) {
					FrozenEconomyHandler.setBalance(player.getUniqueId(), 0);
					player.sendMessage("§cYour balance was fucked, but we unfucked it.");
					return;
				}

				if (player.getInventory().firstEmpty() != -1) {
					FrozenEconomyHandler.withdraw(player.getUniqueId(), price);

					itemStack.setAmount(amount);
					player.getInventory().addItem(itemStack);
					player.updateInventory();

					showSignPacket(player, sign,
							"§aBOUGHT§r " + amount,
							"for §a$" + NumberFormat.getNumberInstance(Locale.US).format(price),
							"New Balance:",
							"§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) FrozenEconomyHandler.getBalance(player.getUniqueId()))
					);
				} else {
					showSignPacket(player, sign,
							"§c§lError!",
							"",
							"§cNo space",
							"§cin inventory!"
					);
				}
			} else {
				showSignPacket(player, sign,
						"§cInsufficient",
						"§cfunds for",
						sign.getLine(2),
						sign.getLine(3)
				);
			}
		} else if (sign.getLine(0).toLowerCase().contains("sell")) {
			double pricePerItem;
			int amount;

			try {
				int price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
				amount = Integer.parseInt(sign.getLine(1));

				pricePerItem = (float) price / (float) amount;
			} catch (NumberFormatException e) {
				return;
			}

			int amountInInventory = Math.min(amount, countItems(player, itemStack.getType(), itemStack.getDurability()));

			if (amountInInventory == 0) {
				showSignPacket(player, sign,
						"§cYou do not",
						"§chave any",
						sign.getLine(2),
						"§con you!"
				);
			} else {
				int totalPrice = (int) (amountInInventory * pricePerItem);

				removeItem(player, itemStack, amountInInventory);
				player.updateInventory();

				FrozenEconomyHandler.deposit(player.getUniqueId(), totalPrice);

				showSignPacket(player, sign,
						"§aSOLD§r " + amountInInventory,
						"for §a$" + NumberFormat.getNumberInstance(Locale.US).format(totalPrice),
						"New Balance:",
						"§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) FrozenEconomyHandler.getBalance(player.getUniqueId()))
				);
			}
		}
	}

	public SellItem getByMaterial(Material material) {
		for (SellItem item : sellItems) {
			if (item.getMaterial() == material) {
				return item;
			}
		}
		return null;
	}

	public void handleKitSign(Sign sign, Player player) {
		String kit = ChatColor.stripColor(sign.getLine(1));

		if (kit.equalsIgnoreCase("Fishing")) {
			int uses = HCF.getInstance().getFishingKitMap().getUses(player.getUniqueId());

			if (uses == 3) {
				showSignPacket(player, sign, "§aFishing Kit:", "", "§cAlready used", "§c3/3 times!");
			} else {
				ItemStack rod = new ItemStack(Material.FISHING_ROD);

				rod.addEnchantment(Enchantment.LURE, 2);
				player.getInventory().addItem(rod);
				player.updateInventory();
				player.sendMessage(ChatColor.GOLD + "Equipped the " + ChatColor.WHITE + "Fishing" + ChatColor.GOLD + " kit!");
				HCF.getInstance().getFishingKitMap().setUses(player.getUniqueId(), uses + 1);
				showSignPacket(player, sign, "§aFishing Kit:", "§bEquipped!", "", "§dUses: §e" + (uses + 1) + "/3");
			}
		}
	}

	public void removeItem(Player p, ItemStack it, int amount) {
		boolean specialDamage = it.getType().getMaxDurability() == (short) 0;

		for (int a = 0; a < amount; a++) {
			for (ItemStack i : p.getInventory()) {
				if (i != null) {
					if (i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
						if (i.getAmount() == 1) {
							p.getInventory().clear(p.getInventory().first(i));
							break;
						} else {
							i.setAmount(i.getAmount() - 1);
							break;
						}
					}
				}
			}
		}

	}

	public ItemStack generateDeathSign(String killed, String killer) {
		ItemStack deathsign = new ItemStack(Material.SIGN);
		ItemMeta meta = deathsign.getItemMeta();

		ArrayList<String> lore = new ArrayList<>();

		lore.add("§4" + killed);
		lore.add("§eSlain By:");
		lore.add("§a" + killer);

		DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

		lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

		meta.setLore(lore);
		meta.setDisplayName("§cDeath Sign");
		deathsign.setItemMeta(meta);

		return (deathsign);
	}

	public ItemStack generateDeathSkull(String killed, String killer) {
		ItemStack deathSkull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 4);
		SkullMeta skullMeta = (SkullMeta) deathSkull.getItemMeta();

		ArrayList<String> lore = new ArrayList<>();

		lore.add("§4" + killed);
		lore.add("§eSlain By:");
		lore.add("§a" + killer);

		DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

		lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

		skullMeta.setOwner(killed);
		skullMeta.setDisplayName("§cDeath Skull");
		skullMeta.setLore(lore);
		deathSkull.setItemMeta(skullMeta);
		return (deathSkull);
	}

	public ItemStack generateKOTHSign(String koth, String capper, EventType eventType) {
		ItemStack kothsign = new ItemStack(Material.SIGN);
		ItemMeta meta = kothsign.getItemMeta();

		ArrayList<String> lore = new ArrayList<>();

		lore.add("§9" + koth);
		lore.add("§eCaptured By:");
		lore.add("§a" + capper);

		DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

		lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

		meta.setLore(lore);
		meta.setDisplayName("§d" + eventType.name() + " Capture Sign");
		kothsign.setItemMeta(meta);

		return (kothsign);
	}

	private HashMap<Sign, BukkitRunnable> showSignTasks = new HashMap<>();

	public void showSignPacket(Player player, Sign sign, String... lines) {
		player.sendSignChange(sign.getLocation(), lines);

		if (showSignTasks.containsKey(sign)) {
			showSignTasks.remove(sign).cancel();
		}

		BukkitRunnable br = new BukkitRunnable() {


			@Override
			public void run() {
				sign.update();
				showSignTasks.remove(sign);
			}

		};

		showSignTasks.put(sign, br);
		br.runTaskLater(HCF.getInstance(), 90L);
	}

	public int countItems(Player player, Material material, int damageValue) {
		PlayerInventory inventory = player.getInventory();
		ItemStack[] items = inventory.getContents();
		int amount = 0;

		for (ItemStack item : items) {
			if (item != null) {
				boolean specialDamage = material.getMaxDurability() == (short) 0;

				if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short) damageValue)) {
					amount += item.getAmount();
				}
			}
		}

		return (amount);
	}

	@AllArgsConstructor
	private class PotionStatus {

		private boolean drinkables;
		private boolean splash;
		private int maxLevel;

	}

}

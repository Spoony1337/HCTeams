package rip.orbit.hcteams;

import com.comphenix.protocol.ProtocolLibrary;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.frozenorb.qlib.util.ClassUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.hcteams.ability.AbilityHandler;
import rip.orbit.hcteams.chat.ChatHandler;
import rip.orbit.hcteams.chatgames.ChatGameHandler;
import rip.orbit.hcteams.crates.CrateHandler;
import rip.orbit.hcteams.deathmessage.DeathMessageHandler;
import rip.orbit.hcteams.events.EventHandler;
import rip.orbit.hcteams.events.citadel.CitadelHandler;
import rip.orbit.hcteams.events.conquest.ConquestHandler;
import rip.orbit.hcteams.events.killtheking.KingEventListener;
import rip.orbit.hcteams.events.pumpkinpatch.PumpkinPatchHandler;
import rip.orbit.hcteams.events.purge.listener.PurgeListener;
import rip.orbit.hcteams.events.region.carepackage.CarePackageHandler;
import rip.orbit.hcteams.events.region.cavern.CavernHandler;
import rip.orbit.hcteams.events.region.glowmtn.GlowHandler;
import rip.orbit.hcteams.listener.*;
import rip.orbit.hcteams.listener.fixes.*;
import rip.orbit.hcteams.listener.kits.*;
import rip.orbit.hcteams.map.MapHandler;
import rip.orbit.hcteams.map.kits.KitListener;
import rip.orbit.hcteams.nametags.ScoreboardManager;
import rip.orbit.hcteams.nametags.util.NmsUtils;
import rip.orbit.hcteams.packetborder.PacketBorderThread;
import rip.orbit.hcteams.persist.RedisSaveTask;
import rip.orbit.hcteams.persist.maps.*;
import rip.orbit.hcteams.polls.PollHandler;
import rip.orbit.hcteams.profile.ProfileListener;
import rip.orbit.hcteams.protocol.ClientCommandPacketAdaper;
import rip.orbit.hcteams.protocol.SignGUIPacketAdaper;
import rip.orbit.hcteams.pvpclasses.PvPClassHandler;
import rip.orbit.hcteams.pvpclasses.pvpclasses.ArcherClass;
import rip.orbit.hcteams.pvpclasses.pvpclasses.BardClass;
import rip.orbit.hcteams.pvpclasses.pvpclasses.RogueClass;
import rip.orbit.hcteams.reclaim.ReclaimHandler;
import rip.orbit.hcteams.reclaim.config.ReclaimConfigFile;
import rip.orbit.hcteams.redeem.RedeemHandler;
import rip.orbit.hcteams.server.EnderpearlCooldownHandler;
import rip.orbit.hcteams.server.ServerHandler;
import rip.orbit.hcteams.server.task.BackupRunnable;
import rip.orbit.hcteams.stars.listener.StarMineListener;
import rip.orbit.hcteams.tab.DefaultFoxtrotTabLayoutProvider;
import rip.orbit.hcteams.team.TeamHandler;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.commands.TeamDataCommands;
import rip.orbit.hcteams.team.commands.team.TeamClaimCommand;
import rip.orbit.hcteams.team.commands.team.subclaim.TeamSubclaimCommand;
import rip.orbit.hcteams.team.dtr.DTRHandler;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.DiscordLogger;
import rip.orbit.hcteams.util.RegenUtils;
import rip.orbit.hcteams.util.menu.page.MenuListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Predicate;

@Getter
public class HCF extends JavaPlugin {

	@Getter private static HCF instance;
	public static String MONGO_DB_NAME = "HCTeams";

	private MongoClient mongoPool;
	private ChatHandler chatHandler;
	private PvPClassHandler pvpClassHandler;
	private CarePackageHandler carePackageHandler;
	private TeamHandler teamHandler;
	private ServerHandler serverHandler;
	private MapHandler mapHandler;
	private CitadelHandler citadelHandler;
	private EventHandler eventHandler;
	private ConquestHandler conquestHandler;
	private CrateHandler crateHandler;
	private ScoreboardManager scoreboardManager;
	private CavernHandler cavernHandler;
	private AbilityHandler abilityHandler;
	private ChatGameHandler chatGameHandler;
	private PollHandler pollHandler;
	private GlowHandler glowHandler;
	private RedeemHandler redeemHandler;
	private PumpkinPatchHandler pumpkinPatchHandler;

	private ReclaimConfigFile reclaimConfig;
	private DiscordLogger discordLogger;
	private CombatLoggerListener combatLoggerListener;
	@Setter private Predicate<Player> inDuelPredicate = (player) -> false;


	@Override
	public void onEnable() {

		if (Bukkit.getServerName().contains(" ")) {
			System.out.println("*********************************************");
			System.out.println("               ATTENTION");
			System.out.println("SET server-name VALUE IN server.properties TO");
			System.out.println("A PROPER SERVER NAME. THIS WILL BE USED AS THE");
			System.out.println("MONGO DATABASE NAME.");
			System.out.println("*********************************************");
			this.getServer().shutdown();
			return;
		}

		instance = this;
		saveDefaultConfig();
		try {
			String host = getConfig().getString("Mongo.Host", "127.0.0.1");
			String authDB = getConfig().getString("Mongo.AuthDB", "admin");
			String username = getConfig().getString("Mongo.Username", "HCTeams");
			String password = getConfig().getString("Mongo.Password", "");

			boolean authRequired = password.length() > 0;
			ServerAddress address = new ServerAddress(host, 27017);

			if (!authRequired) {
				mongoPool = new MongoClient(address);
			} else {
				mongoPool = new MongoClient(address, MongoCredential.createCredential(
						username,
						authDB,
						password.toCharArray()
				), MongoClientOptions.builder()
						.retryWrites(true)
						.build());
			}

			MONGO_DB_NAME = Bukkit.getServerName();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		FrozenNametagHandler.registerProvider(new FoxtrotNametagProvider());
		NmsUtils.init();
		(new DTRHandler()).runTaskTimer(this, 20L, 1200L);
		(new DTRHandler()).runTaskAsynchronously(this);
		(new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
		(new PacketBorderThread()).start();
		scoreboardManager = new ScoreboardManager();
		setupHandlers();
		setupPersistence();
		setupListeners();
		reclaimConfig = new ReclaimConfigFile(this, "reclaims", this.getDataFolder().getAbsolutePath());
		if (HCF.getInstance().getConfig().getBoolean("tab.normal")) {
			FrozenTabHandler.setLayoutProvider(new DefaultFoxtrotTabLayoutProvider());
		}
		ProtocolLibrary.getProtocolManager().addPacketListener(new SignGUIPacketAdaper());
		ProtocolLibrary.getProtocolManager().addPacketListener(new ClientCommandPacketAdaper());

		for (World world : Bukkit.getWorlds()) {
			world.setThundering(false);
			world.setStorm(false);
			world.setWeatherDuration(Integer.MAX_VALUE);
			world.setGameRuleValue("doFireTick", "false");
			world.setGameRuleValue("mobGriefing", "false");
			world.setGameRuleValue("doMobGriefing", "false");
		}

		EndListener.loadEndReturn();

		new BukkitRunnable() {
			@Override
			public void run() {
				List<String> players = new ArrayList<>();
				Bukkit.getOnlinePlayers().forEach(p -> {
					if (Profile.getByUuid(p.getUniqueId()).getActiveRank().getDisplayName().equalsIgnoreCase("Orbit")) {
						players.add(p.getName());
					}
				});
				if (players.isEmpty()) {
					players.add("None");
				}
				String formattedPlayers = StringUtils.join(players, ", ");
				Bukkit.broadcastMessage(CC.chat("&6&lOnline Orbit Donators"));
				Bukkit.broadcastMessage(CC.chat(" &7» &f" + formattedPlayers));
				Bukkit.broadcastMessage(CC.chat("&7&oPurchase &6Orbit&7&o Rank @ store.orbit.rip"));
			}
		}.runTaskTimer(this, 200L, 18000L);

//		Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::backupTeams, 20 * 60 * 30, 20 * 60 * 30);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new BackupRunnable(), 20 * 60, 20 * 60);
	}

	public void backupTeams() {
		long start = System.currentTimeMillis();
		Bukkit.broadcastMessage(CC.translate("&a&oBacking-up all data, this may take a moment..."));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		String date = simpleDateFormat.format(new Date(System.currentTimeMillis()));
		TeamDataCommands.exportTeamData(Bukkit.getConsoleSender(), "Backups/!teams/backup-" + date.replaceAll("/", "-").replaceAll(":", "-"));
		Bukkit.broadcastMessage(CC.translate("&a&oAll data has been backed up &7(Took " + (System.currentTimeMillis() - start) + "ms)"));
	}

	@Override
	public void onDisable() {
		getEventHandler().saveEvents();

		for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
			getPlaytimeMap().playerQuit(player.getUniqueId(), false);
			player.setMetadata("loggedout", new FixedMetadataValue(this, true));
		}

		for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
			PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
		}

		for (Entity e : this.combatLoggerListener.getCombatLoggers()) {
			if (e != null) {
				e.remove();
			}
		}
		RedisSaveTask.save(null, false);
		HCF.getInstance().getServerHandler().save();
		HCF.getInstance().getMapHandler().save();
		HCF.getInstance().getMapHandler().getStatsHandler().save();
		RegenUtils.resetAll();
		qLib.getInstance().runRedisCommand((jedis) -> {
			jedis.save();
			return null;
		});
	}

	private void setupHandlers() {
		serverHandler = new ServerHandler();
		mapHandler = new MapHandler();
		mapHandler.load();
		teamHandler = new TeamHandler();
		chatGameHandler = new ChatGameHandler();
		pollHandler = new PollHandler();
		LandBoard.getInstance().loadFromTeams();
		reclaimHandler = new ReclaimHandler();
		chatHandler = new ChatHandler();
		redeemHandler = new RedeemHandler();
		citadelHandler = new CitadelHandler();
		pvpClassHandler = new PvPClassHandler();
		eventHandler = new EventHandler();
		conquestHandler = new ConquestHandler();
		carePackageHandler = new CarePackageHandler();
		abilityHandler = new AbilityHandler();
		crateHandler = new CrateHandler();
		discordLogger = new DiscordLogger(this);
		pumpkinPatchHandler = new PumpkinPatchHandler();

		if (getConfig().getBoolean("glowstoneMountain", false)) {
			glowHandler = new GlowHandler();
		}

		if (getConfig().getBoolean("cavern", false)) {
			cavernHandler = new CavernHandler();
		}

		FrozenCommandHandler.registerAll(this);
		for (Class<?> clazz : ClassUtils.getClassesInPackage(this, getClass().getPackage().getName())) {
			if (clazz == null || clazz.getCanonicalName() == null) continue;
			if (clazz.getCanonicalName().contains("conditional")) continue;
			FrozenCommandHandler.registerClass(clazz);
		}

		DeathMessageHandler.init();
		DTRHandler.loadDTR();
	}

	private void setupListeners() {
		getServer().getPluginManager().registerEvents(new MenuListener(), this);
		getServer().getPluginManager().registerEvents(new DisposibalSignListener(this), this);
		getServer().getPluginManager().registerEvents(new PurgeListener(), this);
		getServer().getPluginManager().registerEvents(new MapListener(), this);
		getServer().getPluginManager().registerEvents(new AntiGlitchListener(), this);
		getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
		getServer().getPluginManager().registerEvents(new BorderListener(), this);
		getServer().getPluginManager().registerEvents((combatLoggerListener = new CombatLoggerListener()), this);
		getServer().getPluginManager().registerEvents(new CrowbarListener(), this);
		getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), this);
		getServer().getPluginManager().registerEvents(new EndListener(), this);
		getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
		getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), this);
		getServer().getPluginManager().registerEvents(new FoxListener(), this);
		getServer().getPluginManager().registerEvents(new GoldenAppleListener(), this);
		getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), this);
		getServer().getPluginManager().registerEvents(new PvPTimerListener(), this);
		getServer().getPluginManager().registerEvents(new KitListener(), this);
		getServer().getPluginManager().registerEvents(new StarMineListener(), this);
	    getServer().getPluginManager().registerEvents(new NetherPortalListener(), this);
		getServer().getPluginManager().registerEvents(new PortalTrapListener(), this);
		getServer().getPluginManager().registerEvents(new ProfileListener(), this);
		getServer().getPluginManager().registerEvents(new SignSubclaimListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnerTrackerListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnTagListener(), this);
		getServer().getPluginManager().registerEvents(new TeamListener(), this);
//		getServer().getPluginManager().registerEvents(new WebsiteListener(), this);
		getServer().getPluginManager().registerEvents(new PotionLimiterListeners(), this);
		getServer().getPluginManager().registerEvents(new EnchantmentLimiterListeners(), this);
		getServer().getPluginManager().registerEvents(new TeamSubclaimCommand(), this);
		getServer().getPluginManager().registerEvents(new TeamClaimCommand(), this);
		getServer().getPluginManager().registerEvents(new rip.orbit.hcteams.util.menu.MenuListener(this), this);
		getServer().getPluginManager().registerEvents(new LunarClientListener(), this);
		getServer().getPluginManager().registerEvents(new StatTrakListener(), this);

		// Removed because there's a strength fix in the spigot.
		// getServer().getPluginManager().registerEvents(new StrengthListener(), this);

		if (getServerHandler().isReduceArmorDamage()) {
			getServer().getPluginManager().registerEvents(new ArmorDamageListener(), this);
		}

		if (getServerHandler().isBlockEntitiesThroughPortals()) {
			getServer().getPluginManager().registerEvents(new EntityPortalListener(), this);
		}

		if (getServerHandler().isBlockRemovalEnabled()) {
			getServer().getPluginManager().registerEvents(new BlockRegenListener(), this);
		}

		// Register kitmap specific listeners
		if (getServerHandler().isVeltKitMap() || getMapHandler().isKitMap()) {
			getServer().getPluginManager().registerEvents(new KitMapListener(), this);
			getServer().getPluginManager().registerEvents(new BountyListerner(), this);
			getServer().getPluginManager().registerEvents(new CarePackageHandler(), this);
			getServer().getPluginManager().registerEvents(new RefillSignListener(), this);
			getServer().getPluginManager().registerEvents(new RefillSignCreateListener(), this);
		}
		getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), this);
		getServer().getPluginManager().registerEvents(new KingEventListener(), this);

	}

	private void setupPersistence() {
		(playtimeMap = new PlaytimeMap()).loadFromRedis();
		(oppleMap = new OppleMap()).loadFromRedis();
		(deathbanMap = new DeathbanMap()).loadFromRedis();
		(PvPTimerMap = new PvPTimerMap()).loadFromRedis();
		(startingPvPTimerMap = new StartingPvPTimerMap()).loadFromRedis();
		(chatModeMap = new ChatModeMap()).loadFromRedis();
		(toggleGlobalChatMap = new ToggleGlobalChatMap()).loadFromRedis();
		(toggleLFFMessageMap = new ToggleLFFMessageMap()).loadFromRedis();
		(claimOnSbMap = new ClaimOnSbMap()).loadFromRedis();
		(receiveFactionInviteMap = new ReceiveFactionInviteMap()).loadFromRedis();
		(teamColorMap = new TeamColorMap()).loadFromRedis();
		(enemyColorMap = new EnemyColorMap()).loadFromRedis();
		(allyColorMap = new AllyColorMap()).loadFromRedis();
		(archerTagColorMap = new ArcherTagColorMap()).loadFromRedis();
		(focusColorMap = new FocusColorMap()).loadFromRedis();
		(staffBoardMap = new StaffBoardMap()).loadFromRedis();
		(fishingKitMap = new FishingKitMap()).loadFromRedis();
		(livesMap = new LivesMap()).loadFromRedis();
		(chatSpyMap = new ChatSpyMap()).loadFromRedis();
		(diamondMinedMap = new DiamondMinedMap()).loadFromRedis();
		(goldMinedMap = new GoldMinedMap()).loadFromRedis();
		(ironMinedMap = new IronMinedMap()).loadFromRedis();
		(coalMinedMap = new CoalMinedMap()).loadFromRedis();
		(redstoneMinedMap = new RedstoneMinedMap()).loadFromRedis();
		(lapisMinedMap = new LapisMinedMap()).loadFromRedis();
		(emeraldMinedMap = new EmeraldMinedMap()).loadFromRedis();
		(firstJoinMap = new FirstJoinMap()).loadFromRedis();
		(lastJoinMap = new LastJoinMap()).loadFromRedis();
		(wrappedBalanceMap = new WrappedBalanceMap()).loadFromRedis();
		(toggleFoundDiamondsMap = new ToggleFoundDiamondsMap()).loadFromRedis();
		(toggleDeathMessageMap = new ToggleDeathMessageMap()).loadFromRedis();
		(tabListModeMap = new TabListModeMap()).loadFromRedis();
		(cobblePickupMap = new CobblePickupMap()).loadFromRedis();
		(baseCooldownMap = new BaseCooldownMap()).loadFromRedis();
		(toggleAbilityCDsSBMap = new ToggleAbilityCDsSBMap()).loadFromRedis();
		(starsMaps = new StarsMap()).loadFromRedis();
		(receiveGeneratorMessagesMap = new ReceiveGeneratorMessagesMap()).loadFromRedis();
	}

	private PlaytimeMap playtimeMap;
	private OppleMap oppleMap;
	private DeathbanMap deathbanMap;
	private rip.orbit.hcteams.persist.maps.PvPTimerMap PvPTimerMap;
	private StartingPvPTimerMap startingPvPTimerMap;
	private ChatModeMap chatModeMap;
	private FishingKitMap fishingKitMap;
	private ToggleGlobalChatMap toggleGlobalChatMap;
	private ToggleLFFMessageMap toggleLFFMessageMap;
	private BaseCooldownMap baseCooldownMap;
	private StaffBoardMap staffBoardMap;
	private ClaimOnSbMap claimOnSbMap;
	private ReceiveFactionInviteMap receiveFactionInviteMap;
	private TeamColorMap teamColorMap;
	private EnemyColorMap enemyColorMap;
	private AllyColorMap allyColorMap;
	private ArcherTagColorMap archerTagColorMap;
	private FocusColorMap focusColorMap;
	private ChatSpyMap chatSpyMap;
	private DiamondMinedMap diamondMinedMap;
	private GoldMinedMap goldMinedMap;
	private IronMinedMap ironMinedMap;
	private CoalMinedMap coalMinedMap;
	private RedstoneMinedMap redstoneMinedMap;
	private LapisMinedMap lapisMinedMap;
	private EmeraldMinedMap emeraldMinedMap;
	private FirstJoinMap firstJoinMap;
	private ReclaimHandler reclaimHandler;
	private LastJoinMap lastJoinMap;
	private LivesMap livesMap;
	private WrappedBalanceMap wrappedBalanceMap;
	private ToggleFoundDiamondsMap toggleFoundDiamondsMap;
	private ToggleDeathMessageMap toggleDeathMessageMap;
	private TabListModeMap tabListModeMap;
	private CobblePickupMap cobblePickupMap;
	private ToggleAbilityCDsSBMap toggleAbilityCDsSBMap;
	private ReceiveGeneratorMessagesMap receiveGeneratorMessagesMap;
	private StarsMap starsMaps;
	@Setter private ArcherClass archerClass;
	@Setter private BardClass bardClass;
	@Setter private RogueClass rogueClass;

}

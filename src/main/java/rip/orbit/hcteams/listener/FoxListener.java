package rip.orbit.hcteams.listener;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.gravity.profile.global.GlobalInfo;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.citadel.CitadelHandler;
import rip.orbit.hcteams.server.RegionData;
import rip.orbit.hcteams.server.ServerHandler;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.server.spawnershop.SpawnerShopMenu;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.Claim;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.claims.Subclaim;
import rip.orbit.hcteams.team.commands.team.TeamStuckCommand;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.InventoryUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;

public class FoxListener implements Listener {

	private static Map<BlockVector, UUID> pressurePlates = new ConcurrentHashMap<>();
	public static ItemStack FIRST_SPAWN_BOOK = new ItemStack(WRITTEN_BOOK);
	public static ItemStack FIRST_SPAWN_FISHING_ROD = new ItemStack(FISHING_ROD);
	public static Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
	public static Set<Material> NO_INTERACT_WITH = ImmutableSet.of(LAVA_BUCKET, WATER_BUCKET, BUCKET);
	public static Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(GLASS, WOOD_DOOR, IRON_DOOR, FENCE_GATE);
	public static Set<Material> NO_INTERACT = ImmutableSet.of(FENCE_GATE, FURNACE, BURNING_FURNACE, BREWING_STAND, CHEST, HOPPER, DISPENSER, WOODEN_DOOR, STONE_BUTTON, WOOD_BUTTON, TRAPPED_CHEST, TRAP_DOOR, LEVER, DROPPER, ENCHANTMENT_TABLE, BED_BLOCK, ANVIL, BEACON);
	private static List<UUID> processingTeleportPlayers = new CopyOnWriteArrayList<>();

	static {
		FIRST_SPAWN_FISHING_ROD.addEnchantment(Enchantment.LURE, 2);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		processTerritoryInfo(event);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
			return;
		}

		if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
			HCF.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()).getTaskId());
			ServerHandler.getTasks().remove(event.getPlayer().getName());
			event.getPlayer().sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
		}

		processTerritoryInfo(event);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		HCF.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		HCF.getInstance().getPlaytimeMap().playerJoined(event.getPlayer().getUniqueId());
		HCF.getInstance().getLastJoinMap().setLastJoin(event.getPlayer().getUniqueId());

		if (!event.getPlayer().hasPlayedBefore()) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
			if (HCF.getInstance().getMapHandler().isKitMap()) {
				profile.getGlobalInfo().setKitsSeasonsPlayed(profile.getGlobalInfo().getKitsSeasonsPlayed() + 1);
			} else {
				profile.getGlobalInfo().setHcfMapsPlayed(profile.getGlobalInfo().getHcfMapsPlayed() + 1);
			}
			profile.save();
			HCF.getInstance().getFirstJoinMap().setFirstJoin(event.getPlayer().getUniqueId());
			FrozenEconomyHandler.setBalance(event.getPlayer().getUniqueId(), HCF.getInstance().getServerHandler().getStartingBalance());

			event.getPlayer().teleport(HCF.getInstance().getServerHandler().getSpawnLocation());

			if (SOTWCommand.getCustomTimers().get("&e&lSOTW") == null) {
				if (HCF.getInstance().getServerHandler().isStartingTimerEnabled()) {
					HCF.getInstance().getPvPTimerMap().createStartingTimer(event.getPlayer().getUniqueId(), (int) TimeUnit.HOURS.toSeconds(1));
				} else {
					HCF.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onStealthPickaxe(BlockBreakEvent event) {
		Block block = event.getBlock();
		ItemStack inHand = event.getPlayer().getItemInHand();
		if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
			if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
				event.setCancelled(true);

				block.breakNaturally(inHand);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onStealthItemPickup(PlayerPickupItemEvent event) {
		ItemStack inHand = event.getPlayer().getItemInHand();
		if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
			if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
				event.setCancelled(true);
				event.getPlayer().getInventory().addItem(event.getItem().getItemStack());
				event.getItem().remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (ServerHandler.getTasks().containsKey(player.getName())) {
				HCF.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()).getTaskId());
				ServerHandler.getTasks().remove(player.getName());
				player.sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onProjectileInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (event.getItem().getType() == POTION) {
				try {
					ItemStack i = event.getItem();

					if (i.getDurability() != (short) 0) {
						Potion pot = Potion.fromItemStack(i);

						if (pot != null && pot.isSplash() && pot.getType() != null && DEBUFFS.contains(pot.getType().getEffectType())) {
							if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
								player.sendMessage(RED + "You cannot do this while your PVP Timer is active!");
								player.sendMessage(RED + "Type '" + YELLOW + "/pvp enable" + RED + "' to remove your timer.");
								event.setCancelled(true);
								return;
							}

							if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
								event.setCancelled(true);
								event.getPlayer().sendMessage(RED + "You cannot throw debuffs from inside spawn!");
								event.getPlayer().updateInventory();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (event.getClickedBlock() == null) {
			return;
		}

		if (event.getClickedBlock().getType() == ENCHANTMENT_TABLE && event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getType() == ENCHANTED_BOOK) {
					event.getItem().setType(BOOK);

					event.getPlayer().sendMessage(GREEN + "You reverted this book to its original form!");
					event.setCancelled(true);
				}
			}

			return;
		}

		if (HCF.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || HCF.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
			return;
		}

		Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

		if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
			if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
				if (event.getClickedBlock().getType().name().contains("BUTTON") || event.getClickedBlock().getType().name().contains("CHEST") || event.getClickedBlock().getType().name().contains("DOOR")) {
					CitadelHandler citadelHandler = HCF.getInstance().getCitadelHandler();

					if (DTRBitmask.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer())) {
						return;
					}
				}

				event.setCancelled(true);
				event.getPlayer().sendMessage(YELLOW + "You cannot do this in " + team.getName(event.getPlayer()) + YELLOW + "'s territory.");
				List<Material> materials = Arrays.asList(
						HOPPER,
						FENCE_GATE,
						TRAP_DOOR,
						WOODEN_DOOR,
						WOOD_DOOR,
						FURNACE,
						BURNING_FURNACE,
						DISPENSER,
						CHEST,
						TRAPPED_CHEST,
						FENCE
				);
				if (materials.contains(event.getClickedBlock().getType())) {
					if (event.getItem() != null) {
						if (event.getItem().getType() == POTION) {
							if (event.getItem().getDurability() == 16421) {
								event.getPlayer().setItemInHand(null);
								ThrownPotion pot = event.getPlayer().launchProjectile(ThrownPotion.class);
								pot.setItem(event.getItem());
								event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
							}
						}
					}
				}
//                if (event.getMaterial() == TRAP_DOOR || event.getMaterial() == FENCE_GATE || event.getMaterial().name().contains("DOOR")) {
//                    HCF.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
//                }

				return;
			}

			if (event.getAction() == Action.PHYSICAL) {
				event.setCancelled(true);
			}
		} else if (event.getMaterial() == LAVA_BUCKET) {
			if (team == null || !team.isMember(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(RED + "You can only do this in your own claims!");
			}
		} else {
			UUID uuid = player.getUniqueId();

			if (team != null && !team.isCaptain(uuid) && !team.isCoLeader(uuid) && !team.isOwner(uuid)) {
				Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

				if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
					if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(YELLOW + "You do not have access to the subclaim " + GREEN + subclaim.getName() + YELLOW + "!");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) event.getClickedBlock().getState();

				if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
					if (s.getLine(0).contains("Kit")) {
						HCF.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
					} else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("All") || s.getLine(0).contains("Sell")) {
						HCF.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
					} else if (s.getLine(1).contains("Spawner")) {
						new SpawnerShopMenu().openMenu(event.getPlayer());
					}

					event.setCancelled(true);
				}
			}
		}

		if (event.getItem() != null && event.getMaterial() == SIGN) {
			if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getLore() != null) {
				ArrayList<String> lore = (ArrayList<String>) event.getItem().getItemMeta().getLore();

				if (lore.size() > 1 && lore.get(1).contains("§e")) {
					if (event.getClickedBlock() != null) {
						event.getClickedBlock().getRelative(event.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(HCF.getInstance(), true));

						new BukkitRunnable() {

							@Override
							public void run() {
								event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", HCF.getInstance());
							}

						}.runTaskLaterAsynchronously(HCF.getInstance(), 20L);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamageWhilstSOTW(EntityDamageByEntityEvent event) {

		if (event.getEntity() instanceof Player) {
			if (event.getDamager() instanceof Player) {
				Player damager = (Player) event.getDamager();
				Player damaged = (Player) event.getEntity();
				if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(damager.getUniqueId())) {
					if (SOTWCommand.isSOTWTimer() && SOTWCommand.hasSOTWEnabled(damaged.getUniqueId())) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		if (event.getEntity() instanceof Player) {
			if (event.getDamager() instanceof Projectile) {
				if (((Projectile)event.getDamager()).getShooter() instanceof Player) {
					Player damager = (Player) ((Projectile) event.getDamager()).getShooter();
					Player damaged = (Player) event.getEntity();
					if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(damager.getUniqueId())) {
						if (SOTWCommand.isSOTWTimer() && SOTWCommand.hasSOTWEnabled(damaged.getUniqueId())) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onHit(EntityDamageByEntityEvent event) {

		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();

		if (event.isCancelled())
			return;

		if (damager instanceof Projectile)
			return;

		if (!(damager instanceof Player))
			return;

		if (!(damaged instanceof Player))
			return;

		if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation()))
			return;
		if (HCF.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId()) || HCF.getInstance().getPvPTimerMap().hasTimer(event.getDamager().getUniqueId()))
			return;
		if (SOTWCommand.isSOTWTimer()) {
			if (!SOTWCommand.hasSOTWEnabled(event.getDamager().getUniqueId()) || !SOTWCommand.hasSOTWEnabled(event.getEntity().getUniqueId()))
				return;
		}

		Team team = HCF.getInstance().getTeamHandler().getTeam(damager.getUniqueId());

		if (team != null) {
			if (team.getOnlineMembers().contains(damaged)) {
				return;
			}
		}

		final org.bukkit.util.Vector damagerDirection = damager.getLocation().getDirection();
		final org.bukkit.util.Vector damagedDirection = damaged.getLocation().getDirection();
		if (damagerDirection.dot(damagedDirection) > 0.0) { // checks if they're behind the damamger
			org.bukkit.util.Vector inverseDirectionVec = damaged.getLocation().getDirection().normalize().multiply(-0.64);
			inverseDirectionVec.setY(inverseDirectionVec.getY() - 0.25);
			damaged.setVelocity(inverseDirectionVec);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
			if (SOTWCommand.isSOTWTimer()) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.hidePlayer(event.getPlayer());
				}
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (DTRBitmask.SAFE_ZONE.appliesAt(p.getLocation())) {
				event.getPlayer().hidePlayer(p);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		ItemStack hand = e.getItemInHand();

		if (hand.getType() == SIGN) {
			if (hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {
				ArrayList<String> lore = (ArrayList<String>) hand.getItemMeta().getLore();

				if (e.getBlock().getType() == WALL_SIGN || e.getBlock().getType() == SIGN_POST) {
					Sign s = (Sign) e.getBlock().getState();

					for (int i = 0; i < 4; i++) {
						s.setLine(i, lore.get(i));
					}

					s.setMetadata("deathSign", new FixedMetadataValue(HCF.getInstance(), true));
					s.update();
				}
			}
		} else if (hand.getType() == MOB_SPAWNER) {
			if (!(e.isCancelled())) {
				if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() && hand.getItemMeta().getDisplayName().startsWith(RESET.toString())) {
					String name = stripColor(hand.getItemMeta().getDisplayName());
					String entName = name.replace(" Spawner", "");
					EntityType type = EntityType.valueOf(entName.toUpperCase().replaceAll(" ", "_"));

					CreatureSpawner spawner = (CreatureSpawner) block.getState();
					spawner.setSpawnedType(type);
					spawner.update();

					e.getPlayer().sendMessage(AQUA + "You placed a " + entName + " spawner!");
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock().getState()).getLine(1).contains("§e")) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == WALL_SIGN || e.getBlock().getType() == SIGN_POST) {
			if (e.getBlock().getState().hasMetadata("deathSign") || ((e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(1).contains("§e")))) {
				e.setCancelled(true);

				Sign sign = (Sign) e.getBlock().getState();

				ItemStack deathsign = new ItemStack(SIGN);
				ItemMeta meta = deathsign.getItemMeta();

				if (sign.getLine(1).contains("Captured")) {
					meta.setDisplayName("§dKOTH Capture Sign");
				} else {
					meta.setDisplayName("§dDeath Sign");
				}

				meta.setLore(Arrays.asList(sign.getLines()));
				deathsign.setItemMeta(meta);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

				e.getBlock().setType(AIR);
				e.getBlock().getState().removeMetadata("deathSign", HCF.getInstance());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof EnderPearl) || !(event.getEntity() instanceof Player)) {
			return;
		}

		final EnderPearl enderPearl = (EnderPearl) event.getDamager();
		if (!(enderPearl.getShooter() instanceof Player)) {
			return;
		}

		final Player damager = (Player) enderPearl.getShooter();
		final Player target = (Player) event.getEntity();

		if (damager == target) {
			return;
		}

		final Location targetLocation = target.getLocation().clone();

		Block block = damager.getTargetBlock((HashSet<Byte>) null, (int) damager.getLocation().distance(targetLocation));

		if (block != null && block.getType() != Material.AIR)
			return;

		targetLocation.setYaw(damager.getLocation().getYaw());
		targetLocation.setPitch(damager.getLocation().getPitch());

		damager.teleport(targetLocation);
		System.out.println("Telported");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		SpawnTagHandler.removeTag(event.getEntity());
		Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getEntity());
		Player killer = event.getEntity().getKiller();

		if (HCF.getInstance().getInDuelPredicate().test(event.getEntity())) {
			return;
		}

		if (killer != null) {

			Team killerTeam = HCF.getInstance().getTeamHandler().getTeam(killer);
			Location deathLoc = event.getEntity().getLocation();
			int deathX = deathLoc.getBlockX();
			int deathY = deathLoc.getBlockY();
			int deathZ = deathLoc.getBlockZ();

			if (killerTeam != null) {
				TeamActionTracker.logActionAsync(killerTeam, TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP, ImmutableMap.of("playerId", killer.getUniqueId(), "playerName", killer.getName(), "killedId", event.getEntity().getUniqueId(), "killedName", event.getEntity().getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
			}

			if (playerTeam != null) {
				TeamActionTracker.logActionAsync(playerTeam, TeamActionType.MEMBER_KILLED_BY_ENEMY_IN_PVP, ImmutableMap.of("playerId", event.getEntity().getUniqueId(), "playerName", event.getEntity().getName(), "killerId", killer.getUniqueId(), "killerName", killer.getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
			}

			// Add kills to sword lore if the victim does not equal the killer
			if (!event.getEntity().equals(killer)) {
				ItemStack hand = killer.getItemInHand();

				if (hand.getType().name().contains("SWORD") || hand.getType() == BOW) {
					InventoryUtils.addKill(hand, killer.getDisplayName() + YELLOW + " " + (hand.getType() == BOW ? "shot" : "killed") + " " + event.getEntity().getDisplayName());
				}
			}
		}

		if (playerTeam != null) {
			double loss = HCF.getInstance().getServerHandler().getDTRLoss(event.getEntity());
			Player killa = event.getEntity().getKiller();
			if (playerTeam.getDTR() >= 0.1 && (playerTeam.getDTR() - loss) <= 0) {
				if (killa != null) {
					if (!event.getEntity().getUniqueId().equals(killa.getUniqueId())) {
						Profile killaProfile = Profile.getByUuid(killa.getUniqueId());
						GlobalInfo killaInfo = killaProfile.getGlobalInfo();

						killaInfo.setHcfMadeRaidable(killaInfo.getHcfMadeRaidable() + 1);
						killaProfile.save();
						killa.sendMessage(CC.chat("&aYou have just earned a raidable point by making " + playerTeam.getName() + " raidable! Check out your raidable points statistic in /globalinfo"));
					}
				}
			}
			playerTeam.playerDeath(event.getEntity().getName(), loss);
		}

		if (killer == null || (!event.getEntity().equals(killer))) {
			// Add deaths to armor
			String deathMsg = YELLOW + event.getEntity().getName() + RESET + " " + (event.getEntity().getKiller() != null ? "killed by " + YELLOW + event.getEntity().getKiller().getName() : "died") + " " + GOLD +
					InventoryUtils.DEATH_TIME_FORMAT.format(new Date());

			for (ItemStack armor : event.getEntity().getInventory().getArmorContents()) {
				if (armor != null && armor.getType() != AIR) {
					InventoryUtils.addDeath(armor, deathMsg);
				}
			}
		}

		event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());

		// Transfer money
		double bal = FrozenEconomyHandler.getBalance(event.getEntity().getUniqueId());
		FrozenEconomyHandler.withdraw(event.getEntity().getUniqueId(), bal);

		if (event.getEntity().getKiller() != null) {
			if (!Double.isNaN(bal) && bal > 0) {
				FrozenEconomyHandler.deposit(event.getEntity().getKiller().getUniqueId(), bal);
				event.getEntity().getKiller().sendMessage(WHITE + "You earned " + GOLD + "$" + bal + WHITE + " for killing " + RED + event.getEntity().getDisplayName() + YELLOW + "!");
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (DTRBitmask.SAFE_ZONE.appliesAt(event.getTo()) && !DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom())) {
			if (SOTWCommand.isSOTWTimer()) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (event.getPlayer().hasMetadata("modmode")) continue;
					p.hidePlayer(event.getPlayer());
				}
			}
		}

		if (DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom()) && !DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {
			if (SOTWCommand.isSOTWTimer()) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (event.getPlayer().hasMetadata("modmode")) continue;
					p.showPlayer(event.getPlayer());
				}
			}
		}
	}

	private void processTerritoryInfo(PlayerMoveEvent event) {
		Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

		if (HCF.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {

            /*
            //prevent stack overflow
            if (ownerTo != null && ownerTo.getKitName().equalsIgnoreCase("spawn")) {
                return;
            }
            
            //prevent staff from being teleported during the claiming process
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }
            */

			if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {

				if (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo())) {
					HCF.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());

					event.getPlayer().sendMessage(ChatColor.RED + "Your PvP Protection has been removed for entering claimed land.");
				} else if (ownerTo != null && ownerTo.getOwner() != null) {
					if (!ownerTo.getMembers().contains(event.getPlayer().getUniqueId())) {
						event.setCancelled(true);

						for (Claim claim : ownerTo.getClaims()) {
							if (claim.contains(event.getFrom()) && !ownerTo.isMember(event.getPlayer().getUniqueId())) {
								Location nearest = TeamStuckCommand.nearestSafeLocation(event.getPlayer().getLocation());
								boolean spawn = false;

								if (nearest == null) {
									nearest = HCF.getInstance().getServerHandler().getSpawnLocation();
									spawn = true;
								}

								event.getPlayer().teleport(nearest);
								event.getPlayer().sendMessage(ChatColor.RED + "Moved you to " + (spawn ? "spawn" : "nearest unclaimed territory") + " because you were in land that was claimed.");
								return;
							}
						}

						event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter another team's territory with PvP Protection.");
						event.getPlayer().sendMessage(ChatColor.RED + "Use " + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + " to remove your protection.");
						return;
					}
				}
			}
		}

		Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());

		if (ownerFrom != ownerTo) {
			ServerHandler sm = HCF.getInstance().getServerHandler();
			RegionData from = sm.getRegion(ownerFrom, event.getFrom());
			RegionData to = sm.getRegion(ownerTo, event.getTo());

			if (from.equals(to)) return;

			if (!to.getRegionType().getMoveHandler().handleMove(event)) {
				return;
			}

			boolean fromReduceDeathban = from.getData() != null && (from.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));
			boolean toReduceDeathban = to.getData() != null && (to.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));

			if (fromReduceDeathban && from.getData() != null) {
				Event fromLinkedKOTH = HCF.getInstance().getEventHandler().getEvent(from.getData().getName());

				if (fromLinkedKOTH != null && !fromLinkedKOTH.isActive()) {
					fromReduceDeathban = false;
				}
			}

			if (toReduceDeathban && to.getData() != null) {
				Event toLinkedKOTH = HCF.getInstance().getEventHandler().getEvent(to.getData().getName());

				if (toLinkedKOTH != null && !toLinkedKOTH.isActive()) {
					toReduceDeathban = false;
				}
			}

			// create leaving message
			FancyMessage nowLeaving = new FancyMessage("Now leaving: ").color(YELLOW).then(from.getName(event.getPlayer())).color(YELLOW);

			if (ownerFrom != null) {
				nowLeaving.command("/t i " + ownerFrom.getName()).tooltip(GREEN + "View team info");
			}

			if (DTRBitmask.SAFE_ZONE.appliesAt(event.getTo()) && !DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom())) {
				if (SOTWCommand.isSOTWTimer()) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (event.getPlayer().hasMetadata("modmode")) continue;
						p.hidePlayer(event.getPlayer());
					}
				}
			}

			if (DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom()) && !DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {
				if (SOTWCommand.isSOTWTimer()) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (event.getPlayer().hasMetadata("modmode")) continue;
						p.showPlayer(event.getPlayer());
					}
				}
			}

			nowLeaving.then(" (").color(YELLOW).then(fromReduceDeathban ? "Non-Deathban" : "Deathban").color(fromReduceDeathban ? GREEN : RED).then(")").color(YELLOW);

			// create entering message
			FancyMessage nowEntering = new FancyMessage("Now entering: ").color(YELLOW).then(to.getName(event.getPlayer())).color(YELLOW);

			if (ownerTo != null) {
				nowEntering.command("/t i " + ownerTo.getName()).tooltip(GREEN + "View team info");
			}

			nowEntering.then(" (").color(YELLOW).then(toReduceDeathban ? "Non-Deathban" : "Deathban").color(toReduceDeathban ? GREEN : RED).then(")").color(YELLOW);

			// send both
			nowLeaving.send(event.getPlayer());
			nowEntering.send(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamaged(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getEntity() instanceof Player) {
				if (SOTWCommand.isSOTWTimer()) {
					if (!SOTWCommand.hasSOTWEnabled(event.getEntity().getUniqueId())) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

}

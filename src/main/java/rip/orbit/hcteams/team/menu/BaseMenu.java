package rip.orbit.hcteams.team.menu;

import lombok.Setter;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.gravity.util.ItemBuilder;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.scoreboard.FoxtrotScoreGetter;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.JavaUtils;
import rip.orbit.hcteams.util.cooldown.form.DurationFormatter;
import rip.orbit.hcteams.util.object.Cuboid;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 10/07/2021 / 1:01 AM
 * HCTeams / rip.orbit.hcteams.team.menu
 */

public class BaseMenu extends Menu {

	private Team team;

	public BaseMenu(Team team) {
		this.team = team;
	}

	@Setter
	public static boolean queued = false;

	@Override
	public String getTitle(Player player) {
		return "Team Base";
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(3, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.COBBLESTONE)
						.name(CC.chat("&6&lOutline Your Claim"))
						.lore(CC.chat("&fClick to outline your claim with &6cobblestone&f."))
						.lore(CC.chat("&7» &fPrice: &6$10,000"))
						.build();
			}

			@Override
			public String getName(Player player) {
				return CC.chat("&6&lOutline Your Claim");
			}

			@Override
			public List<String> getDescription(Player player) {
				return CC.translate(Arrays.asList(
						"&fClick to outline your claim with &6cobblestone&f.",
						"&7» &fPrice: &6$10,000"
				));
			}

			@Override
			public Material getMaterial(Player player) {
				return Material.COBBLESTONE;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				if (team.getBalance() < 10000) {
					player.sendMessage(CC.chat("&cYou have insufficient funds for this."));
					return;
				}
				if (player.getLocation().getY() >= (90 - 17)) {
					player.sendMessage(CC.chat("&cYou have to be below y level " + (90 - 17) + " to do this."));
					return;
				}
				if (queued) {
					player.sendMessage(CC.chat("&cYou cannot do this, as someone else's building a box. Try again in a few seconds!"));
					return;
				}
				if (HCF.getInstance().getBaseCooldownMap().hasTimer(player.getUniqueId())) {
					String format = FoxtrotScoreGetter.getTimerScore(HCF.getInstance().getBaseCooldownMap().getRemaining(player.getUniqueId()));
					player.sendMessage(CC.chat("&cYou cannot do this for another &l" + format));
					return;
				}
				if (team.isOwner(player.getUniqueId())) {
					setQueued(true);
					CompletableFuture.runAsync(() -> {

						team.getClaims().forEach(claim -> {
							int y = 80;
							while (y > 0) {
								Cuboid cuboid = new Cuboid(new Location(Bukkit.getWorld(claim.getWorld()), claim.getX1(), player.getLocation().getY(), claim.getZ1()), new Location(Bukkit.getWorld(claim.getWorld()), claim.getX2(), player.getLocation().getY(), claim.getZ2()));
								Iterator<Block> north = cuboid.getFace(Cuboid.CuboidDirection.North).iterator();
								Iterator<Block> south = cuboid.getFace(Cuboid.CuboidDirection.South).iterator();
								Iterator<Block> east = cuboid.getFace(Cuboid.CuboidDirection.East).iterator();
								Iterator<Block> west = cuboid.getFace(Cuboid.CuboidDirection.West).iterator();
								while (north.hasNext()) {
									Block block = north.next();
									if (block.getY() > 90 || block.getY() < 1) continue;
									new BukkitRunnable() {
										@Override
										public void run() {
											block.setType(Material.COBBLESTONE);
										}
									}.runTask(HCF.getInstance());
								}
								while (south.hasNext()) {
									Block block = south.next();
									if (block.getY() > 90 || block.getY() < 1) continue;
									new BukkitRunnable() {
										@Override
										public void run() {
											block.setType(Material.COBBLESTONE);
										}
									}.runTask(HCF.getInstance());
								}
								while (east.hasNext()) {
									Block block = east.next();
									if (block.getY() > 90 || block.getY() < 1) continue;
									new BukkitRunnable() {
										@Override
										public void run() {
											block.setType(Material.COBBLESTONE);
										}
									}.runTask(HCF.getInstance());
								}
								while (west.hasNext()) {
									Block block = west.next();
									if (block.getY() > 90 || block.getY() < 1) continue;
									new BukkitRunnable() {
										@Override
										public void run() {
											block.setType(Material.COBBLESTONE);
										}
									}.runTask(HCF.getInstance());
								}
								y--;
							}
						});
					});
					team.setBalance(team.getBalance() - 10000);
					HCF.getInstance().getBaseCooldownMap().apply(player.getUniqueId(), JavaUtils.parse("1m") + System.currentTimeMillis());
					for (UUID knownAlt : Profile.getByUuid(player.getUniqueId()).getKnownAlts()) {
						HCF.getInstance().getBaseCooldownMap().apply(knownAlt, JavaUtils.parse("1m") + System.currentTimeMillis());
					}
					setQueued(false);
				} else {
					player.sendMessage(CC.chat("&cYou must be the leader to do this."));
				}
			}
		});

		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.NETHERRACK)
						.name(CC.chat("&6&lBox Your Claim"))
						.lore(CC.chat("&fClick to box your claim with &6netherrack&f."))
						.lore(CC.chat("&7» &fPrice: &6$50,000"))
						.lore(CC.chat(" "))
						.lore(CC.chat("&6&lInfo"))
						.lore(CC.chat("&fCreates a 16 high netherrack box with the size of your claim."))
						.build();
			}

			@Override
			public String getName(Player player) {
				return CC.chat("&6&lBox Your Claim");
			}

			@Override
			public List<String> getDescription(Player player) {
				return CC.translate(Arrays.asList(
						"&fClick to box your claim with &6netherrack&f.",
						"&7» &fPrice: &6$50,000"
				));
			}

			@Override
			public Material getMaterial(Player player) {
				return Material.NETHERRACK;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				if (team.getBalance() < 50000) {
					player.sendMessage(CC.chat("&cYou have insufficient funds to purchase this."));
					return;
				}
				if (player.getLocation().getY() >= (90 - 17)) {
					player.sendMessage(CC.chat("&cYou have to be below y level " + (90 - 17) + " to do this."));
					return;
				}
				if (queued) {
					player.sendMessage(CC.chat("&cYou cannot do this, as someone else's building a box. Try again in a few seconds!"));
					return;
				}
				if (team.isOwner(player.getUniqueId())) {
					if (HCF.getInstance().getBaseCooldownMap().hasTimer(player.getUniqueId())) {
						String format = DurationFormatter.getRemaining(HCF.getInstance().getBaseCooldownMap().getRemaining(player.getUniqueId()) - System.currentTimeMillis(), true);
						player.sendMessage(CC.chat("&cYou cannot do this for another &l" + format));
						return;
					}
					setQueued(true);

					CompletableFuture.runAsync(() -> team.getClaims().forEach(claim -> {
						int y = 80;
						while (y > 0) {
							Cuboid cuboid = new Cuboid(new Location(Bukkit.getWorld(claim.getWorld()), claim.getX1(), player.getLocation().getY(), claim.getZ1()), new Location(Bukkit.getWorld(claim.getWorld()), claim.getX2(), player.getLocation().getY() + 16.0, claim.getZ2()));
							Iterator<Block> north = cuboid.getFace(Cuboid.CuboidDirection.North).iterator();
							Iterator<Block> south = cuboid.getFace(Cuboid.CuboidDirection.South).iterator();
							Iterator<Block> east = cuboid.getFace(Cuboid.CuboidDirection.East).iterator();
							Iterator<Block> west = cuboid.getFace(Cuboid.CuboidDirection.West).iterator();
							while (north.hasNext()) {
								Block block = north.next();
								if (block.getY() > 90 || block.getY() < 1) continue;
								new BukkitRunnable() {
									@Override
									public void run() {
										block.setType(Material.NETHERRACK);
									}
								}.runTask(HCF.getInstance());
							}
							while (south.hasNext()) {
								Block block = south.next();
								if (block.getY() > 90 || block.getY() < 1) continue;
								new BukkitRunnable() {
									@Override
									public void run() {
										block.setType(Material.NETHERRACK);
									}
								}.runTask(HCF.getInstance());
							}
							while (east.hasNext()) {
								Block block = east.next();
								if (block.getY() > 90 || block.getY() < 1) continue;
								new BukkitRunnable() {
									@Override
									public void run() {
										block.setType(Material.NETHERRACK);
									}
								}.runTask(HCF.getInstance());
							}
							while (west.hasNext()) {
								Block block = west.next();
								if (block.getY() > 90 || block.getY() < 1) continue;
								new BukkitRunnable() {
									@Override
									public void run() {
										block.setType(Material.NETHERRACK);
									}
								}.runTask(HCF.getInstance());
							}
							y--;
						}

						Location loc1 = new Location(Bukkit.getWorld(claim.getWorld()), claim.getX1(), player.getLocation().getY() + 17.0, claim.getZ1());
						Location loc2 = new Location(Bukkit.getWorld(claim.getWorld()), claim.getX2(), player.getLocation().getY() + 17.0, claim.getZ2());
						double timesNeedToExecute = (loc1.distance(loc2) - 1.0);
						double timeExecuted = -1;
						while (timesNeedToExecute > 0) {
							Cuboid cuboid = new Cuboid(new Location(Bukkit.getWorld(claim.getWorld()), claim.getX1(), player.getLocation().getY() + 17.0, claim.getZ1() + timeExecuted), new Location(Bukkit.getWorld(claim.getWorld()), claim.getX2(), player.getLocation().getY() + 17.0, claim.getZ2()));
							for (Block value : cuboid.getFace(Cuboid.CuboidDirection.East)) {
								Block block = value.getRelative(BlockFace.SOUTH);
								new BukkitRunnable() {
									@Override
									public void run() {
										block.setType(Material.NETHERRACK);
									}
								}.runTask(HCF.getInstance());
							}

							timeExecuted++;
							timesNeedToExecute--;
						}
					}));
					team.getClaims().forEach(claim -> {
						Location loc1 = new Location(Bukkit.getWorld(claim.getWorld()), claim.getX1(), player.getLocation().getY() + 17.0, claim.getZ1());
						Location loc2 = new Location(Bukkit.getWorld(claim.getWorld()), claim.getX2(), player.getLocation().getY() + 17.0, claim.getZ2());
						double timesNeedToExecute = (loc1.distance(loc2) - 1.0);
						double timeExecuted = -1;
						while (timesNeedToExecute > 0) {
							Cuboid cuboid = new Cuboid(new Location(Bukkit.getWorld(claim.getWorld()), claim.getX1(), player.getLocation().getY() + 17.0, claim.getZ1() + timeExecuted), new Location(Bukkit.getWorld(claim.getWorld()), claim.getX2(), player.getLocation().getY() + 17.0, claim.getZ2()));
							for (Block value : cuboid.getFace(Cuboid.CuboidDirection.East)) {
								Block block = value.getRelative(BlockFace.SOUTH);
								new BukkitRunnable() {
									@Override
									public void run() {
										block.setType(Material.NETHERRACK);
									}
								}.runTask(HCF.getInstance());
							}

							timeExecuted++;
							timesNeedToExecute--;
						}
					});
					team.setBalance(team.getBalance() - 50000);
					team.sendMessage(CC.chat("&aA box has just been generated for your faction."));
					HCF.getInstance().getBaseCooldownMap().apply(player.getUniqueId(), JavaUtils.parse("5m") + System.currentTimeMillis());
					for (UUID knownAlt : Profile.getByUuid(player.getUniqueId()).getKnownAlts()) {
						HCF.getInstance().getBaseCooldownMap().apply(knownAlt, JavaUtils.parse("5m") + System.currentTimeMillis());
					}
					setQueued(false);
				} else {
					player.sendMessage(CC.chat("&cYou must be the leader to do this."));
				}
			}


		});

		return buttons;
	}


}

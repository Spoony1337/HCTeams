package rip.orbit.hcteams.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.pvpclasses.PvPClassHandler;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Dome extends Ability {

	public Cooldowns cd = new Cooldowns();
	public static Cooldowns antiAbility = new Cooldowns();
	public static List<Block> domes = new ArrayList<>();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				" ",
				"&7Place this to create a 3x3 area",
				"&7anyone that is in the box will be",
				"&7antibuild sticked and will not be",
				"&7able to use ability items and blindness.",
				" "
		));
	}

	@Override
	public List<String> foundInfo() {
		return CC.translate(Arrays.asList(
				"Star Shop (/starshop)",
				"Ability Packages",
				"Partner Crates"
		));
	}

	@Override
	public String displayName() {
		return CC.chat("&3&lDome");
	}

	@Override
	public String name() {
		return "dome";
	}

	@Override
	public int data() {
		return 3;
	}

	@Override
	public Material mat() {
		return Material.STAINED_GLASS;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDome(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (isSimilar(event.getItemInHand())) {
			if (!canUse(player)) {
				event.setBuild(false);
				return;
			}

			List<String> hitMsg = Arrays.asList(
					"",
					"&3You" + " &fhave just used a " + displayName(),
					" ",
					"&7┃ &fAnyone within &33 blocks&f of where",
					"&7┃ &fyou placed this, now has &3antibuild",
					"&7┃ &3effects, antiability effects, and",
					"&7┃ &3blindness&f for &315 seconds&f.",
					" ",
					"&3&lAffected Players"
			);



			List<Player> names = new ArrayList<>();

			player.getNearbyEntities(9, 9, 9).forEach(entity -> {
				if (entity instanceof Player) {
					Player p = (Player) entity;
					if (canAttack(event.getPlayer(), p)) {
						if (p.getLocation().distance(event.getBlockPlaced().getLocation()) <= 3) {
							AntiBuildStick.buildTime.applyCooldown(p, 15);
							antiAbility.applyCooldown(p, 15);
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 2));
							names.add(p);
						}
					}
				}
			});

			hitMsg.forEach(s -> player.sendMessage(CC.chat(s)));

			names.forEach(s -> {
				player.sendMessage(CC.translate("&7┃ &f" + s.getName()));
				if (PvPClassHandler.getPvPClass(player) == null) {
					s.teleport(event.getBlockPlaced().getLocation());
				}
			});

			domes.add(event.getBlockPlaced());

			event.setCancelled(true);
			takeItem(player);

			addCooldown(player, 240);

			activate(player, event.getBlockPlaced());
		}
	}

	private void activate(Player player, Block block) {

		Map<Block, Material> types = new HashMap<>();
		Map<Block, Byte> datas = new HashMap<>();

		List<Block> corners = Arrays.asList(
				block.getRelative(BlockFace.NORTH_EAST).getRelative(BlockFace.NORTH_EAST),
				block.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH_EAST),
				block.getRelative(BlockFace.SOUTH_WEST).getRelative(BlockFace.SOUTH_WEST),
				block.getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.NORTH_WEST)
		);

		List<Block> upFaces = Arrays.asList(
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.WEST),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.EAST),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST),
				block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST)
		);

		List<Block> faces = Arrays.asList(
				block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH),

				block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH),

				block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST),

				block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST),

				block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST),
				block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST),

				block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST),
				block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST),

				block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH),
				block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH),

				block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH),
				block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH)
		);

		faces.forEach(face -> {

			Block up = face.getRelative(BlockFace.UP);
			Block upup = face.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
			Block upupup = face.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP);

			types.put(face, face.getType());
			types.put(up, up.getType());
			types.put(upup, upup.getType());
			types.put(upupup, upupup.getType());

			datas.put(face, face.getData());
			datas.put(up, up.getData());
			datas.put(upup, upup.getData());
			datas.put(upupup, upupup.getData());

			face.setType(Material.STAINED_GLASS);
			up.setType(Material.STAINED_GLASS);
			upup.setType(Material.STAINED_GLASS);
			upupup.setType(Material.STAINED_GLASS);

			face.setData((byte) 3);
			up.setData((byte) 3);
			upup.setData((byte) 3);
			upupup.setData((byte) 3);
		});

		corners.forEach(corner -> {

			Block up = corner.getRelative(BlockFace.UP);
			Block upup = corner.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
			Block upupup = corner.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getRelative(BlockFace.UP);

			types.put(corner, corner.getType());
			types.put(up, up.getType());
			types.put(upup, upup.getType());
			types.put(upupup, upupup.getType());

			datas.put(corner, corner.getData());
			datas.put(up, up.getData());
			datas.put(upup, upup.getData());
			datas.put(upupup, upupup.getData());

			corner.setType(Material.STAINED_GLASS);
			up.setType(Material.STAINED_GLASS);
			upup.setType(Material.STAINED_GLASS);
			upupup.setType(Material.STAINED_GLASS);

			corner.setData((byte) 3);
			up.setData((byte) 3);
			upup.setData((byte) 3);
			upupup.setData((byte) 3);
		});

		upFaces.forEach(block1 -> {

			types.put(block1, block1.getType());
			datas.put(block1, block1.getData());

			block1.setType(Material.STAINED_GLASS);
			block1.setData((byte) 3);
		});

		new BukkitRunnable() {
			@Override
			public void run() {
				deactivate(player, block, types, datas);
			}
		}.runTaskLater(HCF.getInstance(), 20 * 15);
	}

	public static void deactivate(Player player, Block original, Map<Block, Material> blocks, Map<Block, Byte> datas) {
		CompletableFuture.runAsync(() -> {
			for (Map.Entry<Block, Material> entry : blocks.entrySet()) {
				Bukkit.getScheduler().runTask(HCF.getInstance(), () -> {
					entry.getKey().setType(entry.getValue());
				});
			}
			for (Map.Entry<Block, Byte> entry : datas.entrySet()) {
				Bukkit.getScheduler().runTask(HCF.getInstance(), () -> {
					entry.getKey().setData(entry.getValue());
				});
			}
			domes.remove(original);
			player.sendMessage(CC.translate("&fYour &3&lDome" + " &fhas just been broken."));
		});
	}
}
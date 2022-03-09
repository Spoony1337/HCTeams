package rip.orbit.hcteams.ability.items;

import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.pvpclasses.PvPClass;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/07/2021 / 5:11 PM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class GhostMode extends Ability {

	public Cooldowns cd = new Cooldowns();
	private final Set<UUID> players = new HashSet<>();
	private final Set<UUID> offline = new HashSet<>();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "ghostmode";
	}

	@Override
	public String displayName() {
		return CC.chat("&8&lGhost Mode");
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.QUARTZ;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@Override
	public List<String> lore() {
		return CC.translate(
				Arrays.asList(
						" ",
						"&7Right click to activate a sequence for 2 minutes where",
						"&7you're fully invisible until you are hit or hit a player.",
						"&7If you do hit a player you will become visible again.",
						" ",
						"&c&lNOTE&7: Anyone can still see your sprinting particles & held item.",
						" "
				)
		);
	}

	@Override
	public List<String> foundInfo() {
		return CC.translate(Arrays.asList(
				"Ability Packages",
				"Partner Crates",
				"Star Shop (/starshop)"
		));
	}


	@EventHandler
	public void onBr0Invis(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (isSimilar(event.getItem())) {
			if (!isClick(event, "RIGHT")) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			if (!canUse(player)) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			addCooldown(player, 120);
			event.setCancelled(true);
			takeItem(player);

			List<String> hitMsg = Arrays.asList(
					"",
					"&8You" + " &fhave just used a " + displayName(),
					" ",
					"&7┃ &fYou have &82 minutes&f of &8full invisibility&f.",
					"&7┃ &fIf you're hit whilst this is activated, you will",
					"&7┃ &fbe shown and no longer invisible to people.",
					"");

			hitMsg.forEach(s -> {
				player.sendMessage(CC.translate(s));
			});

			this.hidePlayer(player, 120);
		}
	}

	public void hidePlayer(Player player, int duration) {
		PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * duration, 1);
		PvPClass.setRestoreEffect(player, effect);

		updateArmor(player, true);

		this.players.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {

			}
		}.runTaskLater(HCF.getInstance(), 20 * duration);
	}

	public void hidePlayers(Player player) {
		for (UUID uuid : this.players) {
			Player online = Bukkit.getPlayer(uuid);
			if (!player.getWorld().getPlayers().contains(online)) continue;

			updateArmorFor(player, online, true);
		}
	}

	private void showPlayer(Player player, boolean forced) {
		this.players.remove(player.getUniqueId());

		if (forced) {

			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 2), true);
		}

		updateArmor(player, false);
	}

	public void updateArmor(Player player, boolean remove) {
		Set<PacketPlayOutEntityEquipment> packets = this.getEquipmentPackets(player, remove);

		for (Player other : player.getWorld().getPlayers()) {
			if (other == player) continue;

			for (PacketPlayOutEntityEquipment packet : packets) {
				((CraftPlayer)other).getHandle().playerConnection.sendPacket(packet);
			}
		}

		player.updateInventory();
	}

	public void updateArmorFor(Player player, Player target, boolean remove) {
		Set<PacketPlayOutEntityEquipment> packets = this.getEquipmentPackets(target, remove);

		for (PacketPlayOutEntityEquipment packet : packets) {
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		}
	}

	private Set<PacketPlayOutEntityEquipment> getEquipmentPackets(Player player, boolean remove) {
		Set<PacketPlayOutEntityEquipment> packets = new HashSet<>();
		for (int slot = 1; slot < 5; slot++) {
			PacketPlayOutEntityEquipment equipment;
			if (remove) {
				equipment = new PacketPlayOutEntityEquipment(player.getEntityId(), slot, null);
			} else {

				equipment = new PacketPlayOutEntityEquipment(player.getEntityId(), slot, ((CraftPlayer) player).getHandle().getEquipment(slot));
			}
			packets.add(equipment);
		}
		return packets;
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player target = (Player) event.getEntity();

		if (this.players.contains(target.getUniqueId())) {
			this.showPlayer(target, true);
			target.sendMessage(CC.RED + "You have become visible because you received damage.");
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (this.players.contains(player.getUniqueId())) {
			this.showPlayer(player, true);
			this.offline.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (this.offline.remove(player.getUniqueId())) {
			this.hidePlayer(player, 120);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (this.players.contains(player.getUniqueId())) {
			this.showPlayer(player, true);
		}
	}
}

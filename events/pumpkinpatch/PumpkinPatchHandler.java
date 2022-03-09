package rip.orbit.hcteams.events.pumpkinpatch;

import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.events.pumpkinpatch.listener.PumpkinListener;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.Claim;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.CuboidRegion;
import rip.orbit.hcteams.util.object.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/09/2021 / 8:43 PM
 * HCTeams / rip.orbit.hcteams.events.pumpkinpatch
 */

@Getter
public class PumpkinPatchHandler {

	private List<Location> locations;
	private List<ItemStack> loot;

	public PumpkinPatchHandler() {
		if (true)
			return;
		locations = new ArrayList<>();
		loot = new ArrayList<>();

		for (Ability ability : HCF.getInstance().getAbilityHandler().getAbilities()) {
			loot.add(ability.getStack());
		}
		loot.add(new ItemBuilder(Material.DIAMOND_HELMET).name(CC.translate("&6&lPatch Helmet")).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
		loot.add(new ItemBuilder(Material.DIAMOND_CHESTPLATE).name(CC.translate("&6&lPatch Chestplate")).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
		loot.add(new ItemBuilder(Material.DIAMOND_LEGGINGS).name(CC.translate("&6&lPatch Leggings")).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		loot.add(new ItemBuilder(Material.DIAMOND_BOOTS).name(CC.translate("&6&lPatch Boots")).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		loot.add(new ItemBuilder(Material.DIAMOND_SWORD).name(CC.translate("&6&lPatch Sword")).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());

		MongoDatabase database = HCF.getInstance().getMongoPool().getDatabase(HCF.MONGO_DB_NAME);
		for (Document document : database.getCollection("pumpkinPatches").find()) {

			String world = document.getString("world");
			double x = document.getDouble("x");
			double y = document.getDouble("y");
			double z = document.getDouble("z");

			locations.add(new Location(Bukkit.getWorld(world), x, y, z));
		}

		Bukkit.getPluginManager().registerEvents(new PumpkinListener(), HCF.getInstance());
		Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), this::respawn, 20 * 60 * 30, 20 * 60 * 30);

	}

	public void respawn() {
		for (Location location : locations) {
			location.getBlock().setType(Material.PUMPKIN);
		}
		Bukkit.broadcastMessage(CC.translate("&6[Pumpkin Patch]&f All pumpkins have been replaced."));
		PumpkinListener.mined = 0;
	}

	public void scanClaim() {
		MongoDatabase database = HCF.getInstance().getMongoPool().getDatabase(HCF.MONGO_DB_NAME);
		database.getCollection("pumpkinPatches").drop();

		Team team = HCF.getInstance().getTeamHandler().getTeam("PumpkinPatch");

		for (Claim claim : team.getClaims()) {
			for (Location location : new CuboidRegion("Citadel", claim.getMinimumPoint(), claim.getMaximumPoint())) {
				if (location.getBlock().getType() == Material.PUMPKIN) {
					locations.add(location);

					Document document = new Document();

					document.put("world", location.getWorld().getName());
					document.put("x", location.getX());
					document.put("y", location.getY());
					document.put("z", location.getZ());

					database.getCollection("PumpkinPatch").insertOne(document);
				}
			}
		}
	}

}

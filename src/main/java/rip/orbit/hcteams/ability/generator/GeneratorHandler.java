package rip.orbit.hcteams.ability.generator;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.gravity.profile.punishment.Punishment;
import rip.orbit.gravity.util.ItemBuilder;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.generator.listener.GeneratorListener;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:04 AM
 * HCTeams / rip.orbit.hcteams.ability.generator
 */
public class GeneratorHandler {

	@Getter private final List<Generator> generators;
	@Getter private final List<ItemStack> generatorItems;

	public GeneratorHandler() {
		generators = new ArrayList<>();
		generatorItems = new ArrayList<>();

		generatorItems.add(new ItemBuilder(Material.COAL_BLOCK)
				.name(CC.translate("&8&lTier 1 Generator"))
				.lore(CC.translate(
						Arrays.asList(
								"",
								"&8&lGenerator Information",
								"&fTier&7: &81",
								"&fLevel&7: &8%level%",
								"&fTime Per Ability&7: &81 Ability Every 15 Minutes",
								"",
								"&8&lGeneral Information",
								"&fRight Click &8to see view more info",
								"&8regarding this generator.",
								"",
								"&8&lTimes",
								"&fLevel 1&7: &815 minutes",
								"&fLevel 2&7: &814 minutes",
								"&fLevel 3&7: &813 minutes",
								"&fLevel 4&7: &812 minutes",
								"&fLevel 5&7: &810 minutes",
								"",
								"&c&lNOTE&7: Place this anywhere in your claim",
								"&7to activate the generation of this ability",
								"&7item generator. If someone gets in your claim, ",
								"&7they are able to steal it from you.",
								""
						)))
				.build());

		generatorItems.add(new ItemBuilder(Material.LAPIS_BLOCK)
				.name(CC.translate("&9&lTier 2 Generator"))
				.lore(CC.translate(
						Arrays.asList(
								"",
								"&9&lGenerator Information",
								"&fTier&7: &91",
								"&fLevel&7: &9%level%",
								"&fTime Per Ability&7: &91 Ability Every 12 Minutes",
								"",
								"&9&lGeneral Information",
								"&fRight Click &9to see view more info",
								"&9regarding this generator.",
								"",
								"&9&lTimes",
								"&fLevel 1&7: &912 minutes",
								"&fLevel 2&7: &911 minutes",
								"&fLevel 3&7: &910 minutes",
								"&fLevel 4&7: &99 minutes",
								"&fLevel 5&7: &98 minutes",
								"",
								"&c&lNOTE&7: Place this anywhere in your claim",
								"&7to activate the generation of this ability",
								"&7item generator. If someone gets in your claim, ",
								"&7they are able to steal it from you.",
								""
						)))
				.build());
		generatorItems.add(new ItemBuilder(Material.GOLD_BLOCK)
				.name(CC.translate("&e&lTier 3 Generator"))
				.lore(CC.translate(
						Arrays.asList(
								"",
								"&e&lGenerator Information",
								"&fTier&7: &e1",
								"&fLevel&7: &e%level%",
								"&fTime Per Ability&7: &e1 Ability Every 12 Minutes",
								"",
								"&e&lGeneral Information",
								"&fRight Click &eto see view more info",
								"&eregarding this generator.",
								"",
								"&e&lTimes",
								"&fLevel 1&7: &e10 minutes",
								"&fLevel 2&7: &e9 minutes",
								"&fLevel 3&7: &e8 minutes",
								"&fLevel 4&7: &e7 minutes",
								"&fLevel 5&7: &e6 minutes",
								"",
								"&c&lNOTE&7: Place this anywhere in your claim",
								"&7to activate the generation of this ability",
								"&7item generator. If someone gets in your claim, ",
								"&7they are able to steal it from you.",
								""
						)))
				.build());


		loadAll();
		Bukkit.getPluginManager().registerEvents(new GeneratorListener(), HCF.getInstance());

		Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), () -> {
			getGenerators().forEach(Generator::save);
		}, 20 * 15, 20 * 15);


		Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> {
			getGenerators().forEach(generator -> {
				int remaining = generator.getRemaining();
				generator.setRemaining(remaining - 1);
				if (generator.getRemaining() <= 0) {
					new BukkitRunnable() {
						@Override
						public void run() {
							generator.spawnAbilities();
						}
					}.runTask(HCF.getInstance());
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						if (generator.getHologram() != null) {
							generator.getHologram().removeLine(4);
							generator.getHologram().insertTextLine(4, CC.translate("&7Spawn Time: " + Punishment.TimeUtils.formatIntoMMSS(generator.getRemaining())));
						}
					}
				}.runTask(HCF.getInstance());
			});
		}, 20, 20);
	}

	public void place(UUID owner, Location location, int tier) {
		Generator generator = new Generator(UUID.randomUUID());
		this.generators.add(generator);

		generator.setTier(tier);
		generator.setRemaining(60 * generator.getDelay());
		generator.setX(location.getX());
		generator.setY(location.getY());
		generator.setZ(location.getZ());
		generator.setWorld(location.getWorld().getName());
		generator.setHologram(null);
		generator.setOwner(owner);
		generator.setLocation(location);

		generator.save();

		generator.createHologram();
	}

	public ItemStack replacedLevel(ItemStack stack, int level) {
		ItemStack replaced = stack.clone();
		ItemMeta meta = replaced.getItemMeta();

		if (ItemUtils.hasLore(replaced)) {
			List<String> newLore = new ArrayList<>();
			for (String s : meta.getLore()) {
				newLore.add(s.replaceAll("%level%", "" + level));
			}
			meta.setLore(CC.translate(newLore));
		}
		replaced.setItemMeta(meta);

		return replaced;
	}

	public void delete(Generator generator) {
		this.generators.remove(generator);
		if (generator.getHologram() != null) {
			generator.getHologram().delete();
			generator.setHologram(null);
		}

		generator.getLocation().getBlock().setType(Material.AIR);
		Generator.collection.deleteOne(Filters.eq("uuid", generator.getUuid().toString()));
	}

	public Generator byLocation(Location location) {
		for (Generator generator : this.generators) {
			if (generator.getLocation().distance(location) <= 0.2) {
				return generator;
			}
		}
		return null;
	}

	public void loadAll() {
		for (Document document : Generator.collection.find()) {
			try {
				Generator generator = new Generator(UUID.fromString(document.getString("uuid")));
				generator.setRemaining(60 * generator.getDelay());
				this.generators.add(generator);
			}catch (Exception ignored) {

			}
		}
	}

}

package rip.orbit.hcteams.events.citadel;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.serialization.LocationSerializer;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.citadel.events.CitadelCapturedEvent;
import rip.orbit.hcteams.events.citadel.file.CitadelConfigFile;
import rip.orbit.hcteams.events.citadel.listeners.CitadelListener;
import rip.orbit.hcteams.events.citadel.tasks.CitadelSaveTask;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.Claim;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.CuboidRegion;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CitadelHandler {

    public static String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";

    private File citadelInfo;
    @Getter private Set<ObjectId> cappers;
    @Getter private Date lootable;
    @Getter private CitadelConfigFile configFile;

    @Getter private Set<Location> citadelChests = new HashSet<>();
    @Getter private List<ItemStack> citadelLoot = new ArrayList<>();

    public CitadelHandler() {
        citadelInfo = new File(HCF.getInstance().getDataFolder(), "citadelInfo.json");
        configFile = new CitadelConfigFile(HCF.getInstance(), "citadelloot", HCF.getInstance().getDataFolder().getAbsolutePath());
        loadCitadelInfo();
        HCF.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), HCF.getInstance());

        (new CitadelSaveTask()).runTaskTimerAsynchronously(HCF.getInstance(), 0L, 20 * 60 * 5);
    }

    public void loadCitadelInfo() {
        try {
            if (!citadelInfo.exists() && citadelInfo.createNewFile()) {
                BasicDBObject dbo = new BasicDBObject();

                dbo.put("cappers", new HashSet<>());
                dbo.put("lootable", new Date());
                dbo.put("chests", new BasicDBList());
                dbo.put("loot", new BasicDBList());

                FileUtils.write(citadelInfo, qLib.GSON.toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(citadelInfo));

            if (dbo != null) {
                this.cappers = new HashSet<>();

                // Conversion
                if (dbo.containsField("capper")) {
                    cappers.add(new ObjectId(dbo.getString("capper")));
                }

                for (String capper : (List<String>) dbo.get("cappers")) {
                    cappers.add(new ObjectId(capper));
                }

                this.lootable = dbo.getDate("lootable");

                BasicDBList chests = (BasicDBList) dbo.get("chests");

                for (Object chestObj : chests) {
                    BasicDBObject chest = (BasicDBObject) chestObj;
                    citadelChests.add(LocationSerializer.deserialize((BasicDBObject) chest.get("location")));
                }
                YamlConfiguration con =  getConfigFile().getConfiguration();
                for (String sec :  getConfigFile().getConfiguration().getConfigurationSection("loot").getKeys(false)) {
                    String main = "loot.";
                    ItemStack i = new ItemStack(Material.valueOf(con.getString(main + sec + ".Material")));
//                            .displayName(CC.chat(con.getString(main + sec + ".Name").replaceAll("§", "&")))
//                            .setLore(CC.translate(con.getStringList(main + sec + ".Lore")))
//                            .data(((short)con.getInt(main + sec + ".Data")))
//                            .enchant(Enchantment.getByName(con.getString(main + sec + ".Enchants.Type")), con.getInt(main + sec + ".Level"))
                    ItemMeta meta = i.getItemMeta();
                    if (con.contains("loot." + sec + ".Name")) {
                        meta.setDisplayName(CC.translate(con.getString(main + sec + ".Name").replaceAll("§", "&")));
                    }

                    if (con.contains("loot." + sec + ".Lore")) {
                        meta.setLore(CC.translate(con.getStringList(main + sec + ".Lore")));
                    }
                    i.setDurability(((short)con.getInt(main + sec + ".Data")));
                    i.setAmount(con.getInt(main + sec + ".Amount"));
                    i.setItemMeta(meta);
                    if (con.contains("loot." + sec + ".Enchants")) {
                        for (String s : con.getConfigurationSection(main + sec + ".Enchants").getKeys(false)) {
                            try {
                                i.addEnchantment(Enchantment.getByName(con.getString(main + sec + ".Enchants." + s + ".Type")), con.getInt(main + sec + ".Enchants." + s + ".Level"));
                            } catch (IllegalArgumentException e) {
                                i.addUnsafeEnchantment(Enchantment.getByName(con.getString(main + sec + ".Enchants." + s + ".Type")), con.getInt(main + sec + ".Enchants." + s + ".Level"));

                            }
                        }
                    }
                    citadelLoot.add(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCitadelInfo() {
        try {
            BasicDBObject dbo = new BasicDBObject();

            dbo.put("cappers", cappers.stream().map(ObjectId::toString).collect(Collectors.toList()));
            dbo.put("lootable", lootable);

            BasicDBList chests = new BasicDBList();

            for (Location citadelChest : citadelChests) {
                BasicDBObject chest = new BasicDBObject();
                chest.put("location", LocationSerializer.serialize(citadelChest));
                chests.add(chest);
            }
            getConfigFile().getConfiguration().set("loot", null);
            for (ItemStack lootItem : citadelLoot) {
                UUID unique = UUID.randomUUID();
                String main = "loot.";
                getConfigFile().getConfiguration().createSection(main + "" + unique);
                getConfigFile().getConfiguration().createSection(main + "" + unique + ".Material");
                getConfigFile().getConfiguration().createSection(main + "" + unique + ".Lore");
                getConfigFile().getConfiguration().createSection(main + "" + unique + ".Name");
                getConfigFile().getConfiguration().createSection(main + "" + unique + ".Data");
                getConfigFile().getConfiguration().createSection(main + "" + unique + ".Amount");
                getConfigFile().getConfiguration().set(main + "" + unique + ".Material", lootItem.getType().name());
                getConfigFile().getConfiguration().set(main + "" + unique + ".Lore", lootItem.getItemMeta().getLore());
                getConfigFile().getConfiguration().set(main + "" + unique + ".Name", lootItem.getItemMeta().getDisplayName());
                getConfigFile().getConfiguration().set(main + "" + unique + ".Data", lootItem.getDurability());
                getConfigFile().getConfiguration().set(main + "" + unique + ".Amount", lootItem.getAmount());
                if (!lootItem.getEnchantments().isEmpty()) {
                    for (Enchantment e : lootItem.getEnchantments().keySet()) {
                        UUID id = UUID.randomUUID();
                        getConfigFile().getConfiguration().createSection(main + "" + unique + ".Enchants." + id);
                        getConfigFile().getConfiguration().createSection(main + "" + unique + ".Enchants." + id + ".Type");
                        getConfigFile().getConfiguration().createSection(main + "" + unique + ".Enchants." + id + ".Level");
                        getConfigFile().getConfiguration().set(main + "" + unique + ".Enchants." + id + ".Type", e.getName());
                        getConfigFile().getConfiguration().set(main + "" + unique + ".Enchants." + id + ".Level", lootItem.getEnchantmentLevel(e));
                    }
                }
                getConfigFile().save();
            }

            dbo.put("chests", chests);

            citadelInfo.delete();
            FileUtils.write(citadelInfo, qLib.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCappers() {
        this.cappers.clear();
    }

    public void addCapper(ObjectId capper) {
        this.cappers.add(capper);
        this.lootable = generateLootableDate();

        HCF.getInstance().getServer().getPluginManager().callEvent(new CitadelCapturedEvent(capper));
        saveCitadelInfo();
    }

    public boolean canLootCitadel(Player player) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(player);
        return ((team != null && cappers.contains(team.getUniqueId())));
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.TUESDAY  - date.get(Calendar.DAY_OF_WEEK);

        if (diff <= 0) {
            diff += 7;
        }

        date.add(Calendar.DAY_OF_MONTH, diff);

        // 11:59 PM
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);

        return (date.getTime());
    }

    public void scanLoot() {
        citadelChests.clear();

        for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.hasDTRBitmask(DTRBitmask.CITADEL)) {
                for (Claim claim : team.getClaims()) {
                    for (Location location : new CuboidRegion("Citadel", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                        if (location.getBlock().getType() == Material.CHEST) {
                            citadelChests.add(location);
                        }
                    }
                }
            }
        }
    }

    public int respawnCitadelChests() {
        int respawned = 0;

        for (Location chest : citadelChests) {
            if (respawnCitadelChest(chest)) {
                respawned++;
            }
        }

        return (respawned);
    }

    public boolean respawnCitadelChest(Location location) {
        BlockState blockState = location.getBlock().getState();

        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;

            chest.getBlockInventory().clear();
            chest.getBlockInventory().addItem(citadelLoot.get(qLib.RANDOM.nextInt(citadelLoot.size())));
            chest.getBlockInventory().addItem(citadelLoot.get(qLib.RANDOM.nextInt(citadelLoot.size())));
            chest.getBlockInventory().addItem(citadelLoot.get(qLib.RANDOM.nextInt(citadelLoot.size())));
            return (true);
        } else {
            HCF.getInstance().getLogger().warning("Citadel chest defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
            return (false);
        }
    }

}
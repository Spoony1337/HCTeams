package rip.orbit.hcteams.events.region.cavern;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.region.cavern.listeners.CavernListener;
import rip.orbit.hcteams.team.claims.Claim;

import java.io.File;
import java.io.IOException;

public class CavernHandler {

    private static File file;
    @Getter private static String cavernTeamName = "Cavern";
    @Getter @Setter private Cavern cavern;

    public CavernHandler() {
        try {
            file = new File(HCF.getInstance().getDataFolder(), "cavern.json");

            if (!file.exists()) {
                cavern = null;

                if (file.createNewFile()) {
                    HCF.getInstance().getLogger().warning("Created a new Cavern json file.");
                }
            } else {
                cavern = qLib.GSON.fromJson(FileUtils.readFileToString(file), Cavern.class);
                HCF.getInstance().getLogger().info("Successfully loaded the Cavern from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HCF.getInstance().getServer().getScheduler().runTaskTimer(HCF.getInstance(), () -> {
            if (getCavern() == null || HCF.getInstance().getTeamHandler().getTeam(cavernTeamName) == null) return;
            getCavern().reset();
            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.AQUA + "[Cavern]" + ChatColor.GREEN + " All ores have been reset!");
        }, 20 * 60 * 60, 20 * 60 * 60);

        HCF.getInstance().getServer().getPluginManager().registerEvents(new CavernListener(), HCF.getInstance());
    }

    public void save() {
        try {
            FileUtils.write(file, qLib.GSON.toJson(cavern));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCavern() {
        return cavern != null;
    }

    public static Claim getClaim() {
        return HCF.getInstance().getTeamHandler().getTeam(cavernTeamName).getClaims().get(0); // null if no glowmtn is set!
    }
}
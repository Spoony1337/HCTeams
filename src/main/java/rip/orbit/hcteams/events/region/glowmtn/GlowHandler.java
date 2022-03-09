package rip.orbit.hcteams.events.region.glowmtn;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.region.glowmtn.listeners.GlowListener;
import rip.orbit.hcteams.team.claims.Claim;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GlowHandler {

    private static File file;
    @Getter private static String glowTeamName = "Glowstone";
    @Getter @Setter private GlowMountain glowMountain;

    public GlowHandler() {
        try {
            file = new File(HCF.getInstance().getDataFolder(), "glowmtn.json");

            if (!file.exists()) {
                glowMountain = null;

                if (file.createNewFile()) {
                    HCF.getInstance().getLogger().warning("Created a new glow mountain json file.");
                }
            } else {
                glowMountain = qLib.GSON.fromJson(FileUtils.readFileToString(file), GlowMountain.class);
                HCF.getInstance().getLogger().info("Successfully loaded the glow mountain from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int secs = HCF.getInstance().getServerHandler().isHardcore() ? 108000 : (HCF.getInstance().getServerHandler().getTabServerName().contains("cane") ? (HCF.getInstance().getMapHandler().getTeamSize() == 8 ? 30000 : '티') : 12000);
        HCF.getInstance().getServer().getScheduler().runTaskTimer(HCF.getInstance(), () -> {
            getGlowMountain().reset();

            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.YELLOW + " All glowstone has been reset!");
        }, 0L, (HCF.getInstance().getConfig().getInt("Glowstone-Reset-Time") * 20));

        HCF.getInstance().getServer().getPluginManager().registerEvents(new GlowListener(), HCF.getInstance());
    }

    public void save() {
        try {
            FileUtils.write(file, qLib.GSON.toJson(glowMountain));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGlowMountain() {
        return glowMountain != null;
    }

    public static Claim getClaim() {
        List<Claim> claims = HCF.getInstance().getTeamHandler().getTeam(glowTeamName).getClaims();
        return claims.isEmpty() ? null : claims.get(0); // null if no glowmtn is set!
    }
}
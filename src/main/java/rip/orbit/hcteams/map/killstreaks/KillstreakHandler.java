package rip.orbit.hcteams.map.killstreaks;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.ClassUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.deathmessage.event.PlayerKilledEvent;
import rip.orbit.hcteams.map.stats.StatsEntry;
import rip.orbit.hcteams.team.Team;

import java.util.List;
import java.util.stream.Collectors;

public class KillstreakHandler implements Listener {

    @Getter private List<Killstreak> killstreaks = Lists.newArrayList();
    @Getter private List<PersistentKillstreak> persistentKillstreaks = Lists.newArrayList();

    public KillstreakHandler() {
        HCF.getInstance().getServer().getPluginManager().registerEvents(this, HCF.getInstance());
        FrozenCommandHandler.registerClass(this.getClass());

        ClassUtils.getClassesInPackage(HCF.getInstance(), "rip.orbit.hcteams.map.killstreaks.orbittypes").forEach(clazz -> {
            if (Killstreak.class.isAssignableFrom(clazz)) {
                try {
                    Killstreak killstreak = (Killstreak) clazz.newInstance();

                    killstreaks.add(killstreak);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    PersistentKillstreak killstreak = (PersistentKillstreak) clazz.newInstance();

                    persistentKillstreaks.add(killstreak);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        killstreaks.sort((first, second) -> {
            int firstNumber = first.getKills()[0];
            int secondNumber = second.getKills()[0];

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
        
        persistentKillstreaks.sort((first, second) -> {
            int firstNumber = first.getKillsRequired();
            int secondNumber = second.getKillsRequired();

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
    }

    public Killstreak check(int kills) {
        for (Killstreak killstreak : killstreaks) {
            for (int kill : killstreak.getKills()) {
                if (kills == kill) {
                    return killstreak;
                }
            }
        }

        return null;
    }
    
    public List<PersistentKillstreak> getPersistentKillstreaks(Player player, int count) {
        return persistentKillstreaks.stream().filter(s -> s.check(count)).collect(Collectors.toList());
    }

    private void grantTeamKillstreakReward(Player player, Team team, int killstreak, int points) {
        team.addKillstreakPoints(points);
        team.sendMessage(ChatColor.GREEN + "Your team received " + points + " points thanks to " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GREEN + "'s " + killstreak + " killstreak.");
    }

    @EventHandler
    public void onPlayerKilledEvent(PlayerKilledEvent event) {
        StatsEntry killerStats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(event.getKiller());
        Team killerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getKiller());

        if (killerTeam != null) {
            // Check for team killstreak points rewards
            switch (killerStats.getKillstreak()) {
                case 75:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 75, 50);
                    break;
                case 150:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 150, 75);
                    break;
                case 300:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 300, 250);
                    break;
                case 400:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 400, 450);
                    break;
                case 500:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 500, 500);
                    break;
                case 1000:
                    grantTeamKillstreakReward(event.getKiller(), killerTeam, 1000, 750);
                    break;
            }
        }
    }

    @Command(names = "setks", permission = "hcteams.setkillstreak")
    public static void setKillstreak(Player player, @Param(name = "killstreak") int killstreak) {
        if (!HCF.getInstance().getMapHandler().isKitMap() && !HCF.getInstance().getServerHandler().isVeltKitMap()) {
            player.sendMessage("§cThis is a KitMap only command.");
            return;
        }

        StatsEntry statsEntry = HCF.getInstance().getMapHandler().getStatsHandler().getStats(player);
        statsEntry.setKillstreak(killstreak);

        player.sendMessage(ChatColor.GREEN + "You set your killstreak to: " + killstreak);
    }

}

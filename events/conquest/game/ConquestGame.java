package rip.orbit.hcteams.events.conquest.game;

import lombok.Getter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.gravity.profile.global.GlobalInfo;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.events.conquest.ConquestHandler;
import rip.orbit.hcteams.events.conquest.enums.ConquestCapzone;
import rip.orbit.hcteams.events.events.EventCapturedEvent;
import rip.orbit.hcteams.events.koth.KOTH;
import rip.orbit.hcteams.events.koth.events.EventControlTickEvent;
import rip.orbit.hcteams.events.koth.events.KOTHControlLostEvent;
import rip.orbit.hcteams.team.Team;

import java.util.*;

public class ConquestGame implements Listener {

    @Getter private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();

    public ConquestGame() {
        HCF.getInstance().getServer().getPluginManager().registerEvents(this, HCF.getInstance());

        for (Event event : HCF.getInstance().getEventHandler().getEvents()) {
            if (event.getType() != EventType.KOTH) continue;
            KOTH koth = (KOTH) event;
            if (koth.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
                if (!koth.isHidden()) {
                    koth.setHidden(true);
                }

                if (koth.getCapTime() != ConquestHandler.TIME_TO_CAP) {
                    koth.setCapTime(ConquestHandler.TIME_TO_CAP);
                }

                koth.activate();
            }
        }

        HCF.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has started! Use /conquest for more information.");
        HCF.getInstance().getConquestHandler().setGame(this);
    }

    public void endGame(Team winner) {
        if (winner == null) {
            HCF.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has ended.");
        } else {
            HCF.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + winner.getName() + ChatColor.GOLD + " has won Conquest!");
        }

        for (Event koth : HCF.getInstance().getEventHandler().getEvents()) {
            if (koth.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
                koth.deactivate();
            }
        }

        HandlerList.unregisterAll(this);
        HCF.getInstance().getConquestHandler().setGame(null);
    }

    @EventHandler
    public void onKOTHCaptured(EventCapturedEvent event) {
        if (!event.getEvent().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getEvent().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        if (teamPoints.containsKey(team.getUniqueId())) {
            teamPoints.put(team.getUniqueId(), teamPoints.get(team.getUniqueId()) + 1);
        } else {
            teamPoints.put(team.getUniqueId(), 1);
        }

        teamPoints = sortByValues(teamPoints);
        HCF.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + team.getName() + ChatColor.GOLD + " captured " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + " and earned a point!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) +
                "/" + ConquestHandler.getPointsToWin() + ")");

        if (teamPoints.get(team.getUniqueId()) >= ConquestHandler.getPointsToWin()) {
            endGame(team);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + event.getPlayer().getName() + " Event 5");
            team.getMembers().forEach(uuid -> {
                Profile profile = Profile.getByUuid(uuid);
                GlobalInfo info = profile.getGlobalInfo();
                if (HCF.getInstance().getMapHandler().isKitMap()) {
                    info.setKitsConquestCaps(info.getKitsConquestCaps() + 1);
                } else {
                    info.setHcfConquestCaps(info.getHcfConquestCaps() + 1);
                }
                profile.save();
            });

        } else {
            new BukkitRunnable() {

                @Override
				public void run() {
                    if (HCF.getInstance().getConquestHandler().getGame() != null) {
                        event.getEvent().activate();
                    }
                }

            }.runTaskLater(HCF.getInstance(), 10L);
        }
    }

    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {
        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));
        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());

        if (team == null) {
            return;
        }

        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " " + event.getKOTH().getCurrentCapper() + " was knocked off of " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!");
    }
    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        
        if (!event.getKOTH().getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX) || event.getKOTH().getRemainingCapTime() % 5 != 0) {
            return;
        }

        ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace(ConquestHandler.KOTH_NAME_PREFIX, "").toUpperCase());
        Player capper = HCF.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());

        if (capper != null) {
            capper.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Attempting to capture " + capzone.getColor() + capzone.getName() + ChatColor.GOLD + "!" + ChatColor.AQUA + " (" + event.getKOTH().getRemainingCapTime() + "s)");
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(event.getEntity());

        if (team == null || !teamPoints.containsKey(team.getUniqueId())) {
            return;
        }

        teamPoints.put(team.getUniqueId(), Math.max(0, teamPoints.get(team.getUniqueId()) - ConquestHandler.POINTS_DEATH_PENALTY));
        teamPoints = sortByValues(teamPoints);
        team.sendMessage(ConquestHandler.PREFIX + ChatColor.GOLD + " Your team has lost " + ConquestHandler.POINTS_DEATH_PENALTY + " points because of " + event.getEntity().getName() + "'s death!" + ChatColor.AQUA + " (" + teamPoints.get(team.getUniqueId()) + "/" + ConquestHandler.getPointsToWin() + ")");
    }

    private static LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
        LinkedList<Map.Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();
        Iterator<Map.Entry<ObjectId, Integer>> iterator = list.iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<ObjectId, Integer> entry = iterator.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

}
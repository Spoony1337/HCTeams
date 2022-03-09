package rip.orbit.hcteams.tab;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventScheduledTime;
import rip.orbit.hcteams.events.koth.KOTH;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.commands.team.TeamListCommand;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.object.PlayerDirection;

import java.util.*;

public class DefaultFoxtrotTabLayoutProvider implements LayoutProvider {

    private LinkedHashMap<Team, Integer> cachedTeamList = Maps.newLinkedHashMap();
    long cacheLastUpdated;



    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);
        TabListMode mode = HCF.getInstance().getTabListModeMap().getTabListMode(player.getUniqueId());

        String serverName = HCF.getInstance().getServerHandler().getTabServerName();
        Team team = HCF.getInstance().getTeamHandler().getTeam(player);
        String color = HCF.getInstance().getServerHandler().getTabSectionColor();
        String second = HCF.getInstance().getServerHandler().getTabInfoColor();
        String accent = "&f";

        int size = Bukkit.getOnlinePlayers().size();
        for (Player on : Bukkit.getOnlinePlayers()) {
            if (!player.canSee(on)) {
                size = Bukkit.getOnlinePlayers().size() - 1;
            }
        }
        layout.set(1, 0, CC.chat(serverName + " &7(" + size + ")"));

        int y = -1;

        if (team != null) {
//            layout.set(0, ++y, CC.chat(color + "Home:"));
//
//            if (team.getHQ() != null) {
//                String homeLocation = second.toString() + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();
//                layout.set(0, ++y, homeLocation);
//            } else {
//                layout.set(0, ++y, second + "Not Set");
//            }

            ++y; // blank

            int balance = (int) team.getBalance();
            layout.set(0, ++y, color + "Team Info");
            layout.set(0, ++y, second + "HQ" + accent + ": " + (team.getHq() == null ? "Not Set" : "" + team.getHq().getBlockX() + ", " + team.getHq().getBlockY() + ", " + team.getHq().getBlockZ()));
            layout.set(0, ++y, second + "DTR" + accent + "&f: " + (team.isRaidable() ? ChatColor.DARK_RED : second) + team.getDTRColor() + Team.DTR_FORMAT.format(team.getDTR()));
            layout.set(0, ++y, second + "Online" + accent + ": " + team.getOnlineMemberAmount() + "/" + team.getMembers().size());
            layout.set(0, ++y, second + "Balance" + accent + ": $" + balance);

            ++y; // blank
        }

        layout.set(0, ++y, color + "Statistics");
        layout.set(0, ++y, second + "Kills" + accent + ": " + HCF.getInstance().getMapHandler().getStatsHandler().getStats(player).getKills());
        layout.set(0, ++y, second + "Deaths" + accent + ": " + HCF.getInstance().getMapHandler().getStatsHandler().getStats(player).getDeaths());

        ++y; // blank

        layout.set(0, ++y, color + "Your Location");

        String location;

        Location loc = player.getLocation();
        Team ownerTeam = LandBoard.getInstance().getTeam(loc);

        if (ownerTeam != null) {
            location = ownerTeam.getName(player.getPlayer());
        } else if (!HCF.getInstance().getServerHandler().isWarzone(loc)) {
            location = ChatColor.GRAY + "The Wilderness";
        } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance().getTeam(loc).getName().equalsIgnoreCase("citadel")) {
            location = color + "Citadel";
        } else {
            location = ChatColor.RED + "Warzone";
        }

        layout.set(0, ++y, location);

        /* Getting the direction 4 times a second for each player on the server may be intensive.
        We may want to cache the entire location so it is accessed no more than 1 time per second.
        FIXME, WIP */
        String direction = PlayerDirection.getCardinalDirection(player);
        if (direction != null) {
            layout.set(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ") [" + direction + "]");
        } else {
            layout.set(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
        }
        ++y; // blank

        KOTH activeKOTH = null;
        for (Event event : HCF.getInstance().getEventHandler().getEvents()) {
            if (!(event instanceof KOTH)) continue;
            KOTH koth = (KOTH) event;
            if (koth.isActive() && !koth.isHidden()) {
                activeKOTH = koth;
                break;
            }
        }

        if (activeKOTH == null) {
            Date now = new Date();

            String nextKothName = null;
            Date nextKothDate = null;

            for (Map.Entry<EventScheduledTime, String> entry : HCF.getInstance().getEventHandler().getEventSchedule().entrySet()) {
                if (entry.getKey().toDate().after(now)) {
                    if (nextKothDate == null || nextKothDate.getTime() > entry.getKey().toDate().getTime()) {
                        nextKothName = entry.getValue();
                        nextKothDate = entry.getKey().toDate();
                    }
                }
            }

            if (nextKothName != null) {
                layout.set(0, ++y, color + "Next KOTH:");
                layout.set(0, ++y, second + nextKothName);

                Event event = HCF.getInstance().getEventHandler().getEvent(nextKothName);

                if (event != null && event instanceof KOTH) {
                    KOTH koth = (KOTH) event;
                    layout.set(0, ++y, second.toString() + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ()); // location

                    int seconds = (int) ((nextKothDate.getTime() - System.currentTimeMillis()) / 1000);
                    layout.set(0, ++y, color + "Goes active in:");

                    String time = formatIntoDetailedString(seconds)
                            .replace("minutes", "min").replace("minute", "min")
                            .replace("seconds", "sec").replace("second", "sec");

                    layout.set(0, ++y, second + time);
                }
            }
        } else {
            layout.set(0, ++y, color + activeKOTH.getName());
            layout.set(0, ++y, second + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime()));
            layout.set(0, ++y, second.toString() + activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ()); // location
        }

        if (team != null) {
            layout.set(1, 2, color + team.getName());

            String watcherName = ChatColor.DARK_GREEN + player.getName();
            if (team.isOwner(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "***";
            } else if (team.isCoLeader(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCaptain(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "*";
            }

            layout.set(1, 3, watcherName, ((CraftPlayer) player).getHandle().ping); // the viewer is always first on the list

            Player owner = null;
            List<Player> coleaders = Lists.newArrayList();
            List<Player> captains = Lists.newArrayList();
            List<Player> members = Lists.newArrayList();
            for (Player member : team.getOnlineMembers()) {
                if (team.isOwner(member.getUniqueId())) {
                    owner = member;
                } else if (team.isCoLeader(member.getUniqueId())) {
                    coleaders.add(member);
                } else if (team.isCaptain(member.getUniqueId())) {
                    captains.add(member);
                } else {
                    members.add(member);
                }
            }

            int x = 1;
            y = mode == TabListMode.DETAILED ? 4 : 7;

            // then the owner
            if (owner != null && owner != player) {
                layout.set(x, y, ChatColor.DARK_GREEN + owner.getName() + ChatColor.GRAY + "**", ((CraftPlayer) owner).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // then the coleaders
            for (Player coleader : coleaders) {
                if (coleader == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + coleader.getName() + ChatColor.GRAY + "**", ((CraftPlayer) coleader).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }


            // then the captains
            for (Player captain : captains) {
                if (captain == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + captain.getName() + ChatColor.GRAY + "*", ((CraftPlayer) captain).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // and only then, normal members.
            for (Player member : members) {
                if (member == player) continue;

                layout.set(x, y, ChatColor.DARK_GREEN + member.getName(), ((CraftPlayer) member).getHandle().ping);

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // basically, if we're not on the third column yet, set the y to 0, and go to the third column.
            // if we're already there, just place whatever we got under the last player's name
            if (x < 2) {
                y = 0;
            } else {
                y++; // comment this out if you don't want a space in between the last player and the info below:
            }
        }

        if (team == null) {
            y = 0;
        }

        // faction list (10 entries)
        boolean shouldReloadCache = cachedTeamList == null || (System.currentTimeMillis() - cacheLastUpdated > 2000);

        y = 1;

        Map<Team, Integer> teamPlayerCount = new HashMap<>();

        if (shouldReloadCache) {
            // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
            for (Player other : HCF.getInstance().getServer().getOnlinePlayers()) {
                if (other.hasMetadata("invisible")) {
                    continue;
                }

                Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(other);

                if (playerTeam != null) {
                    if (teamPlayerCount.containsKey(playerTeam)) {
                        teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                    } else {
                        teamPlayerCount.put(playerTeam, 1);
                    }
                }
            }
        }

        LinkedHashMap<Team, Integer> sortedTeamPlayerCount;

        if (shouldReloadCache) {
            sortedTeamPlayerCount = TeamListCommand.sortByValues(teamPlayerCount);
            cachedTeamList = sortedTeamPlayerCount;
            cacheLastUpdated = System.currentTimeMillis();
        } else {
            sortedTeamPlayerCount = cachedTeamList;
        }

        int index = 0;

        boolean title = false;

        for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
            index++;

            if (index > 19) {
                break;
            }

            if (!title) {
                title = true;
                layout.set(2, 0, color + "Team List");
            }

            String teamName = teamEntry.getKey().getName();
            String teamColor = teamEntry.getKey().isMember(player.getUniqueId()) ? ChatColor.GREEN.toString() : second;

            if (teamName.length() > 10) teamName = teamName.substring(0, 10);

            layout.set(2, y++, teamColor + teamName + ChatColor.GRAY + " (" + teamEntry.getValue() + ")");
        }
        return layout;
    }
    public static String formatIntoDetailedString(int secs) {
        if (secs <= 60) {
            return "1 minute";
        } else {
            int remainder = secs % 86400;
            int days = secs / 86400;
            int hours = remainder / 3600;
            int minutes = remainder / 60 - hours * 60;
            String fDays = days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "";
            String fHours = hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "";
            String fMinutes = minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "";
            return (fDays + fHours + fMinutes).trim();
        }

    }

}

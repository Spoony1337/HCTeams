package rip.orbit.hcteams.chat.listeners;

import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.HCFConstants;
import rip.orbit.hcteams.chat.ChatHandler;
import rip.orbit.hcteams.chat.enums.ChatMode;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.commands.team.TeamMuteCommand;
import rip.orbit.hcteams.team.commands.team.TeamShadowMuteCommand;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;

import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private String getCustomPrefix(UUID uuid) {
        Map<Integer, UUID> placesMap = HCF.getInstance().getMapHandler().getStatsHandler().getTopKills();

        int place = placesMap.size() == 3 ? placesMap.get(1).equals(uuid) ? 1 : placesMap.get(2).equals(uuid) ? 2 : placesMap.get(3).equals(uuid) ? 3 : 99 : 99;
        return ChatColor.translateAlternateColorCodes('&', place == 1 ? "&8[&6#1&8] " : place == 2 ? "&8[&7#2&8] " : place == 3 ? "&8[&f#3&8] " : "");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEarly(AsyncPlayerChatEvent event) {
        ChatMode playerChatMode = HCF.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
        ChatMode finalChatMode;

        if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
        } else {
            finalChatMode = playerChatMode;
        }

        if (finalChatMode != ChatMode.PUBLIC) {
            event.getPlayer().setMetadata("NoSpamCheck", new FixedMetadataValue(HCF.getInstance(), true));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().removeMetadata("NoSpamCheck", HCF.getInstance());

        Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

        String rankPrefix = Profile.getByUuid(event.getPlayer().getUniqueId()).getActiveRank().getPrefix() + Profile.getByUuid(event.getPlayer().getUniqueId()).getActiveRank().getColor() + event.getPlayer().getName();

        String ranksuffix = Profile.getByUuid(event.getPlayer().getUniqueId()).getActiveRank().getSuffix();
        String customPrefix = Profile.getByUuid(event.getPlayer().getUniqueId()).getTag().getPrefix() + " " + getCustomPrefix(event.getPlayer().getUniqueId());
        ChatMode playerChatMode = HCF.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
        ChatMode finalChatMode;

        if (forcedChatMode != null) {
            event.setMessage(event.getMessage().substring(1).trim());
        }
        if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
        } else {
            finalChatMode = playerChatMode;
        }

        if (event.isCancelled() && finalChatMode == ChatMode.PUBLIC) {
            return;
        }

        event.setCancelled(true);

        if (finalChatMode != ChatMode.PUBLIC && playerTeam == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a team!");
            return;
        }

        if (finalChatMode != ChatMode.PUBLIC) {
            if (playerTeam == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a team!");
                return;
            } else if (finalChatMode == ChatMode.OFFICER && !playerTeam.isCaptain(event.getPlayer().getUniqueId()) && !playerTeam.isCoLeader(event.getPlayer().getUniqueId()) && !playerTeam.isOwner(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in officer chat if you're not an officer!");
                return;
            }
        }

        switch (finalChatMode) {
            case PUBLIC:
                if (TeamMuteCommand.getTeamMutes().containsKey(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team is muted!");
                    return;
                }

                String publicChatFormat = HCFConstants.publicChatFormat(playerTeam, rankPrefix, customPrefix);

                String finalMessage = String.format(publicChatFormat, ranksuffix, event.getMessage());


                for (Player player : event.getRecipients()) {
                    if (playerTeam == null) {
                        if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                            continue;
                        }

                        if (event.getPlayer().isOp() || HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                            player.sendMessage(finalMessage);
                        }
                    } else {
                        if (playerTeam.isMember(player.getUniqueId())) {
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + HCF.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + ChatColor.DARK_GREEN));
                        } else if (playerTeam.isAlly(player.getUniqueId())) {
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + HCF.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + Team.ALLY_COLOR));
                        } else {
                            if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                                continue;
                            }

                            if (event.getPlayer().isOp() || HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                                player.sendMessage(finalMessage);
                            }
                        }
                    }
                }

                ChatHandler.getPublicMessagesSent().incrementAndGet();
                HCF.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
                break;
            case ALLIANCE:
                String allyChatFormat = HCFConstants.allyChatFormat(event.getPlayer(), event.getMessage());
                String allyChatSpyFormat = HCFConstants.allyChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(player.getUniqueId()) || playerTeam.isAlly(player.getUniqueId())) {
                        player.sendMessage(allyChatFormat);
                    } else if (HCF.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        player.sendMessage(allyChatSpyFormat);
                    }
                }

                for (ObjectId allyId : playerTeam.getAllies()) {
                    Team ally = HCF.getInstance().getTeamHandler().getTeam(allyId);

                    if (ally != null) {
                        TeamActionTracker.logActionAsync(ally, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.<String, Object>builder()
                                .put("allyTeamId", playerTeam.getUniqueId())
                                .put("allyTeamName", playerTeam.getName())
                                .put("playerId", event.getPlayer().getUniqueId())
                                .put("playerName", event.getPlayer().getName())
                                .put("message", event.getMessage())
                                .build()
                        );
                    }
                }

                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));

                HCF.getInstance().getServer().getLogger().info("[Ally Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
            case TEAM:
                String teamChatFormat = HCFConstants.teamChatFormat(event.getPlayer(), event.getMessage());
                String teamChatSpyFormat = HCFConstants.teamChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(player.getUniqueId())) {
                        player.sendMessage(teamChatFormat);
                    } else if (HCF.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        player.sendMessage(teamChatSpyFormat);
                    }
                }

                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.TEAM_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));

                HCF.getInstance().getServer().getLogger().info("[Team Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
            case OFFICER:
                String officerChatFormat = HCFConstants.officerChatFormat(event.getPlayer(), event.getMessage());

                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isCaptain(player.getUniqueId()) || playerTeam.isCoLeader(player.getUniqueId()) || playerTeam.isOwner(player.getUniqueId())) {
                        player.sendMessage(officerChatFormat);
                    }
                }

                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.OFFICER_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));

                HCF.getInstance().getServer().getLogger().info("[Officer Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
        }
    }
}
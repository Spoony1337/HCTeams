package rip.orbit.hcteams.map.game.impl.spleef;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.util.ItemBuilder;
import net.frozenorb.qlib.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;
import rip.orbit.hcteams.map.game.GameState;
import rip.orbit.hcteams.map.game.GameType;
import rip.orbit.hcteams.map.game.arena.GameArena;
import rip.orbit.hcteams.util.item.InventoryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpleefGame extends Game {

    public SpleefGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.SPLEEF, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        // just in case whoever made the arena forgets to set bounds, null check so no npe
        if (getVotedArena().getBounds() != null) {
            getVotedArena().createSnapshot();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    startSpleef();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        // another null check
        if (getVotedArena().getBounds() != null) {
            // restore arena after everyone is teleported out
            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    private void startSpleef() {
        for (Player player : getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.getInventory().setItem(0, ItemBuilder.of(Material.DIAMOND_SPADE).enchant(Enchantment.DIG_SPEED, 5).build());
        }

        // split players into 2 groups and tp both to separate spawn point
        int midIndex = (getPlayers().size() - 1) / 2;
        List<List<Player>> split = new ArrayList<>(
                getPlayers().stream()
                        .collect(Collectors.partitioningBy(s -> getPlayers().indexOf(s) > midIndex))
                        .values()
        );

        split.get(0).forEach(player -> player.teleport(getVotedArena().getPointA()));
        split.get(1).forEach(player -> player.teleport(getVotedArena().getPointB()));

        setStartedAt(System.currentTimeMillis());
        new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                i--;

                if (i == 0) {
                    sendMessages(ChatColor.WHITE + "Spleef event has begun!");
                    sendSound(Sound.NOTE_PLING, 1F, 2F);
                } else {
                    sendMessages(ChatColor.WHITE + "Spleef event will begin in " + ChatColor.GOLD + i + ChatColor.WHITE + " second" + (i == 1 ? "" : "s") + "...");
                    sendSound(Sound.NOTE_PLING, 1F, 1F);
                }

                if (i <= 0) {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 20L, 20L);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);
        addSpectator(player);

        if (players.size() == 1) {
            endGame();
        }
    }

    public double getDeathHeight() {
        return Math.min(getVotedArena().getPointA().getBlockY(), getVotedArena().getPointB().getBlockY()) - 2.9;
    }

    @Override
    public Player findWinningPlayer() {
        if (players.size() == 1) {
            return Bukkit.getPlayer(players.iterator().next());
        }

        return null;
    }

    @Override
    public void getScoreboardLines(Player player, LinkedList<String> lines) {
        if (state == GameState.WAITING) {
            lines.add("&7┃ &fPlayers&7: &6" + players.size() + "&7/&6" + getMaxPlayers());

            if (getVotedArena() != null) {
                lines.add("&7┃ &fMap&7: &6" + getVotedArena().getName());
            } else {
                lines.add("");
                lines.add("&6&lMap Voting");

                getArenaOptions().entrySet().stream().sorted((o1, o2) -> o2.getValue().get()).forEach(entry -> {
                    lines.add("&7» " + (getPlayerVotes().getOrDefault(player.getUniqueId(), null) == entry.getKey() ? "&l" : "") + entry.getKey().getName() + " &7(" + entry.getValue().get() + ")");
                });
            }

            if (getStartedAt() == null) {
                int playersNeeded = getGameType().getMinPlayers() - getPlayers().size();
                lines.add("");
                lines.add("&6&oWaiting for " + playersNeeded + " player" + (playersNeeded == 1 ? "" : "s"));
            } else {
                float remainingSeconds = (getStartedAt() - System.currentTimeMillis()) / 1000F;
                lines.add("&6&oStarting in " + ((double) Math.round(10.0D * (double) remainingSeconds) / 10.0D) + "s");
            }
        } else if (state == GameState.RUNNING) {
            lines.add("&7┃ &fRemaining&7: &6" + players.size() + "&7/&6" + getStartedWith());
        } else {
            if (winningPlayer == null) {
                lines.add("&7┃ &fWinner&7: &6None");
            } else {
                lines.add("&7┃ &fWinner&7: &6" + winningPlayer.getName());
            }
        }
    }

    @Override
    public List<FancyMessage> createHostNotification() {
        return Arrays.asList(
                new FancyMessage("█████████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.AQUA)
                        .then("██████").color(ChatColor.GRAY)
                        .then(" " + getGameType().getDisplayName() + " Event").color(ChatColor.GOLD).style(ChatColor.BOLD),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY)
                        .then(" Hosted by ").color(ChatColor.GRAY)
                        .then(getHostName()).color(ChatColor.AQUA),
                new FancyMessage("")
                        .then("██████").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY)
                        .then(" [").color(ChatColor.GRAY)
                        .then("Click to join event").color(ChatColor.WHITE)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.WHITE))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("█████████").color(ChatColor.GRAY)
        );
    }
}

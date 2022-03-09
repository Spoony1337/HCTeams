package rip.orbit.hcteams.map.game.impl.ffa;

import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.util.LinkedList;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;
import rip.orbit.hcteams.map.game.GameState;
import rip.orbit.hcteams.map.game.GameType;
import rip.orbit.hcteams.map.game.arena.GameArena;
import rip.orbit.hcteams.map.kits.DefaultKit;
import rip.orbit.hcteams.util.item.InventoryUtils;
import rip.orbit.hcteams.util.item.ItemUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class FFAGame extends Game {

    private Map<UUID, Integer> kills = new HashMap<>();

    public FFAGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.FFA, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    startFFA();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        if (winningPlayer != null) {
            sendMessages(winningPlayer.getDisplayName() + ChatColor.WHITE + " won the FFA with " + ChatColor.GOLD + kills.get(winningPlayer.getUniqueId()) + ChatColor.WHITE + " kills.");
        }

        if (getVotedArena().getBounds() != null) {
            List<Chunk> arenaChunks = getVotedArena().getBounds().getChunks();
            arenaChunks.stream().filter(chunk -> !chunk.isLoaded()).forEach(Chunk::load);
            arenaChunks.forEach(chunk -> {
                for (Entity entity : chunk.getEntities()) {
                    if (entity instanceof Item && getVotedArena().getBounds().contains(entity.getLocation())) {
                        entity.remove();
                    }
                }
            });
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                kills.clear();
            }
        }.runTaskLater(HCF.getInstance(), 100L);
    }

    private void startFFA() {
        DefaultKit pvpKit = HCF.getInstance().getMapHandler().getKitManager().getDefaultKit("PvP");
        for (Player player : getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            pvpKit.apply(player);
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
                    sendMessages(ChatColor.WHITE + "The FFA has started!");
                    sendSound(Sound.NOTE_PLING, 1F, 2F);
                } else {
                    sendMessages(ChatColor.WHITE + "The FFA is starting in " + ChatColor.GOLD + i + ChatColor.WHITE + " second" + (i == 1 ? "" : "s") + "...");
                    sendSound(Sound.NOTE_PLING, 1F, 1F);
                }

                if (i <= 0) {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(HCF.getInstance(), 20L, 20L);
    }

    @Override
    public void addPlayer(Player player) throws IllegalStateException {
        super.addPlayer(player);
        kills.putIfAbsent(player.getUniqueId(), 0);
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);

        if (killer != null) {
            sendMessages(ChatColor.GOLD.toString() + player.getName() + ChatColor.WHITE + " has been eliminated by " + ChatColor.GOLD + killer.getName() + ChatColor.WHITE + "! " + ChatColor.GRAY + "(" + getPlayers().size() + "/" + getStartedWith() + " players remaining)");
            kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);

            ItemStack crapples = new ItemStack(Material.GOLDEN_APPLE, 2);
            if (killer.getInventory().firstEmpty() == -1 &&
                    Stream.of(killer.getInventory().getContents()).filter(Objects::nonNull).noneMatch(item -> item.isSimilar(crapples))) {
                killer.getInventory().setItem(7, crapples);
            } else {
                player.getInventory().addItem(crapples);
            }

            killer.setHealth(killer.getMaxHealth());
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8*20, 0));
            killer.updateInventory();
        }

        removeSpectator(player);

        if (players.size() == 1) {
            endGame();
        }
    }

    public boolean isGracePeriod() {
        return getStartedAt() == null || System.currentTimeMillis() <= getStartedAt() + 6_000L;
    }

    @Override
    public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
        if (isGracePeriod()) {
            event.setCancelled(true);
            return;
        }

        if (state == GameState.RUNNING) {
            if (!isPlaying(victim.getUniqueId()) && !isPlaying(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    public List<Player> getTopThree() {
        return getPlayers().stream()
                .sorted(Comparator.comparingInt(p -> kills.get(((Player) p).getUniqueId())).reversed())
                .limit(3)
                .collect(Collectors.toList());
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

            int potions = ItemUtils.countStacksMatching(player.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
            lines.add("&7┃ &fHealth Potions&7: &6" + potions);
            lines.add("&7┃ &fKills&7: &6" + kills.get(player.getUniqueId()));
        } else {
            if (winningPlayer == null) {
                lines.add("&7┃ &fWinner&7: &6None");
            } else {
                lines.add("&7┃ &fWinner&7: &6" + winningPlayer.getName());
            }

            lines.add("&7┃ &fKills&7: &6" + kills.get(player.getUniqueId()));
        }
    }

    @Override
    public List<FancyMessage> createHostNotification() {
        return Arrays.asList(
                new FancyMessage("█████████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.GOLD)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.GOLD)
                        .then("██████").color(ChatColor.GRAY)
                        .then(" " + getGameType().getDisplayName() + " Event").color(ChatColor.GOLD).style(ChatColor.BOLD),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("████").color(ChatColor.GOLD)
                        .then("███").color(ChatColor.GRAY)
                        .then(" Hosted by ").color(ChatColor.GRAY)
                        .then(getHostName()).color(ChatColor.AQUA),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.GOLD)
                        .then("██████").color(ChatColor.GRAY)
                        .then(" [").color(ChatColor.GRAY)
                        .then("Click to join event").color(ChatColor.WHITE)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.WHITE))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.GOLD)
                        .then("██████").color(ChatColor.GRAY),
                new FancyMessage("█████████").color(ChatColor.GRAY)
        );
    }
}

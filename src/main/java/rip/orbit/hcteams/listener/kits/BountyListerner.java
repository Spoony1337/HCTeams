package rip.orbit.hcteams.listener.kits;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.dtr.DTRBitmask;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class BountyListerner implements Listener {
    public static Random rand = new Random();
    private static String bountyPrefix = "&7[&6Bounty&7] ";
    @Getter
    private static UUID currentBountyPlayer;
    boolean pickingNewBounty = false;
    private long lastPositionBroadcastMessage = -1L;
    private long lastSuitablePositionTime = -1L;
    private int secondsUnsuitable = 0;
    private Reward reward;

    @Command(names = "bounty set", permission = "bounty.set", async = true)
    public static void setBounty(@Param(name = "target") Player target) {
        currentBountyPlayer = target.getUniqueId();
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', bountyPrefix + " &eA &cBounty &ehas been placed on " + target.getDisplayName() + "&e."));
    }

    @Command(names = {"bounty", "bounty coords"}, permission = "")
    public static void coords(Player sender) {
        Player player = currentBountyPlayer == null ? null : Bukkit.getPlayer(currentBountyPlayer);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "There's no bounty active right now.");
            return;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', bountyPrefix + player.getDisplayName() + " &ehas been spotted at &c" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "&e."));
    }

    private void checkBounty() {

        if (SOTWCommand.isSOTWTimer()) {
            currentBountyPlayer = null;
            return;
        }

        Player targetBountyPlayer = currentBountyPlayer == null ? null : Bukkit.getPlayer(currentBountyPlayer);

        if ((targetBountyPlayer == null || !targetBountyPlayer.isOnline()) && !pickingNewBounty) {
            newBounty();
            return;
        }

        if (!isSuitable(targetBountyPlayer)) {
            if (1000 < System.currentTimeMillis() - lastSuitablePositionTime) {
                if (30 <= secondsUnsuitable++) {
                    currentBountyPlayer = null;
                    secondsUnsuitable = 0;
                    newBounty();
                }
            }
        } else {
            lastSuitablePositionTime = System.currentTimeMillis();
            secondsUnsuitable = 0;
        }

        checkBroadcast();
    }

    private void newBounty() {

        if (SOTWCommand.isSOTWTimer()) {
            currentBountyPlayer = null;
            return;
        }

        this.pickingNewBounty = true;

        if (Bukkit.getOnlinePlayers().size() < 10) {
            return;
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', bountyPrefix + "&eA &cBounty &ewill be placed on a random player in &a30 seconds&e."));
        Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getInstance(), () -> {
            pickNewBounty();
        }, 30 * 20);
    }

    private void pickNewBounty() {
        List<Player> suitablePlayers = Bukkit.getOnlinePlayers().stream().filter(this::isSuitable).collect(Collectors.toList());
        if (suitablePlayers.isEmpty()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getInstance(), this::pickNewBounty, 20L);
            return;
        }

        if (!pickingNewBounty) {
            return;
        }

        Player bountyPlayer = suitablePlayers.get(rand.nextInt(suitablePlayers.size()));
        pickingNewBounty = false;
        setBounty(bountyPlayer);
        this.reward = Reward.values()[rand.nextInt(Reward.values().length)];
    }

    private void checkBroadcast() {
        Player player = currentBountyPlayer == null ? null : Bukkit.getPlayer(currentBountyPlayer);

        if (player == null) {
            return;
        }

        if (15000 <= System.currentTimeMillis() - lastPositionBroadcastMessage) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', bountyPrefix + currentBountyPlayer + " &ehas been spotted @ &c" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aReward&7: &f" + reward.name));
            lastPositionBroadcastMessage = System.currentTimeMillis();
        }
    }

    private boolean isSuitable(Player player) {

        if (player == null) {
            return false;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        if (player.hasMetadata("ModMode") || player.hasMetadata("modmode")) {
            return false;
        }

        if (150 <= player.getLocation().getY()) {
            return false;
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return false;
        }

        Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());

        if (teamAt != null && !teamAt.hasDTRBitmask(DTRBitmask.ROAD)) {
            return false;
        }


        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return false;
        }

        if (500 < Math.abs(player.getLocation().getX()) || 500 < Math.abs(player.getLocation().getZ())) {
            return false;
        }

        return HCF.getInstance().getServerHandler().isWarzone(player.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionAdd(PotionEffectAddEvent event) {
        LivingEntity player = event.getEntity();
        if (!player.getUniqueId().equals(currentBountyPlayer)) {
            return;
        }

        if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
            event.setCancelled(true);

            if (player instanceof Player) {
                ((Player) player).sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou currently have a bounty on and cannot pot an invis."));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKill(PlayerDeathEvent event) {
        Player died = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        if (!died.getUniqueId().equals(currentBountyPlayer)) {
            return;
        }

        currentBountyPlayer = null;

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', bountyPrefix + died.getDisplayName() + " &ehas been slain by &f" + killer.getDisplayName() + "&e."));

        reward.action.reward(killer);
    }

    private enum Reward {
        TWO_HUNDRED_FIFTY_DOLLARS("$250", player -> {
            FrozenEconomyHandler.deposit(player.getUniqueId(), 250);
        }),

        FIVE_HUNDRED_DOLLARS("$500", player -> {
            FrozenEconomyHandler.deposit(player.getUniqueId(), 500);
        }),

        SEVEN_HUNDRED_FIFTY_DOLLARS("$750", player -> {
            FrozenEconomyHandler.deposit(player.getUniqueId(), 750);
        }),

        ONE_BOUNTY_KEY("1 Bounty Key", player -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " bounty 1");
        }),

        TWO_BOUNTY_KEYS("2 Bounty Keys", player -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " bounty 2");
        }),

        THREE_BOUNTY_KEYS("3 Bounty Keys", player -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " bounty 3");
        }),

        CRAPPLES("1 Golden Apple", player -> {
            player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
        }),

        GOD_APPLE("1 God Apple", player -> {
            player.getInventory().addItem(ItemBuilder.of(Material.GOLDEN_APPLE).data((short) 1).build());
        }),

        COBWEBS("8 Cobwebs", player -> {
            player.getInventory().addItem(ItemBuilder.of(Material.WEB).amount(8).build());
        }),

        REFILL_POTS("Potion Refill Token", player -> {
            player.getInventory().addItem(ItemBuilder.of(Material.NETHER_STAR).name("&c&lPotion Refill Token").setUnbreakable(true).setLore(ImmutableList.of("&cRight click this to fill your inventory with potions!")).build());
        });

        private String name;
        private RewardAction action;

        Reward(String name, RewardAction action) {
            this.name = name;
            this.action = action;
        }
    }

    private interface RewardAction {
        void reward(Player player);
    }
}

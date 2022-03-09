package rip.orbit.hcteams.map.duel;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.util.StringUtil;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.duel.arena.DuelArena;
import rip.orbit.hcteams.map.duel.arena.DuelArenaHandler;
import rip.orbit.hcteams.map.duel.listener.DuelListeners;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.item.ItemUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DuelHandler {

    @Getter private DuelArenaHandler arenaHandler = new DuelArenaHandler();

    private Set<Duel> activeDuels = new HashSet<>();
    private Set<DuelInvite> activeInvites = new HashSet<>();

    public DuelHandler() {
        Bukkit.getPluginManager().registerEvents(new DuelListeners(), HCF.getInstance());

        // we hide players in duels, override tab completion for players
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
                String token = event.getLastToken();
                Collection<String> completions = event.getTabCompletions();

                completions.clear();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!event.getPlayer().canSee(player) && player.hasMetadata("invisible")) {
                        continue;
                    }

                    if (StringUtil.startsWithIgnoreCase(player.getName(), token)) {
                        completions.add(player.getName());
                    }
                }
            }
        }, HCF.getInstance());

        Bukkit.getScheduler().runTaskTimerAsynchronously(HCF.getInstance(), () -> activeInvites.removeIf(DuelInvite::hasExpired), 20, 20);
    }

    // Invites

    public boolean canDuel(Player sender, Player target) {
        if (sender.hasMetadata("modmode")) {
            sender.sendMessage(ChatColor.RED + "You can't do this while in mod mode.");
            return false;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.sendMessage(ChatColor.RED + "You must be in spawn to duel someone.");
            return false;
        }

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't duel yourself.");
            return false;
        }

        if (isInDuel(sender)) {
            sender.sendMessage(ChatColor.RED + "You can't do this while in a duel.");
            return false;
        }

        if (isInDuel(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already in a duel.");
            return false;
        }

        if (getInvite(sender.getUniqueId(), target.getUniqueId()) != null) {
            sender.sendMessage(ChatColor.RED + "You have already sent a duel request to " + target.getName() + ".");
            return false;
        }

        if (!ItemUtils.hasEmptyInventory(sender)) {
            sender.sendMessage(ChatColor.RED + "Your inventory must be empty to send a duel request.");
            return false;
        }

        return true;
    }

    public boolean canAccept(Player target, Player sender) {
        if (target.hasMetadata("modmode")) {
            target.sendMessage(ChatColor.RED + "You can't do this while in mod mode.");
            return false;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(target.getLocation())) {
            target.sendMessage(ChatColor.RED + "You must be in spawn to accept a duel.");
            return false;
        }

        if (target == sender) {
            target.sendMessage(ChatColor.RED + "You can't accept a duel from yourself.");
            return false;
        }

        if (isInDuel(target)) {
            target.sendMessage(ChatColor.RED + "You can't do this while in a duel.");
            return false;
        }

        if (isInDuel(sender)) {
            target.sendMessage(ChatColor.RED + sender.getName() + " is already in a duel.");
            return false;
        }

        if (getInvite(sender.getUniqueId(), target.getUniqueId()) == null) {
            target.sendMessage(ChatColor.RED + "You don't have a duel request from " + sender.getName() + ".");
            return false;
        }

        if (!ItemUtils.hasEmptyInventory(target)) {
            target.sendMessage(ChatColor.RED + "Your inventory must be empty to accept the duel request.");
            return false;
        }

        return true;
    }

    public void sendDuelRequest(Player sender, Player target, int wager) {
        activeInvites.add(new DuelInvite(sender.getUniqueId(), target.getUniqueId(), wager));

        sender.sendMessage(ChatColor.GREEN + "Successfully sent duel request!");

        target.sendMessage(ChatColor.RED + "[Duel] " + ChatColor.WHITE + sender.getName()
                + ChatColor.GRAY + " sent you a duel request wagering "
                + ChatColor.GREEN.toString() + ChatColor.BOLD + wager + " Stars"
                + ChatColor.GRAY + "!");
        target.spigot().sendMessage(createAcceptButton(sender.getName()));
    }

    public void acceptDuelRequest(DuelInvite invite) {
        Player sender = Bukkit.getServer().getPlayer(invite.getSender());
        Player target = Bukkit.getServer().getPlayer(invite.getTarget());

        activeInvites.remove(invite);

        if (HCF.getInstance().getStarsMaps().get(target.getUniqueId()) < invite.getWager()) {
            target.sendMessage(ChatColor.RED + "You do not have enough stars for this!");
            return;
        }

        HCF.getInstance().getStarsMaps().subtract(sender.getUniqueId(), invite.getWager());

        Duel duel = startDuel(sender, target, invite.getWager());

        if (duel == null) {
            sender.sendMessage(ChatColor.RED + "Failed to start duel.");
            target.sendMessage(ChatColor.RED + "Failed to start duel.");
        }
    }

    public TextComponent createAcceptButton(String sender) {
        TextComponent acceptButton = new TextComponent("[Click To Accept]");
        acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + sender));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent(ChatColor.GREEN + "Click to accept") }));

        return acceptButton;
    }

    // Duels

    public Duel startDuel(Player player1, Player player2, int wager) {
        DuelArena arena = arenaHandler.getRandomArena();

        if (arena == null) {
            HCF.getInstance().getLogger().warning("Failed to find an arena for duels.");
            return null;
        }

        Duel duel = new Duel(player1.getUniqueId(), player2.getUniqueId(), arena, wager);
        activeDuels.add(duel);
        duel.setup();

        return duel;
    }

    public void removeDuel(Duel duel) {
        activeDuels.remove(duel);
    }

    public Duel getDuel(Player player) {
        return activeDuels.stream().filter(duel -> duel.contains(player)).findFirst().orElse(null);
    }

    public boolean isInDuel(Player player) {
        return activeDuels.stream().anyMatch(duel -> duel.contains(player));
    }

    public DuelInvite getInvite(UUID sender, UUID target) {
        for (DuelInvite duelInvite : activeInvites) {
            if (duelInvite.getSender() == sender && duelInvite.getTarget() == target) {
                return duelInvite;
            }
        }

        return null;
    }

}

package rip.orbit.hcteams.map.game;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.qlib.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum GameType {

    SUMO(
            "Sumo",
            "Knock your opponent off the platform for the win!",
            new ItemStack(Material.LEASH),
            4,
            12,
            24,
            false
    ),
    SPLEEF(
            "Spleef",
            "Break the floor of snow below others before they spleef you. Last man standing wins.",
            new ItemStack(Material.DIAMOND_SPADE),
            4,
            8,
            32,
            false
    ),
    FFA(
            "FFA",
            "Free For All. Invisible with PvP Kit, every man for themselves. Last man standing wins.",
            new ItemStack(Material.DIAMOND_SWORD),
            4,
            8,
            32,
            false
    );

    private final String displayName;
    private final String description;
    private final ItemStack icon;
    private final int minForceStartPlayers;
    private final int minPlayers;
    private final int maxPlayers;
    private final boolean disabled;

    public boolean canHost(Player player) {
        return player.hasPermission("kitmap.game.host." + name().toLowerCase());
    }

    public static class Type implements ParameterType<GameType> {
        @Override
        public GameType transform(CommandSender sender, String source) {
            try {
                return GameType.valueOf(source.toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Game Type '" + source + "' couldn't be found.");
                return null;
            }
        }

        @Override
        public List<String> tabComplete(Player player, Set<String> flags, String source) {
            List<String> completions = Lists.newArrayList();

            for (GameType gameType : GameType.values()) {
                if (StringUtils.startsWithIgnoreCase(gameType.name(), source)) {
                    completions.add(gameType.name());
                }
            }

            return completions;
        }
    }

}

package rip.orbit.hcteams.map.duel.arena;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.angel.spigot.chunksnapshot.ChunkSnapshot;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.cuboid.Cuboid;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.util.LongHash;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class DuelArena {
    private final String name;

    @Setter private Location pointA;
    @Setter private Location pointB;

    @Setter private Cuboid bounds;

    private final Map<Long, ChunkSnapshot> chunkSnapshots = new HashMap<>();

    public boolean isSetup() {
        return pointA != null && pointB != null;
    }

    public void createSnapshot() {
        synchronized (chunkSnapshots) {
            bounds.getChunks().forEach(chunk -> chunkSnapshots.put(LongHash.toLong(chunk.getX(), chunk.getZ()), chunk.takeSnapshot()));
        }
    }

    public void restoreSnapshot() {
        synchronized (chunkSnapshots) {
            World world = bounds.getWorld();
            chunkSnapshots.forEach((key, value) -> world.getChunkAt(LongHash.msw(key), LongHash.lsw(key)).restoreSnapshot(value));
            chunkSnapshots.clear();
        }
    }

    public static class Type implements ParameterType<DuelArena> {
        @Override
        public DuelArena transform(CommandSender sender, String source) {
            DuelArena arena = HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenaByName(source);

            if (arena == null) {
                sender.sendMessage(ChatColor.RED + "Arena named '" + source + "' couldn't be found.");
                return null;
            }

            return arena;
        }

        @Override
        public List<String> tabComplete(Player player, Set<String> flags, String source) {
            List<String> completions = Lists.newArrayList();

            for (DuelArena arena : HCF.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenas()) {
                if (StringUtils.startsWith(arena.getName(), source)) {
                    completions.add(arena.getName());
                }
            }

            return completions;
        }
    }
}

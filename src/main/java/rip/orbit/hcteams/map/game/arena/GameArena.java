package rip.orbit.hcteams.map.game.arena;

import com.google.common.collect.Lists;
import lombok.Getter;
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

import java.util.*;

@Getter
public class GameArena {

    private String name;

    @Setter private Location pointA;
    @Setter private Location pointB;
    @Setter private Location spectatorSpawn;

    @Setter private Cuboid bounds;

    private transient final Map<Long, ChunkSnapshot> chunkSnapshots;
    private final List<String> compatibleGameTypes = new ArrayList<>();

    public GameArena() {
        this.chunkSnapshots = new HashMap<>();
    }

    public GameArena(String name) {
        this();
        this.name = name;
    }

    public boolean isSetup() {
        return pointA != null && pointB != null && spectatorSpawn != null;
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

    public static class Type implements ParameterType<GameArena> {
        @Override
        public GameArena transform(CommandSender sender, String source) {
            GameArena arena = HCF.getInstance().getMapHandler().getGameHandler().getConfig().getArenaByName(source);
            if (arena == null) {
                sender.sendMessage(ChatColor.RED + "Arena named '" + source + "' couldn't be found.");
                return null;
            }

            return arena;
        }

        @Override
        public List<String> tabComplete(Player player, Set<String> flags, String source) {
            List<String> completions = Lists.newArrayList();

            for (GameArena arena : HCF.getInstance().getMapHandler().getGameHandler().getConfig().getArenas()) {
                if (StringUtils.startsWith(arena.getName(), source)) {
                    completions.add(arena.getName());
                }
            }

            return completions;
        }
    }

}

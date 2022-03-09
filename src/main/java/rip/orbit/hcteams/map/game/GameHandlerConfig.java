package rip.orbit.hcteams.map.game;

import lombok.Getter;
import org.bukkit.Location;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.arena.GameArena;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameHandlerConfig {

    private Location lobbySpawn;
    private final List<GameArena> arenas = new ArrayList<>();

    public GameArena getArenaByName(String name) {
        for (GameArena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public void setLobbySpawnLocation(Location location) {
        this.lobbySpawn = location;
        HCF.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

    public void trackArena(GameArena arena) {
        arenas.add(arena);
        HCF.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

    public void forgetArena(GameArena arena) {
        arenas.remove(arena);
        HCF.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

}

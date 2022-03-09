package rip.orbit.hcteams.map.duel.arena;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.Getter;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import rip.orbit.hcteams.HCF;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DuelArenaHandler {

    @Getter private List<DuelArena> arenas = new ArrayList<>();

    private static File ARENAS_FILE = new File(HCF.getInstance().getDataFolder(), "kitmap-duel-arenas.json");

    public DuelArenaHandler() {
        FrozenCommandHandler.registerParameterType(DuelArena.class, new DuelArena.Type());

        // load arenas
        if (ARENAS_FILE.exists()) {
            try (Reader reader = Files.newReader(ARENAS_FILE, Charsets.UTF_8)) {
                Type arenaListType = new TypeToken<List<DuelArena>>() {}.getType();
                arenas = qLib.PLAIN_GSON.fromJson(reader, arenaListType);
            } catch (IOException e) {
                HCF.getInstance().getLogger().severe("Failed to load duel arenas!");
                e.printStackTrace();
            }
        }
    }

    public void saveArenas() {
        try {
            Files.write(qLib.PLAIN_GSON.toJson(arenas), ARENAS_FILE, Charsets.UTF_8);
        } catch (IOException e) {
            HCF.getInstance().getLogger().severe("Failed to save duel arenas!");
            e.printStackTrace();
        }
    }


    public DuelArena getArenaByName(String name) {
        return arenas.stream()
                .filter(arena -> arena.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public DuelArena getRandomArena() {
        if (arenas.isEmpty()) {
            return null;
        }

        return arenas.get(qLib.RANDOM.nextInt(arenas.size()));
    }

    public void addArena(DuelArena arena) {
        arenas.add(arena);
        saveArenas();
    }

    public void removeArena(DuelArena arena) {
        arenas.remove(arena);
        saveArenas();
    }
}

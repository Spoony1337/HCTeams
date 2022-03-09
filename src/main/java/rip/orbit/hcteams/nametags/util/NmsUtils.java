package rip.orbit.hcteams.nametags.util;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import rip.orbit.hcteams.nametags.PlayerScoreboard;

import java.util.concurrent.Executor;

public abstract class NmsUtils {

    @Getter
    private static NmsUtils instance;

    protected Executor bukkitExecutor;

    public static void init() {
        instance = new NmsUtils_1_7();
    }

    public abstract Thread getMainThread();

    public abstract void addPotionEffect(Player player, PotionEffect effect);

    public abstract PlayerScoreboard getNewPlayerScoreboard(Player player);

    public abstract Scoreboard getPlayerScoreboard(Player player);
}

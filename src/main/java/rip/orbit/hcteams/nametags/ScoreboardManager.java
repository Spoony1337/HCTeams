package rip.orbit.hcteams.nametags;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.nametags.util.NmsUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManager implements Listener {

    @Getter @Setter
    private boolean lunarEnabled = true;

    @Getter
	private final Map<UUID, PlayerScoreboard> scoreboards;

    public ScoreboardManager() {
        this.scoreboards = new ConcurrentHashMap<>();

        Bukkit.getOnlinePlayers().forEach(this::loadScoreboard);

        Bukkit.getPluginManager().registerEvents(this, HCF.getInstance());
    }

    public void disable() {
        this.scoreboards.values().forEach(PlayerScoreboard::unregister);
        this.scoreboards.clear();
    }

    public void loadScoreboard(Player player) {
        PlayerScoreboard playerScoreboard = NmsUtils.getInstance().getNewPlayerScoreboard(player);
        this.scoreboards.put(player.getUniqueId(), playerScoreboard);

        playerScoreboard.updateTabRelations(Bukkit.getOnlinePlayers());

        for(PlayerScoreboard other : this.scoreboards.values()) {
            other.updateRelation(player);
        }
    }
    private void fixInvisibilityForPlayer(PotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (getEffect(event).getType().getId() != 14) return;

        Player player = (Player) event.getEntity();

        for (PlayerScoreboard scoreboard : this.scoreboards.values()) {
            scoreboard.updateRelation(player);
        }
    }
    public void removeScoreboard(Player player) {
        PlayerScoreboard scoreboard = this.scoreboards.remove(player.getUniqueId());

        if(scoreboard != null) {
            scoreboard.unregister();
        }
    }

    @EventHandler
    public void onEffect(PotionEffectAddEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEffect().getType() == PotionEffectType.INVISIBILITY) {
                fixInvisibilityForPlayer(event);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.loadScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removeScoreboard(event.getPlayer());
    }

        public static PotionEffect getEffect(PotionEffectEvent event) {
            try {
                return event.getEffect();
            } catch(NoSuchMethodError e) {
                try {
                    Method effectMethod = event.getClass().getSuperclass().getDeclaredMethod("getPotionEffect");
                    return (PotionEffect) effectMethod.invoke(event);
                } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
}

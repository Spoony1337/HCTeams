package rip.orbit.hcteams.nametags;

import org.bukkit.entity.Player;

import java.util.Collections;

public interface PlayerScoreboard  {

    void unregister();

    void clear();
    void update();
    boolean add(String value, String time);

    boolean isEmpty();
    void setUpdate(boolean value);

    void updateTabRelations(Iterable<? extends Player> players, boolean lunarOnly);

    default void updateTabRelations(Iterable<? extends Player> players) {
        this.updateTabRelations(players, false);
    }

    default void updateRelation(Player player) {
        this.updateTabRelations(Collections.singletonList(player), false);
    }

    default void updateRelation(Player player, boolean lunarOnly) {
        this.updateTabRelations(Collections.singletonList(player), lunarOnly);
    }
}

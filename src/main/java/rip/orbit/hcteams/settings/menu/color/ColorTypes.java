package rip.orbit.hcteams.settings.menu.color;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum ColorTypes {

    TEAM("Team"),
    ALLY("Ally"),
    ENEMY("Enemy"),
    FOCUS("Focus"),
    ARCHER_TAG("Archer Tag");

    private String displayName;

}

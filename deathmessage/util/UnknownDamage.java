package rip.orbit.hcteams.deathmessage.util;

import rip.orbit.hcteams.deathmessage.objects.Damage;

public class UnknownDamage extends Damage {

    public UnknownDamage(String damaged, double damage) {
        super(damaged, damage);
    }

    @Override
	public String getDeathMessage() {
        return (wrapName(getDamaged()) + " died.");
    }

}
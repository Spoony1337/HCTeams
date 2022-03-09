package rip.orbit.hcteams.ability;

import lombok.Getter;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import rip.orbit.hcteams.ability.generator.GeneratorHandler;
import rip.orbit.hcteams.ability.items.*;
import rip.orbit.hcteams.ability.items.pocketbard.Regeneration;
import rip.orbit.hcteams.ability.items.pocketbard.Resistance;
import rip.orbit.hcteams.ability.items.pocketbard.Speed;
import rip.orbit.hcteams.ability.items.pocketbard.Strength;
import rip.orbit.hcteams.ability.map.AbilityGivenMap;
import rip.orbit.hcteams.ability.param.AbilityParameterType;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/07/2021 / 12:53 AM
 * HCTeams / rip.orbit.hcteams.ability
 */
public class AbilityHandler {

	@Getter private final GeneratorHandler generatorHandler;
	@Getter private final List<Ability> abilities;
	@Getter private final List<Ability> pocketbards;
	@Getter private final Cooldowns abilityCD;
	@Getter private final Cooldowns abilityEffect;
	@Getter private final AbilityGivenMap abilityGivenMap;

	public AbilityHandler() {
		abilities = new ArrayList<>();
		pocketbards = new ArrayList<>();
		abilityEffect = new Cooldowns();
		abilityCD = new Cooldowns();
		(abilityGivenMap = new AbilityGivenMap()).loadFromRedis();


		FrozenCommandHandler.registerParameterType(Ability.class, new AbilityParameterType());

		abilities.add(new Switcher());
//		abilities.add(new Turret());
		abilities.add(new Dome());
		abilities.add(new Recon());
		abilities.add(new AntiBuildStick());
		abilities.add(new AbilityInspector());
		abilities.add(new Curse());
		abilities.add(new Warrior());
		abilities.add(new TimeWarp());
		abilities.add(new Thorns());
		abilities.add(new GhostMode());
		abilities.add(new NinjaStar());
		abilities.add(new GuardianAngel());
//		abilities.add(new Voider());
		abilities.add(new PocketBard());

		pocketbards.add(new Strength());
		pocketbards.add(new Resistance());
		pocketbards.add(new Speed());
		pocketbards.add(new Regeneration());

		generatorHandler = new GeneratorHandler();

	}

	public Ability byName(String name) {
		for (Ability ability : abilities) {
			if (ability.name().equals(name)) {
				return ability;
			}
		}
		return null;
	}

}

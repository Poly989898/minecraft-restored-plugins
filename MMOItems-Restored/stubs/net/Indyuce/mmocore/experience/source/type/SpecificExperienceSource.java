package net.Indyuce.mmocore.experience.source.type;

import org.bukkit.Location;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;

public abstract class SpecificExperienceSource<T> extends ExperienceSource<T> {
    public SpecificExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) { super(dispenser); }
    public double rollAmount() { return 0; }
    public void giveExperience(PlayerData player, double multiplier, Location hologramLocation) { }
}

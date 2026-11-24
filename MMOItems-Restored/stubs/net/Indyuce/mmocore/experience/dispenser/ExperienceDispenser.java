package net.Indyuce.mmocore.experience.dispenser;

import org.bukkit.Location;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;

public interface ExperienceDispenser {
    void giveExperience(PlayerData data, double value, Location location, EXPSource source);
}

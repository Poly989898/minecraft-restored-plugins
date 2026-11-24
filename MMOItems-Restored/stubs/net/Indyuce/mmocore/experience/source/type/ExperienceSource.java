package net.Indyuce.mmocore.experience.source.type;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;

public abstract class ExperienceSource<T> {
    public ExperienceSource(ExperienceDispenser dispenser) { }
    public ExperienceDispenser getDispenser() { return null; }
    public abstract ExperienceSourceManager<?> newManager();
    public boolean matches(PlayerData player, T obj) { return false; }
    public abstract boolean matchesParameter(PlayerData player, T obj);
}

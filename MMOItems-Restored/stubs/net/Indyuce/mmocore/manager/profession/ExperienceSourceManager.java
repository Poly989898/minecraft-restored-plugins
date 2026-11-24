package net.Indyuce.mmocore.manager.profession;

import java.util.Set;
import org.bukkit.event.Listener;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;

public abstract class ExperienceSourceManager<T extends ExperienceSource> implements Listener {
    public void registerSource(T source) { }
    public Set<T> getSources() { return null; }
}

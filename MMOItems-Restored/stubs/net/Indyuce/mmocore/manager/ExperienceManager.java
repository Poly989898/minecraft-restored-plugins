package net.Indyuce.mmocore.manager;

import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;

public class ExperienceManager {
    public <T extends ExperienceSource> ExperienceSourceManager<T> getManager(Class<T> type) { return null; }
}

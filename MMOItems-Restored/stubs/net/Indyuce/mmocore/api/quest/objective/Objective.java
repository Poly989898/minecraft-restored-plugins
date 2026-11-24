package net.Indyuce.mmocore.api.quest.objective;

import org.bukkit.configuration.ConfigurationSection;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;

public abstract class Objective {
    public Objective(ConfigurationSection config) { }
    public abstract ObjectiveProgress newProgress(QuestProgress progress);
}

package net.Indyuce.mmocore.api.quest;

import net.Indyuce.mmocore.api.quest.objective.Objective;

public abstract class ObjectiveProgress {
    public ObjectiveProgress(QuestProgress questProgress, Objective objective) { }
    public QuestProgress getQuestProgress() { return null; }
    public abstract String formatLore(String lore);
}

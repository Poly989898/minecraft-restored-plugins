package net.Indyuce.mmocore.quest.compat;

import net.Indyuce.mmocore.quest.AbstractQuest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface QuestModule<T extends AbstractQuest> {
   @Nullable
   T getQuestOrThrow(String var1);

   boolean hasCompletedQuest(String var1, Player var2);
}

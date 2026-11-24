package com.guillaumevdn.questcreator.data.user;

import org.bukkit.entity.Player;

public class UserQC {
    public static UserQC cachedOrNull(Player player) { throw new UnsupportedOperationException("compile-only stub"); }
    public QuestHistory getQuestHistory() { throw new UnsupportedOperationException("compile-only stub"); }

    public static class QuestHistory {
        public java.util.List<QuestHistoryElement> getElements(String modelId, java.util.List<com.guillaumevdn.questcreator.lib.quest.QuestEndType> types, long since) { throw new UnsupportedOperationException("compile-only stub"); }
    }
}

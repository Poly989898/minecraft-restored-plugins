package me.blackvein.quests;

import java.util.UUID;

public interface Quests {
    Quest getQuestById(String id);
    Quester getQuester(UUID uuid);
}

package dev.aurelium.auraskills.api.skill;

import java.util.Locale;
import dev.aurelium.auraskills.api.registry.NamespacedId;

public enum Skills implements Skill {
    FARMING, FORAGING, MINING, FISHING, EXCAVATION, ARCHERY, DEFENSE, FIGHTING, ENDURANCE, AGILITY, ALCHEMY, ENCHANTING, SORCERY, HEALING, FORGING;
    public String getDisplayName(Locale locale) { return null; }
    public NamespacedId getId() { return null; }
}

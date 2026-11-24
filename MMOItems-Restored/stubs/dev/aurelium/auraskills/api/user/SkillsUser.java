package dev.aurelium.auraskills.api.user;

import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.skill.Skill;

public interface SkillsUser {
    void addTraitModifier(TraitModifier m);
    void addStatModifier(StatModifier m);
    double getMaxMana();
    double getMana();
    void setMana(double v);
    int getPowerLevel();
    int getSkillLevel(Skill s);
}

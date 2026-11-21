package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.auras.AuraManager;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SkillManager {
   MythicPlugin getPlugin();

   AuraManager getAuraManager();

   SkillEventBus getEventBus();

   void queueSecondPass(Runnable runnable);

   void queueAfterLoad(Runnable runnable);

   SkillCaster getCaster(AbstractEntity abstractEntity);

   void registerSkill(String str, Skill skill);

   Optional<Skill> getSkill(String str);

   Optional<Skill> getSkill(File file, String str);

   Optional<Skill> getSkill(File file, SkillHolder skillHolder, String str);

   Collection<String> getSkillNames();

   Collection<Skill> getSkills();

   void setSkillCooldown(SkillCaster skillCaster, AbstractSkill abstractSkill, double value);

   boolean isSkillOnCooldown(SkillCaster skillCaster, AbstractSkill abstractSkill);

   void clearSkillCooldowns(SkillCaster skillCaster);

   long getSkillCooldownRemainingMillis(SkillCaster skillCaster, AbstractSkill abstractSkill);

   void removeSkillCooldown(SkillCaster skillCaster, AbstractSkill abstractSkill);

   void purgeExpiredCooldowns();

   SkillMechanic getMechanic(String str);

   SkillTargeter getTargeter(String str, MythicLineConfig mythicLineConfig);

   SkillTargeter getTargeter(String str);

   SkillMetadata processTargets(SkillMetadata skillMetadata, SkillTargeter skillTargeter);

   SkillMetadata processTargets(SkillMetadata skillMetadata, SkillTargeter skillTargeter, boolean flag, boolean flag2);

   AbstractLocation getLocationTarget(SkillTargeter skillTargeter, SkillMetadata skillMetadata);

   SkillCondition getCondition(String str);

   List<SkillCondition> getConditions(List<String> strs);

   List<SkillCondition> getConditions(String str);

   SkillAudience getAudience(String str);
}

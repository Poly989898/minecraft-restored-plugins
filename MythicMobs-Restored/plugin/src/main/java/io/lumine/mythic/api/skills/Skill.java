package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.core.skills.SkillCondition;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface Skill {
   String getInternalName();

   MythicConfig getConfig();

   Collection<SkillHolder> getParents();

   void addParent(SkillHolder skillHolder);

   boolean isInlineSkill();

   default boolean isHidden() {
      return false;
   }

   void execute(
      SkillTrigger skillTrigger, SkillCaster skillCaster, AbstractEntity abstractEntity, AbstractLocation abstractLocation, HashSet<AbstractEntity> abstractEntities, HashSet<AbstractLocation> abstractLocations, float value
   );

   void execute(SkillMetadata skillMetadata);

   default boolean executeIfUsable(SkillMetadata data) {
      if (!this.isUsable(data)) {
         return false;
      } else {
         this.execute(data);
         return true;
      }
   }

   default boolean executeIfUsable(SkillMetadata data, SkillTrigger trigger) {
      if (!this.isUsable(data, trigger)) {
         return false;
      } else {
         this.execute(data);
         return true;
      }
   }

   boolean isUsable(SkillMetadata skillMetadata);

   boolean isUsable(SkillMetadata skillMetadata, SkillTrigger skillTrigger);

   boolean onCooldown(SkillCaster skillCaster);

   List<SkillCondition> getConditions();

   List<SkillCondition> getConditionsTarget();

   List<SkillCondition> getConditionsTrigger();
}

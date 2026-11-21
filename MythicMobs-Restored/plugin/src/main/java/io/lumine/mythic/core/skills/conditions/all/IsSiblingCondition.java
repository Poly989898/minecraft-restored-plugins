package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.persistence.PersistentDataType;

@MythicCondition(
   author = "Ashijin",
   name = "issibling",
   aliases = {"sibling"},
   description = "If the target is a sibling of the caster"
)
public class IsSiblingCondition extends SkillCondition implements ISkillMetaComparisonCondition {
   public IsSiblingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      if (meta.getCaster() instanceof ActiveMob activeMob) {
         if (!activeMob.getParentUUID().isPresent()) {
            return false;
         } else if (!target.getDataContainer().has(MobKeys.PARENT, PersistentDataType.STRING)) {
            return false;
         } else {
            String targetParentId = (String)target.getDataContainer().get(MobKeys.PARENT, PersistentDataType.STRING);
            return targetParentId.equals(activeMob.getParentUUID().get().toString());
         }
      } else {
         return false;
      }
   }
}

package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "rally",
   aliases = {"callforhelp"},
   description = "Calls for nearby entities to attack the target"
)
public class RallyMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private double searchRadius;
   private double hRadius;
   private double vRadius;
   private boolean overwriteTarget;
   private String rallyConditionString;
   private List<SkillCondition> rallyConditions = Lists.newArrayList();

   public RallyMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.hRadius = mlc.getDouble(new String[]{"radius", "hradius", "hr", "r"}, 10.0);
      this.vRadius = mlc.getDouble(new String[]{"vradius", "vr"}, this.hRadius);
      this.overwriteTarget = mlc.getBoolean(new String[]{"overwritetarget, ot"}, true);
      this.searchRadius = Numbers.max(this.hRadius, this.vRadius);
      this.rallyConditionString = mlc.getString(new String[]{"rallyconditions", "conditions", "cond", "c"}, null);
      String typeString = mlc.getString(new String[]{"types", "type", "t"}, "");
      String[] types = typeString.split(",");
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         if (this.rallyConditionString != null) {
            this.rallyConditions = this.getPlugin().getSkillManager().getConditions(this.rallyConditionString);
         }

         List<String> converted = Lists.newArrayList();

         for (String type : types) {
            Optional<MythicMob> maybeMob = this.getPlugin().getMobManager().getMythicMob(type);
            if (maybeMob.isPresent()) {
               converted.add("mythicMobType{type=" + type + "} true");
            } else {
               converted.add("entityType{type=" + type + "} true");
            }
         }

         if (!converted.isEmpty()) {
            if (converted.size() == 1) {
               this.rallyConditions.add(this.getPlugin().getSkillManager().getCondition(converted.get(0)));
            } else {
               String convertedLine = "(" + String.join(" || ", converted) + ")";
               this.rallyConditions.add(this.getPlugin().getSkillManager().getCondition(convertedLine));
            }
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isLiving()) {
         return SkillResult.INVALID_TARGET;
      } else {
         for (AbstractEntity entity : this.getPlugin()
            .getVolatileCodeHandler()
            .getWorldHandler()
            .getEntitiesNearLocation(data.getOrigin(), this.searchRadius, entityx -> {
               if (!entityx.isLiving()) {
                  return false;
               } else {
                  for (SkillCondition condition : this.rallyConditions) {
                     if (!condition.evaluateToEntity(data.getCaster().getEntity(), entityx)) {
                        return false;
                     }
                  }

                  return true;
               }
            })) {
            ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(entity);
            if (am != null) {
               am.setTarget(target);
            } else {
               entity.setTarget(target);
            }
         }

         return SkillResult.SUCCESS;
      }
   }
}

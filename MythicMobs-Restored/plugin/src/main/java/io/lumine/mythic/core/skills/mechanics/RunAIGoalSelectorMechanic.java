package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Ashijin",
   name = "runaigoalselector",
   aliases = {"aigoal", "aigoals"},
   description = "Modify an AI Goal Selector of the caster"
)
public class RunAIGoalSelectorMechanic extends SkillMechanic implements INoTargetSkill {
   protected PlaceholderString goal;
   protected PlaceholderInt priority;

   public RunAIGoalSelectorMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.goal = mlc.getPlaceholderString(new String[]{"aigoalselector", "goalselector", "goal", "g", "target", "t", "string", "s"}, null);
      this.priority = mlc.getPlaceholderInteger(new String[]{"priority", "p", "index"}, 0);
      if (this.goal == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'goal' attribute is required.");
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      String goal = this.goal.get(data);
      goal = MythicLineConfigImpl.unparseBlock(goal);
      int p = this.priority.get(data);
      if (p > 0 && !goal.split(" ")[0].matches("[0-9]+")) {
         goal = p + " " + goal;
      }

      List<String> goals = Lists.newArrayList(new String[]{goal});
      this.getPlugin().getVolatileCodeHandler().getAIHandler().addPathfinderGoals((LivingEntity)BukkitAdapter.adapt(data.getCaster().getEntity()), goals);
      return SkillResult.SUCCESS;
   }
}

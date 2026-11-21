package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Ashijin",
   name = "runaitargetselector",
   aliases = {"aitarget"},
   description = "Modify an AI Target Selector of the caster"
)
public class RunAITargetSelectorMechanic extends SkillMechanic implements INoTargetSkill {
   protected ArrayList<String> goal = new ArrayList<>();

   public RunAITargetSelectorMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String g = mlc.getString(new String[]{"aitargetselector", "targetselector", "target", "string", "s", "goal", "g"}, null);
      if (g == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'goal' attribute is required.");
      } else {
         g = MythicLineConfigImpl.unparseBlock(g);
         this.goal.add(g);
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      this.getPlugin().getVolatileCodeHandler().getAIHandler().addTargetGoals((LivingEntity)BukkitAdapter.adapt(data.getCaster().getEntity()), this.goal);
      return SkillResult.SUCCESS;
   }
}

package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "swingarm",
   aliases = {"armAnimation"},
   description = "Makes the caster swing their arm"
)
public class ArmAnimationMechanic extends SkillMechanic implements INoTargetSkill {
   public ArmAnimationMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (ServerVersion.isPaper()) {
         Player player = BukkitAdapter.adapt(data.getCaster().getEntity().asPlayer());
         player.swingMainHand();
      } else {
         this.getPlugin().getVolatileCodeHandler().doEffectArmSwing(data.getCaster().getEntity());
      }

      return SkillResult.SUCCESS;
   }
}

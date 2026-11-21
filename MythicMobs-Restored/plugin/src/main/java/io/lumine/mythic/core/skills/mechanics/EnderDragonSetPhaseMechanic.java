package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EnderDragon.Phase;

@MythicMechanic(
   author = "Ashijin",
   name = "enderDragonSetPhase",
   aliases = {"setEnderDragonPhase"},
   description = "Generates the EnderDragon portal"
)
public class EnderDragonSetPhaseMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "phase",
      aliases = {"p"},
      description = "The EnderDragon phase to set",
      defValue = "CIRCLING"
   )
   private Phase phase = Phase.CIRCLING;

   public EnderDragonSetPhaseMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strPhase = mlc.getString(new String[]{"phase", "p"}, "CIRCLING").toUpperCase();

      try {
         this.phase = Phase.valueOf(strPhase);
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid phase supplied");
         this.phase = Phase.CIRCLING;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Entity entity = target.getBukkitEntity();
      if (!(entity instanceof EnderDragon dragon)) {
         return SkillResult.INVALID_TARGET;
      } else {
         Schedulers.runOnRegion(entity, () -> dragon.setPhase(this.phase));
         return SkillResult.SUCCESS;
      }
   }
}

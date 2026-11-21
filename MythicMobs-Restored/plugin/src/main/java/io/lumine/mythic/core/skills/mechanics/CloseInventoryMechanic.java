package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "closeInventory",
   description = "Closes the target player's inventory"
)
public class CloseInventoryMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public CloseInventoryMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         Entity bukkitTarget = target.asPlayer().getBukkitEntity();
         Schedulers.runOnRegion(bukkitTarget, () -> ((Player)bukkitTarget).closeInventory());
      }

      return SkillResult.SUCCESS;
   }
}

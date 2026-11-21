package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Ashijin",
   name = "setgravity",
   aliases = {"setusegravity"},
   description = "Sets whether gravity affects the target entity"
)
public class SetUseGravityMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "gravity",
      aliases = {"g", "use", "u", "bool", "b"},
      description = "Whether gravity should affect the target entity",
      defValue = "true"
   )
   protected boolean b;

   public SetUseGravityMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.b = mlc.getBoolean(new String[]{"gravity", "g", "use", "u", "bool", "b"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving()) {
         Runnable gravityRunnable = () -> {
            LivingEntity e = (LivingEntity)BukkitAdapter.adapt(target);
            e.setGravity(this.b);
         };
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(target.getBukkitEntity())) {
            Schedulers.of(target.getBukkitEntity()).run(gravityRunnable);
         } else {
            gravityRunnable.run();
         }
      }

      return SkillResult.SUCCESS;
   }
}

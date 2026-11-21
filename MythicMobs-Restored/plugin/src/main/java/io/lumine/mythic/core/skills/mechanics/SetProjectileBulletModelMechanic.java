package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.skills.projectiles.bullet.IItemBulletTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setprojectilebulletmodel",
   description = "Sets the CustomModelData on the current projectile's bullet"
)
public class SetProjectileBulletModelMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "value",
      aliases = {"v", "model", "m"},
      defValue = "0",
      description = "The value to use for the modification."
   )
   private final PlaceholderInt value;

   public SetProjectileBulletModelMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.value = mlc.getPlaceholderInteger(new String[]{"value", "v", "model", "m"}, 0);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCallingEvent() instanceof ProjectileBulletableTracker bulletableTracker) {
         ProjectileBullet.BulletTracker bullet = bulletableTracker.getBullet();
         if (bullet == null) {
            return SkillResult.INVALID_CONFIG;
         } else {
            if (bullet instanceof IItemBulletTracker itemBulletTracker) {
               itemBulletTracker.setBulletItemModel(this.value.get(data));
            }

            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}

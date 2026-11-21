package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:lightning",
   aliases = {"e:lightning", "fakelightning"},
   description = "Causes a lightning strike effect at the target location"
)
public class LightningEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "localized",
      aliases = {"l"},
      description = "Sets where the lightning can only be seen/heard by players in radius",
      defValue = "false"
   )
   protected boolean localized;
   @MythicField(
      name = "localizedRadius",
      aliases = {"lr", "r"},
      description = "The radius of the effect",
      defValue = "128"
   )
   protected PlaceholderDouble localizedRadius;

   public LightningEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.localized = mlc.getBoolean(new String[]{"localized", "l"}, false);
      this.localizedRadius = mlc.getPlaceholderDouble(new String[]{"localizedradius", "lr", "r"}, 128.0);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (this.localized) {
         this.getPlugin().getVolatileCodeHandler().getEntityHandler().spawnFakeLightning(target, this.localizedRadius.get(data));
      } else {
         SkillAdapter.get().strikeLightningEffect(target);
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }
}

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
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "lightning",
   description = "Strikes lightning at the target location"
)
public class LightningMechanic extends DamagingMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "damage",
      aliases = {"d"},
      description = "The damage to deal",
      defValue = "0.01337"
   )
   protected PlaceholderFloat amount;

   public LightningMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderFloat(new String[]{"damage", "d"}, null);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.amount == null) {
         SkillAdapter.get().strikeLightning(target.getLocation());
      } else {
         SkillAdapter.get().strikeLightningEffect(target.getLocation());
         this.doDamage(data, target, this.amount.get(data, target));
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      SkillAdapter.get().strikeLightning(target);
      return SkillResult.SUCCESS;
   }
}

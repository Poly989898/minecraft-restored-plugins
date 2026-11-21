package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "forcepull",
   description = "Teleports the target entity to the caster"
)
public class ForcePullMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicFields({@MythicField(
         name = "spread",
         aliases = {"s"},
         description = "How spread out the entities will be from the caster",
         defValue = "0"
      ), @MythicField(
         name = "spreadV",
         aliases = {"vspread", "vs"},
         description = "The vertical spread",
         defValue = "0"
      )})
   protected PlaceholderFloat spread;
   @MythicFields({@MythicField(
         name = "spread",
         aliases = {"s"},
         description = "How spread out the entities will be from the caster",
         defValue = "0"
      ), @MythicField(
         name = "spreadV",
         aliases = {"vspread", "vs"},
         description = "The vertical spread",
         defValue = "0"
      )})
   protected PlaceholderFloat spreadV;

   public ForcePullMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.spread = mlc.getPlaceholderFloat(new String[]{"spread", "s"}, 0.0F);
      this.spreadV = mlc.getPlaceholderFloat(new String[]{"spreadv", "vspread", "vs"}, 0.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.spread.get(data, target) > 0.0F) {
         AbstractLocation location = this.sourceIsOrigin ? data.getCaster().getLocation() : data.getOrigin();
         location = MobExecutor.findSafeSpawnLocation(location, (int)this.spread.get(data, target), (int)this.spreadV.get(data, target), 1);
         target.teleport(location);
      } else {
         target.teleport(this.sourceIsOrigin ? data.getOrigin() : data.getCaster().getLocation());
      }

      return SkillResult.SUCCESS;
   }
}

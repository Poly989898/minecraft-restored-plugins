package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Josh",
   name = "setpathfindingmalus",
   aliases = {"setmalus", "malus"},
   description = "Sets the pathfinding malus of a mob."
)
public class SetPathfindingMalusMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "malus",
      aliases = {"type", "malustype"},
      description = "The pathfinding malus type to set",
      defValue = "OPEN"
   )
   private String malusType;
   @MythicField(
      name = "value",
      aliases = {"amount"},
      description = "The malus value to apply",
      defValue = "0.0"
   )
   private PlaceholderFloat value;

   public SetPathfindingMalusMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc, int interval) {
      super(manager, file, line, mlc, interval);
      this.malusType = mlc.getString(new String[]{"malus", "type", "malustype"}, "OPEN");
      this.value = mlc.getPlaceholderFloat(new String[]{"value", "amount"}, 0.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.getPlugin().getVolatileCodeHandler().getAIHandler().setPathfindingMalus(target, this.malusType.toUpperCase(), this.value.get(data));
      return SkillResult.SUCCESS;
   }
}

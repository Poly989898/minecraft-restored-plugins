package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joikd",
   name = "hasAuraType",
   aliases = {"hasbufftype", "hasdebufftype"},
   description = "Checks if the target entity has an aura of the given type"
)
public class HasAuraTypeCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "type",
      aliases = {"auragroup", "auratype", "group", "type", "t", "g"},
      description = "The name of the aura type to check for"
   )
   private PlaceholderString auraName;

   public HasAuraTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.auraName = mlc.getPlaceholderString(new String[]{"auragroup", "auratype", "group", "type", "t", "g"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return getPlugin().getSkillManager().getAuraManager().getAuraRegistry(target).hasAuraType(this.auraName.get(target));
   }
}

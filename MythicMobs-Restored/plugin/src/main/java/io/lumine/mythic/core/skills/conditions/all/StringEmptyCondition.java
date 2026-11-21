package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "stringEmpty",
   aliases = {"isEmpty"},
   version = "5.8",
   description = "Checks if the given placeholder is null or empty."
)
public class StringEmptyCondition extends SkillCondition implements IEntityCondition, ISkillMetaCondition {
   @MythicField(
      name = "value",
      aliases = {"val", "v"},
      description = ""
   )
   private PlaceholderString value;

   public StringEmptyCondition(String line, MythicLineConfig mlc) {
      super(line);

      try {
         this.value = PlaceholderString.of(mlc.getString(new String[]{"value", "val", "v", "string", "s"}, null, this.conditionVar));
      } catch (Exception e) {
         MythicLogger.errorConditionConfig(this, mlc, "Variable name must be set.");
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (this.value == null) {
         return true;
      } else {
         String compare = this.value.get(target);
         return compare == null || compare.isEmpty() || compare.equalsIgnoreCase("UNDEFINED");
      }
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (this.value == null) {
         return true;
      } else {
         String compare = this.value.get(meta);
         return compare == null || compare.isEmpty() || compare.equalsIgnoreCase("UNDEFINED");
      }
   }
}

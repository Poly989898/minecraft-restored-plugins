package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "stringEquals",
   aliases = {"stringEq"},
   version = "4.6",
   description = "Checks if value1 equals value2. Both values can use variables and placeholders."
)
public class StringEqualsCondition extends SkillCondition implements IEntityCondition, ILocationCondition, ISkillMetaComparisonCondition {
   @MythicField(
      name = "value1",
      aliases = {"val1", "v1"},
      description = ""
   )
   private PlaceholderString value1;
   @MythicField(
      name = "value2",
      aliases = {"val2", "v2"},
      description = ""
   )
   private PlaceholderString value2;

   public StringEqualsCondition(String line, MythicLineConfig mlc) {
      super(line);

      try {
         this.value1 = PlaceholderString.of(mlc.getString(new String[]{"value1", "val1", "v1", "string", "s"}, null));
      } catch (Exception e) {
         MythicLogger.errorConditionConfig(this, mlc, "Variable name must be set.");
         return;
      }

      try {
         this.value2 = PlaceholderString.of(mlc.getString(new String[]{"value2", "val2", "v2", "value", "val", "v", "equals", "eq", "e"}, null));
      } catch (Exception ex) {
         MythicLogger.errorConditionConfig(this, mlc, "Variable name must be set.");
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      String compare1 = this.value1.get(target);
      String compare2 = this.value2.get(target);
      return compare1.equals(compare2);
   }

   @Override
   public boolean check(AbstractLocation target) {
      String compare1 = this.value1.get();
      String compare2 = this.value2.get();
      return compare1.equals(compare2);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      String compare1 = this.value1.get(meta, target);
      String compare2 = this.value2.get(meta, target);
      return compare1.equals(compare2);
   }
}

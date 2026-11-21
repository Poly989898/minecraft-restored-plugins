package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "name",
   aliases = {"castername"},
   description = "Checks against the entity's name"
)
public class NameCondition extends SkillCondition implements IEntityCondition, ISkillMetaComparisonCondition {
   @MythicField(
      name = "name",
      aliases = {"n"},
      description = "The name to test for",
      defValue = "Ashijin"
   )
   private final PlaceholderString name;

   public NameCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.name = mlc.getPlaceholderString(new String[]{"name", "n"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractEntity target) {
      String check = this.name.get(target);
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "NameCondition {0} == {1}", target.getName(), check);
      return target.getName().equals(check);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      String check = this.name.get(meta, target);
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "NameCondition {0} == {1}", target.getName(), check);
      return target.getName().equals(check);
   }
}

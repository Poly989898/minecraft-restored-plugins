package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "isskill",
   aliases = {"skillexists"},
   description = "Checks if the given skill exists"
)
public class IsSkillCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "name",
      aliases = {"n"},
      description = "The name to test for",
      defValue = ""
   )
   private final PlaceholderString name;

   public IsSkillCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.name = mlc.getPlaceholderString(new String[]{"name", "n", "skill", "s"}, this.conditionVar);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      String check = this.name.get(meta);
      return getPlugin().getSkillManager().getSkill(check).isPresent();
   }
}

package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "hasTag",
   aliases = {"hasScoreboardTag"},
   description = "Tests if the target has a scoreboard tag"
)
public class HasTagCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "tag",
      aliases = {"t"},
      description = "The tag to check for"
   )
   private PlaceholderString tag;

   public HasTagCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.tag = mlc.getPlaceholderString(new String[]{"tag", "t"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return target.hasScoreboardTag(this.tag.get(target));
   }
}

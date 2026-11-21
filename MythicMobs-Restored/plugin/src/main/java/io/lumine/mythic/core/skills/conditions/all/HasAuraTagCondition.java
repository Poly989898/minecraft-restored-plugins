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
   name = "hasAuraTag",
   aliases = {"hasTaggedAura", "hasbufftag", "hasdebufftag"},
   description = "Checks if the target entity has an aura with the given tag"
)
public class HasAuraTagCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "tag",
      aliases = {"tags", "t"},
      description = "The tag to check for"
   )
   private final PlaceholderString auraTag;

   public HasAuraTagCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.auraTag = mlc.getPlaceholderString(new String[]{"tag", "tags", "t"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return getPlugin().getSkillManager().getAuraManager().getAuraRegistry(target).hasAuraTag(this.auraTag.get(target));
   }
}

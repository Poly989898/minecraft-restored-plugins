package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "damageTag",
   aliases = {"damageHasTag"},
   version = "5.5",
   description = "Checks if the damage that caused the current skill tree has a specific tag."
)
public class DamageHasTagCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "tag",
      aliases = {"t"},
      description = "The damage tag to match"
   )
   protected PlaceholderString tag;
   protected boolean value;

   public DamageHasTagCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.tag = mlc.getPlaceholderString(new String[]{"tag", "t"}, "", this.conditionVar);
      this.value = mlc.getBoolean(new String[]{"value", "val", "v", "boolean", "bool", "b"}, true);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (meta.getTriggerMetadata() != null) {
         String tag = this.tag.get(meta);
         boolean bool = meta.getTriggerMetadata().getBoolean(tag);
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DamageTag condition checking {0} = {1} == {2}", tag, this.value, bool);
         return bool == this.value;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "DamageCause condition failed: no damage metadata present");
         return false;
      }
   }
}

package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.triggers.meta.CancellableTriggerMetadata;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Archontas",
   name = "isCancelled",
   aliases = {"isCanceled", "cancelled", "canceled"},
   version = "5.12",
   description = "Checks if the triggering event is cancelled. Useful for onDamaged and similar trigger flows."
)
public class IsCancelledCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "value",
      aliases = {"val", "v", "boolean", "bool", "b"},
      description = "Expected cancelled state"
   )
   private final boolean value;

   public IsCancelledCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.value = mlc.getBoolean(new String[]{"value", "val", "v", "boolean", "bool", "b"}, true);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      boolean cancelled = CancellableTriggerMetadata.resolveCancelled(meta);
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "IsCancelled condition checking {0} == {1}", cancelled, this.value);
      return cancelled == this.value;
   }
}

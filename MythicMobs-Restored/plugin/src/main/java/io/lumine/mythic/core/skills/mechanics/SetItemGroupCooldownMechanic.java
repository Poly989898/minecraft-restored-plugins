package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setItemGroupCooldown",
   description = "Sets the cooldown on an item group for the target player"
)
public class SetItemGroupCooldownMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "key",
      aliases = {"k", "group", "g"},
      description = "The namespaced item group key to apply cooldown to",
      defValue = "minecraft:item_group"
   )
   protected PlaceholderString namespacedKey;
   @MythicField(
      name = "ticks",
      aliases = {"t"},
      description = "Cooldown duration in ticks",
      defValue = "20"
   )
   protected PlaceholderDuration ticks;

   public SetItemGroupCooldownMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.namespacedKey = mlc.getPlaceholderString(new String[]{"key", "k", "group", "g"}, "minecraft:item_group");
      this.ticks = mlc.getPlaceholderDuration(new String[]{"ticks", "t"}, DurationUnit.TICKS, "20");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         target.asPlayer().setItemGroupCooldown(this.namespacedKey.get(data, target), this.ticks.get(data, target).getTicks());
         return SkillResult.SUCCESS;
      }
   }
}

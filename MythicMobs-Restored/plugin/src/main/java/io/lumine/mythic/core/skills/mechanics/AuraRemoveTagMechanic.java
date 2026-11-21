package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "auraRemoveTagged",
   aliases = {"removetaggedaura", "removeauratag"},
   description = "Removes auras that have the given tag from the target."
)
public class AuraRemoveTagMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "tag",
      aliases = {"tags", "t"},
      description = "The aura tag to remove from the target."
   )
   private final PlaceholderString auraTag;
   @MythicField(
      name = "limit",
      aliases = {"count", "amount", "a"},
      description = "The maximum number of auras to remove",
      defValue = "2147483647"
   )
   private final PlaceholderInt limit;

   public AuraRemoveTagMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.auraTag = mlc.getPlaceholderString(new String[]{"tag", "tags", "t"}, "default");
      this.limit = mlc.getPlaceholderInteger(new String[]{"amount", "count", "limit", "a"}, Integer.MAX_VALUE);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String tag = this.auraTag.get(data, target);
      if (tag != null && !tag.isEmpty()) {
         int remaining = this.limit.get(data, target);
         if (remaining <= 0) {
            return SkillResult.SUCCESS;
         } else {
            AuraRegistry registry = this.getPlugin().getSkillManager().getAuraManager().getAuraRegistry(target);

            for (Aura.AuraTracker aura : registry.getAurasByTag(tag)) {
               aura.terminateFromRegistry();
               if (--remaining <= 0) {
                  break;
               }
            }

            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.SUCCESS;
      }
   }
}

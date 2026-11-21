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
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "auraRemove",
   aliases = {"removeaura", "removebuff", "removedebuff"},
   description = "Removes an aura from the target."
)
public class AuraRemoveMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "aura",
      aliases = {"buff", "debuff", "name", "b", "n"},
      description = "The name of the aura to remove from the target. The mechanic will remove all auras if the auraname is \"ANY\"",
      defValue = "default"
   )
   private final PlaceholderString auraName;
   @MythicField(
      name = "stacks",
      aliases = {"s"},
      description = "The amount of stacks to remove",
      defValue = "2147483647"
   )
   private final PlaceholderInt stacks;
   private boolean matchAny = false;

   public AuraRemoveMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.auraName = mlc.getPlaceholderString(new String[]{"aura", "auraname", "b", "buff", "buffname", "debuff", "debuffname", "n", "name"}, "default");
      this.stacks = mlc.getPlaceholderInteger(new String[]{"stacks", "s"}, Integer.MAX_VALUE);
      if (this.auraName.isStatic() && this.auraName.get().equals("ANY")) {
         this.matchAny = true;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.matchAny) {
         int remaining = this.stacks.get(data, target);
         AuraRegistry registry = this.getPlugin().getSkillManager().getAuraManager().getAuraRegistry(target);

         for (String name : registry.getAuras().keySet()) {
            int stacks = registry.getStacks(name);
            registry.removeStack(name, stacks);
            remaining -= stacks;
            if (remaining <= 0) {
               break;
            }
         }
      } else {
         this.getPlugin().getSkillManager().getAuraManager().getAuraRegistry(target).removeStack(this.auraName.get(data), this.stacks.get(data, target));
      }

      return SkillResult.SUCCESS;
   }
}

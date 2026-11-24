package io.lumine.mythic.lib.comp.mythicmobs.condition;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.AttackMetadata;
import org.jetbrains.annotations.NotNull;

@MythicCondition(author = "Indyuce", name = "ismmodamage", aliases = {}, description = "If the target entity is being damaged using the MMO damage system")
public class IsMMODamageCondition extends SkillCondition implements ISkillMetaCondition {
   public IsMMODamageCondition(@NotNull MythicLineConfig mlc) {
      super(mlc.getLine());
   }

   public boolean check(SkillMetadata skillMetadata) {
      for (AbstractEntity target : skillMetadata.getEntityTargets()) {
         AttackMetadata opt = MythicLib.plugin.getDamage().getRegisteredAttackMetadata(target.getBukkitEntity());
         if (opt != null) {
            return true;
         }
      }

      return false;
   }
}

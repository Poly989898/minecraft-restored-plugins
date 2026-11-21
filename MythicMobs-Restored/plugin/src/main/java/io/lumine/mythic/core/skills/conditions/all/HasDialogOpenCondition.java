package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.mechanics.DialogTracker;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   name = "hasDialogOpen",
   aliases = {"dialogOpen"},
   premium = true,
   description = "Whether the target player has a dialog open"
)
public class HasDialogOpenCondition extends SkillCondition implements IEntityCondition {
   public HasDialogOpenCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      return e.isPlayer() && DialogTracker.isOpen(e.getUniqueId());
   }
}

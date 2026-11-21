package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joikd",
   name = "haspermission",
   aliases = {"permission"},
   description = "Tests if the target player has a permission"
)
public class HasPermissionCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "permission",
      aliases = {"p"},
      description = "The permission to check for"
   )
   private final PlaceholderString perm;

   public HasPermissionCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.perm = mlc.getPlaceholderString(new String[]{"permission", "p"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return target.isPlayer() ? target.asPlayer().hasPermission(this.perm.get(target)) : false;
   }
}

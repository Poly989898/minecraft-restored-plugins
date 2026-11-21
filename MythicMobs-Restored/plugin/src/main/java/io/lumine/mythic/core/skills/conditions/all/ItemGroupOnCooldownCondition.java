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
   name = "itemGroupOnCooldown",
   description = "Tests if the target player has the specified item group on cooldown"
)
public class ItemGroupOnCooldownCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "group",
      aliases = {"g"},
      description = "The namespaced key to check for"
   )
   private PlaceholderString group;

   public ItemGroupOnCooldownCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.group = mlc.getPlaceholderString(new String[]{"group", "g"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return !target.isPlayer() ? false : target.asPlayer().isItemGroupOnCooldown(this.group.get(target));
   }
}

package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joikd",
   name = "hasAuraStacks",
   aliases = {"hasbuffstacks", "hasdebuffstacks", "aurastacks", "buffstacks", "debuffstacks"},
   description = "Tests if the target has the given range of stacks from an aura"
)
public class HasAuraStacksCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "auraName",
      aliases = {"name", "aura", "buffname", "buff", "debuffname", "debuff", "n", "b"},
      description = "The aura name"
   )
   private String auraName;
   @MythicField(
      name = "stacks",
      aliases = {"s"},
      description = "The number/range of stacks to check for"
   )
   private RangedInt stacks;

   public HasAuraStacksCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.auraName = mlc.getString(new String[]{"aura", "auraname", "b", "buff", "buffname", "debuff", "debuffname", "n", "name"}, this.conditionVar);
      this.stacks = new RangedInt(mlc.getString(new String[]{"stacks", "s"}, "1"));
   }

   @Override
   public boolean check(AbstractEntity target) {
      int targetStacks = getPlugin().getSkillManager().getAuraManager().getAuraStacks(target, this.auraName);
      return this.stacks.equals(targetStacks);
   }
}

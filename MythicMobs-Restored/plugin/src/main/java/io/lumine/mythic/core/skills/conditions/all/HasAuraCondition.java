package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joikd",
   name = "hasAura",
   aliases = {"hasbuff", "hasdebuff"},
   description = "Checks if the target entity has the given aura"
)
public class HasAuraCondition extends SkillCondition implements IEntityCondition, ISkillMetaComparisonCondition {
   @MythicField(
      name = "auraName",
      aliases = {"name", "aura", "buffname", "buff", "debuffname", "debuff", "n", "b"},
      description = "The name of the aura to check for"
   )
   private final PlaceholderString auraName;

   public HasAuraCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.auraName = mlc.getPlaceholderString(
         new String[]{"aura", "auraname", "b", "buff", "buffname", "debuff", "debuffname", "n", "name"}, this.conditionVar
      );
   }

   @Override
   public boolean check(AbstractEntity target) {
      return getPlugin().getSkillManager().getAuraManager().getAuraRegistry(target).hasAura(this.auraName.get(target));
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      return getPlugin().getSkillManager().getAuraManager().getAuraRegistry(target).hasAura(this.auraName.get(meta, target));
   }
}

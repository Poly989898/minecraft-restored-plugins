package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import java.util.Optional;

@MythicCondition(
   author = "Ghmmy",
   name = "statDamageModifier",
   aliases = {"statDamage"},
   description = "Used in stat triggers to check the increased damage (as a multiplier)."
)
public class StatDamageModifierCondition extends SkillCondition implements ISkillMetaComparisonCondition {
   protected final PlaceholderDouble value;

   public StatDamageModifierCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.value = mlc.getPlaceholderDouble(new String[]{"value", "v", "amount", "a", "damage", "d", "multiplier", "m"}, 1.0);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      Optional<Object> data = meta.getMetadata(PlaceholderMetadataKey.STAT_DAMAGE_MODIFIER);
      if (data.isEmpty()) {
         return false;
      } else {
         double damageModifier = (Double)data.get();
         return damageModifier >= this.value.get(meta, target);
      }
   }
}

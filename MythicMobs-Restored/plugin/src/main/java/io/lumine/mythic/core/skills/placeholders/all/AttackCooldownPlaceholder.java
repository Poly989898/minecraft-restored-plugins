package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "attack_cooldown",
   version = "5.8",
   description = "returns a float value between 0 and 1. (1 = no cooldown)"
)
public class AttackCooldownPlaceholder extends NumericEntityScopedPlaceholder {
   public AttackCooldownPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity.isPlayer()) {
         SkillMetadata meta = placeholderContext.getSkillMetadata().orElse(null);
         return (Number)(meta != null
               && meta.getVariables().has("hit-attack-cooldown")
               && meta.getCaster() != null
               && meta.getCaster().getEntity() != null
               && meta.getCaster().getEntity().getUniqueId().equals(entity.getUniqueId())
            ? meta.getVariables().getDouble("hit-attack-cooldown")
            : BukkitAdapter.adapt(entity.asPlayer()).getAttackCooldown());
      } else {
         return 1;
      }
   }
}

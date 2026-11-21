package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "damage",
   aliases = {"dmg", "d"}
)
public class DamagePlaceholder extends NumericEntityScopedPlaceholder {
   public DamagePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity == null) {
         return null;
      } else if (entity.isPlayer()) {
         return BukkitAdapter.adapt(entity.asPlayer()).getAttribute(BukkitAttribute.getAttribute("ATTACK_DAMAGE")).getValue();
      } else {
         ActiveMob am = this.getActiveMob(entity);
         return (Number)(am != null ? am.getDamage() : 0);
      }
   }
}

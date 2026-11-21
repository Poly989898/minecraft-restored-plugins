package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "mythic_type"
)
public class MythicTypePlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public MythicTypePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity.isPlayer()) {
         return "PLAYER";
      } else {
         ActiveMob am = this.getActiveMob(entity);
         return am == null ? entity.getBukkitEntity().getType().toString() : am.getType().getInternalName();
      }
   }
}

package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "name",
   aliases = {"n"}
)
public class NamePlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public NamePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity == null) {
         return null;
      } else {
         ActiveMob activeMob = this.getActiveMob(entity);
         if (activeMob == null) {
            return getName(entity);
         } else {
            return activeMob.getDisplayName() != null ? activeMob.getDisplayName() : getName(entity);
         }
      }
   }
}

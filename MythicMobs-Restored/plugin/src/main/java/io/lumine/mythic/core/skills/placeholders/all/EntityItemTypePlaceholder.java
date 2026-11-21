package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "item.type"
)
public class EntityItemTypePlaceholder extends EntityScopedPlaceholder<String> implements StringPlaceholder {
   public EntityItemTypePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   public String applyToScope(PlaceholderContext placeholderContext) {
      return this.getEntity.get(placeholderContext).getBukkitEntity() instanceof Item item ? item.getItemStack().getType().toString() : "AIR";
   }
}

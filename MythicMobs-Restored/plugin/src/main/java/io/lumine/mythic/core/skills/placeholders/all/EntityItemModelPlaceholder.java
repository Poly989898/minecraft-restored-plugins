package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "item.model"
)
public class EntityItemModelPlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   public EntityItemModelPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyToScope(PlaceholderContext placeholderContext) {
      return this.getEntity.get(placeholderContext).getBukkitEntity() instanceof Item item ? item.getItemStack().getItemMeta().getCustomModelData() : 0;
   }
}

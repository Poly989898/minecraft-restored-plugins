package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "item.amount",
   aliases = {"itemstack_amount"}
)
public class EntityItemAmountPlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   public EntityItemAmountPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyToScope(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      return entity != null && entity.getBukkitEntity() instanceof Item item ? item.getItemStack().getAmount() : 0;
   }
}

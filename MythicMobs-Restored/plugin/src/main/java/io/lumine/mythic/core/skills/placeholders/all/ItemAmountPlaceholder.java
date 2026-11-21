package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "item.amount",
   version = "5.4"
)
public class ItemAmountPlaceholder extends GenericPlaceholder<Integer> implements IntegerPlaceholder {
   public ItemAmountPlaceholder(GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return placeholderContext.getItemStack().<Integer>map(ItemStack::getAmount).orElse(0);
   }
}

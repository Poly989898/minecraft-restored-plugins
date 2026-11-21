package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.DoublePlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "item.droplevel",
   version = "5.11.2"
)
public class ItemDropLevelPlaceholder extends GenericPlaceholder<Double> implements DoublePlaceholder {
   public ItemDropLevelPlaceholder(GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Double applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return placeholderContext.meta() instanceof DropMetadata dropMetadata
         ? dropMetadata.getLevel()
         : (double)placeholderContext.getItemStack().<Integer>map(ItemStack::getAmount).orElse(0).intValue();
   }
}

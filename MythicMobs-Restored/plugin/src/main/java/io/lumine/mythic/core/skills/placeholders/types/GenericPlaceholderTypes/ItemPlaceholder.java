package io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes;

import io.lumine.mythic.bukkit.adapters.BukkitItemStack;

public interface ItemPlaceholder extends GenericPlaceholderTyping<BukkitItemStack> {
   default String convertType(BukkitItemStack parsedPlaceholder) {
      return parsedPlaceholder.getType().toString();
   }

   @Override
   default Class<BukkitItemStack> getParsedValueClass() {
      return BukkitItemStack.class;
   }
}

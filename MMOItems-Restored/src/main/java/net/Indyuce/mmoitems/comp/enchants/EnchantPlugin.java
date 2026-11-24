package net.Indyuce.mmoitems.comp.enchants;

import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EnchantPlugin<T> {
   @NotNull
   String getNamespace();

   @Nullable
   T transfer(@NotNull Enchantment enchantment);

   void handleEnchant(@NotNull ItemStackBuilder itemStackBuilder, @NotNull T value, int count);

   @NotNull
   default NamespacedKey getNamespacedKey(@NotNull String key) {
      return new NamespacedKey(this.getNamespace(), key);
   }
}

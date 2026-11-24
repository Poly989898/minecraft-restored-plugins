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
   T transfer(@NotNull Enchantment var1);

   void handleEnchant(@NotNull ItemStackBuilder var1, @NotNull T var2, int var3);

   @NotNull
   default NamespacedKey getNamespacedKey(@NotNull String key) {
      return new NamespacedKey(this.getNamespace(), key);
   }
}

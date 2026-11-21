package io.lumine.mythic.api.items;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;

public interface ItemSupplier {
   String getNamespace();

   ItemStack getItem(String str);

   boolean isSimilar(String str, ItemStack itemStack);

   default AbstractItemStack getMythicItem(String name) {
      return BukkitAdapter.adapt(this.getItem(name));
   }

   Collection<String> getAvailableItemNames();
}

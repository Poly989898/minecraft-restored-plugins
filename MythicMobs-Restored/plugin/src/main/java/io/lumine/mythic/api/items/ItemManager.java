package io.lumine.mythic.api.items;

import io.lumine.mythic.core.items.MythicItem;
import java.util.Collection;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public interface ItemManager {
   void registerItemSupplier(ItemSupplier itemSupplier);

   Collection<MythicItem> getItems();

   Collection<String> getItemNames();

   Optional<MythicItem> getItem(String str);

   boolean isMythicItem(ItemStack itemStack);

   String getMythicTypeFromItem(ItemStack itemStack);

   Collection<MythicItem> filterItems(Collection<MythicItem> mythicItems, String str);
}

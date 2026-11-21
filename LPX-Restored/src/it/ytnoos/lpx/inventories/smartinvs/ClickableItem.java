package it.ytnoos.lpx.inventories.smartinvs;

import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClickableItem {
   private final ItemStack item;
   private final Consumer<InventoryClickEvent> consumer;

   private ClickableItem(ItemStack itemStack, Consumer<InventoryClickEvent> consumer2) {
      this.item = itemStack;
      this.consumer = consumer2;
   }

   public static ClickableItem empty(ItemStack itemStack) {
      return of(itemStack, inventoryClickEvent -> {});
   }

   public static ClickableItem of(ItemStack itemStack, Consumer<InventoryClickEvent> consumer2) {
      return new ClickableItem(itemStack, consumer2);
   }

   public void run(Object object) {
      this.consumer.accept((InventoryClickEvent)object);
   }

   public ItemStack getItem() {
      return this.item;
   }
}

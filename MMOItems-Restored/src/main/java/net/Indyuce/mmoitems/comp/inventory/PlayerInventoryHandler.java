package net.Indyuce.mmoitems.comp.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerInventoryHandler {
   @NotNull
   private final List<PlayerInventory> registeredInventories = new ArrayList<>();

   public void register(@NotNull PlayerInventory playerInventory) {
      this.registeredInventories.add(playerInventory);
      if (playerInventory instanceof Listener) {
         Bukkit.getPluginManager().registerEvents((Listener)playerInventory, MMOItems.plugin);
      }
   }

   public void unregisterIf(Predicate<PlayerInventory> predicate) {
      Iterator iterator = this.registeredInventories.iterator();

      while (iterator.hasNext()) {
         PlayerInventory playerInventory = (PlayerInventory)iterator.next();
         if (predicate.test(playerInventory)) {
            if (playerInventory instanceof Listener) {
               HandlerList.unregisterAll((Listener)playerInventory);
            }

            iterator.remove();
         }
      }
   }

   public void unregisterAll() {
      for (PlayerInventory registeredInventory : this.registeredInventories) {
         if (registeredInventory instanceof Listener) {
            HandlerList.unregisterAll((Listener)registeredInventory);
         }
      }

      this.registeredInventories.clear();
   }

   public ArrayList<PlayerInventory> getAll() {
      return new ArrayList<>(this.registeredInventories);
   }

   @NotNull
   public List<EquippedItem> getInventory(@NotNull Player player) {
      ArrayList list = new ArrayList();

      for (PlayerInventory registeredInventory : this.registeredInventories) {
         list.addAll(registeredInventory.getInventory(player));
      }

      return list;
   }
}

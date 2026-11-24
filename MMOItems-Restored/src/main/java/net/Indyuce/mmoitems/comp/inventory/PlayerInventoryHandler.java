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

   public void register(@NotNull PlayerInventory var1) {
      this.registeredInventories.add(var1);
      if (var1 instanceof Listener) {
         Bukkit.getPluginManager().registerEvents((Listener)var1, MMOItems.plugin);
      }
   }

   public void unregisterIf(Predicate<PlayerInventory> var1) {
      Iterator var2 = this.registeredInventories.iterator();

      while (var2.hasNext()) {
         PlayerInventory var3 = (PlayerInventory)var2.next();
         if (var1.test(var3)) {
            if (var3 instanceof Listener) {
               HandlerList.unregisterAll((Listener)var3);
            }

            var2.remove();
         }
      }
   }

   public void unregisterAll() {
      for (PlayerInventory var2 : this.registeredInventories) {
         if (var2 instanceof Listener) {
            HandlerList.unregisterAll((Listener)var2);
         }
      }

      this.registeredInventories.clear();
   }

   public ArrayList<PlayerInventory> getAll() {
      return new ArrayList<>(this.registeredInventories);
   }

   @NotNull
   public List<EquippedItem> getInventory(@NotNull Player var1) {
      ArrayList var2 = new ArrayList();

      for (PlayerInventory var4 : this.registeredInventories) {
         var2.addAll(var4.getInventory(var1));
      }

      return var2;
   }
}

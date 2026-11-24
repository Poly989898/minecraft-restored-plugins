package net.Indyuce.mmoitems.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DeathItemsHandler {
   private final List<ItemStack> items = new ArrayList<>();
   private final Location lastLocation;
   private final UUID playerId;
   private static final Map<UUID, DeathItemsHandler> PLAYER_INFO = new WeakHashMap<>();

   public DeathItemsHandler(@NotNull Player var1) {
      this.playerId = var1.getUniqueId();
      this.lastLocation = var1.getLocation();
   }

   public void registerItem(@NotNull ItemStack var1) {
      this.items.add(var1);
   }

   public void registerIfNecessary() {
      if (!this.items.isEmpty()) {
         PLAYER_INFO.put(this.playerId, this);
      }
   }

   public void dropItems() {
      for (ItemStack var2 : this.items) {
         this.lastLocation.getWorld().dropItem(this.lastLocation, var2);
      }
   }

   public void giveItems(@NotNull Player var1) {
      ItemStack[] var2 = this.items.toArray(new ItemStack[0]);

      for (ItemStack var4 : var1.getInventory().addItem(var2).values()) {
         var1.getWorld().dropItem(var1.getLocation(), var4);
      }
   }

   public static void readAndRemove(@NotNull Player var0) {
      DeathItemsHandler var1 = PLAYER_INFO.remove(var0.getUniqueId());
      if (var1 != null) {
         Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> var1.giveItems(var0), 10L);
      }
   }

   @NotNull
   public static Collection<DeathItemsHandler> getActive() {
      return PLAYER_INFO.values();
   }
}

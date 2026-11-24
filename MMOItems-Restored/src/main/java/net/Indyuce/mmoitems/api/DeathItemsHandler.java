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

   public DeathItemsHandler(@NotNull Player player) {
      this.playerId = player.getUniqueId();
      this.lastLocation = player.getLocation();
   }

   public void registerItem(@NotNull ItemStack item) {
      this.items.add(item);
   }

   public void registerIfNecessary() {
      if (!this.items.isEmpty()) {
         PLAYER_INFO.put(this.playerId, this);
      }
   }

   public void dropItems() {
      for (ItemStack item : this.items) {
         this.lastLocation.getWorld().dropItem(this.lastLocation, item);
      }
   }

   public void giveItems(@NotNull Player player) {
      ItemStack[] items = this.items.toArray(new ItemStack[0]);

      for (ItemStack item : player.getInventory().addItem(items).values()) {
         player.getWorld().dropItem(player.getLocation(), item);
      }
   }

   public static void readAndRemove(@NotNull Player player) {
      DeathItemsHandler deathItemsHandler = PLAYER_INFO.remove(player.getUniqueId());
      if (deathItemsHandler != null) {
         Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> deathItemsHandler.giveItems(player), 10L);
      }
   }

   @NotNull
   public static Collection<DeathItemsHandler> getActive() {
      return PLAYER_INFO.values();
   }
}

package io.lumine.mythic.lib.api.event.armorequip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancel = false;
   private final ArmorEquipEvent.EquipMethod equipType;
   private final ArmorType type;
   private ItemStack oldArmorPiece;
   private ItemStack newArmorPiece;

   public static void registerListener(JavaPlugin plugin) {
      Bukkit.getServer().getPluginManager().registerEvents(new ArmorListener(getBlockedMaterialNames(plugin)), plugin);
      Bukkit.getServer().getPluginManager().registerEvents(new DispenserArmorListener(), plugin);
   }

   private static List<String> getBlockedMaterialNames(JavaPlugin plugin) {
      try {
         InputStream inputStream = plugin.getResource("armorequipevent-blocked.txt");

         List var3;
         label77: {
            try {
               assert inputStream != null;

               try {
                  BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                  try {
                     var3 = reader.lines().collect(Collectors.toList());
                  } catch (Throwable var7) {
                     try {
                        reader.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }

                     throw var7;
                  }

                  reader.close();
                  break label77;
               } catch (Exception var8) {
               }
            } catch (Throwable var9) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var5) {
                     var9.addSuppressed(var5);
                  }
               }

               throw var9;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return new ArrayList<>();
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var3;
      } catch (IOException var10) {
         return new ArrayList<>();
      }
   }

   public ArmorEquipEvent(Player player, ArmorEquipEvent.EquipMethod equipType, ArmorType type, ItemStack oldArmorPiece, ItemStack newArmorPiece) {
      super(player);
      this.equipType = equipType;
      this.type = type;
      this.oldArmorPiece = oldArmorPiece;
      this.newArmorPiece = newArmorPiece;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public final HandlerList getHandlers() {
      return handlers;
   }

   public final void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public final boolean isCancelled() {
      return this.cancel;
   }

   public final ArmorType getType() {
      return this.type;
   }

   public final ItemStack getOldArmorPiece() {
      return this.oldArmorPiece;
   }

   public final void setOldArmorPiece(ItemStack oldArmorPiece) {
      this.oldArmorPiece = oldArmorPiece;
   }

   public final ItemStack getNewArmorPiece() {
      return this.newArmorPiece;
   }

   public final void setNewArmorPiece(ItemStack newArmorPiece) {
      this.newArmorPiece = newArmorPiece;
   }

   public ArmorEquipEvent.EquipMethod getMethod() {
      return this.equipType;
   }

   public enum EquipMethod {
      SHIFT_CLICK,
      DRAG,
      PICK_DROP,
      HOTBAR,
      HOTBAR_SWAP,
      DISPENSER,
      BROKE,
      DEATH;
   }
}

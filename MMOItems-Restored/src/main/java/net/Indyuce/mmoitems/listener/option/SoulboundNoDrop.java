package net.Indyuce.mmoitems.listener.option;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.Map.Entry;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulboundNoDrop implements Listener {
   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void cannotDrop(PlayerDropItemEvent var1) {
      if (this.isBound(var1.getItemDrop().getItemStack(), var1.getPlayer())) {
         var1.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void cannotDragAround(InventoryDragEvent var1) {
      VInventoryView var2 = VersionUtils.getView(var1);
      if (var2.getType() != InventoryType.CRAFTING) {
         int var3 = var2.getTopInventory().getContents().length;

         for (Entry var5 : var1.getNewItems().entrySet()) {
            if ((Integer)var5.getKey() < var3 && this.isBound((ItemStack)var5.getValue(), var1.getWhoClicked())) {
               var1.setCancelled(true);
               return;
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void cannotMoveAround(InventoryClickEvent var1) {
      if (VersionUtils.getView(var1).getType() != InventoryType.CRAFTING) {
         try {
            boolean var2 = this.isSafe(var1);
            if (!var2) {
               var1.setCancelled(true);
            }
         } catch (RuntimeException var3) {
            if (this.isBound(var1.getCurrentItem(), var1.getWhoClicked()) || this.isBound(var1.getCursor(), var1.getWhoClicked())) {
               var1.setCancelled(true);
            }
         }
      }
   }

   private boolean isSafe(@NotNull InventoryClickEvent var1) {
      switch (var1.getAction()) {
         case NOTHING:
         case PICKUP_ALL:
         case PICKUP_SOME:
         case PICKUP_HALF:
         case PICKUP_ONE:
         case COLLECT_TO_CURSOR:
         case CLONE_STACK:
         case HOTBAR_MOVE_AND_READD:
            return true;
         case DROP_ONE_CURSOR:
         case DROP_ALL_CURSOR:
            return !this.isBound(var1.getCursor(), var1.getWhoClicked());
         case DROP_ALL_SLOT:
         case DROP_ONE_SLOT:
            return !this.isBound(var1.getCurrentItem(), var1.getWhoClicked());
         case SWAP_WITH_CURSOR:
         case PLACE_ALL:
         case PLACE_SOME:
         case PLACE_ONE:
            if (var1.getClickedInventory().getType() == InventoryType.PLAYER) {
               return true;
            }

            return !this.isBound(var1.getCursor(), var1.getWhoClicked());
         case HOTBAR_SWAP:
            if (var1.getClickedInventory().getType() == InventoryType.PLAYER) {
               return true;
            }

            ItemStack var2 = var1.getWhoClicked().getInventory().getItem(var1.getHotbarButton());
            return !this.isBound(var2, var1.getWhoClicked());
         case MOVE_TO_OTHER_INVENTORY:
            if (var1.getClickedInventory().getType() != InventoryType.PLAYER) {
               return true;
            }

            return !this.isBound(var1.getCurrentItem(), var1.getWhoClicked());
         case UNKNOWN:
         default:
            throw new RuntimeException("Not implemented");
      }
   }

   private boolean isBound(@Nullable ItemStack var1, @NotNull HumanEntity var2) {
      return var1 != null && var1.hasItemMeta() && MMOUtils.isSoulboundTo(NBTItem.get(var1), (Player)var2);
   }
}

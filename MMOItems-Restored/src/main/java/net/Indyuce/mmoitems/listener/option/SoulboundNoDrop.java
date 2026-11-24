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
   public void cannotDrop(PlayerDropItemEvent event) {
      if (this.isBound(event.getItemDrop().getItemStack(), event.getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void cannotDragAround(InventoryDragEvent event) {
      VInventoryView view = VersionUtils.getView(event);
      if (view.getType() != InventoryType.CRAFTING) {
         int length = view.getTopInventory().getContents().length;

         for (Entry entry : event.getNewItems().entrySet()) {
            if ((Integer)entry.getKey() < length && this.isBound((ItemStack)entry.getValue(), event.getWhoClicked())) {
               event.setCancelled(true);
               return;
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void cannotMoveAround(InventoryClickEvent event) {
      if (VersionUtils.getView(event).getType() != InventoryType.CRAFTING) {
         try {
            boolean isSafe = this.isSafe(event);
            if (!isSafe) {
               event.setCancelled(true);
            }
         } catch (RuntimeException exception) {
            if (this.isBound(event.getCurrentItem(), event.getWhoClicked()) || this.isBound(event.getCursor(), event.getWhoClicked())) {
               event.setCancelled(true);
            }
         }
      }
   }

   private boolean isSafe(@NotNull InventoryClickEvent event) {
      switch (event.getAction()) {
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
            return !this.isBound(event.getCursor(), event.getWhoClicked());
         case DROP_ALL_SLOT:
         case DROP_ONE_SLOT:
            return !this.isBound(event.getCurrentItem(), event.getWhoClicked());
         case SWAP_WITH_CURSOR:
         case PLACE_ALL:
         case PLACE_SOME:
         case PLACE_ONE:
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
               return true;
            }

            return !this.isBound(event.getCursor(), event.getWhoClicked());
         case HOTBAR_SWAP:
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
               return true;
            }

            ItemStack item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            return !this.isBound(item, event.getWhoClicked());
         case MOVE_TO_OTHER_INVENTORY:
            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
               return true;
            }

            return !this.isBound(event.getCurrentItem(), event.getWhoClicked());
         case UNKNOWN:
         default:
            throw new RuntimeException("Not implemented");
      }
   }

   private boolean isBound(@Nullable ItemStack item, @NotNull HumanEntity humanEntity) {
      return item != null && item.hasItemMeta() && MMOUtils.isSoulboundTo(NBTItem.get(item), (Player)humanEntity);
   }
}

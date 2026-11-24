package net.Indyuce.inventory.listener.option;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.profile.ProfileMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.compat.profile.NoCustomInventoryProfile;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class NoCustomInventory implements Listener {
   private static final List<InventoryAction> supported = Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PLACE_ALL, InventoryAction.SWAP_WITH_CURSOR);

   private NoCustomInventory() {
   }

   @NotNull
   public static Iterable<Listener> getListeners() {
      List<Listener> listeners = new ArrayList<>();
      NoCustomInventory main = new NoCustomInventory();
      listeners.add(main);
      listeners.add((Listener)(MythicLib.plugin.getProfileMode() == ProfileMode.LEGACY ? new NoCustomInventoryProfile() : new NoCustomInventory.NoProfiles()));
      return listeners;
   }

   public static void putPlaceholderItems(@NotNull Player player) {
      PlayerInventory inv = player.getInventory();

      for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getCustomSlots()) {
         if (UtilityMethods.isAir(inv.getItem(slot.getIndex()))) {
            inv.setItem(slot.getIndex(), slot.displayItem(player));
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void placeBackSlotItems(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      if (player.getInventory().equals(event.getClickedInventory())) {
         CustomSlot slot = MMOInventory.plugin.getSlotManager().get(event.getSlot());
         if (slot != null) {
            if (!supported.contains(event.getAction())) {
               event.setCancelled(true);
            } else {
               ItemStack item = event.getCurrentItem();
               if (UtilityMethods.isAir(event.getCursor()) && Utils.isGuiItem(item)) {
                  event.setCancelled(true);
               } else {
                  ItemStack cursor = event.getCursor();
                  if (!UtilityMethods.isAir(event.getCursor())) {
                     if (MMOInventory.plugin.getConfig().getBoolean("disable-equiping-stacked-items", true) && event.getCursor().getAmount() > 1) {
                        event.setCancelled(true);
                        return;
                     }

                     if (!slot.canHost((InventoryHandler)MMOInventory.plugin.getDataManager().get(player), cursor)) {
                        event.setCancelled(true);
                        return;
                     }
                  }

                  ItemEquipEvent.EquipAction action = UtilityMethods.isAir(event.getCursor())
                     ? ItemEquipEvent.EquipAction.UNEQUIP
                     : (Utils.isGuiItem(cursor) ? ItemEquipEvent.EquipAction.EQUIP : ItemEquipEvent.EquipAction.SWAP_ITEMS);
                  ItemEquipEvent called = new ItemEquipEvent(player, event.getCursor(), slot, action);
                  Bukkit.getPluginManager().callEvent(called);
                  if (called.isCancelled()) {
                     event.setCancelled(true);
                  } else {
                     if (Utils.isGuiItem(item)) {
                        event.setCurrentItem(null);
                     }

                     if (UtilityMethods.isAir(event.getCursor())) {
                        Bukkit.getScheduler().runTask(MMOInventory.plugin, () -> player.getInventory().setItem(slot.getIndex(), slot.displayItem(player)));
                     }

                     MMOInventory.plugin.updateInventory(player);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void removeGuiDrops(PlayerDeathEvent event) {
      if (!event.getKeepInventory()) {
         event.getDrops().removeIf(Utils::isGuiItem);
      }
   }

   @EventHandler
   public void giveItemsOnRespawn(PlayerRespawnEvent event) {
      putPlaceholderItems(event.getPlayer());
   }

   static class NoProfiles implements Listener {
      @EventHandler
      public void giveItemsOnJoin(PlayerJoinEvent event) {
         NoCustomInventory.putPlaceholderItems(event.getPlayer());
      }
   }
}

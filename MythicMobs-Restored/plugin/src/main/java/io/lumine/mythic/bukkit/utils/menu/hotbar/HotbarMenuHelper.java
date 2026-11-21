package io.lumine.mythic.bukkit.utils.menu.hotbar;

import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MenuHelper;
import io.lumine.mythic.bukkit.utils.menu.MonitoredState;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HotbarMenuHelper implements Listener {
   private static HotbarMenuHelper _instance = new HotbarMenuHelper();
   private final Map<UUID, ActiveHotbarMenu<?>> openMenus = new HashMap<>();

   protected static <T> ActiveHotbarMenu<T> registerOpen(Player player, HotbarMenu<T> menu, T state) {
      ActiveHotbarMenu amenu = new ActiveHotbarMenu<>(player.getUniqueId(), menu, state);
      _instance.openMenus.put(player.getUniqueId(), amenu);
      return amenu;
   }

   protected static void registerClosed(UUID player) {
      _instance.openMenus.remove(player);
   }

   private HotbarMenuHelper() {
      Bukkit.getPluginManager().registerEvents(this, LoaderUtils.getPlugin());
      Bukkit.getScheduler().runTaskTimer(LoaderUtils.getPlugin(), this::updateMenus, 10L, 10L);
   }

   @EventHandler
   private void onClose(PlayerQuitEvent event) {
      this.openMenus.remove(event.getPlayer().getUniqueId());
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() != Action.PHYSICAL) {
         if (event.getHand() == EquipmentSlot.HAND) {
            ActiveHotbarMenu<?> openMenu = this.openMenus.get(event.getPlayer().getUniqueId());
            if (openMenu != null) {
               if (!MenuHelper.get_instance().getOpenMenus().containsKey(event.getPlayer().getUniqueId())) {
                  int slot = event.getPlayer().getInventory().getHeldItemSlot();
                  if (openMenu.getMenu().getFreeSlots().isEmpty() || !openMenu.getMenu().getFreeSlots().contains(slot)) {
                     Player player = event.getPlayer();
                     if (openMenu.getMenu().getIcons().containsKey(slot)) {
                        event.setCancelled(true);
                        this.handleClick(openMenu, player, slot);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      ActiveHotbarMenu<?> openMenu = this.openMenus.get(event.getWhoClicked().getUniqueId());
      if (openMenu != null) {
         int slot = event.getWhoClicked().getInventory().getHeldItemSlot();
         if (openMenu.getMenu().getFreeSlots().isEmpty() || !openMenu.getMenu().getFreeSlots().contains(slot)) {
            if (event.getSlotType() != SlotType.QUICKBAR) {
               event.setCancelled(true);
            } else if (event.getSlotType() == SlotType.ARMOR && event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void onInventoryClick(PlayerInteractEntityEvent event) {
      ActiveHotbarMenu<?> openMenu = this.openMenus.get(event.getPlayer().getUniqueId());
      if (openMenu != null) {
         int slot = event.getPlayer().getInventory().getHeldItemSlot();
         if (openMenu.getMenu().getFreeSlots().isEmpty() || !openMenu.getMenu().getFreeSlots().contains(slot)) {
            if (event.getRightClicked() instanceof ArmorStand || event.getRightClicked() instanceof ItemFrame) {
               Player player = event.getPlayer();
               if (openMenu.getMenu().getIcons().containsKey(player.getInventory().getHeldItemSlot())) {
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryClick(BlockPlaceEvent event) {
      ActiveHotbarMenu<?> openMenu = this.openMenus.get(event.getPlayer().getUniqueId());
      if (openMenu != null) {
         Player player = event.getPlayer();
         if (openMenu.getMenu().getIcons().containsKey(player.getInventory().getHeldItemSlot())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onItemDrop(PlayerDropItemEvent event) {
      ActiveHotbarMenu<?> openMenu = this.openMenus.get(event.getPlayer().getUniqueId());
      if (openMenu != null) {
         int slot = event.getPlayer().getInventory().getHeldItemSlot();
         if (openMenu.getMenu().getFreeSlots().isEmpty() || !openMenu.getMenu().getFreeSlots().contains(slot)) {
            Player player = event.getPlayer();
            if (openMenu.getMenu().getIcons().containsKey(slot)) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onRespawn(PlayerDeathEvent event) {
      ActiveHotbarMenu<?> openMenu = this.openMenus.get(event.getEntity().getUniqueId());
      if (openMenu != null) {
         openMenu.terminate();
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onChangeMode(PlayerGameModeChangeEvent event) {
      ActiveHotbarMenu<?> openMenu = this.openMenus.get(event.getPlayer().getUniqueId());
      if (openMenu != null) {
         this.updateMenu(event.getPlayer().getUniqueId(), openMenu);
      }
   }

   private <T> void handleClick(ActiveHotbarMenu<T> openMenu, Player player, int slot) {
      openMenu.getMenu().getIcon(slot).ifPresent(icon -> icon.getClickFunc().ifPresent(func -> func.accept(openMenu.getState(), player)));
   }

   private <T> void handleRightClick(ActiveHotbarMenu<T> openMenu, Player player, int slot) {
      openMenu.getMenu().getIcon(slot).ifPresent(icon -> icon.getRightClickFunc().ifPresent(func -> func.accept(openMenu.getState(), player)));
   }

   @EventHandler
   private void onDrag(InventoryDragEvent event) {
      if (this.openMenus.containsKey(event.getWhoClicked().getUniqueId())) {
         event.setCancelled(true);
      }
   }

   private void updateMenus() {
      this.openMenus.forEach((uuid, menu) -> this.updateMenu(uuid, (ActiveHotbarMenu<?>)menu));
   }

   private <T> void updateMenu(UUID uuid, ActiveHotbarMenu<T> openMenu) {
      Inventory inv;
      Player player;
      try {
         player = Bukkit.getPlayer(uuid);
         if (player == null) {
            Schedulers.sync().runLater(() -> this.openMenus.remove(uuid), 1L);
            return;
         }

         inv = Bukkit.getPlayer(uuid).getInventory();
      } catch (Exception e) {
         player = Bukkit.getPlayer(uuid);
         if (player == null) {
            Schedulers.sync().runLater(() -> this.openMenus.remove(uuid), 1L);
         }

         return;
      }

      final Player finalPlayer = player;
      HotbarMenu<T> menu = openMenu.getMenu();
      Optional<MonitoredState> maybeMonitor = menu.getMonitoredState(openMenu.getState());
      if (maybeMonitor.isPresent()) {
         if (!maybeMonitor.filter(monitor -> monitor.getMonitorTimestamp() != openMenu.getMonitorTimestamp()).isPresent()) {
            if (menu.getBlinkingSlots().isPresent()) {
               menu.getBlinkingSlots().get().stream().forEach(slot -> {
                  Icon icon = menu.getIcon(slot).get();
                  ItemStack stack = icon.build(openMenu.getState(), finalPlayer);
                  ItemStack comp = inv.getItem(slot);
                  if (comp == null && stack.getType() != Material.AIR || comp != null && !comp.equals(stack)) {
                     inv.setItem(slot, stack);
                  }
               });
            }

            return;
         }

         openMenu.setMonitorTimestamp(maybeMonitor.get().getMonitorTimestamp());
      }

      menu.getIcons().forEach((slot, icon) -> {
         ItemStack stack = icon.build(openMenu.getState(), finalPlayer);
         ItemStack comp = inv.getItem(slot);
         if (comp == null && stack.getType() != Material.AIR || comp != null && !comp.equals(stack)) {
            inv.setItem(slot, stack);
         }
      });
   }
}

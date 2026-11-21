package io.lumine.mythic.bukkit.utils.menu;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuHelper implements Listener {
   private static MenuHelper _instance = new MenuHelper();
   private final Map<UUID, MenuHelper.OpenMenu<?>> openMenus = Maps.newConcurrentMap();
   private final Collection<UUID> swappingMenus = Sets.newHashSet();

   protected static <T> MenuHelper.OpenMenu<T> registerOpen(
      Player player, Menu<T> menu, Inventory inventory, T state, List<MenuData<T>> data, List<MenuData<T>> secondaryData
   ) {
      MenuHelper.OpenMenu<T> om = new MenuHelper.OpenMenu<>(menu, player, inventory, state, data, secondaryData);
      _instance.openMenus.put(player.getUniqueId(), om);
      return om;
   }

   protected static <T> MenuHelper.OpenMenu<T> swapMenu(
      Player player, Menu<T> menu, Inventory inventory, T state, List<MenuData<T>> data, List<MenuData<T>> secondaryData
   ) {
      MenuHelper.OpenMenu lastMenu = _instance.openMenus.remove(player.getUniqueId());
      lastMenu.getMenu().getCloseFunc().accept(lastMenu, new InventoryCloseEvent(player.getOpenInventory(), Reason.OPEN_NEW));
      Inventory inv = lastMenu.getInventory();
      inv.clear();
      _instance.swappingMenus.add(player.getUniqueId());
      MenuHelper.OpenMenu<T> om = new MenuHelper.OpenMenu<>(menu, player, inventory, state, data, secondaryData);
      _instance.openMenus.put(player.getUniqueId(), om);
      return om;
   }

   protected static <T> MenuHelper.OpenMenu<T> unregisterOpen(Player player) {
      MenuHelper.OpenMenu lastMenu = _instance.openMenus.remove(player.getUniqueId());
      if (lastMenu != null) {
         lastMenu.getMenu().getCloseFunc().accept(lastMenu, null);
      }

      return lastMenu;
   }

   public static <T> MenuHelper.OpenMenu<?> getOpenMenu(Player player) {
      return _instance.openMenus.getOrDefault(player.getUniqueId(), null);
   }

   public static void updateMenuTitle(Player player, String title) {
      MenuHelper.OpenMenu<?> openMenu = _instance.openMenus.get(player.getUniqueId());
      if (openMenu != null) {
         Inventory oldInv = openMenu.getInventory();
         Inventory newInv = Bukkit.createInventory(null, oldInv.getSize(), title);
         newInv.setContents(oldInv.getContents());
         _instance.swappingMenus.add(player.getUniqueId());
         openMenu.inventory = newInv;
         player.openInventory(newInv);
      }
   }

   public static <T> void reopenMenu(Player player, MenuHelper.OpenMenu<T> menu) {
      _instance.openMenus.put(player.getUniqueId(), menu);
      player.openInventory(menu.getInventory());
      _instance.updateMenu(player.getUniqueId(), menu, true);
   }

   protected static <T> void moveToNextPage(Player player) {
      MenuHelper.OpenMenu openMenu = _instance.openMenus.getOrDefault(player.getUniqueId(), null);
      if (openMenu != null) {
         openMenu.nextPage();
         _instance.updateMenu(player.getUniqueId(), openMenu, true);
      } else {
         Log.info("moveToNextPage on unopened menu by {0}?", player.getName());
      }
   }

   protected static <T> void moveToPreviousPage(Player player) {
      MenuHelper.OpenMenu openMenu = _instance.openMenus.getOrDefault(player.getUniqueId(), null);
      if (openMenu != null) {
         openMenu.previousPage();
         _instance.updateMenu(player.getUniqueId(), openMenu, true);
      } else {
         Log.info("moveToPreviousPage on unopened menu by {0}?", player.getName());
      }
   }

   protected static <T> void moveToNextSecondaryPage(Player player) {
      MenuHelper.OpenMenu openMenu = _instance.openMenus.getOrDefault(player.getUniqueId(), null);
      if (openMenu != null) {
         openMenu.nextSecondaryPage();
         _instance.updateMenu(player.getUniqueId(), openMenu, true);
      } else {
         Log.info("moveToNextSecondaryPage on unopened menu by {0}?", player.getName());
      }
   }

   protected static <T> void moveToPreviousSecondaryPage(Player player) {
      MenuHelper.OpenMenu openMenu = _instance.openMenus.getOrDefault(player.getUniqueId(), null);
      if (openMenu != null) {
         openMenu.previousSecondaryPage();
         _instance.updateMenu(player.getUniqueId(), openMenu, true);
      } else {
         Log.info("moveToPreviousSecondaryPage on unopened menu by {0}?", player.getName());
      }
   }

   private MenuHelper() {
      Bukkit.getPluginManager().registerEvents(this, LoaderUtils.getPlugin());
      Schedulers.sync().runRepeating(task -> this.updateMenus(), 1L, 1L);
   }

   @EventHandler
   private void onOpen(InventoryOpenEvent event) {
      if (this.openMenus.containsKey(event.getPlayer().getUniqueId())) {
         MenuHelper.OpenMenu menu = this.openMenus.get(event.getPlayer().getUniqueId());
         menu.getMenu().getOpenFunc().accept(menu, event);
         Schedulers.sync().runLater(() -> {
            if (this.openMenus.containsKey(event.getPlayer().getUniqueId())) {
               this.updateMenu(event.getPlayer().getUniqueId(), this.openMenus.get(event.getPlayer().getUniqueId()), true);
            }
         }, 1L);
      }
   }

   @EventHandler
   private void onClose(InventoryCloseEvent event) {
      if (this.openMenus.containsKey(event.getPlayer().getUniqueId())) {
         if (this.swappingMenus.contains(event.getPlayer().getUniqueId())) {
            this.swappingMenus.remove(event.getPlayer().getUniqueId());
         } else {
            MenuHelper.OpenMenu menu = this.openMenus.remove(event.getPlayer().getUniqueId());
            menu.getMenu().getCloseFunc().accept(menu, event);
         }
      }
   }

   public void registerMenuClosed(Player player) {
      if (this.openMenus.containsKey(player.getUniqueId())) {
         MenuHelper.OpenMenu menu = this.openMenus.remove(player.getUniqueId());
         menu.getMenu().getCloseFunc().accept(menu, null);
      }
   }

   @EventHandler
   private void onClick(InventoryClickEvent event) {
      MenuHelper.OpenMenu<?> openMenu = this.openMenus.get(event.getWhoClicked().getUniqueId());
      if (openMenu != null) {
         if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(true);
         }

         if (openMenu.handleClick(event)) {
            event.setCancelled(true);
         }

         if (openMenu.menu.isStaticMenu()) {
            this.updateMenu(event.getWhoClicked().getUniqueId(), this.openMenus.get(event.getWhoClicked().getUniqueId()), true);
         }
      }
   }

   @EventHandler
   private void onDrag(InventoryDragEvent event) {
      MenuHelper.OpenMenu<?> openMenu = this.openMenus.get(event.getWhoClicked().getUniqueId());
      if (openMenu != null) {
         for (Integer slot : event.getRawSlots()) {
            if (slot < openMenu.inventory.getSize() && openMenu.handleClick(event)) {
               event.setCancelled(true);
            }
         }

         if (openMenu.menu.isStaticMenu()) {
            this.updateMenu(event.getWhoClicked().getUniqueId(), this.openMenus.get(event.getWhoClicked().getUniqueId()), true);
         }
      }
   }

   private void updateMenus() {
      this.openMenus.forEach((uuid, menu) -> this.updateMenu(uuid, (MenuHelper.OpenMenu<?>)menu));
   }

   private <T> void updateMenu(UUID uuid, MenuHelper.OpenMenu<T> openMenu, boolean force) {
      Inventory inv;
      Player player;
      try {
         player = Bukkit.getPlayer(uuid);
         if (player == null) {
            Schedulers.sync().runLater(() -> this.openMenus.remove(uuid), 1L);
            return;
         }

         inv = player.getOpenInventory().getTopInventory();
      } catch (Exception e) {
         e.printStackTrace();
         player = Bukkit.getPlayer(uuid);
         if (player == null) {
            player.closeInventory();
            Schedulers.sync().runLater(() -> this.openMenus.remove(uuid), 1L);
         }

         return;
      }

      if (openMenu != null) {
         Menu<T> menu = openMenu.menu;
         Player finalPlayer = player;
         if (menu.getUpdateInterval() <= 1L || System.currentTimeMillis() - openMenu.lastUpdate >= menu.getUpdateInterval() * 50L) {
            if (!menu.isStaticMenu() || force) {
               Optional<MonitoredState> maybeMonitor = menu.getMonitoredState(openMenu.state);
               if (maybeMonitor.isPresent()) {
                  if (!maybeMonitor.filter(monitor -> monitor.getMonitorTimestamp() != openMenu.monitorTimestamp).isPresent()) {
                     if (menu.getBlinkingSlots().isPresent()) {
                        menu.getBlinkingSlots().get().stream().forEach(slotx -> {
                           Icon icon = menu.getIcon(slotx).get();
                           ItemStack stack = icon.build(openMenu.state, finalPlayer);
                           ItemStack compx = inv.getItem(slotx);
                           if (compx == null && stack.getType() != Material.AIR || compx != null && !compx.equals(stack)) {
                              inv.setItem(slotx, stack);
                           }
                        });
                     }

                     return;
                  }

                  openMenu.monitorTimestamp = maybeMonitor.get().getMonitorTimestamp();
               }

               menu.sort(openMenu.state, player);
               openMenu.getIcons().forEach((slotx, icon) -> {
                  ItemStack stack = icon.build(openMenu.state, finalPlayer);
                  ItemStack compx = inv.getItem(slotx);
                  if (compx == null && stack.getType() != Material.AIR || compx != null && !compx.equals(stack)) {
                     inv.setItem(slotx, stack);
                  }
               });
               if (!menu.getDataSlots().isEmpty()) {
                  int count = 0;

                  for (int slot : menu.getDataSlots()) {
                     try {
                        ItemStack stack;
                        if (count >= openMenu.getDisplayedData().size()) {
                           if (menu.getFallbackDataIcon() != null) {
                              stack = menu.getFallbackDataIcon().build(openMenu.state, finalPlayer);
                           } else {
                              stack = new ItemStack(Material.AIR);
                           }
                        } else {
                           stack = openMenu.getDisplayedData().get(count).getIcon().build(openMenu.state, finalPlayer);
                        }

                        ItemStack comp = inv.getItem(slot);
                        if (comp == null && stack.getType() != Material.AIR || comp != null && !comp.equals(stack)) {
                           inv.setItem(slot, stack);
                        }
                     } catch (Error | Exception ex) {
                        Log.error("SOMETHING JUST FUCKING KILLED ITSELF THAT WAS IGNORED BEFORE while loading Data Icons! ~Ghmmy");
                        ex.printStackTrace();
                     }

                     count++;
                  }
               }

               if (!menu.getSecondaryDataSlots().isEmpty()) {
                  int count = 0;

                  for (int slot : menu.getSecondaryDataSlots()) {
                     try {
                        ItemStack stackx;
                        if (count >= openMenu.getSecondaryDisplayedData().size()) {
                           if (menu.getFallbackSecondaryDataIcon() != null) {
                              stackx = menu.getFallbackSecondaryDataIcon().build(openMenu.state, finalPlayer);
                           } else {
                              stackx = new ItemStack(Material.AIR);
                           }
                        } else {
                           stackx = openMenu.getSecondaryDisplayedData().get(count).getIcon().build(openMenu.state, finalPlayer);
                        }

                        ItemStack comp = inv.getItem(slot);
                        if (comp == null && stackx.getType() != Material.AIR || comp != null && !comp.equals(stackx)) {
                           inv.setItem(slot, stackx);
                        }
                     } catch (Error | Exception ex2) {
                        Log.error("SOMETHING JUST FUCKING KILLED ITSELF THAT WAS IGNORED BEFORE while loading Secondary Data Icons! ~Ghmmy");
                        ex2.printStackTrace();
                     }

                     count++;
                  }
               }

               openMenu.setLastUpdate(System.currentTimeMillis());
            }
         }
      }
   }

   private <T> void updateMenu(UUID uuid, MenuHelper.OpenMenu<T> openMenu) {
      this.updateMenu(uuid, openMenu, false);
   }

   public static MenuHelper get_instance() {
      return _instance;
   }

   public Map<UUID, MenuHelper.OpenMenu<?>> getOpenMenus() {
      return this.openMenus;
   }

   public Collection<UUID> getSwappingMenus() {
      return this.swappingMenus;
   }

   public static class OpenMenu<T> {
      private final Menu<T> menu;
      private final Player player;
      private Inventory inventory;
      private final T state;
      private final List<MenuData<T>> data;
      private final List<MenuData<T>> secondaryData;
      private long lastUpdate = System.currentTimeMillis();
      private long monitorTimestamp;
      private int page = 0;
      private int pages = 1;
      private int dataPerPage = 0;
      private final Map<Integer, Icon<? super T>> icons = Maps.newHashMap();
      private List<MenuData<T>> displayedData;
      private Map<Integer, Icon<T>> displayedIcons = Maps.newHashMap();
      private int secondaryPage = 0;
      private int secondaryPages = 1;
      private int secondaryDataPerPage = 0;
      private List<MenuData<T>> secondaryDisplayedData;
      private Map<Integer, Icon<T>> secondaryDisplayedIcons = Maps.newHashMap();

      public OpenMenu(Menu<T> menu, Player player, Inventory inventory, T state, List<MenuData<T>> data, List<MenuData<T>> secondaryData) {
         this.menu = menu;
         this.player = player;
         this.inventory = inventory;
         this.state = state;
         this.icons.putAll(menu.getIcons());
         this.data = data;
         this.secondaryData = secondaryData;
         this.dataPerPage = menu.getDataSlots().size();
         if (this.dataPerPage > 0) {
            int start = this.page * this.dataPerPage;
            int end = start + this.dataPerPage;
            this.displayedData = data.subList(start, end < data.size() ? end : data.size());
            this.pages = Numbers.max(0, (data.size() - 1) / this.dataPerPage);
            int count = 0;

            for (int slot : menu.getDataSlots()) {
               if (count < this.getDisplayedData().size()) {
                  this.displayedIcons.put(slot, this.getDisplayedData().get(count).getIcon());
               }

               count++;
            }
         }

         this.secondaryDataPerPage = menu.getSecondaryDataSlots().size();
         if (this.secondaryDataPerPage > 0) {
            int start = this.secondaryPage * this.secondaryDataPerPage;
            int end = start + this.secondaryDataPerPage;
            this.secondaryDisplayedData = secondaryData.subList(start, Math.min(end, secondaryData.size()));
            this.secondaryPages = Numbers.max(0, (secondaryData.size() - 1) / this.secondaryDataPerPage);
            int count = 0;

            for (int slot : menu.getSecondaryDataSlots()) {
               if (count < this.getSecondaryDisplayedData().size()) {
                  this.secondaryDisplayedIcons.put(slot, this.getSecondaryDisplayedData().get(count).getIcon());
               }

               count++;
            }
         }
      }

      public boolean handleClick(InventoryDragEvent event) {
         try {
            if (event.getInventory() == null) {
               return false;
            } else if (event.getRawSlots().size() > 1) {
               return true;
            } else {
               if (event.getType().equals(DragType.SINGLE)) {
                  event.getInventorySlots().forEach(slot -> this.handleRightClick(slot));
               } else if (event.getType().equals(DragType.EVEN)) {
                  event.getInventorySlots().forEach(slot -> this.handleClick(slot));
               }

               return true;
            }
         } catch (Error | Exception e) {
            Text.sendMessage((Player)event.getWhoClicked(), "<red>Sorry, an error occurred!");
            e.printStackTrace();
            return true;
         }
      }

      public boolean handleClick(InventoryClickEvent event) {
         try {
            if (event.getClickedInventory() == null) {
               return false;
            } else {
               if (event.getClick() == ClickType.RIGHT) {
                  this.handleRightClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.LEFT) {
                  this.handleClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                  this.handleShiftClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                  this.handleShiftRightClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.DOUBLE_CLICK) {
                  this.handleDoubleClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.MIDDLE) {
                  this.handleMiddleClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.DROP) {
                  this.handleDropClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.CONTROL_DROP) {
                  this.handleDropAllClick(event.getRawSlot());
               } else if (event.getClick() == ClickType.SWAP_OFFHAND) {
                  this.handleSwapOffhandClick(event.getRawSlot());
               }

               if (this.menu.getDefaultClickFunc() != null) {
                  return this.menu.getDefaultClickFunc().apply(this, event);
               } else {
                  return event.getClickedInventory().getType() == InventoryType.PLAYER ? event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY : true;
               }
            }
         } catch (Error | Exception e) {
            Text.sendMessage((Player)event.getWhoClicked(), "<red>Sorry, an error occurred!");
            e.printStackTrace();
            return true;
         }
      }

      private void handleClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleRightClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getRightClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleShiftClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getShiftClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleShiftRightClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getShiftRightClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleDoubleClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getDoubleClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleMiddleClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getMiddleClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleDropClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getDropClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleDropAllClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getDropAllClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      private void handleSwapOffhandClick(int slot) {
         this.getIcon(slot).ifPresent(icon -> icon.getSwapOffhandClickFunc().ifPresent(func -> func.accept(this.state, this.player)));
      }

      public Optional<Icon<? super T>> getIcon(int slot) {
         if (!this.menu.getDataSlots().isEmpty() && this.menu.getDataSlots().contains(slot)) {
            return Optional.ofNullable(this.displayedIcons.getOrDefault(slot, null));
         } else {
            return !this.menu.getSecondaryDataSlots().isEmpty() && this.menu.getSecondaryDataSlots().contains(slot)
               ? Optional.ofNullable(this.secondaryDisplayedIcons.getOrDefault(slot, null))
               : this.menu.getIcon(slot);
         }
      }

      public void setIcon(int slot, Icon<? super T> icon) {
         this.icons.put(slot, icon);
      }

      public void setPage(int page) {
         this.page = page;
         int start = page * this.dataPerPage;
         int end = start + this.dataPerPage;
         this.displayedData = this.data.subList(start, end < this.data.size() ? end : this.data.size());
         int count = 0;
         this.displayedIcons.clear();

         for (int slot : this.menu.getDataSlots()) {
            if (count < this.getDisplayedData().size()) {
               this.displayedIcons.put(slot, this.getDisplayedData().get(count).getIcon());
            }

            count++;
         }
      }

      public void nextPage() {
         this.setPage(Math.min(this.page + 1, this.pages));
      }

      public void previousPage() {
         this.setPage(Math.max(this.page - 1, 0));
      }

      public void setSecondaryPage(int page) {
         this.secondaryPage = page;
         int start = page * this.secondaryDataPerPage;
         int end = start + this.secondaryDataPerPage;
         this.secondaryDisplayedData = this.secondaryData.subList(start, end < this.secondaryData.size() ? end : this.secondaryData.size());
         int count = 0;
         this.secondaryDisplayedIcons.clear();

         for (int slot : this.menu.getSecondaryDataSlots()) {
            if (count < this.getSecondaryDisplayedData().size()) {
               this.secondaryDisplayedIcons.put(slot, this.getSecondaryDisplayedData().get(count).getIcon());
            }

            count++;
         }
      }

      public void nextSecondaryPage() {
         this.setSecondaryPage(Math.min(this.secondaryPage + 1, this.secondaryPages));
      }

      public void previousSecondaryPage() {
         this.setSecondaryPage(Math.max(this.secondaryPage - 1, 0));
      }

      public Menu<T> getMenu() {
         return this.menu;
      }

      public Player getPlayer() {
         return this.player;
      }

      public Inventory getInventory() {
         return this.inventory;
      }

      public T getState() {
         return this.state;
      }

      public List<MenuData<T>> getData() {
         return this.data;
      }

      public List<MenuData<T>> getSecondaryData() {
         return this.secondaryData;
      }

      public long getLastUpdate() {
         return this.lastUpdate;
      }

      public void setLastUpdate(long lastUpdate) {
         this.lastUpdate = lastUpdate;
      }

      public int getPage() {
         return this.page;
      }

      public int getPages() {
         return this.pages;
      }

      public int getDataPerPage() {
         return this.dataPerPage;
      }

      public Map<Integer, Icon<? super T>> getIcons() {
         return this.icons;
      }

      public List<MenuData<T>> getDisplayedData() {
         return this.displayedData;
      }

      public Map<Integer, Icon<T>> getDisplayedIcons() {
         return this.displayedIcons;
      }

      public int getSecondaryPage() {
         return this.secondaryPage;
      }

      public int getSecondaryPages() {
         return this.secondaryPages;
      }

      public int getSecondaryDataPerPage() {
         return this.secondaryDataPerPage;
      }

      public List<MenuData<T>> getSecondaryDisplayedData() {
         return this.secondaryDisplayedData;
      }

      public Map<Integer, Icon<T>> getSecondaryDisplayedIcons() {
         return this.secondaryDisplayedIcons;
      }
   }
}

package io.lumine.mythic.bukkit.utils.menu;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.logging.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class Menu<T> {
   ReloadableMenu holder;
   EditableMenuBuilder builder;
   private final Function<T, String> titleFunc;
   private final Function<T, MonitoredState> monitorFunc;
   private BiConsumer<MenuHelper.OpenMenu<T>, InventoryOpenEvent> openFunc = (a, b) -> {};
   private BiConsumer<MenuHelper.OpenMenu<T>, InventoryCloseEvent> closeFunc = (a, b) -> {};
   private final Map<Integer, Icon<? super T>> icons;
   private Icon<? super T> fallbackDataIcon;
   private Icon<? super T> fallbackSecondaryDataIcon;
   private BiFunction<MenuHelper.OpenMenu<T>, InventoryClickEvent, Boolean> defaultClickFunc;
   private int size = -1;
   private List<Integer> blinkSlots = Lists.newArrayList();
   private List<Integer> dataSlots = Lists.newArrayList();
   private List<Integer> secondaryDataSlots = Lists.newArrayList();
   private List<Integer> freeSlots = Lists.newArrayList();
   private List<Integer> sortSlots = null;
   private int minSort;
   private int maxSort;
   private boolean staticMenu = false;
   private long updateInterval = 1L;

   public static <T> MenuBuilder<T> builder() {
      return new MenuBuilder<>();
   }

   protected Menu(Function<T, String> titleFunc, Function<T, MonitoredState> monitorFunc, Map<Integer, Icon<? super T>> icons) {
      this.titleFunc = titleFunc;
      this.monitorFunc = monitorFunc;
      this.icons = icons;
   }

   @Deprecated
   public Menu<T> setSize(int size) {
      this.size = size;
      return this;
   }

   protected Map<Integer, Icon<? super T>> getIcons() {
      return this.icons;
   }

   protected Optional<Icon<? super T>> getIcon(int slot) {
      return Optional.ofNullable(this.icons.get(slot));
   }

   public Menu<T> addIcon(int slot, Icon<? super T> icon) {
      this.icons.put(slot, icon);
      if (icon.isBlinkingIcon()) {
         this.blinkSlots.add(slot);
      }

      return this;
   }

   public Menu<T> createIcon(int slot, Function<IconBuilder<T>, IconBuilder<T>> builderFunc) {
      this.addIcon(slot, builderFunc.apply(IconBuilder.create()).build());
      return this;
   }

   protected Optional<List<Integer>> getBlinkingSlots() {
      return Optional.ofNullable(this.blinkSlots);
   }

   protected Optional<MonitoredState> getMonitoredState(T state) {
      return this.monitorFunc == null ? Optional.empty() : Optional.of(this.monitorFunc.apply(state));
   }

   public void setSortSlots(List<Integer> sortable) {
      this.sortSlots = sortable;
      if (sortable != null) {
         Optional<Integer> maybeMinSort = sortable.stream().min(Comparator.naturalOrder());
         Optional<Integer> maybeMaxSort = sortable.stream().max(Comparator.naturalOrder());
         if (maybeMinSort.isPresent()) {
            this.minSort = maybeMinSort.get();
            if (maybeMaxSort.isPresent()) {
               this.maxSort = maybeMaxSort.get();
            } else {
               this.sortSlots = null;
            }
         } else {
            this.sortSlots = null;
         }
      }
   }

   protected void sort(final T state, final Player player) {
      if (this.sortSlots != null) {
         List<Icon<? super T>> sort = new ArrayList<>();

         for (int i = this.minSort; i <= this.maxSort; i++) {
            if (this.getIcon(i).isPresent()) {
               sort.add(this.getIcon(i).get());
            }
         }

         Collections.sort(sort, new Comparator<Icon<? super T>>() {
            public int compare(Icon<? super T> i1, Icon<? super T> i2) {
               int x1 = i1.getAmount(state, player);
               int x2 = i2.getAmount(state, player);
               return x2 - x1;
            }
         });
         int ix = this.minSort;

         for (Icon<? super T> icon : sort) {
            this.icons.put(ix, icon);
            ix++;
         }
      }
   }

   protected int getMinSortedSlot() {
      return this.minSort;
   }

   protected int getMaxSortedSlot() {
      return this.maxSort;
   }

   public void open(Player player, T state) {
      this.open(player, state, null, Collections.emptyList(), Collections.emptyList());
   }

   public void open(Player player, T state, String title) {
      this.open(player, state, title, Collections.emptyList());
   }

   public void open(Player player, T state, List<?> dynamicData) {
      this.open(player, state, null, dynamicData, Collections.emptyList());
   }

   public void open(Player player, T state, List<?> dynamicData, List<?> secondaryDynamicData) {
      this.open(player, state, null, dynamicData, secondaryDynamicData);
   }

   public void open(Player player, T state, String title, List<?> dynamicData) {
      this.open(player, state, title, dynamicData, Collections.emptyList());
   }

   public void open(Player player, T state, String title, List<?> dynamicData, List<?> secondaryDynamicData) {
      if (this.icons.isEmpty()) {
         throw new IllegalStateException("Tried to open an empty menu!");
      } else {
         List<MenuData<T>> data = Lists.newArrayList();

         for (Object obj : dynamicData) {
            if (obj instanceof MenuData menuData) {
               data.add(menuData);
            } else {
               if (!(obj instanceof BasicIcon basicIcon)) {
                  Log.error("Invalid menu data in menu opened by {0}", player.getName());
                  Log.error("Menu: {0}", this.titleFunc.apply(state));
                  Log.error("Data: {0}", obj.getClass().toString());
                  throw new IllegalArgumentException("Invalid menu data passed");
               }

               data.add(new WrappedMenuData<>(basicIcon));
            }
         }

         List<MenuData<T>> secondaryData = Lists.newArrayList();

         for (Object objx : secondaryDynamicData) {
            if (objx instanceof MenuData) {
               secondaryData.add((MenuData<T>)objx);
            } else {
               if (!(objx instanceof BasicIcon)) {
                  Log.error("Invalid menu secondary data in menu opened by {0}", player.getName());
                  Log.error("Menu: {0}", this.titleFunc.apply(state));
                  Log.error("Data: {0}", objx.getClass().toString());
                  throw new IllegalArgumentException("Invalid menu secondary data passed");
               }

               secondaryData.add(new WrappedMenuData<>((BasicIcon)objx));
            }
         }

         int max = this.icons.keySet().stream().sorted(Comparator.reverseOrder()).findFirst().get();
         int size = this.size;
         if (size < 0) {
            size = (int)Math.ceil((max + 1) / 9.0) * 9;
         }

         if (title == null) {
            title = this.titleFunc.apply(state);
         }

         Inventory inventory = Bukkit.createInventory(null, size, title);
         this.sort(state, player);
         this.icons.forEach((slotx, icon) -> inventory.setItem(slotx, icon.build(state, player)));
         if (this.dataSlots.size() > 0) {
            int dataPerPage = this.dataSlots.size();
            int start = 0 * dataPerPage;
            int end = start + dataPerPage;
            List<MenuData<T>> displayedData = data.subList(start, end < data.size() ? end : data.size());
            int count = 0;

            for (int slot : this.dataSlots) {
               if (count >= displayedData.size()) {
                  break;
               }

               try {
                  inventory.setItem(slot, displayedData.get(count).getIcon().build(state, player));
               } catch (IndexOutOfBoundsException e) {
                  e.printStackTrace();
               } catch (Error | Exception ex) {
                  ex.printStackTrace();
               }

               count++;
            }
         }

         if (this.secondaryDataSlots.size() > 0) {
            int secondaryDataPerPage = this.secondaryDataSlots.size();
            int start = 0 * secondaryDataPerPage;
            int end = start + secondaryDataPerPage;
            List<MenuData<T>> displayedData = secondaryData.subList(start, end < secondaryData.size() ? end : secondaryData.size());
            int count = 0;

            for (int slot : this.secondaryDataSlots) {
               if (count >= displayedData.size()) {
                  break;
               }

               try {
                  inventory.setItem(slot, displayedData.get(count).getIcon().build(state, player));
               } catch (IndexOutOfBoundsException ex2) {
                  ex2.printStackTrace();
               } catch (Error | Exception ex3) {
                  ex3.printStackTrace();
               }

               count++;
            }
         }

         Schedulers.ensureSync(() -> {
            if (MenuHelper.getOpenMenu(player) == null) {
               player.closeInventory();
               MenuHelper.registerOpen(player, this, inventory, state, data, secondaryData);
               player.openInventory(inventory);
            } else {
               MenuHelper.swapMenu(player, this, inventory, state, data, secondaryData);
               player.openInventory(inventory);
            }
         });
      }
   }

   public void nextPage(Player player) {
      MenuHelper.moveToNextPage(player);
   }

   public void previousPage(Player player) {
      MenuHelper.moveToPreviousPage(player);
   }

   public void nextSecondaryPage(Player player) {
      MenuHelper.moveToNextSecondaryPage(player);
   }

   public void previousSecondaryPage(Player player) {
      MenuHelper.moveToPreviousSecondaryPage(player);
   }

   public ReloadableMenu getHolder() {
      return this.holder;
   }

   public void setHolder(ReloadableMenu holder) {
      this.holder = holder;
   }

   public EditableMenuBuilder getBuilder() {
      return this.builder;
   }

   public void setBuilder(EditableMenuBuilder builder) {
      this.builder = builder;
   }

   public BiConsumer<MenuHelper.OpenMenu<T>, InventoryOpenEvent> getOpenFunc() {
      return this.openFunc;
   }

   public void setOpenFunc(BiConsumer<MenuHelper.OpenMenu<T>, InventoryOpenEvent> openFunc) {
      this.openFunc = openFunc;
   }

   public BiConsumer<MenuHelper.OpenMenu<T>, InventoryCloseEvent> getCloseFunc() {
      return this.closeFunc;
   }

   public void setCloseFunc(BiConsumer<MenuHelper.OpenMenu<T>, InventoryCloseEvent> closeFunc) {
      this.closeFunc = closeFunc;
   }

   public Icon<? super T> getFallbackDataIcon() {
      return this.fallbackDataIcon;
   }

   public void setFallbackDataIcon(Icon<? super T> fallbackDataIcon) {
      this.fallbackDataIcon = fallbackDataIcon;
   }

   public Icon<? super T> getFallbackSecondaryDataIcon() {
      return this.fallbackSecondaryDataIcon;
   }

   public void setFallbackSecondaryDataIcon(Icon<? super T> fallbackSecondaryDataIcon) {
      this.fallbackSecondaryDataIcon = fallbackSecondaryDataIcon;
   }

   public BiFunction<MenuHelper.OpenMenu<T>, InventoryClickEvent, Boolean> getDefaultClickFunc() {
      return this.defaultClickFunc;
   }

   public void setDefaultClickFunc(BiFunction<MenuHelper.OpenMenu<T>, InventoryClickEvent, Boolean> defaultClickFunc) {
      this.defaultClickFunc = defaultClickFunc;
   }

   public int getSize() {
      return this.size;
   }

   public List<Integer> getBlinkSlots() {
      return this.blinkSlots;
   }

   public void setBlinkSlots(List<Integer> blinkSlots) {
      this.blinkSlots = blinkSlots;
   }

   public List<Integer> getDataSlots() {
      return this.dataSlots;
   }

   public void setDataSlots(List<Integer> dataSlots) {
      this.dataSlots = dataSlots;
   }

   public List<Integer> getSecondaryDataSlots() {
      return this.secondaryDataSlots;
   }

   public void setSecondaryDataSlots(List<Integer> secondaryDataSlots) {
      this.secondaryDataSlots = secondaryDataSlots;
   }

   public List<Integer> getFreeSlots() {
      return this.freeSlots;
   }

   public void setFreeSlots(List<Integer> freeSlots) {
      this.freeSlots = freeSlots;
   }

   public List<Integer> getSortSlots() {
      return this.sortSlots;
   }

   public boolean isStaticMenu() {
      return this.staticMenu;
   }

   public void setStaticMenu(boolean staticMenu) {
      this.staticMenu = staticMenu;
   }

   public long getUpdateInterval() {
      return this.updateInterval;
   }

   public void setUpdateInterval(long updateInterval) {
      this.updateInterval = updateInterval;
   }
}

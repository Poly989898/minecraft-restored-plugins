package io.lumine.mythic.bukkit.utils.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MenuBuilder<T> {
   private Function<T, String> titleFunc;
   private Function<T, MonitoredState> monitorFunc;
   private BiConsumer<MenuHelper.OpenMenu<T>, InventoryOpenEvent> openFunc = (a, b) -> {};
   private BiConsumer<MenuHelper.OpenMenu<T>, InventoryCloseEvent> closeFunc = (a, b) -> {};
   private final Map<Integer, Icon<? super T>> icons = Maps.newHashMap();
   private Icon<? super T> fallbackDataIcon;
   private Icon<? super T> secondaryFallbackDataIcon;
   private int size = -1;
   private boolean staticMenu = false;
   private BiFunction<MenuHelper.OpenMenu<T>, InventoryClickEvent, Boolean> freeSlotFunc = null;
   private int curIconPos = -1;
   private List<Integer> dataSlots = Lists.newArrayList();
   private List<Integer> secondaryDataSlots = Lists.newArrayList();
   private List<Integer> sortSlots = null;
   private List<Integer> blinkSlots = Lists.newArrayList();
   private List<Integer> freeSlots = Lists.newArrayList();
   private int updateInterval = 1;

   public static <T> MenuBuilder<T> create() {
      return new MenuBuilder<>();
   }

   public MenuBuilder<T> title(String title) {
      this.titleFunc = _state -> title;
      return this;
   }

   public MenuBuilder<T> title(Function<T, String> titleFunc) {
      this.titleFunc = titleFunc;
      return this;
   }

   public MenuBuilder<T> size(int size) {
      this.size = size;
      return this;
   }

   public MenuBuilder<T> openAction(BiConsumer<MenuHelper.OpenMenu<T>, InventoryOpenEvent> func) {
      this.openFunc = func;
      return this;
   }

   public MenuBuilder<T> closeAction(BiConsumer<MenuHelper.OpenMenu<T>, InventoryCloseEvent> func) {
      this.closeFunc = func;
      return this;
   }

   public MenuBuilder<T> addIcon(Icon<? super T> icon) {
      do {
         this.curIconPos++;
      } while (this.icons.containsKey(this.curIconPos));

      this.icons.put(this.curIconPos, icon);
      return this;
   }

   public MenuBuilder<T> addIcon(int slot, Icon<? super T> icon) {
      this.icons.put(slot, icon);
      if (icon.isBlinkingIcon()) {
         this.blinkSlots.add(slot);
      }

      return this;
   }

   public MenuBuilder<T> addIcon(int row, int col, Icon<? super T> icon) {
      int slot = (row - 1) * 9 + (col - 1);
      this.icons.put(slot, icon);
      if (icon.isBlinkingIcon()) {
         this.blinkSlots.add(slot);
      }

      return this;
   }

   public MenuBuilder<T> addAllIcons(List<Icon<T>> iconList) {
      for (Icon<? super T> icon : iconList) {
         do {
            this.curIconPos++;
         } while (this.icons.containsKey(this.curIconPos));

         this.icons.put(this.curIconPos, icon);
         if (icon.isBlinkingIcon()) {
            this.blinkSlots.add(this.curIconPos);
         }
      }

      return this;
   }

   public MenuBuilder<T> addAllIcons(Map<Integer, Icon<T>> icons) {
      icons.entrySet().stream().forEach(integerIconEntry -> {
         this.icons.put(integerIconEntry.getKey(), integerIconEntry.getValue());
         if (integerIconEntry.getValue().isBlinkingIcon()) {
            this.blinkSlots.add(integerIconEntry.getKey());
         }
      });
      return this;
   }

   public MenuBuilder<T> createIcon(Function<IconBuilder<T>, IconBuilder<T>> builderFunc) {
      this.addIcon(builderFunc.apply(IconBuilder.create()).build());
      return this;
   }

   public MenuBuilder<T> createIcon(int slot, Function<IconBuilder<T>, IconBuilder<T>> builderFunc) {
      this.addIcon(slot, builderFunc.apply(IconBuilder.create()).build());
      return this;
   }

   public MenuBuilder<T> createIcon(int row, int col, Function<IconBuilder<T>, IconBuilder<T>> builderFunc) {
      int slot = (row - 1) * 9 + (col - 1);
      this.addIcon(slot, builderFunc.apply(IconBuilder.create()).build());
      return this;
   }

   public MenuBuilder<T> monitor(Function<T, MonitoredState> func) {
      this.monitorFunc = func;
      return this;
   }

   public MenuBuilder<T> staticMenu() {
      this.monitorFunc = _obj -> () -> 0L;
      return this;
   }

   public MenuBuilder<T> unprotectedSlots(List<Integer> freeSlots) {
      this.freeSlots = freeSlots;
      return this;
   }

   public MenuBuilder<T> defaultClick(BiFunction<MenuHelper.OpenMenu<T>, InventoryClickEvent, Boolean> freeSlotFunc) {
      this.freeSlotFunc = freeSlotFunc;
      return this;
   }

   public MenuBuilder<T> setFallbackDataIcon(Icon<? super T> icon) {
      this.fallbackDataIcon = icon;
      return this;
   }

   public MenuBuilder<T> setFallbackSecondaryDataIcon(Icon<? super T> icon) {
      this.secondaryFallbackDataIcon = icon;
      return this;
   }

   public MenuBuilder<T> dataSlot(int... i) {
      for (int ii : i) {
         this.dataSlots.add(ii);
      }

      return this;
   }

   public MenuBuilder<T> dataSlots(List<Integer> dataSlots) {
      this.dataSlots = dataSlots;
      return this;
   }

   public MenuBuilder<T> secondaryDataSlots(List<Integer> dataSlots) {
      this.secondaryDataSlots = dataSlots;
      return this;
   }

   public MenuBuilder<T> secondaryDataSlot(int... i) {
      for (int ii : i) {
         this.secondaryDataSlots.add(ii);
      }

      return this;
   }

   public MenuBuilder<T> sortByAmount(List<Integer> sortable) {
      this.sortSlots = sortable;
      return this;
   }

   public MenuBuilder<T> setStatic(boolean staticMenu) {
      this.staticMenu = staticMenu;
      return this;
   }

   public MenuBuilder<T> setUpdateInterval(int interval) {
      this.updateInterval = interval;
      return this;
   }

   public Menu<T> build() {
      if (this.titleFunc == null) {
         throw new IllegalStateException("Title must be specified!");
      } else {
         Menu<T> menu = new Menu<>(this.titleFunc, this.monitorFunc, this.icons);
         menu.setSize(this.size);
         menu.setBlinkSlots(this.blinkSlots);
         menu.setDataSlots(this.dataSlots);
         menu.setSecondaryDataSlots(this.secondaryDataSlots);
         menu.setFreeSlots(this.freeSlots);
         menu.setSortSlots(this.sortSlots);
         menu.setDefaultClickFunc(this.freeSlotFunc);
         menu.setCloseFunc(this.closeFunc);
         menu.setOpenFunc(this.openFunc);
         menu.setFallbackDataIcon(this.fallbackDataIcon);
         menu.setFallbackSecondaryDataIcon(this.secondaryFallbackDataIcon);
         menu.setStaticMenu(this.staticMenu);
         menu.setUpdateInterval(this.updateInterval);
         return menu;
      }
   }

   public Function<T, String> getTitleFunc() {
      return this.titleFunc;
   }
}

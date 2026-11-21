package io.lumine.mythic.bukkit.utils.menu.hotbar;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MonitoredState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HotbarMenuBuilder<T> {
   private final Map<Integer, Icon<? super T>> icons = new HashMap<>();
   private Function<T, MonitoredState> monitorFunc;
   private int curIconPos = -1;
   private List<Integer> blinkSlots = new ArrayList<>();
   private List<Integer> freeSlots = Lists.newArrayList();

   public static <T> HotbarMenuBuilder<T> create() {
      return new HotbarMenuBuilder<>();
   }

   private HotbarMenuBuilder() {
   }

   public HotbarMenuBuilder<T> addIcon(Icon<? super T> icon) {
      do {
         this.curIconPos++;
      } while (this.icons.containsKey(this.curIconPos));

      this.icons.put(this.curIconPos, icon);
      return this;
   }

   public HotbarMenuBuilder<T> addIcon(int slot, Icon<? super T> icon) {
      this.icons.put(slot, icon);
      if (icon.isBlinkingIcon()) {
         this.blinkSlots.add(slot);
      }

      return this;
   }

   public HotbarMenuBuilder<T> addAllIcons(List<Icon<T>> iconList) {
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

   public HotbarMenuBuilder<T> addAllIcons(Map<Integer, Icon<T>> icons) {
      icons.entrySet().stream().forEach(integerIconEntry -> {
         this.icons.put(integerIconEntry.getKey(), integerIconEntry.getValue());
         if (integerIconEntry.getValue().isBlinkingIcon()) {
            this.blinkSlots.add(integerIconEntry.getKey());
         }
      });
      return this;
   }

   public HotbarMenuBuilder<T> createIcon(Function<IconBuilder<T>, IconBuilder<T>> builderFunc) {
      this.addIcon(builderFunc.apply(IconBuilder.create()).build());
      return this;
   }

   public HotbarMenuBuilder<T> createIcon(int slot, Function<IconBuilder<T>, IconBuilder<T>> builderFunc) {
      this.addIcon(slot, builderFunc.apply(IconBuilder.create()).build());
      return this;
   }

   public HotbarMenuBuilder<T> unprotectedSlots(List<Integer> freeSlots) {
      this.freeSlots = freeSlots;
      return this;
   }

   public HotbarMenuBuilder<T> monitor(Function<T, MonitoredState> func) {
      this.monitorFunc = func;
      return this;
   }

   public HotbarMenuBuilder<T> staticMenu() {
      this.monitorFunc = _obj -> () -> 0L;
      return this;
   }

   public HotbarMenu<T> build() {
      HotbarMenu<T> menu = new HotbarMenu<>(this.monitorFunc, this.icons, this.blinkSlots);
      menu.setFreeSlots(this.freeSlots);
      return menu;
   }
}

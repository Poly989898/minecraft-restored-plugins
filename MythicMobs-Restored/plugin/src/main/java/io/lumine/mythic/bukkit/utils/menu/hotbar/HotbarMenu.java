package io.lumine.mythic.bukkit.utils.menu.hotbar;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MonitoredState;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HotbarMenu<T> {
   private final Function<T, MonitoredState> monitorFunc;
   private final Map<Integer, Icon<? super T>> icons;
   private List<Integer> blinkSlots = new ArrayList<>();
   private List<Integer> freeSlots = Lists.newArrayList();

   protected HotbarMenu(Function<T, MonitoredState> monitorFunc, Map<Integer, Icon<? super T>> icons, List<Integer> blinking) {
      this.monitorFunc = monitorFunc;
      this.icons = icons;
      if (blinking != null && blinking.size() > 0) {
         this.blinkSlots = blinking;
      }
   }

   public ActiveHotbarMenu open(Player player, T state) {
      if (this.icons.isEmpty()) {
         throw new IllegalStateException("Tried to open an empty menu!");
      } else {
         int max = this.icons.keySet().stream().sorted(Comparator.reverseOrder()).findFirst().get();
         int size = 9;
         if (size < 0) {
            size = (int)Math.ceil((max + 1) / 9.0) * 9;
         }

         player.getInventory().clear();

         for (Entry<Integer, Icon<? super T>> entry : this.icons.entrySet()) {
            int slot = entry.getKey();
            Icon<? super T> button = entry.getValue();
            ItemStack stack = button.build(state, player);
            player.getInventory().setItem(slot, stack);
         }

         return HotbarMenuHelper.registerOpen(player, this, state);
      }
   }

   protected Map<Integer, Icon<? super T>> getIcons() {
      return this.icons;
   }

   protected Optional<Icon<? super T>> getIcon(int slot) {
      return Optional.ofNullable(this.icons.get(slot));
   }

   protected Optional<List<Integer>> getBlinkingSlots() {
      return Optional.ofNullable(this.blinkSlots);
   }

   protected Optional<MonitoredState> getMonitoredState(T state) {
      return this.monitorFunc == null ? Optional.empty() : Optional.of(this.monitorFunc.apply(state));
   }

   public List<Integer> getFreeSlots() {
      return this.freeSlots;
   }

   public void setFreeSlots(List<Integer> freeSlots) {
      this.freeSlots = freeSlots;
   }
}

package io.lumine.mythic.bukkit.utils.menu;

import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConditionalIcon<T> implements Icon<T> {
   private final Icon<T> fallbackIcon;
   private final Map<BiPredicate<T, Player>, Icon<T>> conditionalIcons;

   public ConditionalIcon(Icon<T> fallbackIcon, Map<BiPredicate<T, Player>, Icon<T>> conditionalIcons) {
      this.fallbackIcon = fallbackIcon;
      this.conditionalIcons = conditionalIcons;
   }

   private Icon<T> getIcon(T state, Player player) {
      for (Entry<BiPredicate<T, Player>, Icon<T>> entry : this.conditionalIcons.entrySet()) {
         if (entry.getKey().test(state, player)) {
            return entry.getValue();
         }
      }

      return this.fallbackIcon;
   }

   @Override
   public ItemStack build(T state, Player player) {
      return this.getIcon(state, player).build(state, player);
   }

   @Override
   public ItemFactory getIconFactory(T state, Player player) {
      return this.getIcon(state, player).getIconFactory(state, player);
   }

   @Override
   public int getAmount(T state, Player player) {
      return this.getIcon(state, player).getAmount(state, player);
   }

   @Override
   public boolean isBlinkingIcon() {
      return false;
   }

   @Override
   public long getMonitorTimestamp() {
      return 0L;
   }

   @Override
   public Optional<BiConsumer<T, Player>> getClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getRightClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getRightClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getShiftClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getShiftClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getShiftRightClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getShiftRightClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getDoubleClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getDoubleClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getDropClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getDropClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getDropAllClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getDropAllClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getMiddleClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getMiddleClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }

   @Override
   public Optional<BiConsumer<T, Player>> getSwapOffhandClickFunc() {
      return Optional.of((state, player) -> this.getIcon(state, player).getSwapOffhandClickFunc().ifPresent(func -> func.accept((T)state, player)));
   }
}

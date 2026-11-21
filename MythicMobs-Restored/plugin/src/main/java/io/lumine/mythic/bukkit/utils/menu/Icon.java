package io.lumine.mythic.bukkit.utils.menu;

import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Icon<T> extends MonitoredState {
   ItemStack build(T t, Player player);

   ItemFactory getIconFactory(T t, Player player);

   int getAmount(T t, Player player);

   boolean isBlinkingIcon();

   @Override
   long getMonitorTimestamp();

   Optional<BiConsumer<T, Player>> getClickFunc();

   Optional<BiConsumer<T, Player>> getRightClickFunc();

   Optional<BiConsumer<T, Player>> getShiftClickFunc();

   Optional<BiConsumer<T, Player>> getShiftRightClickFunc();

   Optional<BiConsumer<T, Player>> getDoubleClickFunc();

   Optional<BiConsumer<T, Player>> getDropClickFunc();

   Optional<BiConsumer<T, Player>> getDropAllClickFunc();

   Optional<BiConsumer<T, Player>> getMiddleClickFunc();

   Optional<BiConsumer<T, Player>> getSwapOffhandClickFunc();
}

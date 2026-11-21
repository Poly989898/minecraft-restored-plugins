package io.lumine.mythic.bukkit.utils.menu;

import java.util.function.BiConsumer;
import org.bukkit.entity.Player;

public class ClickAction {
   public static <T> BiConsumer<T, Player> openMenu(Menu<T> menu) {
      return (impl, player) -> menu.open(player, impl);
   }
}

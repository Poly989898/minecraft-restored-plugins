package io.lumine.mythic.bukkit.utils.menu;

import java.util.function.BiConsumer;
import org.bukkit.entity.Player;

public class MenuAction {
   public static final BiConsumer NEXT_PAGE = (a, player) -> MenuHelper.moveToNextPage((Player)player);
   public static final BiConsumer PREV_PAGE = (a, player) -> MenuHelper.moveToPreviousPage((Player)player);
}

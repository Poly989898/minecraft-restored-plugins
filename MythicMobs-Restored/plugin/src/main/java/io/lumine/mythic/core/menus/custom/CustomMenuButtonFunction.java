package io.lumine.mythic.core.menus.custom;

import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import org.bukkit.entity.Player;

public interface CustomMenuButtonFunction {
   void applyButtonPresets(IconBuilder<CustomMenuContext> iconBuilder);

   void leftClick(CustomMenuContext customMenuContext, Player player);

   void rightClick(CustomMenuContext customMenuContext, Player player);
}

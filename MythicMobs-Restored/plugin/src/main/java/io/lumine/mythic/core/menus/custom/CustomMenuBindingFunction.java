package io.lumine.mythic.core.menus.custom;

import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.core.players.PlayerData;
import java.util.List;

public interface CustomMenuBindingFunction {
   List<MenuData<?>> getButtons(PlayerData playerData);
}

package io.lumine.mythic.core.menus.custom;

import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.types.MenuProp;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.core.menus.MenuManager;
import java.io.File;

public class CustomPlayerMenu<CustomMenuContext> extends CustomMenu {
   public CustomPlayerMenu(MythicBukkit plugin, MenuManager manager, Pack pack, File file, String key, MenuProp menu) {
      super(plugin, manager, pack, file, key, menu);
   }

   @Override
   public EditableMenuBuilder build(EditableMenuBuilder builder) {
      return super.build(builder);
   }
}

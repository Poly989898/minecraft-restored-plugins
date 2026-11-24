package net.Indyuce.mmoitems.api.edition;

import net.Indyuce.mmoitems.gui.PluginInventory;

public interface Edition {
   boolean processInput(String text);

   PluginInventory getInventory();

   void enable(String... texts);

   boolean shouldGoBack();
}

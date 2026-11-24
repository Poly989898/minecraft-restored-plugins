package net.Indyuce.mmoitems.api.edition;

import net.Indyuce.mmoitems.gui.PluginInventory;

public interface Edition {
   boolean processInput(String var1);

   PluginInventory getInventory();

   void enable(String... var1);

   boolean shouldGoBack();
}

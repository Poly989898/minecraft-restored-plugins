package net.Indyuce.mmoitems;

import net.Indyuce.mmoitems.comp.inventory.InventoryManager;

/** Compile-only stub: the parts of MMOItems used by MMOInventory. */
public class MMOItems {
   public static MMOItems plugin;
   public InventoryManager getInventory() { throw new UnsupportedOperationException(); }
   public Language getLanguage() { throw new UnsupportedOperationException(); }
   public static class Language { public boolean keepSoulboundOnDeath; }
}

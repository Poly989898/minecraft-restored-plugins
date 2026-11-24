package net.Indyuce.mmoitems;

import net.Indyuce.mmoitems.comp.inventory.PlayerInventoryHandler;

/** Compile-only stub: the parts of MMOItems used by MMOInventory. */
public class MMOItems {
   public static MMOItems plugin;
   public PlayerInventoryHandler getInventory() { throw new UnsupportedOperationException(); }
   public Language getLanguage() { throw new UnsupportedOperationException(); }
   public static class Language { public boolean keepSoulboundOnDeath; }
}

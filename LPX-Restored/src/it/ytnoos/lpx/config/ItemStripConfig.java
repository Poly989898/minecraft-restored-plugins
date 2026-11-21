package it.ytnoos.lpx.config;

import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.messages.MessagesConfig;
import java.util.HashSet;
import java.util.Set;

public class ItemStripConfig extends ConfigSection {
   private static final String SECTION = "item-strip";
   private final boolean enabled = this.getBoolean("enabled");
   private final boolean setSlot = this.getBoolean("set-slot");
   private final Set<String> removedComponents = new HashSet<>(this.getStringList("remove-components"));
   private final Set<String> removedNbt = new HashSet<>(this.getStringList("remove-nbt"));

   public ItemStripConfig(LPXPlugin lpxPlugin, String string, MessagesConfig messagesConfig) {
      super(lpxPlugin, string + ".item-strip", messagesConfig);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isSetSlot() {
      return this.setSlot;
   }

   public Set getRemovedComponents() {
      return this.removedComponents;
   }

   public Set getRemovedNbt() {
      return this.removedNbt;
   }
}

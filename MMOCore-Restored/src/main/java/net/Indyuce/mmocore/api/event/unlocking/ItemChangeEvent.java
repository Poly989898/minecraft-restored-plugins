package net.Indyuce.mmocore.api.event.unlocking;

import net.Indyuce.mmocore.api.event.PlayerDataEvent;
import net.Indyuce.mmocore.api.player.PlayerData;

public abstract class ItemChangeEvent extends PlayerDataEvent {
   private final String itemKey;

   public ItemChangeEvent(PlayerData playerData, String itemKey) {
      super(playerData);
      this.itemKey = itemKey;
   }

   public String getItemKey() {
      return this.itemKey;
   }

   public String getItemTypeId() {
      return this.itemKey.split(":")[0];
   }

   public String getItemId() {
      return this.itemKey.split(":")[1];
   }
}

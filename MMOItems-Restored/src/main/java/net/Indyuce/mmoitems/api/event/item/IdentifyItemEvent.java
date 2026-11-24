package net.Indyuce.mmoitems.api.event.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.HandlerList;

public class IdentifyItemEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final VolatileMMOItem consumable;
   private final NBTItem unidentified;

   public IdentifyItemEvent(PlayerData playerData, VolatileMMOItem volatileMMOItem, NBTItem nbtItem) {
      super(playerData);
      this.consumable = volatileMMOItem;
      this.unidentified = nbtItem;
   }

   public VolatileMMOItem getConsumable() {
      return this.consumable;
   }

   public NBTItem getUnidentifiedItem() {
      return this.unidentified;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

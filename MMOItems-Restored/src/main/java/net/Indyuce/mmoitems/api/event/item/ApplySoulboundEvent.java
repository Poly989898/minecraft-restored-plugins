package net.Indyuce.mmoitems.api.event.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.HandlerList;

public class ApplySoulboundEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final VolatileMMOItem consumable;
   private final NBTItem target;

   public ApplySoulboundEvent(PlayerData playerData, VolatileMMOItem volatileMMOItem, NBTItem nbtItem) {
      super(playerData);
      this.consumable = volatileMMOItem;
      this.target = nbtItem;
   }

   public VolatileMMOItem getConsumable() {
      return this.consumable;
   }

   public NBTItem getTargetItem() {
      return this.target;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

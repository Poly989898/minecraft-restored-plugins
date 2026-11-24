package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.HandlerList;

public class UnsocketGemStoneEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final VolatileMMOItem consumable;
   private final MMOItem targetItem;

   public UnsocketGemStoneEvent(PlayerData var1, VolatileMMOItem var2, MMOItem var3) {
      super(var1);
      this.consumable = var2;
      this.targetItem = var3;
   }

   public VolatileMMOItem getConsumable() {
      return this.consumable;
   }

   public MMOItem getTargetItem() {
      return this.targetItem;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

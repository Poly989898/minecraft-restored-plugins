package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import org.bukkit.event.HandlerList;

public class UpgradeItemEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final VolatileMMOItem consumable;
   private final MMOItem target;
   private final UpgradeData consumableData;
   private final UpgradeData targetData;

   public UpgradeItemEvent(PlayerData var1, VolatileMMOItem var2, MMOItem var3, UpgradeData var4, UpgradeData var5) {
      super(var1);
      this.consumable = var2;
      this.target = var3;
      this.consumableData = var4;
      this.targetData = var5;
   }

   public VolatileMMOItem getConsumable() {
      return this.consumable;
   }

   public MMOItem getTargetItem() {
      return this.target;
   }

   public UpgradeData getConsumableData() {
      return this.consumableData;
   }

   public UpgradeData getTargetData() {
      return this.targetData;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

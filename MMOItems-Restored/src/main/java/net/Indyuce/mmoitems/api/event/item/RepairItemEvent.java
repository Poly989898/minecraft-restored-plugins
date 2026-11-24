package net.Indyuce.mmoitems.api.event.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.HandlerList;

public class RepairItemEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final VolatileMMOItem consumable;
   private final NBTItem target;
   private int repaired;

   public RepairItemEvent(PlayerData playerData, VolatileMMOItem volatileMMOItem, NBTItem nbtItem, int count) {
      super(playerData);
      this.consumable = volatileMMOItem;
      this.target = nbtItem;
      this.repaired = count;
   }

   public VolatileMMOItem getConsumable() {
      return this.consumable;
   }

   public NBTItem getTargetItem() {
      return this.target;
   }

   public int getRepaired() {
      return this.repaired;
   }

   @Deprecated
   public double getRepairedPercent() {
      boolean hasTag = this.target.hasTag("MMOITEMS_DURABILITY");
      double value = hasTag ? this.target.getDouble("MMOITEMS_MAX_DURABILITY") : this.target.getItem().getType().getMaxDurability();
      return this.getRepaired() / value;
   }

   public void setRepaired(int count) {
      this.repaired = Math.max(0, count);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

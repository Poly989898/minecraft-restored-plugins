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

   public RepairItemEvent(PlayerData var1, VolatileMMOItem var2, NBTItem var3, int var4) {
      super(var1);
      this.consumable = var2;
      this.target = var3;
      this.repaired = var4;
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
      boolean var1 = this.target.hasTag("MMOITEMS_DURABILITY");
      double var2 = var1 ? this.target.getDouble("MMOITEMS_MAX_DURABILITY") : this.target.getItem().getType().getMaxDurability();
      return this.getRepaired() / var2;
   }

   public void setRepaired(int var1) {
      this.repaired = Math.max(0, var1);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

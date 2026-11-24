package net.Indyuce.mmoitems.api.event.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.List;
import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class DeconstructItemEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final VolatileMMOItem consumable;
   private final NBTItem deconstructed;
   private final List<ItemStack> loot;

   public DeconstructItemEvent(PlayerData playerData, VolatileMMOItem volatileMMOItem, NBTItem nbtItem, List<ItemStack> items) {
      super(playerData);
      this.consumable = volatileMMOItem;
      this.deconstructed = nbtItem;
      this.loot = items;
   }

   public VolatileMMOItem getConsumable() {
      return this.consumable;
   }

   public NBTItem getDeconstructedItem() {
      return this.deconstructed;
   }

   public List<ItemStack> getLoot() {
      return this.loot;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConsumableConsumedEvent extends PlayerDataEvent {
   @NotNull
   private final VolatileMMOItem mmoitem;
   @NotNull
   private final Consumable useItem;
   @Nullable
   private boolean consumed = true;
   private static final HandlerList handlers = new HandlerList();

   public ConsumableConsumedEvent(@NotNull PlayerData playerData, @NotNull VolatileMMOItem volatileMMOItem, @NotNull Consumable consumable) {
      super(playerData);
      this.mmoitem = volatileMMOItem;
      this.useItem = consumable;
   }

   @Deprecated
   public ConsumableConsumedEvent(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, @NotNull Consumable consumable) {
      super(PlayerData.get(player));
      this.mmoitem = volatileMMOItem;
      this.useItem = consumable;
   }

   @NotNull
   public VolatileMMOItem getMMOItem() {
      return this.mmoitem;
   }

   @NotNull
   public Consumable getUseItem() {
      return this.useItem;
   }

   @Deprecated
   public Boolean isConsume() {
      return this.consumed;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   /** @deprecated */
   public void setConsume(@Nullable Boolean flag) {
      this.consumed = flag;
   }

   public void setConsumed(boolean flag) {
      this.consumed = flag;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

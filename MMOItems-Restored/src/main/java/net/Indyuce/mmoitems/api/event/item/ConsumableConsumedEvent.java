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

   public ConsumableConsumedEvent(@NotNull PlayerData var1, @NotNull VolatileMMOItem var2, @NotNull Consumable var3) {
      super(var1);
      this.mmoitem = var2;
      this.useItem = var3;
   }

   @Deprecated
   public ConsumableConsumedEvent(@NotNull VolatileMMOItem var1, @NotNull Player var2, @NotNull Consumable var3) {
      super(PlayerData.get(var2));
      this.mmoitem = var1;
      this.useItem = var3;
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
   public void setConsume(@Nullable Boolean var1) {
      this.consumed = var1;
   }

   public void setConsumed(boolean var1) {
      this.consumed = var1;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

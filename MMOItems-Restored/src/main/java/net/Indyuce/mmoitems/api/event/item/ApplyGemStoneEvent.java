package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.event.PlayerDataEvent;
import net.Indyuce.mmoitems.api.interaction.GemStone;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;

public class ApplyGemStoneEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final VolatileMMOItem gemStone;
   private final MMOItem targetItem;
   private GemStone.ResultType result;

   public ApplyGemStoneEvent(PlayerData var1, VolatileMMOItem var2, MMOItem var3, GemStone.ResultType var4) {
      super(var1);
      this.gemStone = var2;
      this.targetItem = var3;
      this.result = var4;
   }

   public VolatileMMOItem getGemStone() {
      return this.gemStone;
   }

   public MMOItem getTargetItem() {
      return this.targetItem;
   }

   public GemStone.ResultType getResult() {
      return this.result;
   }

   public void setResult(GemStone.ResultType var1) {
      Validate.notNull(var1, "Result cannot be null");
      this.result = var1;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

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

   public ApplyGemStoneEvent(PlayerData playerData, VolatileMMOItem volatileMMOItem, MMOItem mmoItem, GemStone.ResultType resultType) {
      super(playerData);
      this.gemStone = volatileMMOItem;
      this.targetItem = mmoItem;
      this.result = resultType;
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

   public void setResult(GemStone.ResultType resultType) {
      Validate.notNull(resultType, "Result cannot be null");
      this.result = resultType;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

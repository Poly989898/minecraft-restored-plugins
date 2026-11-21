package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class ModelSetupEvent extends AbstractEvent {
   private static final HandlerList handlers = new HandlerList();
   private final ModeledEntity modeledEntity;
   private final ActiveModel activeModel;

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }
}

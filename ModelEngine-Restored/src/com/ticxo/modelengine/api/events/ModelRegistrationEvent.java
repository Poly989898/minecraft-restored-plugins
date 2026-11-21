package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.generator.ModelGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class ModelRegistrationEvent extends AbstractEvent {
   private static final HandlerList handlers = new HandlerList();
   private final ModelGenerator.Phase phase;

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }
}

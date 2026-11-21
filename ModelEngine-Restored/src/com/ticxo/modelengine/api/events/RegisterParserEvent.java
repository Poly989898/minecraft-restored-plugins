package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.generator.parser.ModelParser;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Setter
@RequiredArgsConstructor
public class RegisterParserEvent extends AbstractEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final List<ModelParser> parsers;
   @Getter
   private boolean cancelled;

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public void register(ModelParser parser) {
      this.parsers.add(parser);
   }
}

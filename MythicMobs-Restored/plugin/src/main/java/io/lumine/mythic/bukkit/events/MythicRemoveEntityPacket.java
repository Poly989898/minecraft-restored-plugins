package io.lumine.mythic.bukkit.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Getter
@Internal
public class MythicRemoveEntityPacket extends Event {
   private final int id;
   private static final HandlerList handlers = new HandlerList();

   public MythicRemoveEntityPacket(int id) {
      this.id = id;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

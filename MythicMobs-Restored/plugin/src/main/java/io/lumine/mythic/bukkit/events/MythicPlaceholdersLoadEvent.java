package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.skills.placeholders.PlaceholderManager;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MythicPlaceholdersLoadEvent extends Event {
   private final PlaceholderManager manager;
   private static final HandlerList handlers = new HandlerList();

   public MythicPlaceholdersLoadEvent(PlaceholderManager manager) {
      this.manager = manager;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

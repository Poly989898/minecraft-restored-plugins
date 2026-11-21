package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.core.skills.cinematic.CameraPath;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MythicCinematicStartEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private final String pathId;
   private final CameraPath path;
   private boolean cancelled = false;

   public MythicCinematicStartEvent(@NotNull Player player, @NotNull String pathId, @NotNull CameraPath path) {
      this.player = player;
      this.pathId = pathId;
      this.path = path;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.core.skills.cinematic.CameraPath;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class MythicCinematicEndEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private final CameraPath path;
   private final Location originalLocation;
   private final int frame;
   private final MythicCinematicEndEvent.Reason reason;

   public MythicCinematicEndEvent(
      @NotNull Player player, @NotNull CameraPath path, @Nullable Location originalLocation, int frame, @NotNull MythicCinematicEndEvent.Reason reason
   ) {
      this.player = player;
      this.path = path;
      this.originalLocation = originalLocation;
      this.frame = frame;
      this.reason = reason;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public static enum Reason {
      FINISHED,
      CANCELLED,
      QUIT,
      DEATH;
   }
}

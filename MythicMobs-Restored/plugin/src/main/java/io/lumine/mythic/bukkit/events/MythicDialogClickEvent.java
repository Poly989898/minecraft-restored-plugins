package io.lumine.mythic.bukkit.events;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MythicDialogClickEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private final String dialog;
   private final int button;
   private final Map<String, String> values;
   private boolean cancelled = false;

   public MythicDialogClickEvent(@NotNull Player player, @NotNull String dialog, int button, @NotNull Map<String, String> values) {
      this.player = player;
      this.dialog = dialog;
      this.button = button;
      this.values = values;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

package fr.phoenixdevt.profiles.event;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerIdDispatchEvent extends PlayerEvent {
   private final UUID initialId;
   private final UUID fakeId;
   private static final HandlerList HANDLER_LIST = new HandlerList();

   public PlayerIdDispatchEvent(@NotNull Player player, @NotNull UUID initialId, @Nullable UUID fakeId) {
      super(player);
      this.initialId = initialId;
      this.fakeId = fakeId;
   }

   @NotNull
   public UUID getInitialId() {
      return this.initialId;
   }

   @Nullable
   public UUID getFakeId() {
      return this.fakeId;
   }

   public HandlerList getHandlers() {
      return HANDLER_LIST;
   }

   public static HandlerList getHandlerList() {
      return HANDLER_LIST;
   }
}

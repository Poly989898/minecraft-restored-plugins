package net.Indyuce.mmocore.api.event.social;

import net.Indyuce.mmocore.api.event.PlayerDataEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.guild.provided.Guild;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GuildChatEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Guild guild;
   private boolean cancelled;
   private String message;

   public GuildChatEvent(PlayerData playerData, String message) {
      super(playerData);
      this.guild = playerData.getGuild();
      this.message = message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }

   public Guild getGuild() {
      return this.guild;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

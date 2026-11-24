package net.Indyuce.mmocore.api.event.social;

import net.Indyuce.mmocore.api.event.PlayerDataEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PartyChatEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Party party;
   private boolean cancelled;
   private String message;

   public PartyChatEvent(Party party, PlayerData playerData, String message) {
      super(playerData);
      this.party = party;
      this.message = message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }

   public Party getParty() {
      return this.party;
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

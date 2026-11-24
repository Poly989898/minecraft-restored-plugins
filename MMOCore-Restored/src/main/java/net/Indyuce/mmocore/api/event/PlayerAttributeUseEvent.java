package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.bukkit.event.HandlerList;

public class PlayerAttributeUseEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final PlayerAttribute attribute;

   public PlayerAttributeUseEvent(PlayerData playerData, PlayerAttribute attribute) {
      super(playerData);
      this.attribute = attribute;
   }

   public PlayerAttribute getAttribute() {
      return this.attribute;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

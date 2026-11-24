package io.lumine.mythic.lib.api.event.skill;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerCastSkillEvent extends PlayerSkillEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancelled;

   public PlayerCastSkillEvent(SkillMetadata skillMeta, SkillResult result) {
      super(skillMeta, result);
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean value) {
      this.cancelled = value;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

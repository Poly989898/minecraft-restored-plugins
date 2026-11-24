package io.lumine.mythic.lib.api.event.skill;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import org.bukkit.event.HandlerList;

public class SkillCastEvent extends PlayerSkillEvent {
   private static final HandlerList handlers = new HandlerList();

   public SkillCastEvent(SkillMetadata skillMeta, SkillResult result) {
      super(skillMeta, result);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

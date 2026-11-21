package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.skills.SkillMetadata;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class MythicTargetEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final SkillMetadata skillMetadata;

   public MythicTargetEvent(SkillMetadata skillMetadata) {
      super(!Bukkit.isPrimaryThread());
      this.skillMetadata = skillMetadata;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

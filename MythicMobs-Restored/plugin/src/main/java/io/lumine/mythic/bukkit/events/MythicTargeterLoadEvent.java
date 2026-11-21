package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.core.skills.targeters.CustomTargeter;
import java.util.Optional;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicTargeterLoadEvent extends Event {
   private final CustomTargeter holder;
   private String name;
   private MythicLineConfig config;
   private ISkillTargeter targeter = null;
   private static final HandlerList handlers = new HandlerList();

   public MythicTargeterLoadEvent(CustomTargeter holder, String name, MythicLineConfig mlc) {
      this.holder = holder;
      this.name = name;
      this.config = mlc;
   }

   public CustomTargeter getContainer() {
      return this.holder;
   }

   public String getTargeterName() {
      return this.name;
   }

   public MythicLineConfig getConfig() {
      return this.config;
   }

   public void register(ISkillTargeter targeter) {
      this.targeter = targeter;
   }

   public Optional<ISkillTargeter> getTargeter() {
      return Optional.ofNullable(this.targeter);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

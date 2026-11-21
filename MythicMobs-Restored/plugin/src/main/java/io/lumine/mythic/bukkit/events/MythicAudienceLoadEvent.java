package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.audience.CustomAudience;
import java.util.Optional;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicAudienceLoadEvent extends Event {
   private final CustomAudience holder;
   private String name;
   private MythicLineConfig config;
   private SkillAudience audience = null;
   private static final HandlerList handlers = new HandlerList();

   public MythicAudienceLoadEvent(CustomAudience holder, String name, MythicLineConfig mlc) {
      this.holder = holder;
      this.name = name;
      this.config = mlc;
   }

   public CustomAudience getContainer() {
      return this.holder;
   }

   public String getAudienceName() {
      return this.name;
   }

   public MythicLineConfig getConfig() {
      return this.config;
   }

   public void register(SkillAudience audience) {
      this.audience = audience;
   }

   public Optional<SkillAudience> getAudience() {
      return Optional.ofNullable(this.audience);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

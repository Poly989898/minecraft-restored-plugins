package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ISkillMechanic;
import io.lumine.mythic.core.skills.mechanics.CustomMechanic;
import java.util.Optional;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicMechanicLoadEvent extends Event {
   private final CustomMechanic holder;
   private String name;
   private MythicLineConfig config;
   private ISkillMechanic mechanic = null;
   private static final HandlerList handlers = new HandlerList();

   public MythicMechanicLoadEvent(CustomMechanic holder, String name, MythicLineConfig mlc) {
      this.holder = holder;
      this.name = name;
      this.config = mlc;
   }

   public CustomMechanic getContainer() {
      return this.holder;
   }

   @Deprecated
   public CustomMechanic getHolder() {
      return this.holder;
   }

   public String getMechanicName() {
      return this.name;
   }

   public MythicLineConfig getConfig() {
      return this.config;
   }

   public void register(ISkillMechanic mechanic) {
      this.mechanic = mechanic;
   }

   public Optional<ISkillMechanic> getMechanic() {
      return Optional.ofNullable(this.mechanic);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

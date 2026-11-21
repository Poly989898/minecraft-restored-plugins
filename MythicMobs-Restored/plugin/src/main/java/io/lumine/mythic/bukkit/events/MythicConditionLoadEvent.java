package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.core.skills.conditions.CustomCondition;
import java.util.Optional;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicConditionLoadEvent extends Event {
   private final CustomCondition holder;
   private String name;
   private String conditionVar;
   private MythicLineConfig config;
   private ISkillCondition condition = null;
   private static final HandlerList handlers = new HandlerList();

   public MythicConditionLoadEvent(CustomCondition holder, String name, String conditionVar, MythicLineConfig mlc) {
      this.holder = holder;
      this.name = name;
      this.conditionVar = conditionVar;
      this.config = mlc;
   }

   public CustomCondition getContainer() {
      return this.holder;
   }

   public String getConditionName() {
      return this.name;
   }

   public MythicLineConfig getConfig() {
      return this.config;
   }

   public String getArgument() {
      return this.conditionVar;
   }

   public void register(ISkillCondition condition) {
      this.condition = condition;
   }

   public Optional<ISkillCondition> getCondition() {
      return Optional.ofNullable(this.condition);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}

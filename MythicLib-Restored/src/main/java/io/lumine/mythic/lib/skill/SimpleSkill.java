package io.lumine.mythic.lib.skill;

import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class SimpleSkill extends Skill {
   private final SkillHandler<?> handler;
   private final Map<String, Double> modifiers = new HashMap<>();

   public SimpleSkill(SkillHandler<?> handler) {
      this(TriggerType.API, handler);
   }

   public SimpleSkill(TriggerType trigger, SkillHandler<?> handler) {
      super(trigger);
      this.handler = handler;
   }

   @NotNull
   @Override
   public boolean getResult(SkillMetadata skillMeta) {
      return true;
   }

   @Override
   public void whenCast(SkillMetadata skillMeta) {
   }

   @Override
   public double getParameter(String path) {
      return this.modifiers.getOrDefault(path, 0.0);
   }

   @Override
   public SkillHandler<?> getHandler() {
      return this.handler;
   }

   public void registerModifier(String path, double value) {
      this.modifiers.put(path, value);
   }
}

package io.lumine.mythic.api.skills;

import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EmptyMetadata;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.Validate;

public record SkillTrigger<T extends SkillTriggerMetadata>(String name, Class<T> metadataClass, List<String> aliases) {
   private static final Map<String, SkillTrigger> TRIGGERS = new ConcurrentHashMap<>();

   public SkillTrigger(String name, Class<T> metadataClass, List<String> aliases) {
      this.name = name.toUpperCase();
      this.metadataClass = metadataClass;
      this.aliases = aliases.stream().map(String::toUpperCase).toList();
   }

   @Deprecated
   public static SkillTrigger trigger(String name) {
      String thatName = name.toUpperCase();
      SkillTrigger existing = TRIGGERS.get(thatName);
      return existing != null ? existing : create(thatName);
   }

   public static SkillTrigger create(String name, String... aliases) {
      return new SkillTrigger<>(name, EmptyMetadata.class, Arrays.asList(aliases));
   }

   public static SkillTrigger create(String name, Class<? extends SkillTriggerMetadata> clazz, String... aliases) {
      return new SkillTrigger<>(name, clazz, Arrays.asList(aliases));
   }

   public static SkillTrigger get(String name) {
      String thatName = name.toUpperCase();
      SkillTrigger result = TRIGGERS.get(thatName);
      return result != null ? result : TRIGGERS.get("DEFAULT");
   }

   public static void register(SkillTrigger trigger) {
      Validate.notNull(trigger, "trigger cannot be null", new Object[0]);
      String name = trigger.name();
      List<String> aliases = trigger.aliases;
      TRIGGERS.putIfAbsent(name, trigger);
      aliases.forEach(alias -> TRIGGERS.putIfAbsent(alias, trigger));
   }

   public static Collection<SkillTrigger> values() {
      return TRIGGERS.values();
   }

   public void register() {
      register(this);
   }
}

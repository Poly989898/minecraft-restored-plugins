package io.lumine.mythic.lib.skill.handler;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SkillHandler<T extends SkillResult> {
   private final String id;
   private final Set<String> parameters = new HashSet<>();
   private final boolean triggerable;
   protected static final Random RANDOM = new Random();
   @Deprecated
   protected static final Random random = RANDOM;

   public SkillHandler() {
      this(true);
   }

   public SkillHandler(boolean triggerable) {
      this.id = UtilityMethods.enumName(this.getClass().getSimpleName());
      this.triggerable = triggerable;
      this.registerModifiers("cooldown", "mana", "stamina", "timer", "delay");
   }

   public SkillHandler(@NotNull String id) {
      this(null, id);
   }

   public SkillHandler(@Nullable ConfigurationSection config, @NotNull String id) {
      this.id = UtilityMethods.enumName(id);
      this.triggerable = true;
      if (config != null && config.contains("modifiers")) {
         this.registerModifiers(config.getStringList("modifiers"));
      }

      this.registerModifiers("cooldown", "mana", "stamina", "timer", "delay");
   }

   public String getId() {
      return this.id;
   }

   public String getLowerCaseId() {
      return this.id.toLowerCase().replace("_", "-");
   }

   public void registerModifiers(String... mods) {
      this.registerModifiers(Arrays.asList(mods));
   }

   public void registerModifiers(Collection<String> mods) {
      this.parameters.addAll(mods);
   }

   public boolean isTriggerable() {
      return this.triggerable;
   }

   @Deprecated
   public Set<String> getModifiers() {
      return this.getParameters();
   }

   public Set<String> getParameters() {
      return this.parameters;
   }

   @NotNull
   public abstract T getResult(SkillMetadata skillMetadata);

   public abstract void whenCast(T value, SkillMetadata skillMetadata);

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SkillHandler<?> that = (SkillHandler<?>)o;
         return this.id.equals(that.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}

package net.Indyuce.mmoitems.skill;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegisteredSkill {
   @NotNull
   private final SkillHandler<?> handler;
   @NotNull
   private final String name;
   @NotNull
   private final Map<String, String> parameterNames = new HashMap<>();
   @NotNull
   private final Map<String, Double> defaultParameterValues = new HashMap<>();

   public RegisteredSkill(@NotNull SkillHandler<?> skillHandler, @NotNull ConfigurationSection section) {
      this.handler = skillHandler;
      this.name = Objects.requireNonNull(section.getString("name"), "Could not fill skill name");

      for (String modifier : skillHandler.getModifiers()) {
         this.parameterNames
            .put(modifier, section.getString("modifier." + modifier + ".name", UtilityMethods.caseOnWords(modifier.replace("_", " ").replace("-", " ").toLowerCase())));
         this.defaultParameterValues.put(modifier, section.getDouble("modifier." + modifier + ".default-value"));
      }
   }

   @Deprecated
   public RegisteredSkill(@NotNull SkillHandler skillHandler, @NotNull String text) {
      this.handler = skillHandler;
      this.name = text;
   }

   @NotNull
   public SkillHandler<?> getHandler() {
      return this.handler;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @Deprecated
   public void setDefaultValue(String text, double value) {
      this.defaultParameterValues.put(text, value);
   }

   @Deprecated
   public void setName(String text, String name) {
      this.parameterNames.put(text, name);
   }

   @Nullable
   public String getParameterName(String text) {
      return this.parameterNames.get(text);
   }

   public double getDefaultModifier(String text) {
      return this.defaultParameterValues.get(text);
   }
}

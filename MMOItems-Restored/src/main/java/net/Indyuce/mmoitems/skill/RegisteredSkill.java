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

   public RegisteredSkill(@NotNull SkillHandler<?> var1, @NotNull ConfigurationSection var2) {
      this.handler = var1;
      this.name = Objects.requireNonNull(var2.getString("name"), "Could not fill skill name");

      for (String var4 : var1.getModifiers()) {
         this.parameterNames
            .put(var4, var2.getString("modifier." + var4 + ".name", UtilityMethods.caseOnWords(var4.replace("_", " ").replace("-", " ").toLowerCase())));
         this.defaultParameterValues.put(var4, var2.getDouble("modifier." + var4 + ".default-value"));
      }
   }

   @Deprecated
   public RegisteredSkill(@NotNull SkillHandler var1, @NotNull String var2) {
      this.handler = var1;
      this.name = var2;
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
   public void setDefaultValue(String var1, double var2) {
      this.defaultParameterValues.put(var1, var2);
   }

   @Deprecated
   public void setName(String var1, String var2) {
      this.parameterNames.put(var1, var2);
   }

   @Nullable
   public String getParameterName(String var1) {
      return this.parameterNames.get(var1);
   }

   public double getDefaultModifier(String var1) {
      return this.defaultParameterValues.get(var1);
   }
}

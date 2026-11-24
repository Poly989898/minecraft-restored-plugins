package net.Indyuce.mmocore.skill;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.formula.BooleanExpression;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmocore.api.util.math.formula.IntegerLinearValue;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import net.Indyuce.mmocore.util.Icon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegisteredSkill {
   private final SkillHandler<?> handler;
   private final String name;
   private final Map<String, LinearValue> defaultParameters = new HashMap<>();
   private final Map<String, DecimalFormat> parameterDecimalFormats = new HashMap<>();
   private final Icon icon;
   private final List<String> lore;
   private final List<String> categories;
   private final TriggerType triggerType;

   public RegisteredSkill(SkillHandler<?> handler, ConfigurationSection config) {
      this.handler = handler;
      this.name = Objects.requireNonNull(config.getString("name"), "Could not find skill name");
      this.icon = Icon.from(config.get("material"));
      this.lore = Objects.requireNonNull(config.getStringList("lore"), "Could not find skill lore");
      this.triggerType = this.getHandler().isTriggerable()
         ? (config.contains("passive-type") ? TriggerType.valueOf(UtilityMethods.enumName(config.getString("passive-type"))) : TriggerType.CAST)
         : TriggerType.API;
      this.categories = config.getStringList("categories");
      this.categories.add(this.getHandler().getId());
      if (this.triggerType.isPassive()) {
         this.categories.add("PASSIVE");
      } else {
         this.categories.add("ACTIVE");
      }

      for (String param : handler.getParameters()) {
         if (config.contains(param + ".decimal-format")) {
            this.parameterDecimalFormats.put(param, new DecimalFormat(config.getString(param + ".decimal-format")));
         }

         this.defaultParameters.put(param, config.contains(param) ? new LinearValue(config.getConfigurationSection(param)) : LinearValue.ZERO);
      }

      this.defaultParameters.put("level", new IntegerLinearValue(0.0, 1.0));
   }

   public RegisteredSkill(SkillHandler<?> handler, String name, ItemStack icon, List<String> lore, @Nullable TriggerType triggerType) {
      this.handler = handler;
      this.name = name;
      this.icon = Icon.fromItem(icon);
      this.lore = lore;
      this.triggerType = triggerType;
      this.categories = new ArrayList<>();
   }

   public SkillHandler<?> getHandler() {
      return this.handler;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public List<String> getCategories() {
      return this.categories;
   }

   @Deprecated
   public ItemStack getIcon() {
      return this.icon.toItem();
   }

   @NotNull
   public Icon getRawIcon() {
      return this.icon;
   }

   public boolean hasParameter(String parameter) {
      return this.defaultParameters.containsKey(parameter);
   }

   @Deprecated
   public boolean hasModifier(String modifier) {
      return this.defaultParameters.containsKey(modifier);
   }

   @NotNull
   public TriggerType getTrigger() {
      return Objects.requireNonNull(this.triggerType, "Skill has no trigger");
   }

   @Deprecated
   public void addModifier(String modifier, LinearValue linear) {
      this.defaultParameters.put(modifier, linear);
   }

   public void addParameter(String parameter, LinearValue linear) {
      this.defaultParameters.put(parameter, linear);
   }

   public DecimalFormat getDecimalFormat(String parameter) {
      return this.parameterDecimalFormats.getOrDefault(parameter, MythicLib.plugin.getMMOConfig().decimal);
   }

   @Deprecated
   public void addModifierIfNone(String mod, LinearValue defaultValue) {
      if (!this.hasParameter(mod)) {
         this.addParameter(mod, defaultValue);
      }
   }

   @Deprecated
   public LinearValue getModifierInfo(String modifier) {
      return this.defaultParameters.get(modifier);
   }

   @NotNull
   public LinearValue getParameterInfo(String parameter) {
      return this.defaultParameters.get(parameter);
   }

   public double getModifier(String modifier, int level) {
      return this.defaultParameters.get(modifier).calculate(level);
   }

   public boolean matchesFormula(String formula) {
      String parsedExpression = formula;

      for (String category : this.categories) {
         parsedExpression = parsedExpression.replace("<" + category + ">", "true");
      }

      parsedExpression = parsedExpression.replaceAll("<.*?>", "false");
      return BooleanExpression.eval(parsedExpression);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RegisteredSkill that = (RegisteredSkill)o;
         return this.handler.equals(that.handler) && this.triggerType.equals(that.triggerType);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.handler, this.triggerType);
   }
}

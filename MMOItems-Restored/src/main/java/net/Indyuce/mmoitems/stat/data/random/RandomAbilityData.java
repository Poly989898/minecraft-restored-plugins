package net.Indyuce.mmoitems.stat.data.random;

import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class RandomAbilityData {
   private final RegisteredSkill ability;
   private final TriggerType triggerType;
   private final Map<String, NumericStatFormula> modifiers = new HashMap<>();

   public RandomAbilityData(ConfigurationSection var1) {
      Validate.isTrue(var1.contains("type"), "Missing ability type");
      Validate.isTrue(var1.contains("mode"), "Missing ability trigger type/casting mode");
      String var2 = var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getSkills().hasSkill(var2), "Could not find ability called '" + var2 + "'");
      this.ability = MMOItems.plugin.getSkills().getSkill(var2);
      String var3 = var1.getString("mode").toUpperCase().replace("-", "_").replace(" ", "_");
      this.triggerType = MMOUtils.backwardsCompatibleTriggerType(var3);

      for (String var5 : var1.getKeys(false)) {
         if (!var5.equalsIgnoreCase("mode") && !var5.equalsIgnoreCase("type") && this.ability.getHandler().getModifiers().contains(var5)) {
            this.modifiers.put(var5, new NumericStatFormula(var1.get(var5)));
         }
      }
   }

   public RandomAbilityData(RegisteredSkill var1, TriggerType var2) {
      this.ability = var1;
      this.triggerType = var2;
   }

   public RegisteredSkill getAbility() {
      return this.ability;
   }

   public TriggerType getTriggerType() {
      return this.triggerType;
   }

   public Set<String> getModifiers() {
      return this.modifiers.keySet();
   }

   public void setModifier(String var1, NumericStatFormula var2) {
      this.modifiers.put(var1, var2);
   }

   public boolean hasModifier(String var1) {
      return this.modifiers.containsKey(var1);
   }

   public NumericStatFormula getModifier(String var1) {
      return this.modifiers.get(var1);
   }

   public AbilityData randomize(MMOItemBuilder var1) {
      AbilityData var2 = new AbilityData(this.ability, this.triggerType);
      this.modifiers.forEach((var2x, var3) -> var2.setModifier(var2x, var3.calculate(var1.getLevel())));
      return var2;
   }
}

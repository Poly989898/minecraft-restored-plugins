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

   public RandomAbilityData(ConfigurationSection section) {
      Validate.isTrue(section.contains("type"), "Missing ability type");
      Validate.isTrue(section.contains("mode"), "Missing ability trigger type/casting mode");
      String text = section.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getSkills().hasSkill(text), "Could not find ability called '" + text + "'");
      this.ability = MMOItems.plugin.getSkills().getSkill(text);
      String modeName = section.getString("mode").toUpperCase().replace("-", "_").replace(" ", "_");
      this.triggerType = MMOUtils.backwardsCompatibleTriggerType(modeName);

      for (String key : section.getKeys(false)) {
         if (!key.equalsIgnoreCase("mode") && !key.equalsIgnoreCase("type") && this.ability.getHandler().getModifiers().contains(key)) {
            this.modifiers.put(key, new NumericStatFormula(section.get(key)));
         }
      }
   }

   public RandomAbilityData(RegisteredSkill registeredSkill, TriggerType triggerType) {
      this.ability = registeredSkill;
      this.triggerType = triggerType;
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

   public void setModifier(String text, NumericStatFormula numericStatFormula) {
      this.modifiers.put(text, numericStatFormula);
   }

   public boolean hasModifier(String text) {
      return this.modifiers.containsKey(text);
   }

   public NumericStatFormula getModifier(String text) {
      return this.modifiers.get(text);
   }

   public AbilityData randomize(MMOItemBuilder mmoItemBuilder) {
      AbilityData abilityData = new AbilityData(this.ability, this.triggerType);
      this.modifiers.forEach((modifier, numericStatFormula) -> abilityData.setModifier(modifier, numericStatFormula.calculate(mmoItemBuilder.getLevel())));
      return abilityData;
   }
}

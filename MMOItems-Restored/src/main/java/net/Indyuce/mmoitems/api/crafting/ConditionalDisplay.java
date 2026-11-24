package net.Indyuce.mmoitems.api.crafting;

import org.bukkit.configuration.ConfigurationSection;

public class ConditionalDisplay {
   private final String positive;
   private final String negative;

   public ConditionalDisplay(String text, String negativeText) {
      this.positive = text;
      this.negative = negativeText;
   }

   public ConditionalDisplay(ConfigurationSection section) {
      this(section.getString("positive"), section.getString("negative"));
   }

   public String format(boolean flag) {
      return flag ? this.positive : this.negative;
   }

   public void setup(ConfigurationSection section) {
      section.set("positive", this.positive);
      section.set("negative", this.negative);
   }
}

package net.Indyuce.mmoitems.api.crafting;

import org.bukkit.configuration.ConfigurationSection;

public class ConditionalDisplay {
   private final String positive;
   private final String negative;

   public ConditionalDisplay(String var1, String var2) {
      this.positive = var1;
      this.negative = var2;
   }

   public ConditionalDisplay(ConfigurationSection var1) {
      this(var1.getString("positive"), var1.getString("negative"));
   }

   public String format(boolean var1) {
      return var1 ? this.positive : this.negative;
   }

   public void setup(ConfigurationSection var1) {
      var1.set("positive", this.positive);
      var1.set("negative", this.negative);
   }
}

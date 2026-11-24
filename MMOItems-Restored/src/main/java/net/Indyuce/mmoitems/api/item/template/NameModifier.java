package net.Indyuce.mmoitems.api.item.template;

import io.lumine.mythic.lib.UtilityMethods;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class NameModifier {
   private final NameModifier.ModifierType type;
   private final String format;
   private final int priority;

   public NameModifier(NameModifier.ModifierType var1, Object var2) {
      Validate.notNull(var2, "Object cannot be null");
      this.type = var1;
      if (var2 instanceof String) {
         this.format = (String)var2;
         this.priority = 0;
      } else if (var2 instanceof ConfigurationSection) {
         ConfigurationSection var3 = (ConfigurationSection)var2;
         Validate.isTrue(var3.contains("format"), UtilityMethods.caseOnWords(var1.name().toLowerCase()) + " format cannot be null");
         this.format = var3.get("format").toString();
         this.priority = var3.getInt("priority");
      } else {
         throw new IllegalArgumentException("Must specify a string or a config section");
      }
   }

   public NameModifier(NameModifier.ModifierType var1, String var2, int var3) {
      Validate.notNull(var2, "Format cannot be null");
      this.type = var1;
      this.format = var2;
      this.priority = var3;
      Validate.notNull(var1, "Type cannot be null");
   }

   public String getFormat() {
      return this.format;
   }

   public NameModifier.ModifierType getType() {
      return this.type;
   }

   public boolean hasPriority() {
      return this.priority > 0;
   }

   public int getPriority() {
      return this.priority;
   }

   public enum ModifierType {
      PREFIX,
      SUFFIX;
   }
}

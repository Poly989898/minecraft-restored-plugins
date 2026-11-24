package net.Indyuce.mmoitems.api.item.template;

import io.lumine.mythic.lib.UtilityMethods;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class NameModifier {
   private final NameModifier.ModifierType type;
   private final String format;
   private final int priority;

   public NameModifier(NameModifier.ModifierType modifierType, Object obj) {
      Validate.notNull(obj, "Object cannot be null");
      this.type = modifierType;
      if (obj instanceof String) {
         this.format = (String)obj;
         this.priority = 0;
      } else if (obj instanceof ConfigurationSection) {
         ConfigurationSection section = (ConfigurationSection)obj;
         Validate.isTrue(section.contains("format"), UtilityMethods.caseOnWords(modifierType.name().toLowerCase()) + " format cannot be null");
         this.format = section.get("format").toString();
         this.priority = section.getInt("priority");
      } else {
         throw new IllegalArgumentException("Must specify a string or a config section");
      }
   }

   public NameModifier(NameModifier.ModifierType modifierType, String text, int count) {
      Validate.notNull(text, "Format cannot be null");
      this.type = modifierType;
      this.format = text;
      this.priority = count;
      Validate.notNull(modifierType, "Type cannot be null");
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

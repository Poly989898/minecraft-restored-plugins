package net.Indyuce.mmoitems.util;

import io.lumine.mythic.lib.element.Element;

public enum ElementStatType {
   DAMAGE("Flat Damage"),
   DAMAGE_PERCENT("Extra Damage (%)"),
   WEAKNESS("Weakness (%)"),
   DEFENSE("Defense"),
   DEFENSE_PERCENT("Extra Defense (%)");

   private final String name;

   ElementStatType(String nullxx) {
      this.name = nullxx;
   }

   public String getName() {
      return this.name;
   }

   public String lowerCaseName() {
      return this.name().toLowerCase().replace("_", "-");
   }

   public String getConcatenatedTagPath(Element element) {
      return element.getId() + "_" + this.name();
   }

   public String getConcatenatedConfigPath(Element element) {
      return element.getId().toLowerCase().replace("_", "-") + "." + this.name().toLowerCase().replace("_", "-");
   }
}

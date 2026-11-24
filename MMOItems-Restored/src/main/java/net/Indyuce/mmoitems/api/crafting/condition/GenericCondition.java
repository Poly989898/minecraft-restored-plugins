package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;

public abstract class GenericCondition extends Condition {
   public final String display;

   public GenericCondition(String text, MMOLineConfig mmoLineConfig) {
      super(text);
      this.display = mmoLineConfig.getString("display", "<No display chosen>");
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#display#", this.display);
   }
}

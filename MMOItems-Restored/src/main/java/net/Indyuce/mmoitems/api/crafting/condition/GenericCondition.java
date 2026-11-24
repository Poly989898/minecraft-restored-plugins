package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;

public abstract class GenericCondition extends Condition {
   public final String display;

   public GenericCondition(String var1, MMOLineConfig var2) {
      super(var1);
      this.display = var2.getString("display", "<No display chosen>");
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#display#", this.display);
   }
}

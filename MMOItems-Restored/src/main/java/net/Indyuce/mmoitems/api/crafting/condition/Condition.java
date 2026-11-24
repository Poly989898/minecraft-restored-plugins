package net.Indyuce.mmoitems.api.crafting.condition;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.player.PlayerData;

public abstract class Condition {
   private final String id;

   public Condition(String var1) {
      this.id = var1;
   }

   public String getId() {
      return this.id;
   }

   public ConditionalDisplay getDisplay() {
      return MMOItems.plugin.getCrafting().getConditionInfo(this.id).getDisplay();
   }

   public abstract boolean isMet(PlayerData var1);

   public abstract String formatDisplay(String var1);

   public abstract void whenCrafting(PlayerData var1);

   public CheckedCondition evaluateCondition(PlayerData var1) {
      return new CheckedCondition(this, this.isMet(var1));
   }
}

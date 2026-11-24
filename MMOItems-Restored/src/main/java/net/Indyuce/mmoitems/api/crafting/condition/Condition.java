package net.Indyuce.mmoitems.api.crafting.condition;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.player.PlayerData;

public abstract class Condition {
   private final String id;

   public Condition(String text) {
      this.id = text;
   }

   public String getId() {
      return this.id;
   }

   public ConditionalDisplay getDisplay() {
      return MMOItems.plugin.getCrafting().getConditionInfo(this.id).getDisplay();
   }

   public abstract boolean isMet(PlayerData playerData);

   public abstract String formatDisplay(String text);

   public abstract void whenCrafting(PlayerData playerData);

   public CheckedCondition evaluateCondition(PlayerData playerData) {
      return new CheckedCondition(this, this.isMet(playerData));
   }
}

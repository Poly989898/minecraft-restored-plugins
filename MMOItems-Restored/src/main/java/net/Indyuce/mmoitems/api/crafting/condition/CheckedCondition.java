package net.Indyuce.mmoitems.api.crafting.condition;

public class CheckedCondition {
   private final Condition condition;
   private final boolean met;

   public CheckedCondition(Condition var1, boolean var2) {
      this.condition = var1;
      this.met = var2;
   }

   public boolean isMet() {
      return this.met;
   }

   public Condition getCondition() {
      return this.condition;
   }

   public String format() {
      return this.condition.formatDisplay(this.condition.getDisplay().format(this.met));
   }
}

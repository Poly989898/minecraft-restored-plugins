package net.Indyuce.mmoitems.api.crafting.condition;

public class CheckedCondition {
   private final Condition condition;
   private final boolean met;

   public CheckedCondition(Condition condition, boolean flag) {
      this.condition = condition;
      this.met = flag;
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

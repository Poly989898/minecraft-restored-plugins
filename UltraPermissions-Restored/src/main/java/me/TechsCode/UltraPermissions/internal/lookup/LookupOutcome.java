package me.TechsCode.UltraPermissions.internal.lookup;

public class LookupOutcome {
   private final boolean result;
   private final String source;

   public LookupOutcome(boolean result, String source) {
      this.result = result;
      this.source = source;
   }

   public boolean getResult() {
      return this.result;
   }

   public boolean hasSource() {
      return this.source != null;
   }

   public String getSource() {
      return this.source;
   }
}

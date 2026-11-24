package me.TechsCode.UltraPermissions.internal.lookup.checks;

import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.LookupOutcome;

public class BlankCheck implements LookupCheck {
   public BlankCheck() {
   }

   @Override
   public LookupOutcome perform(String text) {
      return null;
   }
}

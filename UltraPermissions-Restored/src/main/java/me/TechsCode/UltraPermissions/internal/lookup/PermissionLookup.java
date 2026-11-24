package me.TechsCode.UltraPermissions.internal.lookup;

import java.util.Arrays;
import java.util.Objects;
import me.TechsCode.UltraPermissions.internal.Node;

public abstract class PermissionLookup {
   private final Node permission;
   private LookupOutcome outcome;

   public PermissionLookup(String text) {
      this.permission = new Node(text);
      this.outcome = null;
   }

   public LookupOutcome getOutcome() {
      return this.outcome;
   }

   public LookupOutcome perform() {
      if (this.outcome != null) {
         return this.outcome;
      }

      for (String selfAndWildcard : this.permission.getSelfAndWildcards()) {
         this.outcome = this.checkPermission(selfAndWildcard);
         if (this.outcome != null) {
            return this.outcome;
         }
      }

      return new LookupOutcome(false, null);
   }

   private LookupOutcome checkPermission(String text) {
      return Arrays.stream(this.getChecks()).map(lookupCheck -> lookupCheck.perform(text)).filter(Objects::nonNull).findFirst().orElse(null);
   }

   public abstract LookupCheck[] getChecks();
}

package me.TechsCode.UltraPermissions.internal.lookup.checks;

import java.util.List;
import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.LookupOutcome;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public abstract class DefinedPermissionCheck implements LookupCheck {
   private final List<Permission> definedPermissions = this.getDefinedPermissions();

   public DefinedPermissionCheck() {
   }

   @Override
   public LookupOutcome perform(String text) {
      boolean flag = false;
      Permission permission = null;
      boolean negated = false;

      for (Permission definedPermission : this.definedPermissions) {
         for (String selfAndContainedPermission : definedPermission.asNode().getSelfAndContainedPermissions()) {
            if (selfAndContainedPermission.equalsIgnoreCase(text)) {
               if (definedPermission.isPositive()) {
                  negated = false;
                  flag = true;
                  permission = definedPermission;
               } else {
                  if (!definedPermission.getHolder().isGroup()) {
                     return new LookupOutcome(false, "Negated from -" + definedPermission.getName());
                  }

                  negated = true;
               }
            }
         }
      }

      return !negated ? (flag ? new LookupOutcome(true, "Through Permission '" + permission.getName() + "'") : null) : new LookupOutcome(false, "Negated from -" + text);
   }

   public abstract List<Permission> getDefinedPermissions();
}

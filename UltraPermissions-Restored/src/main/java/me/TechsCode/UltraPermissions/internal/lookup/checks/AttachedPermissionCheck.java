package me.TechsCode.UltraPermissions.internal.lookup.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.LookupOutcome;
import org.bukkit.permissions.PermissionAttachment;

public abstract class AttachedPermissionCheck implements LookupCheck {
   public AttachedPermissionCheck() {
   }

   @Override
   public LookupOutcome perform(String text) {
      List<Entry<String, Boolean>> entries = new ArrayList<>(this.getAttachments()).stream().flatMap(permissionAttachment -> permissionAttachment.getPermissions().entrySet().stream()).collect(Collectors.toList());
      boolean allowed = false;
      boolean negated = false;

      for (Entry<String, Boolean> entry : entries) {
         if (((String)entry.getKey()).equalsIgnoreCase(text)) {
            if ((Boolean)entry.getValue()) {
               allowed = true;
            } else {
               negated = true;
            }
         }
      }

      if (negated) {
         return new LookupOutcome(false, "Negated by Permission Attachment");
      } else {
         return allowed ? new LookupOutcome(true, "Allowed by Permission Attachment") : null;
      }
   }

   public abstract List<PermissionAttachment> getAttachments();
}

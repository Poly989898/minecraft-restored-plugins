package me.TechsCode.UltraPermissions.events.permission;

import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionPolarityChangeEvent extends PermissionEvent {
   private final boolean oldPolarity;
   private final boolean newPolarity;

   public PermissionPolarityChangeEvent(Permission permission, boolean oldPolarity, boolean newPolarity) {
      super(permission);
      this.oldPolarity = oldPolarity;
      this.newPolarity = newPolarity;
   }

   public boolean getNewPolarity() {
      return this.newPolarity;
   }

   public boolean getOldPolarity() {
      return this.oldPolarity;
   }
}

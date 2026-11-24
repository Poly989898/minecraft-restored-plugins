package me.TechsCode.UltraPermissions.events.permission;

import me.TechsCode.UltraPermissions.events.UPEvent;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionEvent extends UPEvent {
   private final Permission permission;

   public PermissionEvent(Permission permission) {
      this.permission = permission;
   }

   public Permission getPermission() {
      return this.permission;
   }
}

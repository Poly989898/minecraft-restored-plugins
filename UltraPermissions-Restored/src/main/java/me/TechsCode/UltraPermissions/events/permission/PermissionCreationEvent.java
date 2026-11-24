package me.TechsCode.UltraPermissions.events.permission;

import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionCreationEvent extends PermissionEvent {
   public PermissionCreationEvent(Permission permission) {
      super(permission);
   }
}

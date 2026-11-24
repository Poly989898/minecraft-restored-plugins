package me.TechsCode.UltraPermissions.events.permission;

import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionDeletionEvent extends PermissionEvent {
   public PermissionDeletionEvent(Permission permission) {
      super(permission);
   }
}

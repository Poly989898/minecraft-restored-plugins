package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.PermissionColor;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionCopy {
   private final Permission permission;
   private final boolean inherited;

   public PermissionCopy(Permission permission, boolean flag) {
      this.permission = permission;
      this.inherited = flag;
   }

   public Permission getPermission() {
      return this.permission;
   }

   public String getName() {
      return this.permission.getName();
   }

   public String getColoredName() {
      PermissionColor permissionColor = PermissionColor.fromPermission(this);
      return permissionColor.getColor() + this.getName();
   }

   public PermissionColor getColor() {
      return PermissionColor.fromPermission(this);
   }

   public boolean isInherited() {
      return this.inherited;
   }
}

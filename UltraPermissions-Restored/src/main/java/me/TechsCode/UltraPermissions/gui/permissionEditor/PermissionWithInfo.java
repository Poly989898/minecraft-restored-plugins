package me.TechsCode.UltraPermissions.gui.permissionEditor;

import me.TechsCode.UltraPermissions.permissionDatabase.PermissionInfo;

public class PermissionWithInfo {
   private final String permission;
   private final PermissionInfo info;

   public PermissionWithInfo(String permission, PermissionInfo info) {
      this.permission = permission;
      this.info = info;
   }

   public boolean hasInfo() {
      return this.info != null;
   }

   public String getName() {
      return this.permission;
   }

   public PermissionInfo getInfo() {
      return this.info;
   }

   public boolean isThisPermission(String text) {
      return this.hasInfo() ? this.info.isThisPermission(text) : this.permission.equalsIgnoreCase(text);
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof PermissionWithInfo ? this.permission.equalsIgnoreCase(((PermissionWithInfo)object).permission) : false;
   }

   @Override
   public int hashCode() {
      return this.permission.hashCode();
   }
}

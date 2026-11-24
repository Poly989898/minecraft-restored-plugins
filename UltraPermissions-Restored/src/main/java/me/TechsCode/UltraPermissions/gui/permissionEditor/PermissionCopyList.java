package me.TechsCode.UltraPermissions.gui.permissionEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;

public class PermissionCopyList extends ArrayList<PermissionCopy> {
   public PermissionCopyList(int capacity) {
      super(capacity);
   }

   public PermissionCopyList() {
   }

   public PermissionCopyList(Collection<? extends PermissionCopy> permissionCopies) {
      super(permissionCopies);
   }

   public PermissionCopyList(PermissionList list, PermissionList other) {
      list.forEach(permission -> this.add(new PermissionCopy(permission, false)));
      other.forEach(permission -> this.add(new PermissionCopy(permission, true)));
   }

   public List<PermissionCopy> inheritedCopies() {
      return this.stream().filter(PermissionCopy::isInherited).collect(Collectors.toList());
   }

   public List<PermissionCopy> regularCopies() {
      return this.stream().filter(permissionCopy -> !permissionCopy.isInherited()).collect(Collectors.toList());
   }
}

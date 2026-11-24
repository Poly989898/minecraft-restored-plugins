package me.TechsCode.UltraPermissions.permissionDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PermissionInfoList extends ArrayList<PermissionInfo> {
   public PermissionInfoList(int count) {
      super(count);
   }

   public PermissionInfoList() {
   }

   public PermissionInfoList(Collection<? extends PermissionInfo> permissionInfos) {
      super(permissionInfos);
   }

   public Optional<PermissionInfo> find(String text) {
      for (PermissionInfo thi : this) {
         if (thi.isThisPermission(text)) {
            return Optional.of(thi);
         }
      }

      return Optional.empty();
   }

   public PermissionInfoList plugins(List<String> texts) {
      return this.stream().filter(permissionInfo -> texts.contains(permissionInfo.getPlugin())).collect(Collectors.toCollection(PermissionInfoList::new));
   }
}

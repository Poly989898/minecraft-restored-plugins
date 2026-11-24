package me.TechsCode.UltraPermissions.storage.objects;

import java.util.Optional;
import me.TechsCode.UltraPermissions.storage.PermissionCreator;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;

public interface PermissionHolder {
   String getName();

   PermissionList getPermissions();

   PermissionList getAdditionalPermissions();

   PermissionCreator newPermission(String text);

   Optional<String> getPrefix();

   Optional<String> getSuffix();

   void setPrefix(String text);

   void setSuffix(String text);
}

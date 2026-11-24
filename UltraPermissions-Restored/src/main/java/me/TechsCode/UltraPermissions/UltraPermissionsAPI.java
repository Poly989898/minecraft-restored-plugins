package me.TechsCode.UltraPermissions;

import java.util.UUID;
import me.TechsCode.UltraPermissions.base.visual.Color;
import me.TechsCode.UltraPermissions.storage.GroupCreator;
import me.TechsCode.UltraPermissions.storage.PermissionCreator;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.collection.GroupPathList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.collection.UserList;
import me.TechsCode.UltraPermissions.storage.objects.Holder;
import me.TechsCode.UltraPermissions.storage.objects.User;

public interface UltraPermissionsAPI {
   GroupCreator newGroup(String text);

   PermissionCreator newPermission(String text, Holder holder);

   PermissionList getPermissions();

   GroupList getGroups();

   GroupPathList getGroupPaths();

   UserList getUsers();

   Color getPluginMainColor();

   PublicRegistry getPublicRegistry();

   User registerUser(UUID uuid, String text, boolean flag);
}

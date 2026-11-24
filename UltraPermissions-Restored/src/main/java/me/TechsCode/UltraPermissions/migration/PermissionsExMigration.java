package me.TechsCode.UltraPermissions.migration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.Bukkit;
import ru.tehkode.permissions.PermissionEntity;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.PermissionsData;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExMigration extends MigrationAssistant {
   private static final Method GET_DATA_METHOD;

   public PermissionsExMigration() {
   }

   @Override
   public String getPluginName() {
      return "Permissions Ex";
   }

   @Override
   protected void migrate(UltraPermissions ultraPermissions, Runnable callback) {
      PermissionManager permissionManager = PermissionsEx.getPermissionManager();
      HashMap<String, Group> map = new HashMap<>();
      permissionManager.getGroupList()
         .forEach(
            permissionGroup -> {
               Group group = ultraPermissions.newGroup(permissionGroup.getName())
                  .setPrefix(permissionGroup.getOwnPrefix())
                  .setSuffix(permissionGroup.getOwnSuffix())
                  .setDefault(permissionGroup.isDefault("world") || permissionGroup.getName().equals("default"))
                  .setPriority(permissionGroup.getRank() + 1)
                  .create();
               ultraPermissions.log("Created Group " + group.getName());
               this.addPermissions(ultraPermissions, group, permissionGroup);
               map.put(permissionGroup.getName(), group);
            }
         );
      map.forEach((text, group) -> {
         PermissionGroup exGroup = permissionManager.getGroup(text);

         for (PermissionGroup parent : exGroup.getParents()) {
            if (map.containsKey(parent.getName())) {
               group.addInheritance((Group)map.get(parent.getName()));
               ultraPermissions.log("Added Inherited Group " + parent.getName() + " to " + group.getName());
            }
         }
      });

      for (PermissionUser user : permissionManager.getUsers()) {
         UUID uniqueId = Bukkit.getOfflinePlayer(user.getName()).getUniqueId();
         User ultraUser = ultraPermissions.registerUser(uniqueId, user.getName(), false);

         for (PermissionGroup group : user.getGroups()) {
            if (map.containsKey(group.getName())) {
               ultraUser.addGroup((Group)map.get(group.getName()));
            }
         }

         ultraUser.setPrefix(user.getOwnPrefix());
         ultraUser.setSuffix(user.getOwnSuffix());
         ultraPermissions.log("Added User " + ultraUser.getName());
         this.addPermissions(ultraPermissions, ultraUser, user);
      }

      callback.run();
   }

   private void addPermissions(UltraPermissions ultraPermissions, PermissionHolder permissionHolder, PermissionEntity permissionEntity) {
      for (Entry<String, List<String>> entry : getPermanentPermissions(permissionEntity).entrySet()) {
         String text = null;
         if (entry.getKey() != null && !((String)entry.getKey()).isEmpty()) {
            text = (String)entry.getKey();
         }

         for (String permissionNode : entry.getValue()) {
            if (!permissionNode.isEmpty()) {
               permissionHolder.newPermission(permissionNode).setWorld(text).create();
               ultraPermissions.log("Added Permission " + permissionNode + " to " + permissionHolder.getName());
            }
         }
      }
   }

   private static Map<String, List<String>> getPermanentPermissions(PermissionEntity permissionEntity) {
      try {
         PermissionsData permissionsData = (PermissionsData)GET_DATA_METHOD.invoke(permissionEntity);
         return permissionsData.getPermissionsMap();
      } catch (IllegalAccessException | InvocationTargetException exception) {
         throw new RuntimeException(exception);
      }
   }

   static {
      try {
         GET_DATA_METHOD = PermissionEntity.class.getDeclaredMethod("getData");
         GET_DATA_METHOD.setAccessible(true);
      } catch (NoSuchMethodException exception) {
         throw new ExceptionInInitializerError(exception);
      }
   }
}

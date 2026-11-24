package me.TechsCode.UltraPermissions.storage;

import java.util.Optional;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.events.permission.PermissionCreationEvent;
import me.TechsCode.UltraPermissions.events.permission.PermissionDeletionEvent;
import me.TechsCode.UltraPermissions.events.permission.PermissionPolarityChangeEvent;
import me.TechsCode.UltraPermissions.events.permission.PermissionServerChangeEvent;
import me.TechsCode.UltraPermissions.events.permission.PermissionWorldChangeEvent;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class SpigotPermissionStorage extends PermissionStorage {
   public SpigotPermissionStorage(TechPlugin techPlugin, Class<? extends StorageImplementation> type) {
      super(techPlugin, type);
   }

   @Override
   public void onCreation(Permission permission) {
      super.onCreation(permission);
      PermissionCreationEvent event = new PermissionCreationEvent(permission);
      event.call(this.plugin, event);
   }

   @Override
   public void onDestroy(Permission permission) {
      super.onDestroy(permission);
      PermissionDeletionEvent event = new PermissionDeletionEvent(permission);
      event.call(this.plugin, event);
   }

   @Override
   public void onChange(Permission permission, Permission newPermission) {
      super.onChange(permission, newPermission);
      Optional server = permission.getServer();
      Optional newServer = newPermission.getServer();
      if (!server.equals(newServer)) {
         PermissionServerChangeEvent event = new PermissionServerChangeEvent(newPermission, server, newServer);
         event.call(this.plugin, event);
      }

      Optional world = permission.getWorld();
      Optional newWorld = newPermission.getWorld();
      if (!world.equals(newWorld)) {
         PermissionWorldChangeEvent event = new PermissionWorldChangeEvent(newPermission, world, newWorld);
         event.call(this.plugin, event);
      }

      if (!server.equals(newServer)) {
         PermissionServerChangeEvent event = new PermissionServerChangeEvent(newPermission, permission.getServer(), newPermission.getServer());
         event.call(this.plugin, event);
      }

      if (permission.isPositive() != newPermission.isPositive()) {
         PermissionPolarityChangeEvent event = new PermissionPolarityChangeEvent(newPermission, permission.isPositive(), newPermission.isPositive());
         event.call(this.plugin, event);
      }
   }
}

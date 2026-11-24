package me.TechsCode.UltraPermissions.storage;

import java.util.Comparator;
import me.TechsCode.UltraPermissions.StorageController;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.Holder;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionStorage extends Storage<Permission> {
   public PermissionStorage(TechPlugin techPlugin, Class<? extends StorageImplementation> type) {
      super(techPlugin, "Permissions", Permission.class, type, true);
   }

   public void onMount(Permission permission) {
   }

   public void onCreation(Permission permission) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public void onDestroy(Permission permission) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public void onChange(Permission previousPermission, Permission updatedPermission) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   @Override
   public void onDataSynchronization() {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public PermissionList getPermissions() {
      PermissionList list = new PermissionList();
      list.addAll(this.get());
      list.sort(Comparator.comparing(Permission::getId));
      return list;
   }

   public PermissionCreator newPermission(String text, Holder holder) {
      return new PermissionCreator(this, text, holder);
   }
}

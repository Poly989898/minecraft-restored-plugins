package me.TechsCode.UltraPermissions.storage;

import me.TechsCode.UltraPermissions.StorageController;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.objects.Group;

public class GroupStorage extends Storage<Group> {
   public GroupStorage(TechPlugin techPlugin, Class<? extends StorageImplementation> type) {
      super(techPlugin, "Groups", Group.class, type, true);
   }

   public void onMount(Group group) {
   }

   public void onCreation(Group group) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public void onDestroy(Group group) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public void onChange(Group group, Group newGroup) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   @Override
   public void onDataSynchronization() {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public GroupList getGroups() {
      GroupList list = new GroupList();
      list.addAll(this.get());
      list.bestToWorst();
      return list;
   }

   public GroupCreator newGroup(String text) {
      return new GroupCreator(this, text);
   }
}

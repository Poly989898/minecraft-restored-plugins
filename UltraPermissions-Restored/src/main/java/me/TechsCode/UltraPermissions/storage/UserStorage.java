package me.TechsCode.UltraPermissions.storage;

import java.util.HashMap;
import java.util.UUID;
import me.TechsCode.UltraPermissions.StorageController;
import me.TechsCode.UltraPermissions.UltraPermissionsAPI;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.storage.collection.UserList;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserStorage extends Storage<User> {
   public UserStorage(TechPlugin techPlugin, Class<? extends StorageImplementation> type) {
      super(techPlugin, "Users", User.class, type, true);
   }

   public void onMount(User user) {
   }

   public void onCreation(User user) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public void onDestroy(User user) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public void onChange(User user, User newUser) {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   @Override
   public void onDataSynchronization() {
      StorageController storageController = (StorageController)this.plugin;
      storageController.onDataModification();
   }

   public UserList getUsers() {
      UserList list = new UserList();
      list.addAll(this.get());
      return list;
   }

   public User registerUser(UUID uuid, String text, boolean flag) {
      UltraPermissionsAPI ultraPermissionsAPI = (UltraPermissionsAPI)this.plugin;
      User user = this.create(new User(uuid, text, false, new HashMap<>(), null, null, null));
      if (flag) {
         ultraPermissionsAPI.getGroups().defaults(true).forEach(user::addGroup);
      }

      return user;
   }
}

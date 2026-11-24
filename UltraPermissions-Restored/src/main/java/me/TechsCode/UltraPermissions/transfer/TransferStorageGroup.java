package me.TechsCode.UltraPermissions.transfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.storage.implementations.LocalFile;
import me.TechsCode.UltraPermissions.base.storage.implementations.MySQL;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.User;

class TransferStorageGroup {
   TransferStorage<User> userStorage;
   TransferStorage<Permission> permissionStorage;
   TransferStorage<Group> groupStorage;

   public TransferStorageGroup(UltraPermissions ultraPermissions, boolean global) {
      this.userStorage = new TransferStorage<>(ultraPermissions, "Users", User.class, global ? MySQL.class : LocalFile.class, false);
      this.permissionStorage = new TransferStorage<>(ultraPermissions, "Permissions", Permission.class, global ? MySQL.class : LocalFile.class, false);
      this.groupStorage = new TransferStorage<>(ultraPermissions, "Groups", Group.class, global ? MySQL.class : LocalFile.class, false);
   }

   public void copyTo(TransferStorageGroup transferStorageGroup) {
      new ArrayList<>(transferStorageGroup.userStorage.get()).forEach(User::_justDestroy);
      new ArrayList<>(transferStorageGroup.permissionStorage.get()).forEach(Permission::_justDestroy);
      new ArrayList<>(transferStorageGroup.groupStorage.get()).forEach(Group::_justDestroy);
      this.groupStorage.get().forEach(group -> transferStorageGroup.groupStorage.create(group.createCopy()));
      this.permissionStorage.get().forEach(permission -> transferStorageGroup.permissionStorage.create(permission.createCopy()));
      this.userStorage.get().forEach(user -> transferStorageGroup.userStorage.create(user.createCopy()));
   }

   public long getLastWrite() {
      return Collections.max(Arrays.asList(this.userStorage.getLastWrite(), this.permissionStorage.getLastWrite(), this.groupStorage.getLastWrite()));
   }
}

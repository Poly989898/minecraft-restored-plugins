package me.TechsCode.UltraPermissions.storage;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.SimpleStorage;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.storage.collection.GroupPathList;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;

public class GroupPathStorage extends SimpleStorage<GroupPath> {
   public GroupPathStorage(TechPlugin techPlugin, Class<? extends StorageImplementation> type) {
      super(techPlugin, "GroupPaths", GroupPath.class, type, true);
   }

   public GroupPathList getGroupPaths() {
      return new GroupPathList(this.get());
   }
}

package me.TechsCode.UltraPermissions.storage.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;

public class GroupPathList extends ArrayList<GroupPath> {
   public GroupPathList(int count) {
      super(count);
   }

   public GroupPathList() {
   }

   public GroupPathList(Collection<? extends GroupPath> groupPaths) {
      super(groupPaths);
   }

   public Optional<GroupPath> name(String text) {
      return this.stream().filter(groupPath -> groupPath.getName().equalsIgnoreCase(text)).findFirst();
   }
}

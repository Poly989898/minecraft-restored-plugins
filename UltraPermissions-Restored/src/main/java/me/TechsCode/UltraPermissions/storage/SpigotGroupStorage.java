package me.TechsCode.UltraPermissions.storage;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.events.group.GroupChangeEvent;
import me.TechsCode.UltraPermissions.events.group.GroupCreationEvent;
import me.TechsCode.UltraPermissions.events.group.GroupDeletionEvent;
import me.TechsCode.UltraPermissions.events.group.GroupPrefixChangeEvent;
import me.TechsCode.UltraPermissions.events.group.GroupSuffixChangeEvent;
import me.TechsCode.UltraPermissions.storage.objects.Group;

public class SpigotGroupStorage extends GroupStorage {
   public SpigotGroupStorage(TechPlugin techPlugin, Class<? extends StorageImplementation> type) {
      super(techPlugin, type);
   }

   @Override
   public void onCreation(Group group) {
      super.onCreation(group);
      GroupCreationEvent event = new GroupCreationEvent(group);
      event.call(this.plugin, event);
   }

   @Override
   public void onDestroy(Group group) {
      super.onDestroy(group);
      GroupDeletionEvent event = new GroupDeletionEvent(group);
      event.call(this.plugin, event);
   }

   @Override
   public void onChange(Group group, Group newGroup) {
      super.onChange(group, newGroup);
      GroupChangeEvent event = new GroupChangeEvent(group, newGroup);
      event.call(this.plugin, event);
      if (!group.getPrefix().equals(newGroup.getPrefix())) {
         GroupPrefixChangeEvent changeEvent = new GroupPrefixChangeEvent(newGroup, group.getPrefix().orElse(null), newGroup.getPrefix().orElse(null));
         changeEvent.call(this.plugin, changeEvent);
      }

      if (!group.getSuffix().equals(newGroup.getSuffix())) {
         GroupSuffixChangeEvent changeEvent = new GroupSuffixChangeEvent(newGroup, group.getSuffix().orElse(null), newGroup.getSuffix().orElse(null));
         changeEvent.call(this.plugin, changeEvent);
      }
   }
}

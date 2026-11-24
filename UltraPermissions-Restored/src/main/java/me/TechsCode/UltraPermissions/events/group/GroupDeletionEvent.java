package me.TechsCode.UltraPermissions.events.group;

import me.TechsCode.UltraPermissions.storage.objects.Group;

public class GroupDeletionEvent extends GroupEvent {
   public GroupDeletionEvent(Group group) {
      super(group);
   }
}

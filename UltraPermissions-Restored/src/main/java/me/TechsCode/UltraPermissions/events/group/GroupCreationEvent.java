package me.TechsCode.UltraPermissions.events.group;

import me.TechsCode.UltraPermissions.storage.objects.Group;

public class GroupCreationEvent extends GroupEvent {
   public GroupCreationEvent(Group group) {
      super(group);
   }
}

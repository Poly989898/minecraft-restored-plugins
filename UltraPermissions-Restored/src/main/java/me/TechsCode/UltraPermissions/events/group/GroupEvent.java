package me.TechsCode.UltraPermissions.events.group;

import me.TechsCode.UltraPermissions.events.UPEvent;
import me.TechsCode.UltraPermissions.storage.objects.Group;

public abstract class GroupEvent extends UPEvent {
   public final Group group;

   public GroupEvent(Group group) {
      this.group = group;
   }

   public Group getGroup() {
      return this.group;
   }
}

package me.TechsCode.UltraPermissions.events.user;

import me.TechsCode.UltraPermissions.base.storage.Stored;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserGroupRemovedEvent extends UserEvent {
   private final User user;
   private final Stored<Group> group;

   public UserGroupRemovedEvent(User user, Stored<Group> group) {
      super(user);
      this.user = user;
      this.group = group;
   }

   public Stored<Group> getGroup() {
      return this.group;
   }

   @Override
   public User getUser() {
      return this.user;
   }
}

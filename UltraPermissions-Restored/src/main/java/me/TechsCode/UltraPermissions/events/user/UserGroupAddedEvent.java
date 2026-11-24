package me.TechsCode.UltraPermissions.events.user;

import me.TechsCode.UltraPermissions.base.storage.Stored;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserGroupAddedEvent extends UserEvent {
   private final User user;
   private final Stored<Group> group;
   private final long expiry;

   public UserGroupAddedEvent(User user, Stored<Group> stored, long expiry) {
      super(user);
      this.user = user;
      this.group = stored;
      this.expiry = expiry;
   }

   public Stored<Group> getGroup() {
      return this.group;
   }

   public long getExpiry() {
      return this.expiry;
   }

   @Override
   public User getUser() {
      return this.user;
   }
}

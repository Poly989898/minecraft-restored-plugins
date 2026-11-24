package me.TechsCode.UltraPermissions.events.user;

import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserCreationEvent extends UserEvent {
   public UserCreationEvent(User user) {
      super(user);
   }
}

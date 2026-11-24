package me.TechsCode.UltraPermissions.events.user;

import javax.annotation.Nullable;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserPrefixChangeEvent extends UserEvent {
   private final String oldPrefix;
   private final String newPrefix;

   public UserPrefixChangeEvent(User user, @Nullable String text, @Nullable String newPrefix) {
      super(user);
      this.oldPrefix = text;
      this.newPrefix = newPrefix;
   }

   public String getNewPrefix() {
      return this.newPrefix;
   }

   public String getOldPrefix() {
      return this.oldPrefix;
   }
}

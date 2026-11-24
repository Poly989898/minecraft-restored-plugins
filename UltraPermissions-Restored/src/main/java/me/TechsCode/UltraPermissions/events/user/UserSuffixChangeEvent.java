package me.TechsCode.UltraPermissions.events.user;

import javax.annotation.Nullable;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserSuffixChangeEvent extends UserEvent {
   private final String oldSuffix;
   private final String newSuffix;

   public UserSuffixChangeEvent(User user, @Nullable String text, @Nullable String newSuffix) {
      super(user);
      this.oldSuffix = text;
      this.newSuffix = newSuffix;
   }

   public String getOldSuffix() {
      return this.oldSuffix;
   }

   public String getNewSuffix() {
      return this.newSuffix;
   }
}

package me.TechsCode.UltraPermissions.events.user;

import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserSuperAdminChangeEvent extends UserEvent {
   private final boolean oldSuperadmin;
   private final boolean newSuperadmin;

   public UserSuperAdminChangeEvent(User user, boolean oldSuperadmin, boolean newSuperadmin) {
      super(user);
      this.oldSuperadmin = oldSuperadmin;
      this.newSuperadmin = newSuperadmin;
   }

   public boolean isNowSuperAdmin() {
      return this.newSuperadmin;
   }

   public boolean wasSuperAdmin() {
      return this.oldSuperadmin;
   }
}

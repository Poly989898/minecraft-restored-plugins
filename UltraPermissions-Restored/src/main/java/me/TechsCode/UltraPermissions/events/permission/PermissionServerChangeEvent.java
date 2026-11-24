package me.TechsCode.UltraPermissions.events.permission;

import java.util.Optional;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionServerChangeEvent extends PermissionEvent {
   private final Optional<String> oldServer;
   private final Optional<String> newServer;

   public PermissionServerChangeEvent(Permission permission, Optional<String> optional, Optional<String> newServer) {
      super(permission);
      this.oldServer = optional;
      this.newServer = newServer;
   }

   public Optional<String> getOldServer() {
      return this.oldServer;
   }

   public Optional<String> getNewServer() {
      return this.newServer;
   }
}

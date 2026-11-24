package me.TechsCode.UltraPermissions.events.permission;

import java.util.Optional;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionWorldChangeEvent extends PermissionEvent {
   private final Optional<String> oldWorld;
   private final Optional<String> newWorld;

   public PermissionWorldChangeEvent(Permission permission, Optional<String> optional, Optional<String> newWorld) {
      super(permission);
      this.oldWorld = optional;
      this.newWorld = newWorld;
   }

   public Optional<String> getOldWorld() {
      return this.oldWorld;
   }

   public Optional<String> getNewWorld() {
      return this.newWorld;
   }
}

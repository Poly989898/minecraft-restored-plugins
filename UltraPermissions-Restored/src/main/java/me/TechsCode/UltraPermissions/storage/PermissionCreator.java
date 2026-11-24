package me.TechsCode.UltraPermissions.storage;

import me.TechsCode.UltraPermissions.storage.objects.Holder;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionCreator {
   private PermissionStorage storage;
   private String name;
   private Holder holder;
   private String world;
   private String server;
   private boolean positive = true;
   private long expiration;

   public PermissionCreator(PermissionStorage permissionStorage, String text, Holder holder) {
      this.storage = permissionStorage;
      this.name = text;
      this.holder = holder;
      if (text.startsWith("-")) {
         this.name = text.substring(1);
         this.positive = false;
      }

      this.name = this.name.replace(" ", ".");
   }

   public PermissionCreator setServer(String text) {
      this.server = text;
      return this;
   }

   public PermissionCreator setWorld(String text) {
      this.world = text;
      return this;
   }

   public PermissionCreator setPositive(boolean flag) {
      this.positive = flag;
      return this;
   }

   public PermissionCreator setExpiration(long value) {
      this.expiration = value;
      return this;
   }

   public Permission create() {
      return this.storage
         .create(new Permission(this.storage.getNextNumericId(), this.holder, this.world, this.server, this.name, this.positive, this.expiration));
   }
}

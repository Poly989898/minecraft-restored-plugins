package me.TechsCode.UltraPermissions.storage.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.storage.objects.Holder;
import me.TechsCode.UltraPermissions.storage.objects.Permission;

public class PermissionList extends ArrayList<Permission> {
   public PermissionList(int capacity) {
      super(capacity);
   }

   public PermissionList() {
   }

   public PermissionList(Collection<? extends Permission> permissions) {
      super(permissions);
   }

   public PermissionList name(String text) {
      return this.stream().filter(permission -> permission.getName().equalsIgnoreCase(text)).collect(Collectors.toCollection(PermissionList::new));
   }

   public PermissionList holder(List<Holder> holders) {
      return this.stream().filter(permission -> holders.contains(permission.getHolder())).collect(Collectors.toCollection(PermissionList::new));
   }

   public PermissionList holder(Holder... holders) {
      return this.holder(Arrays.asList(holders));
   }

   public PermissionList bungee() {
      return this.stream().filter(Permission::isBungeePermission).collect(Collectors.toCollection(PermissionList::new));
   }

   public PermissionList expired() {
      return this.stream().filter(Permission::isExpired).collect(Collectors.toCollection(PermissionList::new));
   }

   public PermissionList worlds(boolean include, List<String> texts) {
      return this.stream().filter(permission -> {
         Optional world = permission.getWorld();
         return include && !world.isPresent() || world.isPresent() && texts.contains(world.get());
      }).collect(Collectors.toCollection(PermissionList::new));
   }

   public PermissionList worlds(boolean include, String... texts) {
      return this.worlds(include, Arrays.asList(texts));
   }

   public PermissionList servers(boolean include, List<String> texts) {
      return this.stream().filter(permission -> {
         Optional server = permission.getServer();
         return include && !server.isPresent() || server.isPresent() && texts.contains(server.get());
      }).collect(Collectors.toCollection(PermissionList::new));
   }

   public PermissionList servers(boolean include, String... texts) {
      return this.servers(include, Arrays.asList(texts));
   }

   public PermissionList key(Integer key) {
      return this.stream().filter(permission -> permission.getKey().equals(String.valueOf(key))).collect(Collectors.toCollection(PermissionList::new));
   }
}

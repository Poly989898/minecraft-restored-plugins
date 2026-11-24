package me.TechsCode.UltraPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.Holder;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class CachedPlayerPermissionProvider {
   private static Optional<NServer> thisServer;
   private UltraPermissions plugin;
   private Map<UUID, CachedPlayerPermissionProvider.CacheEntry> cache;

   public CachedPlayerPermissionProvider(UltraPermissions ultraPermissions) {
      thisServer = ultraPermissions.getThisServer();
      this.plugin = ultraPermissions;
      this.cache = new HashMap<>();
   }

   public PermissionList retrieve(UUID uuid, String text) {
      if ((thisServer == null || !thisServer.isPresent()) && this.plugin.getThisServer().isPresent()) {
         thisServer = this.plugin.getThisServer();
         this.cache.clear();
      }

      if (this.cache.containsKey(uuid)) {
         CachedPlayerPermissionProvider.CacheEntry cacheEntry = this.cache.get(uuid);
         if (cacheEntry.world.equals(text)) {
            return cacheEntry.list;
         }
      }

      Optional optional = this.plugin.getUsers().uuid(uuid);
      if (!optional.isPresent()) {
         return new PermissionList();
      }

      String serverName = thisServer.map(NServer::getName).orElse(null);
      GroupList list = ((User)optional.get())
         .getActiveGroups()
         .servers(true, serverName)
         .worlds(true, text)
         .getWithRecursiveInherits()
         .servers(true, serverName)
         .worlds(true, text);
      ArrayList holders = new ArrayList<>(list.holders());
      holders.add(Holder.fromUser((User)optional.get()));
      PermissionList permissions = this.plugin.getPermissions().holder(holders).servers(true, serverName).worlds(true, text);
      this.cache.put(uuid, new CachedPlayerPermissionProvider.CacheEntry(permissions, text));
      return permissions;
   }

   public void clearCache() {
      this.cache = new HashMap<>();
   }

   public class CacheEntry {
      final PermissionList list;
      final String world;

      public CacheEntry(PermissionList list, String text) {
         this.list = list;
         this.world = text;
      }
   }
}

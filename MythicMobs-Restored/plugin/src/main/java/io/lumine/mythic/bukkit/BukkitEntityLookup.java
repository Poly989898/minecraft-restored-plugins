package io.lumine.mythic.bukkit;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitEntityLookup implements Terminable {
   private static final long TTL_MILLIS = 300000L;
   private final ConcurrentMap<UUID, BukkitEntityLookup.CacheEntry> cache = new ConcurrentHashMap<>();
   private final Task cleanupTask = Schedulers.sync().runRepeating(this::purgeStaleEntries, 6000L, 6000L);

   public BukkitEntityLookup(JavaPlugin plugin) {
   }

   @Override
   public void close() {
      this.cleanupTask.terminate();
      this.cache.clear();
   }

   public AbstractEntity getEntity(UUID uuid) {
      return uuid == null ? null : this.getCachedOrLoad(uuid, System.currentTimeMillis());
   }

   public Collection<AbstractEntity> getEntities(Collection<UUID> uuids) {
      if (uuids.isEmpty()) {
         return Collections.emptyList();
      } else {
         long now = System.currentTimeMillis();
         List<AbstractEntity> result = new ArrayList<>(uuids.size());
         if (Bukkit.isPrimaryThread()) {
            for (UUID uuid : uuids) {
               AbstractEntity ent = this.getCachedOrLoad(uuid, now);
               if (ent != null) {
                  result.add(ent);
               }
            }
         } else {
            List<UUID> missing = new ArrayList<>();

            for (UUID uuidx : uuids) {
               if (uuidx != null) {
                  BukkitEntityLookup.CacheEntry entry = this.cache.get(uuidx);
                  if (entry != null && entry.ref.get() != null && entry.expireAt > now) {
                     result.add(entry.wrapper);
                  } else {
                     missing.add(uuidx);
                  }
               }
            }

            if (missing.isEmpty()) {
               return result;
            }

            try {
               Map<UUID, Entity> rawMap = Promise.<Map<UUID, Entity>>supplyingSync(() -> {
                  Map<UUID, Entity> m = new HashMap<>(missing.size());

                  for (UUID uuidxx : missing) {
                     Entity be = Bukkit.getEntity(uuidxx);
                     if (be != null) {
                        m.put(uuidxx, be);
                     }
                  }

                  return m;
               }).get();

               for (Entry<UUID, Entity> e : rawMap.entrySet()) {
                  AbstractEntity ae = BukkitAdapter.adapt(e.getValue());
                  this.cache.put(e.getKey(), new BukkitEntityLookup.CacheEntry(ae, e.getValue(), now + 300000L));
                  result.add(ae);
               }
            } catch (Throwable ex) {
               ex.printStackTrace();

               for (UUID uuidxx : missing) {
                  AbstractEntity ae = this.getEntity(uuidxx);
                  if (ae != null) {
                     result.add(ae);
                  }
               }
            }
         }

         return result;
      }
   }

   private AbstractEntity getCachedOrLoad(UUID uuid, long now) {
      BukkitEntityLookup.CacheEntry entry = this.cache.get(uuid);
      if (entry != null) {
         if (entry.ref.get() != null && entry.expireAt > now) {
            return entry.wrapper;
         }

         this.cache.remove(uuid);
      }

      Entity bukkitEntity = this.loadBukkitEntity(uuid);
      if (bukkitEntity != null) {
         AbstractEntity loaded = BukkitAdapter.adapt(bukkitEntity);
         this.cache.put(uuid, new BukkitEntityLookup.CacheEntry(loaded, bukkitEntity, now + 300000L));
         return loaded;
      } else {
         return null;
      }
   }

   public void invalidate(UUID uuid) {
      this.cache.remove(uuid);
   }

   private void purgeStaleEntries() {
      long now = System.currentTimeMillis();
      Iterator<Entry<UUID, BukkitEntityLookup.CacheEntry>> it = this.cache.entrySet().iterator();

      while (it.hasNext()) {
         Entry<UUID, BukkitEntityLookup.CacheEntry> kv = it.next();
         BukkitEntityLookup.CacheEntry e = kv.getValue();
         if (e.expireAt <= now || e.ref.get() == null) {
            it.remove();
         }
      }
   }

   private Entity loadBukkitEntity(UUID uuid) {
      try {
         return Bukkit.isPrimaryThread() ? Bukkit.getEntity(uuid) : Promise.<Entity>supplyingSync(() -> Bukkit.getEntity(uuid)).get();
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   private static class CacheEntry {
      final AbstractEntity wrapper;
      final WeakReference<Entity> ref;
      final long expireAt;

      CacheEntry(AbstractEntity wrapper, Entity entity, long expireAt) {
         this.wrapper = wrapper;
         this.ref = new WeakReference<>(entity);
         this.expireAt = expireAt;
      }
   }
}

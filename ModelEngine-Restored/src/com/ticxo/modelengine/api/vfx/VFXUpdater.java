package com.ticxo.modelengine.api.vfx;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

public class VFXUpdater {
   private final Map<Integer, UUID> entityIdLookup = Maps.newConcurrentMap();
   private final Map<UUID, VFX> uuidLookup = Maps.newConcurrentMap();
   private final AtomicLong removalEpoch = new AtomicLong();

   public long getRemovalEpoch() {
      return this.removalEpoch.get();
   }

   public void updateAllVFXs() {
      for (Entry<UUID, VFX> entry : this.uuidLookup.entrySet()) {
         VFX entity = entry.getValue();
         if (entity.isInitialized()) {
            VFXRenderer renderer = entity.getRenderer();
            if (renderer != null) {
               try {
                  entity.getBase().getData().asyncUpdate();
                  if (!entity.tick()) {
                     this.removeAndDestroyVFX(entry.getKey(), entity, renderer);
                  } else {
                     renderer.sendToClient();
                     entity.getBase().getData().cleanup();
                  }
               } catch (Throwable t) {
                  t.printStackTrace();
                  this.removeAndDestroyVFX(entry.getKey(), entity, entity.getRenderer());
               }
            }
         }
      }
   }

   private void removeAndDestroyVFX(UUID uuid, VFX entity, VFXRenderer renderer) {
      this.uuidLookup.remove(uuid, entity);
      this.entityIdLookup.remove(entity.getBase().getEntityId(), uuid);
      this.removalEpoch.incrementAndGet();
      if (renderer != null) {
         try {
            renderer.destroy();
         } catch (Throwable t) {
            t.printStackTrace();
         }
      }

      try {
         entity.getBase().getData().cleanup();
         entity.getBase().setForcedAlive(false);
      } catch (Throwable t) {
         t.printStackTrace();
      }

      entity.destroy();
   }

   public void registerVFX(BaseEntity<?> base, VFX vfx) {
      this.entityIdLookup.put(base.getEntityId(), base.getUUID());
      this.uuidLookup.put(base.getUUID(), vfx);
   }

   public VFX getVFX(int id) {
      return this.getVFX(this.entityIdLookup.get(id));
   }

   public VFX getVFX(UUID uuid) {
      return uuid == null ? null : this.uuidLookup.get(uuid);
   }

   public VFX removeVFX(int id) {
      return this.removeVFX(this.entityIdLookup.get(id));
   }

   public VFX removeVFX(UUID uuid) {
      if (uuid == null) {
         return null;
      }

      VFX vfx = this.uuidLookup.get(uuid);
      if (vfx != null) {
         vfx.markRemoved();
      }

      return vfx;
   }
}

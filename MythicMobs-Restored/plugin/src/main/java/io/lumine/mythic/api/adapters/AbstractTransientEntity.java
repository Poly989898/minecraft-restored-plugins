package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class AbstractTransientEntity {
   private UUID uniqueId;
   private WeakReference<AbstractEntity> reference;
   private long lastCheck = 0L;

   public AbstractTransientEntity(UUID uniqueId) {
      this.uniqueId = uniqueId;
      AbstractEntity entity = MythicProvider.get().getBootstrap().getEntity(uniqueId);
      if (entity != null) {
         this.reference = new WeakReference<>(entity);
      }
   }

   public AbstractTransientEntity(UUID uniqueId, AbstractEntity entity) {
      this.uniqueId = uniqueId;
      this.reference = new WeakReference<>(entity);
   }

   public Optl<AbstractEntity> get() {
      if (this.reference != null && this.reference.get() != null) {
         return Optl.of(this.reference.get());
      } else if (System.currentTimeMillis() - this.lastCheck > 2000L) {
         this.lastCheck = System.currentTimeMillis();
         AbstractEntity entity = MythicProvider.get().getBootstrap().getEntity(this.uniqueId);
         if (entity != null) {
            this.reference = new WeakReference<>(entity);
            return Optl.of(entity);
         } else {
            return Optl.empty();
         }
      } else {
         return Optl.empty();
      }
   }
}

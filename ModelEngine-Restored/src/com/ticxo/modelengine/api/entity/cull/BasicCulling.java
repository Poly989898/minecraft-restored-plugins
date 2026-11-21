package com.ticxo.modelengine.api.entity.cull;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.mount.MountPairManager;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class BasicCulling implements ModelCuller {
   private static final Set<UUID> GLOBAL_EXEMPT = ConcurrentHashMap.newKeySet();
   @Getter @Setter
   protected IEntityData data;
   protected int lastCulled;
   @Getter
   protected int culledCount;

   public static void setGlobalCullExempt(UUID uuid, boolean exempt) {
      if (exempt) {
         GLOBAL_EXEMPT.add(uuid);
      } else {
         GLOBAL_EXEMPT.remove(uuid);
      }
   }

   public static boolean isGlobalCullExempt(UUID uuid) {
      return !GLOBAL_EXEMPT.isEmpty() && GLOBAL_EXEMPT.contains(uuid);
   }

   @Override
   public void updateCulledPlayer() {
      if (this.data != null && --this.lastCulled <= 0) {
         this.lastCulled = this.data.cullInterval();
         MountPairManager pairManager = ModelEngineAPI.getMountPairManager();
         Location location = this.data.getLocation();
         BoundingBox box = this.data.getCullHitbox() == null
            ? this.data.getBaseEntity().getBoundingBox()
            : this.data.getCullHitbox().createBoundingBox(location.toVector());

         for (UUID uuid : this.data.getMutableTracking().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
               if (!isGlobalCullExempt(uuid) && !this.data.isCullExempt(uuid)) {
                  ActiveModel mounted = pairManager.getMountedPair(player.getUniqueId());
                  if (mounted != null && mounted.getModeledEntity().getBase().getData() == this.data) {
                     this.put(player.getUniqueId(), CullType.NO_CULL);
                  } else if (location.getWorld() != player.getWorld()) {
                     this.put(player.getUniqueId(), CullType.CULLED);
                  } else {
                     Location eyeLoc = PLAYER_CALLBACK.invoker().calculate(this.data, player, player.getEyeLocation());
                     if (this.data.verticalCull()) {
                        double distance = Math.max(eyeLoc.getY() - box.getMaxY(), box.getMinY() - eyeLoc.getY());
                        if (distance > this.data.verticalCullDistance()) {
                           this.put(player.getUniqueId(), this.data.verticalCullType());
                           continue;
                        }
                     }

                     double deltaX = location.getX() - eyeLoc.getX();
                     double deltaY = location.getY() - eyeLoc.getY();
                     double deltaZ = location.getZ() - eyeLoc.getZ();
                     double deltaLengthSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
                     if (this.data.blockedCull()
                        && !this.data.isBaseGlowing()
                        && !this.data.isModelGlowing()
                        && deltaLengthSquared > this.data.blockedCullIgnoreRadius()
                        && ModelEngineAPI.getEntityHandler().shouldCull(player, eyeLoc, box)) {
                        this.put(player.getUniqueId(), this.data.blockedCullType());
                     } else {
                        if (this.data.backCull()) {
                           if (box.contains(eyeLoc.getX(), eyeLoc.getY(), eyeLoc.getZ())) {
                              this.put(player.getUniqueId(), CullType.NO_CULL);
                              continue;
                           }

                           double deltaLength = Math.sqrt(deltaLengthSquared);
                           Vector direction = eyeLoc.getDirection();
                           double dot = direction.getX() * (deltaX / deltaLength)
                              + direction.getY() * (deltaY / deltaLength)
                              + direction.getZ() * (deltaZ / deltaLength);
                           if (deltaLengthSquared > this.data.backCullIgnoreRadius() && dot <= this.data.backCullAngle()) {
                              this.put(player.getUniqueId(), this.data.backCullType());
                              continue;
                           }
                        }

                        this.put(player.getUniqueId(), CullType.NO_CULL);
                     }
                  }
               } else {
                  this.put(uuid, CullType.NO_CULL);
               }
            }
         }
      }
   }

   @Override
   public CullType put(UUID uuid, CullType type) {
      CullType last = this.data.getMutableTracking().put(uuid, type);
      if (last == CullType.CULLED) {
         this.culledCount--;
      }

      if (type == CullType.CULLED) {
         this.culledCount++;
      }

      return last;
   }

   @Override
   public CullType remove(UUID uuid) {
      CullType removed = this.data.getMutableTracking().remove(uuid);
      if (removed == CullType.CULLED) {
         this.culledCount--;
      }

      return removed;
   }
}

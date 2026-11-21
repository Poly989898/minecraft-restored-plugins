package com.ticxo.modelengine.api.interaction;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class InteractionTracker {
   private final Map<UUID, Integer> hitboxesUUID = Maps.newConcurrentMap();
   private final Map<Integer, HitboxEntity> hitboxes = Maps.newConcurrentMap();
   private final Map<UUID, DynamicHitbox> playerRelay = Maps.newConcurrentMap();
   private final Map<Integer, ActiveModel> modelRelay = Maps.newConcurrentMap();
   private final Map<Integer, Integer> entityRelay = Maps.newConcurrentMap();
   @Getter
   private final Callback<BiPredicate<Player, Boolean>> raytraceCallback = new Callback<>(predicates -> (player, cancel) -> {
      for (BiPredicate<Player, Boolean> predicate : predicates) {
         cancel = predicate.test(player, cancel);
      }

      return cancel;
   });

   public void raytraceHitboxes() {
      this.hitboxes.values().removeIf(hitbox -> {
         if (hitbox.isValid()) {
            return false;
         }

         this.hitboxesUUID.remove(hitbox.getUniqueId());
         return true;
      });
      if (this.hitboxes.isEmpty()) {
         this.playerRelay.forEach((uuid, existing) -> existing.destroy());
         this.playerRelay.clear();
      } else {
         for (Player player : Bukkit.getOnlinePlayers()) {
            try {
               if (!this.raytraceCallback.invoker().test(player, false)) {
                  if (player.getGameMode() == GameMode.SPECTATOR) {
                     this.playerRelay.computeIfPresent(player.getUniqueId(), (uuid, existing) -> {
                        existing.destroy();
                        return null;
                     });
                  } else {
                     Vector startVec = player.getEyeLocation().toVector();
                     Vector dirVec = player.getEyeLocation().getDirection();
                     if (Double.isFinite(startVec.getX())
                        && Double.isFinite(startVec.getY())
                        && Double.isFinite(startVec.getZ())
                        && Double.isFinite(dirVec.getX())
                        && Double.isFinite(dirVec.getY())
                        && Double.isFinite(dirVec.getZ())) {
                        Vector3f start = startVec.toVector3f();
                        Vector3f dir = dirVec.toVector3f();
                        double traceDist = player.getGameMode() == GameMode.CREATIVE ? 5.0 : 3.0;
                        double dist = Double.MAX_VALUE;
                        HitboxEntity closestHitbox = null;
                        RayTraceResult closestResult = null;
                        for (HitboxEntity hitbox : this.hitboxes.values()) {
                           OrientedBoundingBox obb = hitbox.getOrientedBoundingBox();
                           if (obb == null
                              || !obb.isWithinRange(start, traceDist)
                              || hitbox.getLocation().getWorld() != player.getWorld()
                              || this.entityRelay.getOrDefault(hitbox.getEntityId(), player.getEntityId() + 1) == player.getEntityId()) {
                              continue;
                           }

                           ActiveModel hitModel = hitbox.getBone().getActiveModel();
                           if (hitModel == null
                              || hitModel.getModeledEntity() == null
                              || hitModel.getModeledEntity().getBase().getEntityId() == player.getEntityId()) {
                              continue;
                           }

                           if (!obb.contains(start)) {
                              RayTraceResult result = obb.rayTrace(start, dir, traceDist, null);
                              if (result != null) {
                                 double hitDistSqr = result.getHitPosition().distanceSquared(startVec);
                                 if (!(dist < hitDistSqr)) {
                                    closestHitbox = hitbox;
                                    closestResult = result;
                                 }
                              }
                              continue;
                           }

                           closestHitbox = hitbox;
                           closestResult = null;
                           break;
                        }

                        if (closestHitbox == null) {
                           this.playerRelay.computeIfPresent(player.getUniqueId(), (uuid, existing) -> {
                              existing.destroy();
                              return null;
                           });
                        } else {
                           Vector hitPos = closestResult == null ? startVec.add(dirVec.multiply(traceDist)) : closestResult.getHitPosition();
                           DynamicHitbox dynamicHitbox = this.playerRelay.computeIfAbsent(player.getUniqueId(), uuid -> new DynamicHitbox(player, hitPos));
                           dynamicHitbox.setTarget(closestHitbox.getEntityId());
                           dynamicHitbox.update(hitPos);
                        }
                     } else {
                        this.playerRelay.computeIfPresent(player.getUniqueId(), (uuid, existing) -> {
                           existing.destroy();
                           return null;
                        });
                     }
                  }
               }
            } catch (Throwable t) {
               t.printStackTrace();
            }
         }
      }
   }

   public void addHitbox(HitboxEntity hitbox) {
      this.hitboxesUUID.put(hitbox.getUniqueId(), hitbox.getEntityId());
      this.hitboxes.put(hitbox.getEntityId(), hitbox);
   }

   public void removeHitbox(UUID uuid) {
      Integer id = this.hitboxesUUID.remove(uuid);
      if (id != null) {
         this.hitboxes.remove(id);
      }
   }

   public void removeHitbox(int id) {
      HitboxEntity hitbox = this.hitboxes.remove(id);
      if (hitbox != null) {
         this.hitboxesUUID.remove(hitbox.getUniqueId());
      }
   }

   public HitboxEntity getHitbox(UUID uuid) {
      Integer id = this.hitboxesUUID.get(uuid);
      return id == null ? null : this.hitboxes.get(id);
   }

   public HitboxEntity getHitbox(int id) {
      return this.hitboxes.get(id);
   }

   public void removeDynamicHitbox(UUID uuid) {
      this.playerRelay.remove(uuid);
   }

   public DynamicHitbox getDynamicHitbox(UUID uuid) {
      return this.playerRelay.get(uuid);
   }

   public void setModelRelay(int id, ActiveModel activeModel) {
      this.modelRelay.put(id, activeModel);
   }

   public void removeModelRelay(int id) {
      ActiveModel activeModel = this.modelRelay.remove(id);
   }

   public ActiveModel getModelRelay(int id) {
      return this.modelRelay.get(id);
   }

   public void setEntityRelay(int id, Integer hitboxId) {
      this.entityRelay.put(id, hitboxId);
   }

   public void removeEntityRelay(int id) {
      this.entityRelay.remove(id);
   }

   public Integer getEntityRelay(int id) {
      return this.entityRelay.get(id);
   }
}

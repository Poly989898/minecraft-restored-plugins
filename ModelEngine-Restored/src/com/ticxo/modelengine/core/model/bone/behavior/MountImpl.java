package com.ticxo.modelengine.core.model.bone.behavior;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public class MountImpl extends AbstractBoneBehavior<MountImpl> implements Mount {
   @Getter
   private final boolean driver;
   @Getter
   private final Vector3f location = new Vector3f();
   @Getter
   private final Vector3f globalLocation = new Vector3f();
   private final Set<Entity> passengers = Sets.newConcurrentHashSet();
   private final Map<Entity, Integer> entityIdCache = new ConcurrentHashMap<>();

   public MountImpl(ModelBone bone, BoneBehaviorType<MountImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
      this.driver = data.get("driver", false);
   }

   @Override
   public void onApply() {
      this.bone.getActiveModel().getMountManager().ifPresent(mountManager -> {
         if (this.driver) {
            ((MountManager)mountManager).setDriverBone(this);
         } else {
            ((MountManager)mountManager).registerSeat(this);
         }
      });
      this.bone.getBlueprintBone().getLocalPosition().rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0), this.location);
      Location baseLocation = this.bone.calculatePivotLocation();
      this.globalLocation.set(this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
   }

   @Override
   public void onFinalize() {
      this.bone.getGlobalTransform().mutatePosition(pos -> pos.rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0), this.location));
      Location baseLocation = this.bone.calculatePivotLocation();
      this.globalLocation.set(this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
      this.passengers.removeIf(entity -> ModelEngineAPI.getEntityHandler().isRemoved(entity));
   }

   @Override
   public boolean addPassenger(Entity entity) {
      if (this.canMountMore()) {
         this.passengers.add(entity);
         if (ServerInfo.IS_FOLIA) {
            this.entityIdCache.put(entity, entity.getEntityId());
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean addPassengers(Collection<Entity> entities) {
      for (Entity entity : entities) {
         if (!this.canMountMore()) {
            return false;
         }

         this.passengers.add(entity);
         if (ServerInfo.IS_FOLIA) {
            this.entityIdCache.put(entity, entity.getEntityId());
         }
      }

      return true;
   }

   @Override
   public void removePassenger(Entity entity) {
      this.passengers.remove(entity);
      if (ServerInfo.IS_FOLIA) {
         this.entityIdCache.remove(entity);
      }
   }

   @Override
   public void removePassengers(Collection<Entity> entities) {
      this.passengers.removeAll(entities);
      if (ServerInfo.IS_FOLIA) {
         entities.forEach(this.entityIdCache::remove);
      }
   }

   @Override
   public Set<Entity> clearPassengers() {
      HashSet<Entity> set = new HashSet<>(this.passengers);
      this.passengers.clear();
      if (ServerInfo.IS_FOLIA) {
         this.entityIdCache.clear();
      }

      return set;
   }

   public Collection<Integer> getPassengerEntityIds() {
      return Collections.unmodifiableCollection(this.entityIdCache.values());
   }

   @Override
   public Set<Entity> getPassengers() {
      return ImmutableSet.copyOf(this.passengers);
   }

   @Override
   public boolean canMountMore() {
      return !this.isDriver() || this.passengers.isEmpty();
   }
}

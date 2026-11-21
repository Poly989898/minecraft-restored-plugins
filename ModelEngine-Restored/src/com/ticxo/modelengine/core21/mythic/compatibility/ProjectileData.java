package com.ticxo.modelengine.core21.mythic.compatibility;

import com.google.common.collect.ImmutableSet;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.cull.BasicCulling;
import com.ticxo.modelengine.api.entity.cull.ModelCuller;
import com.ticxo.modelengine.api.entity.data.AbstractEntityData;
import com.ticxo.modelengine.api.entity.data.TrackingMap;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProjectileData extends AbstractEntityData {
   private final ProjectileEntity<?> projectileEntity;
   protected final ModelCuller culler;
   private final AtomicBoolean isAlive = new AtomicBoolean(true);
   private final AtomicReference<Location> location = new AtomicReference<>();
   private final Set<UUID> syncTracking = new HashSet<>();
   private final TrackingMap trackingMap = new TrackingMap();
   private final Map<UUID, CullType> asyncTracking = this.trackingMap;
   private final Queue<UUID> startTrackingQueue = new ConcurrentLinkedQueue<>();
   private final Set<UUID> startTracking = new HashSet<>();
   private final Queue<UUID> stopTrackingQueue = new ConcurrentLinkedQueue<>();
   private final Set<UUID> stopTracking = new HashSet<>();

   public ProjectileData(ProjectileEntity<?> projectileEntity) {
      this(projectileEntity, new BasicCulling());
   }

   public ProjectileData(ProjectileEntity<?> projectileEntity, ModelCuller culler) {
      this.projectileEntity = projectileEntity;
      this.culler = culler;
      culler.setData(this);
      this.syncUpdate();
      this.asyncUpdate();
   }

   @Override
   public void asyncUpdate() {
      while (!this.startTrackingQueue.isEmpty()) {
         UUID player = this.startTrackingQueue.poll();
         this.startTracking.add(player);
         this.asyncTracking.put(player, CullType.NO_CULL);
      }

      while (!this.stopTrackingQueue.isEmpty()) {
         UUID player = this.stopTrackingQueue.poll();
         this.stopTracking.add(player);
         if (this.asyncTracking.get(player) != CullType.CULLED) {
            this.asyncTracking.remove(player);
         }
      }
   }

   @Override
   public void syncUpdate() {
      Location location = BukkitAdapter.adapt(this.projectileEntity.getOriginal().getCurrentLocation());
      this.location.set(location);
      this.isAlive.set(!((IParentSkill)this.projectileEntity.getOriginal()).getCancelled());
      if (!this.isDataValid()) {
         this.projectileEntity.removeSelf();
      }

      HashSet<UUID> updatedTracking = new HashSet<>();
      int radiusSqr = this.projectileEntity.getRenderRadius() * this.projectileEntity.getRenderRadius();
      World world = location.getWorld();
      double lx = location.getX();
      double ly = location.getY();
      double lz = location.getZ();

      for (Player player : Bukkit.getOnlinePlayers()) {
         if (world.equals(player.getWorld())) {
            double dx = player.getX() - lx;
            double dy = player.getY() - ly;
            double dz = player.getZ() - lz;
            if (dx * dx + dy * dy + dz * dz <= radiusSqr) {
               updatedTracking.add(player.getUniqueId());
            }
         }
      }

      for (UUID player : updatedTracking) {
         if (!this.syncTracking.contains(player)) {
            this.startTrackingQueue.add(player);
         }
      }

      for (UUID player : this.syncTracking) {
         if (!updatedTracking.contains(player)) {
            this.stopTrackingQueue.add(player);
         }
      }

      this.syncTracking.clear();
      this.syncTracking.addAll(updatedTracking);
   }

   @Override
   public void cullUpdate() {
      this.culler.updateCulledPlayer();
   }

   @Override
   public void cleanup() {
      this.startTracking.clear();
      this.stopTracking.clear();
   }

   @Override
   public void destroy() {
   }

   @Override
   public BaseEntity<?> getBaseEntity() {
      return this.projectileEntity;
   }

   @Override
   public boolean isDataValid() {
      return this.isAlive.get();
   }

   @Override
   public Location getLocation() {
      return this.location.get();
   }

   @Override
   public List<Entity> getPassengers() {
      return List.of();
   }

   @Override
   public Set<UUID> getStartTracking() {
      return ImmutableSet.copyOf(this.startTracking);
   }

   @Override
   public Map<UUID, CullType> getMutableTracking() {
      return this.asyncTracking;
   }

   @Override
   public Map<UUID, CullType> getTracking() {
      return this.trackingMap.snapshot();
   }

   @Override
   public Set<UUID> getStopTracking() {
      return ImmutableSet.copyOf(this.stopTracking);
   }

   @Override
   public boolean hasTracking() {
      return !this.asyncTracking.isEmpty();
   }

   @Override
   public boolean isBaseGlowing() {
      return false;
   }
}

package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.utils.cache.DataTracker;
import io.lumine.mythic.bukkit.utils.cache.DataTrackerFloat;
import lombok.Getter;
import org.joml.Vector3dc;

public abstract class PacketEntity<T extends IPacketEntityRenderer> {
   @Getter
   protected T renderer;
   @Getter
   protected boolean isDirty;
   @Getter
   protected AbstractWorld world;
   protected DataTracker<Vector3dc> location;
   protected DataTrackerFloat yaw;
   protected DataTrackerFloat pitch;
   protected DataTracker<AbstractVector> velocity;
   @Getter
   protected AbstractLocation previousLocation;
   @Getter
   protected DataTracker<Boolean> hasGravity;

   public PacketEntity(AbstractLocation location) {
      this.world = location.getWorld();
      this.location = DataTracker.of(location.toVector3d());
      this.yaw = DataTrackerFloat.of(location.getYaw());
      this.pitch = DataTrackerFloat.of(location.getPitch());
      this.velocity = DataTracker.of(new AbstractVector(0, 0, 0));
      this.previousLocation = new AbstractLocation();
      this.hasGravity = DataTracker.of(Boolean.TRUE);
   }

   protected void setRenderer(T renderer) {
      this.renderer = renderer;
   }

   public void update() {
      this.renderer.update();
   }

   public void destroy() {
      this.renderer.destroy();
   }

   public int getEntityId() {
      return this.renderer.getEntityId();
   }

   public DataTracker<Vector3dc> getLocation() {
      return this.location;
   }

   public DataTracker<Float> getYaw() {
      return this.yaw;
   }

   public DataTracker<Float> getPitch() {
      return this.pitch;
   }

   public void setPosition(AbstractLocation location) {
      this.world = location.getWorld();
      this.location.poke(location.toVector3d());
      this.yaw.pokeFloat(location.getYaw());
      this.pitch.pokeFloat(location.getPitch());
      this.location.clearDirty();
      this.yaw.clearDirty();
      this.pitch.clearDirty();
   }

   public void teleport(AbstractLocation location) {
      this.world = location.getWorld();
      this.location.set(location.toVector3d());
      this.yaw.setFloat(location.getYaw());
      this.pitch.setFloat(location.getPitch());
      this.renderer.update();
   }

   public void setVelocity(AbstractVector velocity) {
      this.velocity.set(velocity.clone());
      this.renderer.update();
   }

   public void setHasGravity(Boolean bool) {
      this.hasGravity.set(bool);
      this.renderer.update();
   }

   public void setDirty() {
      this.isDirty = true;
   }

   public void clearDirty() {
      this.isDirty = false;
   }
}
